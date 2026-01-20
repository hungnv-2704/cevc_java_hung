package com.travel.tourbooking.controller.web;

import com.travel.tourbooking.dto.BookingTourRequest;
import com.travel.tourbooking.dto.BookingTourResponse;
import com.travel.tourbooking.model.User;
import com.travel.tourbooking.repository.UserRepository;
import com.travel.tourbooking.service.BookingTourService;
import com.travel.tourbooking.service.TourService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bookings")
public class BookingWebController {

    private final BookingTourService bookingTourService;
    private final TourService tourService;
    private final UserRepository userRepository;

    public BookingWebController(BookingTourService bookingTourService, TourService tourService, UserRepository userRepository) {
        this.bookingTourService = bookingTourService;
        this.tourService = tourService;
        this.userRepository = userRepository;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                String email = oauth2User.getAttribute("email");
                if (email != null) {
                    return userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("OAuth user not found: " + email));
                }
            }
            
            String username = authentication.getName();
            return userRepository.findByUsername(username)
                    .or(() -> userRepository.findByEmail(username))
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
        }
        return null;
    }

    @GetMapping
    public String listBookings(Model model) {
        User currentUser = getCurrentUser();
        List<BookingTourResponse> bookings;
        
        if (currentUser != null) {
            bookings = bookingTourService.findAll().stream()
                    .filter(booking -> booking.getUserId().equals(String.valueOf(currentUser.getId())))
                    .collect(Collectors.toList());
            model.addAttribute("currentUserName", currentUser.getName());
        } else {
            bookings = bookingTourService.findAll();
        }
        
        model.addAttribute("bookings", bookings);
        return "bookings/list";
    }

    @GetMapping("/{id}")
    public String bookingDetail(@PathVariable String id, Model model) {
        BookingTourResponse booking = bookingTourService.findById(Long.parseLong(id));
        model.addAttribute("booking", booking);
        return "bookings/detail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        BookingTourRequest bookingRequest = BookingTourRequest.builder()
                .adults(1)
                .children(0)
                .build();
        model.addAttribute("bookingRequest", bookingRequest);
        model.addAttribute("tours", tourService.findAllResponses());
        return "bookings/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute BookingTourRequest bookingRequest,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tours", tourService.findAllResponses());
            return "bookings/create";
        }

        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "Bạn phải đăng nhập để đặt tour!");
                return "redirect:/login";
            }
            
            bookingTourService.create(bookingRequest, currentUser.getId());
            redirectAttributes.addFlashAttribute("message", "Đặt tour thành công!");
            return "redirect:/bookings";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tours", tourService.findAllResponses());
            return "bookings/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            BookingTourResponse booking = bookingTourService.findById(id);
            BookingTourRequest bookingRequest = BookingTourRequest.builder()
                    .tourId(Long.parseLong(booking.getTourId()))
                    .startDate(booking.getStartDate())
                    .adults(booking.getNumberOfGuests())
                    .children(0)
                    .contactFullName("")
                    .contactEmail("")
                    .contactPhone("")
                    .build();
            
            model.addAttribute("booking", booking);
            model.addAttribute("bookingRequest", bookingRequest);
            model.addAttribute("tours", tourService.findAllResponses());
            return "bookings/edit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/bookings";
        }
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute BookingTourRequest bookingRequest,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (result.hasErrors()) {
            BookingTourResponse booking = bookingTourService.findById(id);
            model.addAttribute("booking", booking);
            model.addAttribute("tours", tourService.findAllResponses());
            return "bookings/edit";
        }

        try {
            bookingTourService.update(id, bookingRequest);
            redirectAttributes.addFlashAttribute("message", "Cập nhật booking thành công!");
            return "redirect:/bookings";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            BookingTourResponse booking = bookingTourService.findById(id);
            model.addAttribute("booking", booking);
            model.addAttribute("tours", tourService.findAllResponses());
            return "bookings/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingTourService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Xóa booking thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa booking: " + e.getMessage());
        }
        return "redirect:/bookings";
    }
}
