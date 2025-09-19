package com.mythictales.bms.taplist.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mythictales.bms.taplist.catalog.domain.Recipe;
import com.mythictales.bms.taplist.catalog.repo.RecipeRepository;
import com.mythictales.bms.taplist.catalog.service.RecipeImportService;
import com.mythictales.bms.taplist.catalog.service.RecipeImportService.DuplicateRecipeException;
import com.mythictales.bms.taplist.security.CurrentUser;
import com.mythictales.bms.taplist.service.BusinessValidationException;

@Controller
@RequestMapping("/admin/catalog/recipes")
public class AdminCatalogController {
  private static final long MAX_IMPORT_BYTES = 2_000_000L;

  private final RecipeRepository recipes;
  private final RecipeImportService importer;

  public AdminCatalogController(RecipeRepository recipes, RecipeImportService importer) {
    this.recipes = recipes;
    this.importer = importer;
  }

  @GetMapping
  public String list(
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam(value = "q", required = false) String q,
      Model model) {
    Long breweryId = user != null ? user.getBreweryId() : null;
    List<Recipe> list =
        breweryId != null
            ? recipes.findAll().stream()
                .filter(r -> r.getBrewery() != null && breweryId.equals(r.getBrewery().getId()))
                .collect(Collectors.toList())
            : recipes.findAll();
    if (q != null && !q.isBlank()) {
      String qq = q.toLowerCase();
      list =
          list.stream()
              .filter(
                  r ->
                      (r.getName() != null && r.getName().toLowerCase().contains(qq))
                          || (r.getStyleName() != null
                              && r.getStyleName().toLowerCase().contains(qq)))
              .collect(Collectors.toList());
      if (list.size() == 1) {
        return "redirect:/admin/catalog/recipes/" + list.get(0).getId();
      }
    }
    model.addAttribute("recipes", list);
    model.addAttribute("q", q);
    model.addAttribute("recipeCount", list.size());
    return "admin/catalog-recipes";
  }

  @PostMapping("/import")
  public String importRecipes(
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "force", defaultValue = "false") boolean force,
      RedirectAttributes redirectAttributes) {
    if (user == null || user.getBreweryId() == null) {
      redirectAttributes.addFlashAttribute(
          "importError", "Assign a brewery to your profile before importing recipes.");
      redirectAttributes.addFlashAttribute("importForce", force);
      return "redirect:/admin/catalog/recipes";
    }

    if (file == null || file.isEmpty()) {
      redirectAttributes.addFlashAttribute(
          "importError", "Select a BeerXML or BeerSmith XML file to import.");
      redirectAttributes.addFlashAttribute("importForce", force);
      return "redirect:/admin/catalog/recipes";
    }

    if (file.getSize() > MAX_IMPORT_BYTES) {
      redirectAttributes.addFlashAttribute(
          "importError", "File too large. Recipe imports are limited to 2MB.");
      redirectAttributes.addFlashAttribute("importForce", force);
      return "redirect:/admin/catalog/recipes";
    }

    String contentType = file.getContentType();
    if (contentType != null
        && !(contentType.equalsIgnoreCase(MediaType.APPLICATION_XML_VALUE)
            || contentType.equalsIgnoreCase(MediaType.TEXT_XML_VALUE)
            || contentType.equalsIgnoreCase(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            || contentType.equalsIgnoreCase("application/zip")
            || contentType.equalsIgnoreCase("application/x-zip-compressed")
            || contentType.equalsIgnoreCase("application/x-zip"))) {
      redirectAttributes.addFlashAttribute("importError", "Unsupported file type: " + contentType);
      redirectAttributes.addFlashAttribute("importForce", force);
      return "redirect:/admin/catalog/recipes";
    }

    try {
      List<Long> ids =
          importer.importFile(
              user.getBreweryId(), file.getBytes(), file.getOriginalFilename(), force);
      redirectAttributes.addFlashAttribute("importSuccessCount", ids.size());
      redirectAttributes.addFlashAttribute("importForce", force);
    } catch (DuplicateRecipeException dup) {
      redirectAttributes.addFlashAttribute("importDuplicateId", dup.getExistingId());
      redirectAttributes.addFlashAttribute(
          "importError", "Recipe already exists. Enable 'Overwrite duplicates' to replace it.");
      redirectAttributes.addFlashAttribute("importForce", force);
    } catch (BusinessValidationException e) {
      redirectAttributes.addFlashAttribute("importError", e.getMessage());
      redirectAttributes.addFlashAttribute("importForce", force);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute(
          "importError", "Import failed. Verify the XML and try again.");
      redirectAttributes.addFlashAttribute("importForce", force);
    }

    return "redirect:/admin/catalog/recipes";
  }

  @GetMapping("/{id}")
  public String edit(
      @PathVariable Long id, @AuthenticationPrincipal CurrentUser user, Model model) {
    Recipe r = recipes.findById(id).orElseThrow();
    if (user != null && user.getBreweryId() != null && r.getBrewery() != null) {
      if (!user.getBreweryId().equals(r.getBrewery().getId())) {
        throw new org.springframework.security.access.AccessDeniedException(
            "Cross-tenant edit forbidden");
      }
    }
    model.addAttribute("recipe", r);
    model.addAttribute("fermentableCount", r.getFermentables().size());
    model.addAttribute("hopCount", r.getHops().size());
    model.addAttribute("yeastCount", r.getYeasts().size());
    model.addAttribute("miscCount", r.getMiscs().size());
    model.addAttribute("mashStepCount", r.getMashSteps().size());
    return "admin/catalog-recipe-edit";
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam("name") String name,
      @RequestParam(value = "styleName", required = false) String styleName,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "batchSizeLiters", required = false) Double batchSizeLiters,
      @RequestParam(value = "boilTimeMinutes", required = false) Integer boilTimeMinutes,
      @RequestParam(value = "notes", required = false) String notes) {
    Recipe r = recipes.findById(id).orElseThrow();
    if (user != null && user.getBreweryId() != null && r.getBrewery() != null) {
      if (!user.getBreweryId().equals(r.getBrewery().getId())) {
        throw new org.springframework.security.access.AccessDeniedException(
            "Cross-tenant edit forbidden");
      }
    }
    if (name != null && !name.isBlank()) r.setName(name.trim());
    r.setStyleName(styleName != null ? styleName.trim() : null);
    r.setType(type != null ? type.trim() : null);
    r.setBatchSizeLiters(batchSizeLiters);
    r.setBoilTimeMinutes(boilTimeMinutes);
    r.setNotes(notes);
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  // ---------- Fermentables ----------
  @PostMapping("/{id}/fermentables/add")
  public String addFermentable(
      @PathVariable Long id,
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam("name") String name,
      @RequestParam(value = "amountKg", required = false) Double amountKg,
      @RequestParam(value = "yieldPercent", required = false) Double yieldPercent,
      @RequestParam(value = "colorLovibond", required = false) Double colorLovibond,
      @RequestParam(value = "lateAddition", required = false) Boolean lateAddition,
      @RequestParam(value = "type", required = false) String type) {
    Recipe r = recipes.findById(id).orElseThrow();
    if (user != null && user.getBreweryId() != null && r.getBrewery() != null) {
      if (!user.getBreweryId().equals(r.getBrewery().getId())) {
        throw new org.springframework.security.access.AccessDeniedException(
            "Cross-tenant edit forbidden");
      }
    }
    var f = new com.mythictales.bms.taplist.catalog.domain.RecipeFermentable();
    f.setRecipe(r);
    f.setName(name);
    f.setAmountKg(amountKg);
    f.setYieldPercent(yieldPercent);
    f.setColorLovibond(colorLovibond);
    f.setLateAddition(lateAddition);
    f.setType(type);
    r.getFermentables().add(f);
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  @PostMapping("/{id}/fermentables/{fid}/delete")
  public String deleteFermentable(
      @PathVariable Long id, @PathVariable Long fid, @AuthenticationPrincipal CurrentUser user) {
    Recipe r = recipes.findById(id).orElseThrow();
    authorize(user, r);
    r.getFermentables().removeIf(x -> fid.equals(x.getId()));
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  // ---------- Hops ----------
  @PostMapping("/{id}/hops/add")
  public String addHop(
      @PathVariable Long id,
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam("name") String name,
      @RequestParam(value = "alphaAcid", required = false) Double alphaAcid,
      @RequestParam(value = "amountGrams", required = false) Double amountGrams,
      @RequestParam(value = "timeMinutes", required = false) Integer timeMinutes,
      @RequestParam(value = "useFor", required = false) String useFor,
      @RequestParam(value = "form", required = false) String form,
      @RequestParam(value = "ibuContribution", required = false) Double ibuContribution) {
    Recipe r = recipes.findById(id).orElseThrow();
    authorize(user, r);
    var h = new com.mythictales.bms.taplist.catalog.domain.RecipeHop();
    h.setRecipe(r);
    h.setName(name);
    h.setAlphaAcid(alphaAcid);
    h.setAmountGrams(amountGrams);
    h.setTimeMinutes(timeMinutes);
    h.setUseFor(useFor);
    h.setForm(form);
    h.setIbuContribution(ibuContribution);
    r.getHops().add(h);
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  @PostMapping("/{id}/hops/{hid}/delete")
  public String deleteHop(
      @PathVariable Long id, @PathVariable Long hid, @AuthenticationPrincipal CurrentUser user) {
    Recipe r = recipes.findById(id).orElseThrow();
    authorize(user, r);
    r.getHops().removeIf(x -> hid.equals(x.getId()));
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  // ---------- Yeasts ----------
  @PostMapping("/{id}/yeasts/add")
  public String addYeast(
      @PathVariable Long id,
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "laboratory", required = false) String laboratory,
      @RequestParam(value = "productId", required = false) String productId,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "form", required = false) String form,
      @RequestParam(value = "attenuation", required = false) Double attenuation) {
    Recipe r = recipes.findById(id).orElseThrow();
    authorize(user, r);
    var y = new com.mythictales.bms.taplist.catalog.domain.RecipeYeast();
    y.setRecipe(r);
    y.setName(name);
    y.setLaboratory(laboratory);
    y.setProductId(productId);
    y.setType(type);
    y.setForm(form);
    y.setAttenuation(attenuation);
    r.getYeasts().add(y);
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  @PostMapping("/{id}/yeasts/{yid}/delete")
  public String deleteYeast(
      @PathVariable Long id, @PathVariable Long yid, @AuthenticationPrincipal CurrentUser user) {
    Recipe r = recipes.findById(id).orElseThrow();
    authorize(user, r);
    r.getYeasts().removeIf(x -> yid.equals(x.getId()));
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  // ---------- Miscs ----------
  @PostMapping("/{id}/miscs/add")
  public String addMisc(
      @PathVariable Long id,
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "amount", required = false) Double amount,
      @RequestParam(value = "amountUnit", required = false) String amountUnit,
      @RequestParam(value = "useFor", required = false) String useFor) {
    Recipe r = recipes.findById(id).orElseThrow();
    authorize(user, r);
    var m = new com.mythictales.bms.taplist.catalog.domain.RecipeMisc();
    m.setRecipe(r);
    m.setName(name);
    m.setType(type);
    m.setAmount(amount);
    m.setAmountUnit(amountUnit);
    m.setUseFor(useFor);
    r.getMiscs().add(m);
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  @PostMapping("/{id}/miscs/{mid}/delete")
  public String deleteMisc(
      @PathVariable Long id, @PathVariable Long mid, @AuthenticationPrincipal CurrentUser user) {
    Recipe r = recipes.findById(id).orElseThrow();
    authorize(user, r);
    r.getMiscs().removeIf(x -> mid.equals(x.getId()));
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  // ---------- Mash Steps ----------
  @PostMapping("/{id}/mash/add")
  public String addMash(
      @PathVariable Long id,
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "stepTempC", required = false) Double stepTempC,
      @RequestParam(value = "stepTimeMinutes", required = false) Integer stepTimeMinutes,
      @RequestParam(value = "infuseAmountLiters", required = false) Double infuseAmountLiters) {
    Recipe r = recipes.findById(id).orElseThrow();
    authorize(user, r);
    var ms = new com.mythictales.bms.taplist.catalog.domain.MashStep();
    ms.setRecipe(r);
    ms.setName(name);
    ms.setType(type);
    ms.setStepTempC(stepTempC);
    ms.setStepTimeMinutes(stepTimeMinutes);
    ms.setInfuseAmountLiters(infuseAmountLiters);
    r.getMashSteps().add(ms);
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  @PostMapping("/{id}/mash/{msid}/delete")
  public String deleteMash(
      @PathVariable Long id, @PathVariable Long msid, @AuthenticationPrincipal CurrentUser user) {
    Recipe r = recipes.findById(id).orElseThrow();
    authorize(user, r);
    r.getMashSteps().removeIf(x -> msid.equals(x.getId()));
    recipes.save(r);
    return "redirect:/admin/catalog/recipes/" + id;
  }

  private void authorize(CurrentUser user, Recipe r) {
    if (user != null && user.getBreweryId() != null && r.getBrewery() != null) {
      if (!user.getBreweryId().equals(r.getBrewery().getId())) {
        throw new org.springframework.security.access.AccessDeniedException(
            "Cross-tenant edit forbidden");
      }
    }
  }

  @GetMapping("/{id}/export")
  public ResponseEntity<byte[]> exportXml(
      @PathVariable Long id,
      @RequestParam(value = "format", defaultValue = "beerxml") String format,
      @AuthenticationPrincipal CurrentUser user) {
    Recipe r = recipes.findById(id).orElseThrow();
    if (user != null && user.getBreweryId() != null && r.getBrewery() != null) {
      if (!user.getBreweryId().equals(r.getBrewery().getId())) {
        throw new org.springframework.security.access.AccessDeniedException(
            "Cross-tenant export forbidden");
      }
    }
    String xml =
        switch (format.toLowerCase()) {
          case "beersmith" -> toBeerSmithXml(r);
          default -> toBeerXml(r);
        };
    String filename =
        (r.getName() != null ? r.getName().replaceAll("\\s+", "_") : "recipe")
            + "-"
            + format
            + ".xml";
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.APPLICATION_XML)
        .body(xml.getBytes(StandardCharsets.UTF_8));
  }

  private String toBeerXml(Recipe r) {
    StringBuilder sb = new StringBuilder();
    sb.append("<RECIPES><RECIPE>");
    tag(sb, "NAME", r.getName());
    sb.append("<STYLE>");
    tag(sb, "NAME", r.getStyleName());
    sb.append("</STYLE>");
    tag(sb, "TYPE", r.getType());
    tag(sb, "BATCH_SIZE", dbl(r.getBatchSizeLiters()));
    tag(sb, "BOIL_TIME", intv(r.getBoilTimeMinutes()));
    tag(sb, "IBU", dbl(r.getIbu()));
    tag(sb, "ABV", dbl(r.getAbv()));
    tag(sb, "OG", dbl(r.getOg()));
    tag(sb, "FG", dbl(r.getFg()));
    tag(sb, "EFFICIENCY", dbl(r.getEfficiency()));
    tag(sb, "EQUIPMENT", r.getEquipment());
    tag(sb, "NOTES", r.getNotes());
    // Fermentables
    sb.append("<FERMENTABLES>");
    for (var f : r.getFermentables()) {
      sb.append("<FERMENTABLE>");
      tag(sb, "NAME", f.getName());
      tag(sb, "AMOUNT", dbl(f.getAmountKg()));
      tag(sb, "YIELD", dbl(f.getYieldPercent()));
      tag(sb, "COLOR", dbl(f.getColorLovibond()));
      tag(sb, "TYPE", f.getType());
      sb.append("</FERMENTABLE>");
    }
    sb.append("</FERMENTABLES>");
    // Hops
    sb.append("<HOPS>");
    for (var h : r.getHops()) {
      sb.append("<HOP>");
      tag(sb, "NAME", h.getName());
      tag(sb, "ALPHA", dbl(h.getAlphaAcid()));
      tag(sb, "AMOUNT", dbl(h.getAmountGrams() != null ? h.getAmountGrams() / 1000.0 : null));
      tag(sb, "TIME", intv(h.getTimeMinutes()));
      tag(sb, "USE", h.getUseFor());
      tag(sb, "FORM", h.getForm());
      tag(sb, "IBU", dbl(h.getIbuContribution()));
      sb.append("</HOP>");
    }
    sb.append("</HOPS>");
    // Yeasts
    sb.append("<YEASTS>");
    for (var y : r.getYeasts()) {
      sb.append("<YEAST>");
      tag(sb, "NAME", y.getName());
      tag(sb, "LABORATORY", y.getLaboratory());
      tag(sb, "PRODUCT_ID", y.getProductId());
      tag(sb, "TYPE", y.getType());
      tag(sb, "FORM", y.getForm());
      tag(sb, "ATTENUATION", dbl(y.getAttenuation()));
      sb.append("</YEAST>");
    }
    sb.append("</YEASTS>");
    // Misc
    sb.append("<MISCS>");
    for (var m : r.getMiscs()) {
      sb.append("<MISC>");
      tag(sb, "NAME", m.getName());
      tag(sb, "TYPE", m.getType());
      tag(sb, "AMOUNT", dbl(m.getAmount()));
      tag(sb, "USE", m.getUseFor());
      sb.append("</MISC>");
    }
    sb.append("</MISCS>");
    // Mash Steps
    sb.append("<MASH><MASH_STEPS>");
    for (var ms : r.getMashSteps()) {
      sb.append("<MASH_STEP>");
      tag(sb, "NAME", ms.getName());
      tag(sb, "TYPE", ms.getType());
      tag(sb, "STEP_TEMP", dbl(ms.getStepTempC()));
      tag(sb, "STEP_TIME", intv(ms.getStepTimeMinutes()));
      tag(sb, "INFUSE_AMOUNT", dbl(ms.getInfuseAmountLiters()));
      sb.append("</MASH_STEP>");
    }
    sb.append("</MASH_STEPS></MASH>");
    sb.append("</RECIPE></RECIPES>");
    return sb.toString();
  }

  private String toBeerSmithXml(Recipe r) {
    StringBuilder sb = new StringBuilder();
    sb.append("<Recipes><Recipe>");
    tag(sb, "Name", r.getName());
    sb.append("<Style>");
    tag(sb, "Name", r.getStyleName());
    sb.append("</Style>");
    tag(sb, "Type", r.getType());
    tag(sb, "BatchSizeLiters", dbl(r.getBatchSizeLiters()));
    tag(sb, "BoilTime", intv(r.getBoilTimeMinutes()));
    tag(sb, "IBUs", dbl(r.getIbu()));
    tag(sb, "ABV", dbl(r.getAbv()));
    tag(sb, "OG", dbl(r.getOg()));
    tag(sb, "FG", dbl(r.getFg()));
    tag(sb, "Efficiency", dbl(r.getEfficiency()));
    tag(sb, "Equipment", r.getEquipment());
    tag(sb, "Notes", r.getNotes());
    // Fermentables
    sb.append("<Fermentables>");
    for (var f : r.getFermentables()) {
      sb.append("<Fermentable>");
      tag(sb, "Name", f.getName());
      tag(sb, "AmountKg", dbl(f.getAmountKg()));
      tag(sb, "Yield", dbl(f.getYieldPercent()));
      tag(sb, "Color", dbl(f.getColorLovibond()));
      tag(
          sb,
          "LateAddition",
          f.getLateAddition() != null ? (f.getLateAddition() ? "true" : "false") : null);
      tag(sb, "Type", f.getType());
      sb.append("</Fermentable>");
    }
    sb.append("</Fermentables>");
    // Hops
    sb.append("<Hops>");
    for (var h : r.getHops()) {
      sb.append("<Hop>");
      tag(sb, "Name", h.getName());
      tag(sb, "Alpha", dbl(h.getAlphaAcid()));
      tag(sb, "AmountKg", dbl(h.getAmountGrams() != null ? h.getAmountGrams() / 1000.0 : null));
      tag(sb, "Time", intv(h.getTimeMinutes()));
      tag(sb, "Use", h.getUseFor());
      tag(sb, "Form", h.getForm());
      tag(sb, "IBU", dbl(h.getIbuContribution()));
      sb.append("</Hop>");
    }
    sb.append("</Hops>");
    // Yeasts
    sb.append("<Yeasts>");
    for (var y : r.getYeasts()) {
      sb.append("<Yeast>");
      tag(sb, "Name", y.getName());
      tag(sb, "Laboratory", y.getLaboratory());
      tag(sb, "ProductId", y.getProductId());
      tag(sb, "Type", y.getType());
      tag(sb, "Form", y.getForm());
      tag(sb, "Attenuation", dbl(y.getAttenuation()));
      sb.append("</Yeast>");
    }
    sb.append("</Yeasts>");
    // Misc
    sb.append("<Miscs>");
    for (var m : r.getMiscs()) {
      sb.append("<Misc>");
      tag(sb, "Name", m.getName());
      tag(sb, "Type", m.getType());
      tag(sb, "Amount", dbl(m.getAmount()));
      tag(sb, "Use", m.getUseFor());
      sb.append("</Misc>");
    }
    sb.append("</Miscs>");
    // Mash Steps
    sb.append("<Mash><MashSteps>");
    for (var ms : r.getMashSteps()) {
      sb.append("<MashStep>");
      tag(sb, "Name", ms.getName());
      tag(sb, "Type", ms.getType());
      tag(sb, "StepTempC", dbl(ms.getStepTempC()));
      tag(sb, "StepTime", intv(ms.getStepTimeMinutes()));
      tag(sb, "InfuseAmount", dbl(ms.getInfuseAmountLiters()));
      sb.append("</MashStep>");
    }
    sb.append("</MashSteps></Mash>");
    sb.append("</Recipe></Recipes>");
    return sb.toString();
  }

  private static void tag(StringBuilder sb, String name, String val) {
    if (val == null) return;
    sb.append('<')
        .append(name)
        .append('>')
        .append(escape(val))
        .append("</")
        .append(name)
        .append('>');
  }

  private static void tag(StringBuilder sb, String name, Integer val) {
    if (val == null) return;
    tag(sb, name, String.valueOf(val));
  }

  private static void tag(StringBuilder sb, String name, Double val) {
    if (val == null) return;
    tag(sb, name, String.valueOf(val));
  }

  private static String dbl(Double d) {
    return d == null ? null : String.valueOf(d);
  }

  private static Integer intv(Integer i) {
    return i;
  }

  private static String escape(String s) {
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }
}
