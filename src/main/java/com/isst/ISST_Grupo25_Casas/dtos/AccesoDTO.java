package com.isst.ISST_Grupo25_Casas.dtos;

import java.sql.Date;

public class AccesoDTO {
    private Date horario;
    private Boolean resultado;
    private String nombreHuesped;

    public AccesoDTO(Date horario, Boolean resultado, String nombreHuesped) {
        this.horario = horario;
        this.resultado = resultado;
        this.nombreHuesped = nombreHuesped;
    }

    public Date getHorario() {
        return horario;
    }

    public Boolean getResultado() {
        return resultado;
    }

    public String getNombreHuesped() {
        return nombreHuesped;
    }
}
