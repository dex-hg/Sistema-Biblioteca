package biblioteca.automatizadas;

import biblioteca.dao.interfaces.EstudianteDAO;
import biblioteca.dao.interfaces.LibroDAO;
import biblioteca.estructuras.ListaEnlazada;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.Libro;
import biblioteca.servicios.EstudianteService;
import biblioteca.servicios.LibroService;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiciosValidacionAutomatizadaTest {

    @Test
    void libroServiceDebeRechazarDatosInvalidosAntesDeGuardar() {
        LibroDaoMemoria dao = new LibroDaoMemoria();
        LibroService service = new LibroService(dao);

        assertFalse(service.registrarLibro(null));
        assertFalse(service.registrarLibro(libro("", "Autor", 1)));
        assertFalse(service.registrarLibro(libro("Título", "", 1)));
        assertFalse(service.registrarLibro(libro("Título", "Autor", -1)));
        assertEquals(0, dao.guardados);
    }

    @Test
    void libroServiceDebeOrdenarCatalogoPorTitulo() {
        LibroDaoMemoria dao = new LibroDaoMemoria();
        dao.libros.agregar(libro("Introducción a los Algoritmos", "Cormen", 2));
        dao.libros.agregar(libro("Cálculo de una Variable", "Stewart", 3));
        dao.libros.agregar(libro("Algebra Lineal", "Lay", 4));
        LibroService service = new LibroService(dao);

        ListaEnlazada<Libro> ordenados = service.obtenerCatalogoOrdenadoPorTitulo();

        assertEquals("Algebra Lineal", ordenados.obtener(0).getTitulo());
        assertEquals("Cálculo de una Variable", ordenados.obtener(1).getTitulo());
        assertEquals("Introducción a los Algoritmos", ordenados.obtener(2).getTitulo());
    }

    @Test
    void libroServiceDebeDescontarUnaUnidadDeStock() {
        LibroDaoMemoria dao = new LibroDaoMemoria();
        Libro libro = libro("Estructuras de Datos", "Autor", 2);
        libro.setId(10);
        dao.libros.agregar(libro);
        LibroService service = new LibroService(dao);

        assertTrue(service.descontarUnidadStock(10));
        assertEquals(1, dao.buscarPorId(10).get().getStock());
        assertTrue(service.descontarUnidadStock(10));
        assertEquals(0, dao.buscarPorId(10).get().getStock());
        assertFalse(service.descontarUnidadStock(10));
    }

    @Test
    void estudianteServiceDebeRechazarCamposInvalidosAntesDeGuardar() {
        EstudianteDaoMemoria dao = new EstudianteDaoMemoria();
        EstudianteService service = new EstudianteService(dao);

        assertFalse(service.registrarEstudiante(null));
        assertFalse(service.registrarEstudiante(estudiante("", "Ana", "Rojas", "ana", "pass", "ana@uni.edu")));
        assertFalse(service.registrarEstudiante(estudiante("EST001", "", "Rojas", "ana", "pass", "ana@uni.edu")));
        assertFalse(service.registrarEstudiante(estudiante("EST001", "Ana", "Rojas", "", "pass", "ana@uni.edu")));
        assertFalse(service.registrarEstudiante(estudiante("EST001", "Ana", "Rojas", "ana", "", "ana@uni.edu")));
        assertFalse(service.registrarEstudiante(estudiante("EST001", "Ana", "Rojas", "ana", "pass", "correo_invalido.com")));
        assertEquals(0, dao.guardados);
    }

    @Test
    void estudianteServiceDebeDetectarCodigoDuplicado() {
        EstudianteDaoMemoria dao = new EstudianteDaoMemoria();
        Estudiante existente = estudiante("EST001", "Ana", "Rojas", "ana", "pass", "ana@uni.edu");
        dao.estudiantes.agregar(existente);
        EstudianteService service = new EstudianteService(dao);

        Estudiante duplicado = estudiante("EST001", "Luis", "Mendoza", "luis", "pass", "luis@uni.edu");

        assertFalse(service.registrarEstudiante(duplicado));
        assertEquals(0, dao.guardados);
    }

    private Libro libro(String titulo, String autor, int stock) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setStock(stock);
        return libro;
    }

    private Estudiante estudiante(String codigo, String nombres, String apellidos,
            String username, String password, String correo) {
        Estudiante estudiante = new Estudiante();
        estudiante.setCodigo(codigo);
        estudiante.setNombres(nombres);
        estudiante.setApellidos(apellidos);
        estudiante.setUsername(username);
        estudiante.setPassword(password);
        estudiante.setCorreo(correo);
        return estudiante;
    }

    private static class LibroDaoMemoria implements LibroDAO {

        private final ListaEnlazada<Libro> libros = new ListaEnlazada<>();
        private int guardados;

        @Override
        public boolean guardar(Libro libro) {
            libro.setId(libros.size() + 1);
            libros.agregar(libro);
            guardados++;
            return true;
        }

        @Override
        public boolean actualizar(Libro libro) {
            return true;
        }

        @Override
        public boolean eliminar(Integer id) {
            return true;
        }

        @Override
        public Optional<Libro> buscarPorId(Integer id) {
            for (int i = 0; i < libros.size(); i++) {
                Libro libro = libros.obtener(i);
                if (libro.getId() == id) {
                    return Optional.of(libro);
                }
            }
            return Optional.empty();
        }

        @Override
        public ListaEnlazada<Libro> listarTodos() {
            return libros;
        }

        @Override
        public ListaEnlazada<Libro> buscarPorTitulo(String titulo) {
            ListaEnlazada<Libro> encontrados = new ListaEnlazada<>();
            for (int i = 0; i < libros.size(); i++) {
                Libro libro = libros.obtener(i);
                if (libro.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                    encontrados.agregar(libro);
                }
            }
            return encontrados;
        }

        @Override
        public ListaEnlazada<Libro> buscarPorAutor(String autor) {
            return new ListaEnlazada<>();
        }

        @Override
        public boolean actualizarStock(int idLibro, int nuevoStock) {
            Optional<Libro> libroOpt = buscarPorId(idLibro);
            if (libroOpt.isEmpty()) {
                return false;
            }
            libroOpt.get().setStock(nuevoStock);
            return true;
        }
    }

    private static class EstudianteDaoMemoria implements EstudianteDAO {

        private final ListaEnlazada<Estudiante> estudiantes = new ListaEnlazada<>();
        private int guardados;

        @Override
        public Optional<Estudiante> buscarPorCodigo(String codigo) {
            for (int i = 0; i < estudiantes.size(); i++) {
                Estudiante estudiante = estudiantes.obtener(i);
                if (estudiante.getCodigo().equals(codigo)) {
                    return Optional.of(estudiante);
                }
            }
            return Optional.empty();
        }

        @Override
        public boolean guardar(Estudiante estudiante) {
            estudiantes.agregar(estudiante);
            guardados++;
            return true;
        }

        @Override
        public boolean actualizar(Estudiante estudiante) {
            return true;
        }

        @Override
        public boolean eliminar(Integer id) {
            return true;
        }

        @Override
        public Optional<Estudiante> buscarPorId(Integer id) {
            return Optional.empty();
        }

        @Override
        public ListaEnlazada<Estudiante> listarTodos() {
            return estudiantes;
        }
    }
}
