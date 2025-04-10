

package com.isst.ISST_Grupo25_Casas.models;
import java.sql.Date;

import jakarta.persistence.*;


@Entity
@Table(name = "cerraduras")
public class Cerradura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private String token;

    @Column(nullable = true)
    private Integer bateria;

    @ManyToOne
    @JoinColumn(name = "ID_gestor", foreignKey = @ForeignKey(name = "fk_cerradura_gestor"))
    private Gestor gestor;

    // 🔹 GETTERS Y SETTERS MANUALES
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getBateria() {
        return bateria;
    }

    public void setBateria(Integer bateria) {
        this.bateria = bateria;
    }

    public Gestor getGestor() {
        return gestor;
    }
    public void setGestor(Gestor gestor) {
        this.gestor = gestor;
    }
    
}
