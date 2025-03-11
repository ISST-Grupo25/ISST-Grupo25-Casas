package com.isst.ISST_Grupo25_Casas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class SettingsController {

    @PostMapping("/settings/update-notifications")
    public String updateNotifications(@RequestParam(value = "emailNotifications", required = false) String emailNotifications,
                                      @RequestParam(value = "smsNotifications", required = false) String smsNotifications,
                                      HttpSession session) {
        session.setAttribute("emailNotifications", emailNotifications != null);
        session.setAttribute("smsNotifications", smsNotifications != null);
        return "redirect:/settings";
    }

    @PostMapping("/settings/update-preferences")
    public String updatePreferences(@RequestParam("theme") String theme,
                                    @RequestParam("language") String language,
                                    HttpSession session) {
        session.setAttribute("theme", theme);
        session.setAttribute("language", language);
        return "redirect:/settings";
    }
}
