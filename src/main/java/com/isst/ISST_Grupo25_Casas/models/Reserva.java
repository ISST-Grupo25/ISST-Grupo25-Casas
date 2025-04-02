// Reserva.java -----------------------------------------------------------
package com.isst.ISST_Grupo25_Casas.models;
import java.sql.Date;
import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pin", nullable = false, unique = true)
    private String pin;


    @Column(nullable = false)
    private Date fechainicio;

    @Column(nullable = false)
    private Date fechafin;

    @ManyToOne
    @JoinColumn(name = "ID_gestor", foreignKey = @ForeignKey(name = "fk_reserva_gestor"))
    private Gestor gestor;

    @ManyToOne
    @JoinColumn(name = "ID_cerradura", foreignKey = @ForeignKey(name = "fk_reserva_cerradura"))
    private Cerradura cerradura;

    @ManyToMany(mappedBy = "reservas") // Relación bidireccional
    private List<Huesped> huespedes;

    // 🔹 GETTERS Y SETTERS MANUALES
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }


    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    public Gestor getGestor() {
        return gestor;
    }

    public void setGestor(Gestor gestor) {
        this.gestor = gestor;
    }

    public Cerradura getCerradura() {
        return cerradura;
    }

    public void setCerradura(Cerradura cerradura) {
        this.cerradura = cerradura;
    }

    public List<Huesped> getHuespedes() {
        return huespedes;
    }

    public void setHuespedes(List<Huesped> huespedes) {
        this.huespedes = huespedes;
    }
}
