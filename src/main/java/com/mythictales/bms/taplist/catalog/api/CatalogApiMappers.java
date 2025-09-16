package com.mythictales.bms.taplist.catalog.api;

import java.util.List;

import com.mythictales.bms.taplist.catalog.api.dto.*;
import com.mythictales.bms.taplist.catalog.domain.*;

public final class CatalogApiMappers {
  private CatalogApiMappers() {}

  public static RecipeDto toDto(Recipe r) {
    if (r == null) return null;
    return new RecipeDto(
        r.getId(),
        r.getBrewery() != null ? r.getBrewery().getId() : null,
        r.getName(),
        r.getStyleName(),
        r.getType(),
        r.getBatchSizeLiters(),
        r.getBoilTimeMinutes(),
        r.getIbu(),
        r.getAbv(),
        r.getOg(),
        r.getFg(),
        r.getEfficiency(),
        r.getEquipment(),
        r.getSourceFormat() != null ? r.getSourceFormat().name() : null,
        r.getSourceHash(),
        r.getNotes(),
        r.getCreatedAt(),
        toDtoFermentables(r.getFermentables()),
        toDtoHops(r.getHops()),
        toDtoYeasts(r.getYeasts()),
        toDtoMiscs(r.getMiscs()),
        toDtoMashSteps(r.getMashSteps()));
  }

  public static List<RecipeFermentableDto> toDtoFermentables(List<RecipeFermentable> list) {
    return list == null
        ? java.util.List.of()
        : list.stream()
            .map(
                f ->
                    new RecipeFermentableDto(
                        f.getId(),
                        f.getName(),
                        f.getAmountKg(),
                        f.getYieldPercent(),
                        f.getColorLovibond(),
                        f.getLateAddition(),
                        f.getType()))
            .toList();
  }

  public static List<RecipeHopDto> toDtoHops(List<RecipeHop> list) {
    return list == null
        ? java.util.List.of()
        : list.stream()
            .map(
                h ->
                    new RecipeHopDto(
                        h.getId(),
                        h.getName(),
                        h.getAlphaAcid(),
                        h.getAmountGrams(),
                        h.getTimeMinutes(),
                        h.getUseFor(),
                        h.getForm(),
                        h.getIbuContribution()))
            .toList();
  }

  public static List<RecipeYeastDto> toDtoYeasts(List<RecipeYeast> list) {
    return list == null
        ? java.util.List.of()
        : list.stream()
            .map(
                y ->
                    new RecipeYeastDto(
                        y.getId(),
                        y.getName(),
                        y.getLaboratory(),
                        y.getProductId(),
                        y.getType(),
                        y.getForm(),
                        y.getAttenuation()))
            .toList();
  }

  public static List<RecipeMiscDto> toDtoMiscs(List<RecipeMisc> list) {
    return list == null
        ? java.util.List.of()
        : list.stream()
            .map(
                m ->
                    new RecipeMiscDto(
                        m.getId(),
                        m.getName(),
                        m.getType(),
                        m.getAmount(),
                        m.getAmountUnit(),
                        m.getUseFor()))
            .toList();
  }

  public static List<MashStepDto> toDtoMashSteps(List<MashStep> list) {
    return list == null
        ? java.util.List.of()
        : list.stream()
            .map(
                ms ->
                    new MashStepDto(
                        ms.getId(),
                        ms.getName(),
                        ms.getType(),
                        ms.getStepTempC(),
                        ms.getStepTimeMinutes(),
                        ms.getInfuseAmountLiters()))
            .toList();
  }
}
