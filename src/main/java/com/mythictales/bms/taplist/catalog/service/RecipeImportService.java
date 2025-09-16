package com.mythictales.bms.taplist.catalog.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mythictales.bms.taplist.catalog.domain.MashStep;
import com.mythictales.bms.taplist.catalog.domain.Recipe;
import com.mythictales.bms.taplist.catalog.domain.Recipe.SourceFormat;
import com.mythictales.bms.taplist.catalog.domain.RecipeFermentable;
import com.mythictales.bms.taplist.catalog.domain.RecipeHop;
import com.mythictales.bms.taplist.catalog.domain.RecipeMisc;
import com.mythictales.bms.taplist.catalog.domain.RecipeYeast;
import com.mythictales.bms.taplist.catalog.repo.RecipeRepository;
import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.repo.BreweryRepository;

@Service
public class RecipeImportService {
  private final RecipeRepository recipes;
  private final BreweryRepository breweries;

  public RecipeImportService(RecipeRepository recipes, BreweryRepository breweries) {
    this.recipes = recipes;
    this.breweries = breweries;
  }

  @Transactional
  public List<Long> importXml(Long breweryId, String xml, boolean force) {
    Brewery brewery = breweries.findById(breweryId).orElseThrow();
    try {
      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      Document doc =
          f.newDocumentBuilder()
              .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
      Element root = doc.getDocumentElement();
      String rootName = root.getTagName();
      List<Long> ids = new ArrayList<>();
      if (rootName.equalsIgnoreCase("RECIPES") || rootName.equalsIgnoreCase("Recipes")) {
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
          Node n = children.item(i);
          if (n.getNodeType() == Node.ELEMENT_NODE
              && (n.getNodeName().equalsIgnoreCase("RECIPE")
                  || n.getNodeName().equalsIgnoreCase("Recipe"))) {
            Long id = importSingleRecipeNode(brewery, (Element) n, xmlFragment((Element) n), force);
            ids.add(id);
          }
        }
      } else if (rootName.equalsIgnoreCase("RECIPE") || rootName.equalsIgnoreCase("Recipe")) {
        ids.add(importSingleRecipeNode(brewery, root, xmlFragment(root), force));
      } else {
        // Fallback: treat entire doc as one recipe
        ids.add(importSingleRecipeNode(brewery, root, normalize(xml), force));
      }
      return ids;
    } catch (Exception e) {
      throw new com.mythictales.bms.taplist.service.BusinessValidationException(
          "Failed to parse recipe XML", java.util.Map.of("reason", "XML_PARSE_ERROR"));
    }
  }

  private Long importSingleRecipeNode(
      Brewery brewery, Element el, String xmlFragment, boolean force) throws Exception {
    String hash = sha256(normalize(xmlFragment));
    if (!force) {
      var dup = recipes.findFirstByBrewery_IdAndSourceHash(brewery.getId(), hash);
      if (dup.isPresent()) throw new DuplicateRecipeException(dup.get().getId());
    }
    Recipe r = new Recipe();
    r.setBrewery(brewery);
    String name = text(el, "NAME");
    if (name == null) name = text(el, "Name");
    r.setName(name != null ? name : "Imported Recipe");
    r.setStyleName(coalesce(text(el, "STYLE/NAME"), text(el, "Style/Name")));
    r.setType(coalesce(text(el, "TYPE"), text(el, "Type")));
    r.setBoilTimeMinutes(intOrNull(coalesce(text(el, "BOIL_TIME"), text(el, "BoilTime"))));
    r.setBatchSizeLiters(
        doubleOrNull(coalesce(text(el, "BATCH_SIZE"), text(el, "BatchSizeLiters"))));
    r.setIbu(doubleOrNull(coalesce(text(el, "IBU"), text(el, "IBUs"))));
    r.setAbv(doubleOrNull(coalesce(text(el, "ABV"), text(el, "ABV"))));
    r.setOg(doubleOrNull(coalesce(text(el, "OG"), text(el, "OG"))));
    r.setFg(doubleOrNull(coalesce(text(el, "FG"), text(el, "FG"))));
    r.setEquipment(coalesce(text(el, "EQUIPMENT"), text(el, "Equipment/Name")));
    r.setNotes(coalesce(text(el, "NOTES"), text(el, "Notes")));
    r.setSourceFormat(SourceFormat.BEERXML); // default
    if (el.getTagName().equalsIgnoreCase("Recipe")
        || el.getOwnerDocument().getDocumentElement().getTagName().equalsIgnoreCase("Recipes")) {
      r.setSourceFormat(SourceFormat.BEERSMITH);
    }
    r.setSourceHash(hash);
    // parse children
    RecipeChildParsers.parseFermentables(el, r);
    RecipeChildParsers.parseHops(el, r);
    RecipeChildParsers.parseYeasts(el, r);
    RecipeChildParsers.parseMiscs(el, r);
    RecipeChildParsers.parseMash(el, r);
    recipes.save(r);
    return r.getId();
  }

  private static String coalesce(String a, String b) {
    return a != null ? a : b;
  }

  private static Integer intOrNull(String s) {
    try {
      return s == null ? null : Integer.valueOf(s.trim());
    } catch (Exception e) {
      return null;
    }
  }

  private static Double doubleOrNull(String s) {
    try {
      return s == null ? null : Double.valueOf(s.trim());
    } catch (Exception e) {
      return null;
    }
  }

  private static String text(Element el, String path) {
    // rudimentary path parser: supports single level or STYLE/NAME style
    try {
      if (path.contains("/")) {
        String[] parts = path.split("/");
        Element cur = el;
        for (String p : parts) {
          NodeList nl = cur.getElementsByTagName(p);
          if (nl.getLength() == 0) return null;
          cur = (Element) nl.item(0);
        }
        return cur.getTextContent();
      } else {
        NodeList nl = el.getElementsByTagName(path);
        if (nl.getLength() == 0) return null;
        return nl.item(0).getTextContent();
      }
    } catch (Exception e) {
      return null;
    }
  }

  private static String normalize(String xml) {
    return xml.replaceAll("<!--.*?-->", "").replaceAll("\n|\r|\t", "").trim();
  }

  private static String xmlFragment(Element el) {
    // naive: use text content + tag name as uniqueness signal
    return ("<" + el.getTagName() + ">" + el.getTextContent() + "</" + el.getTagName() + ">");
  }

  private static String sha256(String s) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
    return Base64.getUrlEncoder().withoutPadding().encodeToString(dig);
  }

  public static class DuplicateRecipeException extends RuntimeException {
    private final Long existingId;

    public DuplicateRecipeException(Long id) {
      super("duplicate");
      this.existingId = id;
    }

    public Long getExistingId() {
      return existingId;
    }
  }
}

// Helper parsing methods
class XmlUtil {
  static Element first(Element parent, String... names) {
    for (String name : names) {
      NodeList nl = parent.getElementsByTagName(name);
      if (nl.getLength() > 0) return (Element) nl.item(0);
    }
    return null;
  }

  static List<Element> children(Element parent, String... names) {
    List<Element> out = new ArrayList<>();
    for (String name : names) {
      NodeList nl = parent.getElementsByTagName(name);
      for (int i = 0; i < nl.getLength(); i++) {
        Node n = nl.item(i);
        if (n.getNodeType() == Node.ELEMENT_NODE) out.add((Element) n);
      }
    }
    return out;
  }
}

// Extend service with child parsing
class RecipeChildParsers {
  static void parseFermentables(Element el, Recipe r) {
    Element fer = XmlUtil.first(el, "FERMENTABLES", "Fermentables");
    if (fer == null) return;
    for (Element fe : XmlUtil.children(fer, "FERMENTABLE", "Fermentable")) {
      RecipeFermentable f = new RecipeFermentable();
      f.setRecipe(r);
      f.setName(text(fe, "NAME", "Name"));
      Double amtKg = dbl(text(fe, "AMOUNT", "AmountKg"));
      f.setAmountKg(amtKg);
      f.setYieldPercent(dbl(text(fe, "YIELD", "Yield")));
      f.setColorLovibond(dbl(text(fe, "COLOR", "Color")));
      String late = text(fe, "LATE_ADDITION", "LateAddition", "LateExtractAddition");
      f.setLateAddition(bool(late));
      f.setType(text(fe, "TYPE", "Type"));
      if (f.getName() != null) {
        r.getFermentables().add(f);
      }
    }
  }

  static void parseHops(Element el, Recipe r) {
    Element hops = XmlUtil.first(el, "HOPS", "Hops");
    if (hops == null) return;
    for (Element he : XmlUtil.children(hops, "HOP", "Hop")) {
      RecipeHop h = new RecipeHop();
      h.setRecipe(r);
      h.setName(text(he, "NAME", "Name"));
      h.setAlphaAcid(dbl(text(he, "ALPHA", "Alpha")));
      Double amtKg = dbl(text(he, "AMOUNT", "AmountKg"));
      h.setAmountGrams(amtKg != null ? amtKg * 1000.0 : null);
      h.setTimeMinutes(intOrNull(text(he, "TIME", "Time")));
      h.setUseFor(text(he, "USE", "Use"));
      h.setForm(text(he, "FORM", "Form"));
      h.setIbuContribution(dbl(text(he, "IBU", "IBU")));
      if (h.getName() != null) {
        r.getHops().add(h);
      }
    }
  }

  static void parseYeasts(Element el, Recipe r) {
    Element ys = XmlUtil.first(el, "YEASTS", "Yeasts");
    if (ys == null) return;
    for (Element ye : XmlUtil.children(ys, "YEAST", "Yeast")) {
      RecipeYeast y = new RecipeYeast();
      y.setRecipe(r);
      y.setName(text(ye, "NAME", "Name"));
      y.setLaboratory(text(ye, "LABORATORY", "Laboratory"));
      y.setProductId(text(ye, "PRODUCT_ID", "ProductId", "ProductID"));
      y.setType(text(ye, "TYPE", "Type"));
      y.setForm(text(ye, "FORM", "Form"));
      y.setAttenuation(dbl(text(ye, "ATTENUATION", "Attenuation")));
      if (y.getName() != null || y.getLaboratory() != null || y.getProductId() != null) {
        r.getYeasts().add(y);
      }
    }
  }

  static void parseMiscs(Element el, Recipe r) {
    Element ms = XmlUtil.first(el, "MISCS", "Miscs");
    if (ms == null) return;
    for (Element me : XmlUtil.children(ms, "MISC", "Misc")) {
      RecipeMisc m = new RecipeMisc();
      m.setRecipe(r);
      m.setName(text(me, "NAME", "Name"));
      m.setType(text(me, "TYPE", "Type"));
      m.setAmount(dbl(text(me, "AMOUNT", "Amount")));
      String isWeight = text(me, "AMOUNT_IS_WEIGHT", "AmountIsWeight");
      if (m.getAmount() != null) {
        m.setAmountUnit(Boolean.TRUE.equals(bool(isWeight)) ? "g" : "l");
      }
      m.setUseFor(text(me, "USE", "Use"));
      if (m.getName() != null) {
        r.getMiscs().add(m);
      }
    }
  }

  static void parseMash(Element el, Recipe r) {
    Element mash = XmlUtil.first(el, "MASH", "Mash");
    if (mash == null) return;
    Element steps = XmlUtil.first(mash, "MASH_STEPS", "Mash_Steps", "MashSteps");
    if (steps == null) return;
    for (Element se : XmlUtil.children(steps, "MASH_STEP", "Mash_Step", "MashStep")) {
      MashStep ms = new MashStep();
      ms.setRecipe(r);
      ms.setName(text(se, "NAME", "Name"));
      ms.setType(text(se, "TYPE", "Type"));
      ms.setStepTempC(dbl(text(se, "STEP_TEMP", "StepTempC", "StepTemp")));
      ms.setStepTimeMinutes(intOrNull(text(se, "STEP_TIME", "StepTime")));
      ms.setInfuseAmountLiters(dbl(text(se, "INFUSE_AMOUNT", "InfuseAmount")));
      if (ms.getName() != null || ms.getStepTempC() != null || ms.getStepTimeMinutes() != null) {
        r.getMashSteps().add(ms);
      }
    }
  }

  static String text(Element el, String... names) {
    for (String n : names) {
      try {
        NodeList nl = el.getElementsByTagName(n);
        if (nl.getLength() > 0) return nl.item(0).getTextContent();
      } catch (Exception ignore) {
      }
    }
    return null;
  }

  static Integer intOrNull(String s) {
    try {
      return s == null || s.isBlank() ? null : Integer.valueOf(s.trim());
    } catch (Exception e) {
      return null;
    }
  }

  static Double dbl(String s) {
    try {
      return s == null || s.isBlank() ? null : Double.valueOf(s.trim());
    } catch (Exception e) {
      return null;
    }
  }

  static Boolean bool(String s) {
    if (s == null) return null;
    String v = s.trim().toLowerCase();
    return switch (v) {
      case "true", "1", "yes", "y" -> true;
      case "false", "0", "no", "n" -> false;
      default -> null;
    };
  }
}
