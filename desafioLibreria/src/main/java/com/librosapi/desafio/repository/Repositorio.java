package com.librosapi.desafio.repository;

import com.librosapi.desafio.model.Autor;
import com.librosapi.desafio.model.Lenguaje;
import com.librosapi.desafio.model.Libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface Repositorio extends JpaRepository<Autor,Long> {
    @Query("SELECT a FROM Libros l JOIN l.autor a WHERE a.nombre LIKE %:nombre%")
    Optional<Autor> buscarAutorPorNombre(String nombre);

    @Query("SELECT l FROM Libros l JOIN l.autor a WHERE l.titulo LIKE %:nombre%")
    Optional<Libros> buscarLibroPorNombre(String nombre);

    @Query("SELECT l FROM Autor a JOIN a.libros l")
    List<Libros> buscarLibros();

    @Query("SELECT a FROM Autor a WHERE a.fechaFallecimiento > :fecha")
    List<Autor> buscarAutoresVivos(Integer fecha);

    @Query("SELECT l FROM Autor a JOIN a.libros l WHERE l.lenguaje = :idioma ")
    List<Libros> buscarLibrosPorIdioma(Lenguaje idioma);

}
