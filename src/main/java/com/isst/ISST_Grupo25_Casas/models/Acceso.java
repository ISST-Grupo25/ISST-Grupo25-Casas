

package com.isst.ISST_Grupo25_Casas.models;
import java.sql.Date;

import jakarta.persistence.*;


@Entity
@Table(name = "accesos")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date horario;

    @Column(nullable = false)
    private Boolean resultado;

    @ManyToOne
    @JoinColumn(name = "ID_huesped", foreignKey = @ForeignKey(name = "fk_acceso_huesped"))
    private Huesped huesped;

    @ManyToOne
    @JoinColumn(name = "ID_reserva", foreignKey = @ForeignKey(name = "fk_acceso_reserva"))
    private Reserva reserva;


    // 🔹 GETTERS Y SETTERS MANUALES
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public Boolean getResultado() {
        return resultado;
    }

    public void setResultado(Boolean resultado) {
        this.resultado = resultado;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public void setHuesped(Huesped huesped) {
        this.huesped = huesped;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }


}
