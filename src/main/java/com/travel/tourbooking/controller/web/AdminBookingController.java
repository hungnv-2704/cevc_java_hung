package com.travel.tourbooking.controller.web;

import com.travel.tourbooking.dto.BookingTourResponse;
import com.travel.tourbooking.enums.BookingStatus;
import com.travel.tourbooking.model.BookingTour.PaymentStatus;
import com.travel.tourbooking.service.BookingTourService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final BookingTourService bookingTourService;

    @GetMapping
    public String listAllBookings(Model model) {
        List<BookingTourResponse> bookings = bookingTourService.findAll();
        model.addAttribute("bookings", bookings);
        return "admin/bookings/list";
    }

    @GetMapping("/{id}")
    public String bookingDetail(@PathVariable String id, Model model) {
        BookingTourResponse booking = bookingTourService.findById(Long.parseLong(id));
        model.addAttribute("booking", booking);
        return "admin/bookings/detail";
    }

    @PostMapping("/{id}/update-status")
    public String updateBookingStatus(
            @PathVariable String id,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        try {
            bookingTourService.updateBookingStatus(Long.parseLong(id), BookingStatus.valueOf(status));
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái booking thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            bookingTourService.cancelBooking(Long.parseLong(id));
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy booking thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi hủy booking: " + e.getMessage());
        }
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/{id}/update-payment-status")
    public String updatePaymentStatus(
            @PathVariable String id,
            @RequestParam("paymentStatus") String paymentStatus,
            RedirectAttributes redirectAttributes) {
        try {
            bookingTourService.updatePaymentStatus(Long.parseLong(id), PaymentStatus.valueOf(paymentStatus));
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái thanh toán thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật trạng thái thanh toán: " + e.getMessage());
        }
        return "redirect:/admin/bookings/" + id;
    }
}
