package com.aluracursos.literAlura.repository;

import com.aluracursos.literAlura.model.libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<libro, Long > {
    List<libro> findByIdioma(String idioma);
}
