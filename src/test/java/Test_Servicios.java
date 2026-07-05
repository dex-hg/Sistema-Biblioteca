import biblioteca.estructuras.ListaEnlazada;
import biblioteca.modelo.*;
import biblioteca.servicios.*;
import biblioteca.dao.impl.*;
import biblioteca.dao.interfaces.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Clase de prueba de integración para validar el funcionamiento de los servicios del sistema,
 * interactuando con la base de datos PostgreSQL mediante los DAOs y aplicando las reglas de negocio.
 */
public class Test_Servicios {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  INICIANDO PRUEBAS DE INTEGRACIÓN DE SERVICIOS    ");
        System.out.println("==================================================\n");

        AutenticacionService authService = new AutenticacionService();
        LibroService libroService = new LibroService();
        EstudianteService estudianteService = new EstudianteService();
        MultaService multaService = new MultaService();
        PrestamoService prestamoService = new PrestamoService();

        try {
            // 1. Probar Autenticación (Login)
            System.out.println("--- 1. PROBANDO SERVICIO DE AUTENTICACIÓN ---");
            Optional<Usuario> adminOpt = authService.login("admin", "root");
            if (adminOpt.isPresent()) {
                System.out.println("Login exitoso: " + adminOpt.get().getNombreCompleto() + 
                                   " | Rol ID: " + adminOpt.get().getRol().getId());
            } else {
                System.err.println("FALLO: No se pudo loguear al admin de prueba.");
            }

            Optional<Usuario> loginFallido = authService.login("admin", "wrong_pass");
            System.out.println("Login fallido con contraseña incorrecta retorna vacío: " + loginFallido.isEmpty());
            System.out.println();

            // 2. Probar LibroService (Validaciones, Ordenamiento y Búsqueda)
            System.out.println("--- 2. PROBANDO SERVICIO DE LIBROS ---");
            Libro libroNuevo = new Libro();
            libroNuevo.setTitulo("Estructuras de Datos y Algoritmos en Java");
            libroNuevo.setAutor("Robert Lafore");
            libroNuevo.setStock(4);

            boolean libroGuardado = libroService.registrarLibro(libroNuevo);
            System.out.println("¿Libro nuevo guardado?: " + libroGuardado);

            // Validar stock negativo (debe fallar)
            Libro libroErroneo = new Libro();
            libroErroneo.setTitulo("Título Inválido");
            libroErroneo.setAutor("Autor");
            libroErroneo.setStock(-5);
            boolean errorGuardado = libroService.registrarLibro(libroErroneo);
            System.out.println("Registro con stock negativo es rechazado (retorna false): " + (!errorGuardado));

            // Verificar catálogo ordenado usando QuickSort
            System.out.println("\nCatálogo ordenado por Título en memoria:");
            ListaEnlazada<Libro> catalogoOrdenado = libroService.obtenerCatalogoOrdenadoPorTitulo();
            for (int i = 0; i < catalogoOrdenado.size(); i++) {
                Libro l = catalogoOrdenado.obtener(i);
                System.out.println("  - " + l.getTitulo() + " (Stock: " + l.getStock() + ")");
            }

            // Buscar libro usando Búsqueda Binaria
            String tituloBuscado = "Introducción a los Algoritmos";
            Optional<Libro> libroBuscadoOpt = libroService.buscarLibroPorTituloBinario(tituloBuscado);
            if (libroBuscadoOpt.isPresent()) {
                System.out.println("\nBúsqueda Binaria exitosa para '" + tituloBuscado + "'. ID: " + libroBuscadoOpt.get().getId());
            } else {
                System.err.println("\nFALLO: No se encontró '" + tituloBuscado + "' mediante búsqueda binaria.");
            }
            System.out.println();

            // 3. Probar EstudianteService (Validación y Registro)
            System.out.println("--- 3. PROBANDO SERVICIO DE ESTUDIANTES ---");
            Estudiante estudiante = new Estudiante();
            String codigoUnico = "EST" + (System.currentTimeMillis() % 1000000);
            estudiante.setCodigo(codigoUnico);
            estudiante.setNombres("Ana");
            estudiante.setApellidos("Rojas Silva");
            estudiante.setCarrera("Ingeniería Civil");
            estudiante.setTelefono("955123456");
            estudiante.setCorreo("ana.rojas@universidad.edu");
            estudiante.setUsername("anarojas_" + (System.currentTimeMillis() % 1000));
            estudiante.setPassword("ana123");

            boolean estGuardado = estudianteService.registrarEstudiante(estudiante);
            System.out.println("¿Estudiante registrado correctamente?: " + estGuardado);

            // Validar correo sin '@' (debe fallar)
            Estudiante estErroneo = new Estudiante();
            estErroneo.setCodigo("ESTERR_" + System.currentTimeMillis());
            estErroneo.setNombres("Err");
            estErroneo.setApellidos("Err");
            estErroneo.setCorreo("correo_invalido.com");
            estErroneo.setUsername("err_" + System.currentTimeMillis());
            estErroneo.setPassword("pass");
            boolean estErrorGuardado = estudianteService.registrarEstudiante(estErroneo);
            System.out.println("Registro con correo inválido es rechazado (retorna false): " + (!estErrorGuardado));
            System.out.println();

            // 4. Probar PrestamoService (Préstamo, stocks y multa por retraso)
            System.out.println("--- 4. PROBANDO SERVICIO DE PRÉSTAMOS Y DEVOLUCIONES ---");
            
            // Recuperar el libro que creamos para ver su stock inicial
            Optional<Libro> libroParaPrestarOpt = libroService.buscarLibroPorTituloBinario("Estructuras de Datos y Algoritmos en Java");
            if (libroParaPrestarOpt.isEmpty()) {
                System.err.println("Fallo al recuperar el libro para el préstamo.");
                return;
            }
            Libro libroParaPrestar = libroParaPrestarOpt.get();
            int stockInicial = libroParaPrestar.getStock();
            System.out.println("Stock inicial del libro: " + stockInicial);

            // Crear cabecera de préstamo
            Prestamo prestamo = new Prestamo();
            prestamo.setEstudiante(estudiante);
            
            Bibliotecario bibliotecario = new Bibliotecario();
            bibliotecario.setId(adminOpt.get().getId()); // Usamos al administrador logueado
            prestamo.setBibliotecario(bibliotecario);

            // Crear detalle del préstamo
            ListaEnlazada<DetallePrestamo> detalles = new ListaEnlazada<>();
            DetallePrestamo detalle = new DetallePrestamo();
            detalle.setLibro(libroParaPrestar);
            detalle.setCantidad(1);
            detalles.agregar(detalle);

            // Registrar préstamo
            boolean prestamoRealizado = prestamoService.registrarPrestamo(prestamo, detalles);
            System.out.println("¿Préstamo registrado exitosamente?: " + prestamoRealizado);

            // Verificar disminución de stock en base de datos
            Optional<Libro> libroPrestadoOpt = libroService.buscarLibroPorId(libroParaPrestar.getId());
            int stockDespuesPrestamo = libroPrestadoOpt.get().getStock();
            System.out.println("Stock después del préstamo: " + stockDespuesPrestamo + " (Debe ser " + (stockInicial - 1) + ")");

            // SIMULACIÓN DE RETRASO PARA CÁLCULO DE MULTAS
            System.out.println("\nSimulando retraso en la devolución...");
            // Retroceder la fecha de préstamo en la base de datos a hace 10 días
            prestamo.setFechaPrestamo(LocalDate.now().minusDays(10));
            PrestamoDAO prestamoDAO = new PrestamoDAOImpl();
            prestamoDAO.actualizar(prestamo);

            // Registrar devolución
            boolean devolucionRealizada = prestamoService.registrarDevolucion(prestamo.getId());
            System.out.println("¿Devolución procesada?: " + devolucionRealizada);

            // Verificar si se generó la multa correspondiente
            ListaEnlazada<Multa> multasEstudiante = multaService.obtenerMultasPendientes(estudiante.getId());
            System.out.println("¿Se generaron multas para el estudiante?: " + (!multasEstudiante.isEmpty()));
            if (!multasEstudiante.isEmpty()) {
                Multa multaGenerada = multasEstudiante.obtener(0);
                System.out.println("  - ID Multa: " + multaGenerada.getId() + 
                                   " | Monto: S/. " + multaGenerada.getMonto() + 
                                   " | Motivo: " + multaGenerada.getMotivo() + 
                                   " | Estado: " + multaGenerada.getEstado());
                
                // Pagar la multa
                boolean pagada = multaService.pagarMulta(multaGenerada.getId());
                System.out.println("¿Multa pagada exitosamente?: " + pagada);
                System.out.println("¿Multas pendientes ahora?: " + multaService.obtenerMultasPendientes(estudiante.getId()).size());
            }

            // Verificar que el stock fue restaurado
            Optional<Libro> libroDevueltoOpt = libroService.buscarLibroPorId(libroParaPrestar.getId());
            int stockDespuesDevolucion = libroDevueltoOpt.get().getStock();
            System.out.println("Stock final después de devolución: " + stockDespuesDevolucion + " (Debe ser " + stockInicial + ")");
            System.out.println();

            System.out.println("==================================================");
            System.out.println("  TODAS LAS PRUEBAS DE SERVICIOS PASARON CON ÉXITO ");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("OCURRIÓ UN ERROR DURANTE LAS PRUEBAS:");
            e.printStackTrace();
        }
    }
}
