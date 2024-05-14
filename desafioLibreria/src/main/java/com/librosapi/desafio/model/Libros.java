package com.librosapi.desafio.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libros {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Lenguaje lenguaje;
    private Integer numeroDeDescargas;
    @ManyToOne
    private Autor autor;

    public Libros(){}

    public Libros(DatosLibros datosLibros){
        this.id=datosLibros.id();
        this.titulo=datosLibros.titulo();
        this.lenguaje=Lenguaje.fromString(datosLibros.idiomas().stream()
                .limit(1).collect(Collectors.joining()));
        this.numeroDeDescargas=datosLibros.numeroDeDescargas();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Lenguaje getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(Lenguaje lenguaje) {
        this.lenguaje = lenguaje;
    }

    public Integer getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Integer numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "autor=" + autor +
                ", numeroDeDescargas=" + numeroDeDescargas +
                ", lenguaje=" + lenguaje +
                ", titulo='" + titulo + '\'' +
                ", id=" + id ;
    }
}
