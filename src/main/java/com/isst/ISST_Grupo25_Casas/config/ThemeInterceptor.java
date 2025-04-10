package com.isst.ISST_Grupo25_Casas.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ThemeInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        HttpSession session = request.getSession(false);
        if (modelAndView != null && session != null) {
            String theme = (String) session.getAttribute("theme");
            if (theme == null) {
                theme = "light";
                session.setAttribute("theme", theme);
            }
            modelAndView.addObject("theme", theme);
        }
    }
}
