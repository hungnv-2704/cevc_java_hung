package com.travel.tourbooking.controller.web;

import com.travel.tourbooking.dto.TourRequest;
import com.travel.tourbooking.dto.TourResponse;
import com.travel.tourbooking.service.TourService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tours")
public class TourWebController {

    private final TourService tourService;

    public TourWebController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public String listTours(@RequestParam(value = "q", required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("tours", tourService.search(keyword));
        } else {
            model.addAttribute("tours", tourService.findAllResponses());
        }
        model.addAttribute("keyword", keyword);
        return "tours/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("tourRequest", new TourRequest());
        return "tours/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute TourRequest tourRequest,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (result.hasErrors()) {
            return "tours/create";
        }

        try {
            tourService.createFromRequest(tourRequest);
            redirectAttributes.addFlashAttribute("message", "Tạo tour thành công!");
            return "redirect:/tours";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "tours/create";
        }
    }

    @GetMapping("/{id}")
    public String viewTour(@PathVariable Long id, Model model) {
        try {
            TourResponse tour = tourService.findByIdResponse(id);
            model.addAttribute("tour", tour);
            return "tours/detail";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/tours";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model, 
                          org.springframework.security.core.Authentication authentication) {
        System.out.println("=== EDIT FORM CALLED ===");
        System.out.println("User: " + (authentication != null ? authentication.getName() : "null"));
        System.out.println("Authorities: " + (authentication != null ? authentication.getAuthorities() : "null"));
        
        try {
            TourResponse tour = tourService.findByIdResponse(id);
            TourRequest tourRequest = TourRequest.builder()
                    .name(tour.getName())
                    .description(tour.getDescription())
                    .price(tour.getPrice())
                    .durationDays(tour.getDurationDays())
                    .startDate(tour.getStartDate())
                    .maxGroupSize(tour.getMaxGroupSize())
                    .status(tour.getStatus())
                    .build();
            model.addAttribute("tour", tour);
            model.addAttribute("tourRequest", tourRequest);
            return "tours/edit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/tours";
        }
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute TourRequest tourRequest,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (result.hasErrors()) {
            TourResponse tour = tourService.findByIdResponse(id);
            model.addAttribute("tour", tour);
            return "tours/edit";
        }

        try {
            tourService.updateFromRequest(id, tourRequest);
            redirectAttributes.addFlashAttribute("message", "Cập nhật tour thành công!");
            return "redirect:/tours";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            TourResponse tour = tourService.findByIdResponse(id);
            model.addAttribute("tour", tour);
            return "tours/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tourService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa tour thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa tour: " + e.getMessage());
        }
        return "redirect:/tours";
    }
}