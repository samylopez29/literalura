package com.aluracursos.literAlura.principal;

import com.aluracursos.literAlura.model.Autor;
import com.aluracursos.literAlura.model.Datos;
import com.aluracursos.literAlura.model.datosLibro;
import com.aluracursos.literAlura.model.libro;
import com.aluracursos.literAlura.repository.AutorRepository;
import com.aluracursos.literAlura.repository.LibroRepository;
import com.aluracursos.literAlura.service.ConsumoApi;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL ="https://gutendex.com/";
    private ConsumoApi consumoApi = new ConsumoApi();
    private Conversor conversor = new Conversor();
    private Integer opcion =6;
    private Scanner scanner = new Scanner(System.in);
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }
    private datosLibro getLibro (String nombreLibro){
        String json = consumoApi.obtenerLibros(URL + "?search=" + nombreLibro.replace(" ", "+"));
        List<datosLibro>libros=conversor.obtenerDatos(json, Datos.class).resultados();
        Optional<datosLibro> libro = libros.stream()
                .filter(l -> l.titulo().toLowerCase().contains(nombreLibro.toLowerCase()))
                .findFirst();
        if (libro.isPresent()) {
            return libro.get();
        }
        System.out.println("El libro no ha sido encontrado");
        return null;
    }
    private void leerLibro(libro libro) {
        System.out.println("----- LIBRO -----");
        System.out.println("Titulo: " + libro.getTitulo());
        System.out.println("Autor: " + libro.getAutor().getNombre());
        System.out.println("Idioma: " + libro.getIdioma());
        System.out.println("Numero de descargas: " + libro.getNumeroDeDescargas());
        System.out.println("----------\n");
    }
    private void leerAutor(Autor autor) {
        System.out.println("Autor: " + autor.getNombre());
        System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
        System.out.println("Fecha de fallecimiento: " + autor.getFechaDeFallecimiento());
        List<String> libros = autor.getLibros().stream()
                .map(l -> l.getTitulo())
                .collect(Collectors.toList());
        System.out.println("Libros: " + libros + "\n");
    }

    public void mostrarElMenu() {
        while (opcion != 0) {
            System.out.println("""
                    Elija la opcion a traves de su numero:
                    1- Buscar libro por titulo
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado año
                    5- Listar libros por idioma
                    0- Salir
                    ******************************************""");
            opcion = scanner.nextInt();
            if (opcion == 1) {
                System.out.println("Ingrese el nombre del libro que desea buscar:");
                String nombreLibro = scanner.next();
                libro libro = new libro(getLibro(nombreLibro));
                leerLibro(libro);
                libroRepository.save(libro);
            } else if (opcion == 2) {
                List<libro> libros = libroRepository.findAll();
                libros.stream()
                        .forEach(this::leerLibro);
            } else if (opcion == 3) {
                List<Autor> autores = autorRepository.findAll();
                autores.stream()
                        .forEach(this::leerAutor);
            } else if (opcion == 4) {
                System.out.println("Ingresa el año vivo de autor(es) que desea buscar");
                Integer fechaDeFallecimiento = scanner.nextInt();
                List<Autor> autores = autorRepository.findByFechaDeFallecimientoGreaterThan(fechaDeFallecimiento);
                autores.stream()
                        .forEach(this::leerAutor);
            } else if (opcion == 5) {
                System.out.println("Ingrese el idioma para buscar los libros:");
                System.out.println("es - español");
                System.out.println("en - ingles");
                System.out.println("fr - frances");
                System.out.println("pt - portugues");
                String idioma = scanner.next();
                List<libro> libros = libroRepository.findByIdioma(idioma);
                libros.stream()
                        .forEach(this::leerLibro);
            }
        }
    }
}
