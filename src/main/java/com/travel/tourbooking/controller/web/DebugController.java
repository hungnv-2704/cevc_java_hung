package com.travel.tourbooking.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/user-info")
    @ResponseBody
    public String getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return "Not authenticated";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Username: ").append(auth.getName()).append("<br>");
        sb.append("Principal: ").append(auth.getPrincipal().getClass().getName()).append("<br>");
        sb.append("Authorities: <br>");
        auth.getAuthorities().forEach(authority -> 
            sb.append("  - ").append(authority.getAuthority()).append("<br>")
        );
        
        return sb.toString();
    }
}
