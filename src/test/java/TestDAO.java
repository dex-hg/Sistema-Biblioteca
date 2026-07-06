import biblioteca.modelo.Bibliotecario;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Usuario;
import biblioteca.dao.interfaces.EstudianteDAO;
import biblioteca.dao.impl.LibroDAOImpl;
import biblioteca.dao.interfaces.UsuarioDAO;
import biblioteca.dao.impl.PrestamoDAOImpl;
import biblioteca.dao.interfaces.DetallePrestamoDAO;
import biblioteca.modelo.Multa;
import biblioteca.dao.impl.EstudianteDAOImpl;
import biblioteca.modelo.Estudiante;
import biblioteca.dao.interfaces.LibroDAO;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Rol;
import biblioteca.dao.interfaces.PrestamoDAO;
import biblioteca.dao.impl.DetallePrestamoDAOImpl;
import biblioteca.dao.impl.MultaDAOImpl;
import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.EstadoPrestamo;
import biblioteca.dao.interfaces.MultaDAO;
import biblioteca.dao.impl.UsuarioDAOImpl;
import biblioteca.estructuras.ListaEnlazada;

import java.time.LocalDate;
import java.util.Optional;

public class TestDAO {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE DAO Y PERSISTENCIA ===");

        LibroDAO libroDAO = new LibroDAOImpl();
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
        EstudianteDAO estudianteDAO = new EstudianteDAOImpl();
        PrestamoDAO prestamoDAO = new PrestamoDAOImpl();
        DetallePrestamoDAO detalleDAO = new DetallePrestamoDAOImpl();
        MultaDAO multaDAO = new MultaDAOImpl();

        try {
            // 1. Probar inserción y búsqueda de Libro
            System.out.println("\n1. Probando LibroDAO...");
            Libro libro = new Libro();
            libro.setTitulo("Cálculo de una Variable");
            libro.setAutor("James Stewart");
            libro.setStock(5);
            
            boolean libroGuardado = libroDAO.guardar(libro);
            System.out.println("¿Libro guardado?: " + libroGuardado);

            // Buscar libro recién guardado para obtener su ID
            ListaEnlazada<Libro> libros = libroDAO.buscarPorTitulo("Cálculo de una Variable");
            if (libros.isEmpty()) {
                System.out.println("Error: No se pudo recuperar el libro guardado.");
                return;
            }
            Libro libroRecuperado = libros.obtener(0);
            System.out.println("Libro recuperado con ID: " + libroRecuperado.getId() + " - " + libroRecuperado.getTitulo());

            // 2. Probar inserción y búsqueda de Usuario (Administrador / Bibliotecario)
            System.out.println("\n2. Probando UsuarioDAO...");
            Usuario usuario = new Usuario();
            Rol rolAdmin = new Rol();
            rolAdmin.setId(1); // 1 = ADMINISTRADOR
            usuario.setUsername("admin_prueba_" + System.currentTimeMillis());
            usuario.setPassword("pass123");
            usuario.setNombreCompleto("Administrador de Prueba");
            usuario.setRol(rolAdmin);
            
            boolean usuarioGuardado = usuarioDAO.guardar(usuario);
            System.out.println("¿Usuario guardado?: " + usuarioGuardado);

            Optional<Usuario> usuarioRecuperadoOpt = usuarioDAO.buscarPorUsername(usuario.getUsername());
            if (usuarioRecuperadoOpt.isEmpty()) {
                System.out.println("Error: No se pudo recuperar el usuario guardado.");
                return;
            }
            Usuario usuarioRecuperado = usuarioRecuperadoOpt.get();
            System.out.println("Usuario recuperado con ID: " + usuarioRecuperado.getId() + " - " + usuarioRecuperado.getUsername());

            // 3. Probar inserción transaccional de Estudiante (con credenciales automáticas)
            System.out.println("\n3. Probando EstudianteDAO (Con herencia 1:1 de Usuario)...");
            Estudiante estudiante = new Estudiante();
            
            // Campos heredados de Usuario (para permitir login del estudiante!)
            estudiante.setUsername("estudiante_login_" + (System.currentTimeMillis() % 1000));
            estudiante.setPassword("estudiante123");
            
            // Campos específicos de Estudiante
            estudiante.setCodigo("EST" + (System.currentTimeMillis() % 100000));
            estudiante.setNombres("Juan");
            estudiante.setApellidos("Pérez");
            estudiante.setCarrera("Ingeniería de Sistemas");
            estudiante.setTelefono("987654321");
            estudiante.setCorreo("juan.perez@universidad.edu");

            boolean estudianteGuardado = estudianteDAO.guardar(estudiante);
            System.out.println("¿Estudiante guardado (tabla estudiantes + usuarios)?: " + estudianteGuardado);
            System.out.println("Estudiante guardado con ID heredado de Usuario: " + estudiante.getId());

            // Buscar estudiante por código para comprobar los JOINs automáticos
            Optional<Estudiante> estudianteRecuperadoOpt = estudianteDAO.buscarPorCodigo(estudiante.getCodigo());
            if (estudianteRecuperadoOpt.isEmpty()) {
                System.out.println("Error: No se pudo recuperar el estudiante por su código.");
                return;
            }
            Estudiante estudianteRecuperado = estudianteRecuperadoOpt.get();
            System.out.println("Estudiante recuperado por código: " + estudianteRecuperado.getCodigo() + 
                               " | Nombre completo de Usuario: " + estudianteRecuperado.getNombreCompleto() + 
                               " | Username de login: " + estudianteRecuperado.getUsername());

            // 4. Probar inserción y búsqueda de Préstamo
            System.out.println("\n4. Probando PrestamoDAO...");
            Prestamo prestamo = new Prestamo();
            prestamo.setFechaPrestamo(LocalDate.now());
            prestamo.setEstudiante(estudianteRecuperado);
            
            // Asociamos el usuario bibliotecario que registra el préstamo
            Bibliotecario bibliotecario = new Bibliotecario();
            bibliotecario.setId(usuarioRecuperado.getId());
            prestamo.setBibliotecario(bibliotecario);
            prestamo.setEstado(EstadoPrestamo.ACTIVO);
            
            boolean prestamoGuardado = prestamoDAO.guardar(prestamo);
            System.out.println("¿Préstamo guardado?: " + prestamoGuardado);

            ListaEnlazada<Prestamo> prestamosActivos = prestamoDAO.listarPrestamosActivos();
            if (prestamosActivos.isEmpty()) {
                System.out.println("Error: No hay préstamos activos registrados.");
                return;
            }
            
            // Buscar el préstamo correspondiente al estudiante creado
            Prestamo prestamoRecuperado = null;
            for (int i = 0; i < prestamosActivos.size(); i++) {
                Prestamo p = prestamosActivos.obtener(i);
                if (p.getEstudiante() != null && p.getEstudiante().getId() == estudianteRecuperado.getId()) {
                    prestamoRecuperado = p;
                    break;
                }
            }
            
            if (prestamoRecuperado == null) {
                System.out.println("Error: No se pudo encontrar el préstamo del estudiante.");
                return;
            }
            System.out.println("Préstamo recuperado con ID: " + prestamoRecuperado.getId() + 
                               " | Estudiante ID: " + prestamoRecuperado.getEstudiante().getId() +
                               " | Registrado por Usuario ID: " + prestamoRecuperado.getBibliotecario().getId() +
                               " | Estado: " + prestamoRecuperado.getEstado());

            // 5. Probar inserción y búsqueda de DetallePrestamo
            System.out.println("\n5. Probando DetallePrestamoDAO...");
            DetallePrestamo detalle = new DetallePrestamo();
            detalle.setPrestamo(prestamoRecuperado);
            detalle.setLibro(libroRecuperado);
            detalle.setCantidad(2); // Cantidad de libros de este tipo

            boolean detalleGuardado = detalleDAO.guardar(detalle);
            System.out.println("¿Detalle de Préstamo guardado?: " + detalleGuardado);

            // Listar todos los detalles para verificar la recuperación completa
            ListaEnlazada<DetallePrestamo> todosDetalles = detalleDAO.listarTodos();
            System.out.println("\n=== LISTA DE DETALLES DE PRÉSTAMOS ===");
            for (int i = 0; i < todosDetalles.size(); i++) {
                DetallePrestamo d = todosDetalles.obtener(i);
                if (d.getPrestamo().getId() == prestamoRecuperado.getId()) {
                    System.out.println("ID Detalle: " + d.getId() + 
                                       " | ID Préstamo: " + d.getPrestamo().getId() + 
                                       " | ID Libro: " + d.getLibro().getId() + 
                                       " | Cantidad: " + d.getCantidad());
                }
            }

            // 6. Probar inserción y búsqueda de Multa
            System.out.println("\n6. Probando MultaDAO...");
            Multa multa = new Multa();
            multa.setPrestamo(prestamoRecuperado);
            multa.setMonto(15.50);
            multa.setMotivo("Entrega tardía");
            multa.setEstado("PENDIENTE");

            boolean multaGuardada = multaDAO.guardar(multa);
            System.out.println("¿Multa guardada?: " + multaGuardada);

            // Listar todos los detalles de multas
            ListaEnlazada<Multa> todasMultas = multaDAO.listarTodos();
            System.out.println("\n=== LISTA DE MULTAS ===");
            for (int i = 0; i < todasMultas.size(); i++) {
                Multa m = todasMultas.obtener(i);
                if (m.getPrestamo().getId() == prestamoRecuperado.getId()) {
                    System.out.println("ID Multa: " + m.getId() + 
                                       " | ID Préstamo: " + m.getPrestamo().getId() + 
                                       " | Monto: " + m.getMonto() + 
                                       " | Motivo: " + m.getMotivo() + 
                                       " | Estado: " + m.getEstado() + 
                                       " | ¿Pagada?: " + m.isPagada());
                }
            }

            System.out.println("\n=== PRUEBAS COMPLETADAS CON ÉXITO ===");

        } catch (Exception e) {
            System.err.println("\nOCURRIÓ UN ERROR DURANTE LAS PRUEBAS:");
            e.printStackTrace();
        }
    }
}
