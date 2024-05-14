package com.librosapi.desafio.principal;

import com.librosapi.desafio.model.*;
import com.librosapi.desafio.repository.Repositorio;
import com.librosapi.desafio.service.ConsumoApi;
import com.librosapi.desafio.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE ="https://gutendex.com/books/";
    private ConsumoApi consumoApi=new ConsumoApi();
    private ConvierteDatos convertor=new ConvierteDatos();
    private Scanner teclado=new Scanner(System.in);
    private Repositorio repositorio;

    public Principal(Repositorio repository) {
        this.repositorio=repository;
    }

    public void muestraElMenu(){
        var numero=-1;
        String menu= """
                1 - Buscar libro por titulo
                2 - listar libros registrados
                3 - listar autores registrados
                4 - listar autores vivos por año
                5 - listar libros por idioma
                0 - Salir
                """;
        while (numero!=0){
            System.out.println(menu);
            try {
                numero = teclado.nextInt();
                teclado.nextLine();
                switch (numero) {
                    case 1:
                        buscarLibrosAPI();
                    break;
                    case 2:
                        listarLibros();
                    break;
                    case 3:
                        listarAutores();
                    break;
                    case 4:
                        listarAutoresVivos();
                    break;
                    case 5:
                        listarLibrosPorIdioma();
                    break;
                    case 0:
                        System.out.println("Saliendo");
                    break;
                    default:
                        System.out.println("Opcion no valida");
                }
            }catch (NumberFormatException e){
                System.out.println("Opcion invalida!! "+e.getMessage());
            }
        }
    }

    public void buscarLibrosAPI(){
        System.out.println("Introduce el nombre del libro que deseas buscar:");
        var nombre = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombre.replace(" ","+"));
        var datos = convertor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datos.resultados().stream()
                .findFirst();
        if(libroBuscado.isPresent()){
            System.out.println(
                    "\n----- LIBRO -----" +
                            "\nTitulo: " + libroBuscado.get().titulo() +
                            "\nAutor: " + libroBuscado.get().autor().stream()
                            .map(a -> a.nombre()).limit(1).collect(Collectors.joining())+
                            "\nIdioma: " + libroBuscado.get().idiomas().stream().collect(Collectors.joining()) +
                            "\nNumero de descargas: " + libroBuscado.get().numeroDeDescargas() +
                            "\n-----------------\n"
            );

            try{
                List<Libros> libroEncontrado = libroBuscado.stream().map(a -> new Libros(a)).collect(Collectors.toList());
                Autor autorAPI = libroBuscado.stream().
                        flatMap(l -> l.autor().stream()
                                .map(a -> new Autor(a)))
                        .collect(Collectors.toList()).stream().findFirst().get();
                Optional<Autor> autorBD = repositorio.buscarAutorPorNombre(libroBuscado.get().autor().stream()
                        .map(a -> a.nombre())
                        .collect(Collectors.joining()));
                Optional<Libros> libroOptional = repositorio.buscarLibroPorNombre(nombre);
                if (libroOptional.isPresent()) {
                    System.out.println("El libro se guardo con exito");
                } else {
                    Autor autor;
                    if (autorBD.isPresent()) {
                        autor = autorBD.get();
                        System.out.println("EL autor ya esta guardado en la BD!");
                    } else {
                        autor = autorAPI;
                        repositorio.save(autor);
                    }
                    autor.setLibros(libroEncontrado);
                    repositorio.save(autor);
                }
            } catch(Exception e) {
                System.out.println("Advertencia! " + e.getMessage());
            }
        } else {
            System.out.println("Libro no encontrado!");
        }
    }

    public void listarLibros(){
        List<Libros> libros = repositorio.buscarLibros();
        libros.forEach(l -> System.out.println(
                "*** LIBROS ***" +
                        "\nTitulo: " + l.getTitulo() +
                        "\nAutor: " + l.getAutor().getNombre() +
                        "\nLenguaje: " + l.getLenguaje().getIdioma() +
                        "\nDescargas: " + l.getNumeroDeDescargas() +
                        "\n-----------------\n"
        ));
    }

    public void listarAutores(){
        List<Autor> autores = repositorio.findAll();
        System.out.println();
        autores.forEach(l-> System.out.println(
                "Autor: " + l.getNombre() +
                        "\nFecha de nacimiento: " + l.getFechaDeNacimiento() +
                        "\nFecha de fallecimiento: " + l.getFechaFallecimiento() +
                        "\nLibros: " + l.getLibros().stream()
                        .map(t -> t.getTitulo()).collect(Collectors.toList()) + "\n"
        ));
    }

    public void listarAutoresVivos(){
        System.out.println("Introduce el año vivo del autor(es) que deseas buscar:");
        try{
            var fecha = Integer.valueOf(teclado.nextLine());
            List<Autor> autores = repositorio.buscarAutoresVivos(fecha);
            if(!autores.isEmpty()){
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNombre() +
                                "\nFecha de nacimiento: " + a.getFechaDeNacimiento() +
                                "\nFecha de fallecimiento: " + a.getFechaFallecimiento() +
                                "\nLibros: " + a.getLibros().stream()
                                .map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            } else{
                System.out.println("No hay autores vivos en el año elegido");
            }
        } catch(NumberFormatException e){
            System.out.println("introduce un año valido " + e.getMessage());
        }
    }

    public void listarLibrosPorIdioma(){
        var menu = """
                Ingrese el idioma del libros:
                es - español
                en - inglés
                fr - francés
                pt - portugués
                """;
        System.out.println(menu);
        var idioma = teclado.nextLine();
        if(idioma.equalsIgnoreCase("es") || idioma.equalsIgnoreCase("en") ||
                idioma.equalsIgnoreCase("fr") || idioma.equalsIgnoreCase("pt")){
            Lenguaje lenguaje = Lenguaje.fromString(idioma);
            List<Libros> libros = repositorio.buscarLibrosPorIdioma(lenguaje);
            if(libros.isEmpty()){
                System.out.println("No hay libros registrados en ese idioma!");
            } else{
                System.out.println();
                libros.forEach(l -> System.out.println(
                        "*** LIBROS ***" +
                                "\nTitulo: " + l.getTitulo() +
                                "\nAutor: " + l.getAutor().getNombre() +
                                "\nIdioma: " + l.getLenguaje().getIdioma() +
                                "\nDescargas: " + l.getNumeroDeDescargas() +
                                "\n-----------------\n"
                ));
            }
        } else{
            System.out.println("Introduce un idioma valido");
        }
    }
}
