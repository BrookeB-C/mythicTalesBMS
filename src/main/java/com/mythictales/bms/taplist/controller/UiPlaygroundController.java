package com.mythictales.bms.taplist.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Profile("dev")
@Controller
@RequestMapping("/ui/playground")
public class UiPlaygroundController {

  private static final List<Story> STORIES =
      List.of(
          new Story(
              "foundations",
              "Foundations",
              "Color, typography, spacing tokens for the enterprise UI"),
          new Story("buttons", "Buttons", "Primary, secondary, and ghost button treatments"),
          new Story("cards", "Cards", "Metrics and content containers with headers and footers"),
          new Story("tables", "Tables", "Data grid with zebra striping, badges, and bulk actions"),
          new Story("forms", "Forms", "Input controls, validation states, and inline guidance"),
          new Story(
              "overlays",
              "Overlays & Dialogs",
              "Modal dialogs, slideovers, and toast notifications"),
          new Story(
              "visualizations",
              "Visualizations",
              "Spark lines, tank capacity, and progress indicators"));

  @GetMapping
  public String playground(
      @RequestParam(value = "story", required = false) String story, Model model) {
    Story selected = selectStory(story);
    model.addAttribute("stories", STORIES);
    model.addAttribute("selectedStory", selected.id());
    model.addAttribute("selectedStoryMeta", selected);
    return "ui-playground/index";
  }

  private Story selectStory(String story) {
    if (story == null || story.isBlank()) {
      return STORIES.get(0);
    }
    String normalized = story.toLowerCase(Locale.ROOT);
    return STORIES.stream()
        .filter(s -> s.id().equals(normalized))
        .findFirst()
        .orElse(STORIES.get(0));
  }

  public record Story(String id, String label, String description) {}
}
