package com.travel.tourbooking.controller.web;

import com.travel.tourbooking.dto.UserRequest;
import com.travel.tourbooking.dto.UserResponse;
import com.travel.tourbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserWebController {

    private final UserService userService;

    public UserWebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserRequest userRequest,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            return "users/register";
        }

        try {
            userService.create(userRequest);
            redirectAttributes.addFlashAttribute("message", "Đăng ký người dùng thành công!");
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "users/register";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            UserResponse user = userService.findById(id);
            UserRequest userRequest = UserRequest.builder()
                    .username(user.getUsername())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .password("")
                    .build();
            model.addAttribute("user", user);
            model.addAttribute("userRequest", userRequest);
            return "users/edit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/users";
        }
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute UserRequest userRequest,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (result.hasErrors()) {
            UserResponse user = userService.findById(id);
            model.addAttribute("user", user);
            return "users/edit";
        }

        try {
            userService.update(id, userRequest);
            redirectAttributes.addFlashAttribute("message", "Cập nhật người dùng thành công!");
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            UserResponse user = userService.findById(id);
            model.addAttribute("user", user);
            return "users/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa người dùng: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
