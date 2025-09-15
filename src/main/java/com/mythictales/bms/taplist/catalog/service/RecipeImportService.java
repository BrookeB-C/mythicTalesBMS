package com.mythictales.bms.taplist.catalog.service;

import com.mythictales.bms.taplist.catalog.domain.Recipe;
import com.mythictales.bms.taplist.catalog.domain.Recipe.SourceFormat;
import com.mythictales.bms.taplist.catalog.repo.RecipeRepository;
import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.repo.BreweryRepository;
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
      Document doc = f.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
      Element root = doc.getDocumentElement();
      String rootName = root.getTagName();
      List<Long> ids = new ArrayList<>();
      if (rootName.equalsIgnoreCase("RECIPES") || rootName.equalsIgnoreCase("Recipes")) {
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
          Node n = children.item(i);
          if (n.getNodeType() == Node.ELEMENT_NODE && (n.getNodeName().equalsIgnoreCase("RECIPE") || n.getNodeName().equalsIgnoreCase("Recipe"))) {
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
      throw new RuntimeException("Failed to parse recipe XML", e);
    }
  }

  private Long importSingleRecipeNode(Brewery brewery, Element el, String xmlFragment, boolean force) throws Exception {
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
    r.setBatchSizeLiters(doubleOrNull(coalesce(text(el, "BATCH_SIZE"), text(el, "BatchSizeLiters"))));
    r.setIbu(doubleOrNull(coalesce(text(el, "IBU"), text(el, "IBUs"))));
    r.setAbv(doubleOrNull(coalesce(text(el, "ABV"), text(el, "ABV"))));
    r.setOg(doubleOrNull(coalesce(text(el, "OG"), text(el, "OG"))));
    r.setFg(doubleOrNull(coalesce(text(el, "FG"), text(el, "FG"))));
    r.setEquipment(coalesce(text(el, "EQUIPMENT"), text(el, "Equipment/Name")));
    r.setNotes(coalesce(text(el, "NOTES"), text(el, "Notes")));
    r.setSourceFormat(SourceFormat.BEERXML); // default
    if (el.getTagName().equalsIgnoreCase("Recipe") || el.getOwnerDocument().getDocumentElement().getTagName().equalsIgnoreCase("Recipes")) {
      r.setSourceFormat(SourceFormat.BEERSMITH);
    }
    r.setSourceHash(hash);
    recipes.save(r);
    return r.getId();
  }

  private static String coalesce(String a, String b) { return a != null ? a : b; }

  private static Integer intOrNull(String s) {
    try { return s == null ? null : Integer.valueOf(s.trim()); } catch (Exception e) { return null; }
  }
  private static Double doubleOrNull(String s) {
    try { return s == null ? null : Double.valueOf(s.trim()); } catch (Exception e) { return null; }
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
    public DuplicateRecipeException(Long id) { super("duplicate"); this.existingId = id; }
    public Long getExistingId() { return existingId; }
  }
}

