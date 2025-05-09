package com.isst.ISST_Grupo25_Casas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.services.AccesoService;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private AccesoService accesoService;

    /**  
     * Añade al modelo 'unreadCount' con el número de accesos no-leídos 
     * para el gestor que esté en sesión (o 0 si no hay gestor). 
     */
    @ModelAttribute("unreadCount")
    public long addUnreadCount(HttpSession session) {
        Object u = session.getAttribute("usuario");
        if (u instanceof Gestor gestor) {
            return accesoService.contarNoLeidosPorGestor(gestor.getId());
        }
        return 0;
    }
}

