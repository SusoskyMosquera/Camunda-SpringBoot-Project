package com.example.workflow;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Pqrds {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String numeroSolicitud;
    private String tipoSolicitud;
    private String modo;
    private String nombre;
    private String descripcion;
    private String medioRespuesta;
    private String email;
    private String telefono;
    private String fechaSolicitud;
    private Boolean adjuntaDocumento;
    private String nombreArchivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroSolicitud() {
        return numeroSolicitud;
    }

    public void setNumeroSolicitud(String numeroSolicitud) {
        this.numeroSolicitud = numeroSolicitud;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMedioRespuesta() {
        return medioRespuesta;
    }

    public void setMedioRespuesta(String medioRespuesta) {
        this.medioRespuesta = medioRespuesta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Boolean getAdjuntaDocumento() {
        return adjuntaDocumento;
    }

    public void setAdjuntaDocumento(Boolean adjuntaDocumento) {
        this.adjuntaDocumento = adjuntaDocumento;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
}
