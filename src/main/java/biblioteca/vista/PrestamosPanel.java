package biblioteca.vista;

import biblioteca.controlador.PrestamoController;
import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import biblioteca.estructuras.ListaEnlazada;

public class PrestamosPanel extends JPanel {

    private final Usuario usuarioLogueado;
    private final PrestamoController prestamoController;

    // Datos temporales para la transacción de préstamo
    private Estudiante estudianteSeleccionado;
    private Libro libroSeleccionado;
    private final ListaEnlazada<DetallePrestamo> carritoLibros = new ListaEnlazada<>();

    // Componentes del Panel Registrar Préstamo
    private JTextField txtCodigoEstudiante;
    private JButton btnBuscarEstudiante;
    private JLabel lblNombreEstudianteVal;
    private JLabel lblEstadoEstudianteVal;

    private JTextField txtIdLibro;
    private JButton btnBuscarLibro;
    private JLabel lblTituloLibroVal;
    private JLabel lblStockLibroVal;
    private JButton btnAgregarCarrito;

    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JButton btnConfirmarPrestamo;
    private JButton btnLimpiarCarrito;

    // Componentes del Panel Devoluciones
    private JTextField txtIdPrestamoDev;
    private JButton btnBuscarPrestamo;
    private JLabel lblDetallesPrestamoVal;
    private JButton btnConfirmarDevolucion;

    // Tabla general de préstamos en el sistema
    private JTable tablaPrestamos;
    private DefaultTableModel modeloPrestamos;

    public PrestamosPanel(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.prestamoController = new PrestamoController();
        inicializarUI();
        actualizarTablaPrestamos();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);

        // Título Principal
        JLabel lblTitulo = new JLabel("Operaciones de Préstamos y Devoluciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(33, 37, 41));
        add(lblTitulo, BorderLayout.NORTH);

        // Crear Pestañas (TabbedPane)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel panelRegistro = crearPanelRegistroPrestamo();
        JPanel panelDevolucion = crearPanelDevolucion();

        tabbedPane.addTab("📖 Registrar Préstamo", panelRegistro);
        tabbedPane.addTab("🔄 Registrar Devolución", panelDevolucion);

        // SplitPane para colocar las pestañas arriba/izquierda y la tabla general de préstamos al lado/abajo
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setTopComponent(tabbedPane);
        splitPane.setBottomComponent(crearPanelTablaGeneralPrestamos());
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelRegistroPrestamo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;

        // --- 1. BUSCADOR DE ESTUDIANTE ---
        JPanel pEstudiante = new JPanel(new GridBagLayout());
        pEstudiante.setBorder(BorderFactory.createTitledBorder("1. Buscar Estudiante"));
        pEstudiante.setBackground(Color.WHITE);
        GridBagConstraints gbcEst = new GridBagConstraints();
        gbcEst.fill = GridBagConstraints.HORIZONTAL;
        gbcEst.insets = new Insets(4, 5, 4, 5);

        gbcEst.gridx = 0; gbcEst.gridy = 0;
        pEstudiante.add(new JLabel("Código:"), gbcEst);

        gbcEst.gridx = 1; gbcEst.weightx = 1.0;
        txtCodigoEstudiante = new JTextField();
        txtCodigoEstudiante.putClientProperty("JTextField.placeholderText", "Ej. EST2026101");
        pEstudiante.add(txtCodigoEstudiante, gbcEst);

        gbcEst.gridx = 2; gbcEst.weightx = 0.0;
        btnBuscarEstudiante = new JButton("Buscar");
        btnBuscarEstudiante.setBackground(new Color(33, 37, 41));
        btnBuscarEstudiante.setForeground(Color.WHITE);
        btnBuscarEstudiante.putClientProperty("JButton.buttonType", "roundRect");
        pEstudiante.add(btnBuscarEstudiante, gbcEst);

        gbcEst.gridx = 0; gbcEst.gridy = 1; gbcEst.gridwidth = 3;
        lblNombreEstudianteVal = new JLabel("Estudiante: (No seleccionado)");
        lblNombreEstudianteVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pEstudiante.add(lblNombreEstudianteVal, gbcEst);

        gbcEst.gridy = 2;
        lblEstadoEstudianteVal = new JLabel("Estado: -");
        pEstudiante.add(lblEstadoEstudianteVal, gbcEst);

        gbc.gridx = 0; gbc.weightx = 0.4; gbc.gridheight = 1;
        panel.add(pEstudiante, gbc);

        // --- 2. BUSCADOR DE LIBRO ---
        JPanel pLibro = new JPanel(new GridBagLayout());
        pLibro.setBorder(BorderFactory.createTitledBorder("2. Añadir Libro"));
        pLibro.setBackground(Color.WHITE);
        GridBagConstraints gbcLib = new GridBagConstraints();
        gbcLib.fill = GridBagConstraints.HORIZONTAL;
        gbcLib.insets = new Insets(4, 5, 4, 5);

        gbcLib.gridx = 0; gbcLib.gridy = 0;
        pLibro.add(new JLabel("ID Libro:"), gbcLib);

        gbcLib.gridx = 1; gbcLib.weightx = 1.0;
        txtIdLibro = new JTextField();
        pLibro.add(txtIdLibro, gbcLib);

        gbcLib.gridx = 2; gbcLib.weightx = 0.0;
        btnBuscarLibro = new JButton("Buscar");
        btnBuscarLibro.setBackground(new Color(33, 37, 41));
        btnBuscarLibro.setForeground(Color.WHITE);
        btnBuscarLibro.putClientProperty("JButton.buttonType", "roundRect");
        pLibro.add(btnBuscarLibro, gbcLib);

        gbcLib.gridx = 0; gbcLib.gridy = 1; gbcLib.gridwidth = 3;
        lblTituloLibroVal = new JLabel("Título: (Ninguno)");
        lblTituloLibroVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pLibro.add(lblTituloLibroVal, gbcLib);

        gbcLib.gridy = 2;
        lblStockLibroVal = new JLabel("Stock actual: -");
        pLibro.add(lblStockLibroVal, gbcLib);

        gbcLib.gridy = 3;
        btnAgregarCarrito = new JButton("Añadir a la lista");
        btnAgregarCarrito.setEnabled(false);
        btnAgregarCarrito.setBackground(new Color(25, 135, 84));
        btnAgregarCarrito.setForeground(Color.WHITE);
        btnAgregarCarrito.putClientProperty("JButton.buttonType", "roundRect");
        pLibro.add(btnAgregarCarrito, gbcLib);

        gbc.gridy = 1;
        panel.add(pLibro, gbc);

        // --- 3. TABLA DEL CARRITO ---
        JPanel pCarrito = new JPanel(new BorderLayout(5, 5));
        pCarrito.setBorder(BorderFactory.createTitledBorder("3. Libros seleccionados para préstamo"));
        pCarrito.setBackground(Color.WHITE);

        modeloCarrito = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Libro", "Título", "Cantidad"}
        );
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setRowHeight(22);
        JScrollPane scrollCart = new JScrollPane(tablaCarrito);
        pCarrito.add(scrollCart, BorderLayout.CENTER);

        JPanel pAccionesCart = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pAccionesCart.setOpaque(false);
        
        btnLimpiarCarrito = new JButton("Limpiar lista");
        btnLimpiarCarrito.putClientProperty("JButton.buttonType", "roundRect");
        pAccionesCart.add(btnLimpiarCarrito);

        btnConfirmarPrestamo = new JButton("Confirmar Préstamo");
        btnConfirmarPrestamo.setBackground(new Color(13, 110, 253));
        btnConfirmarPrestamo.setForeground(Color.WHITE);
        btnConfirmarPrestamo.putClientProperty("JButton.buttonType", "roundRect");
        pAccionesCart.add(btnConfirmarPrestamo);

        pCarrito.add(pAccionesCart, BorderLayout.SOUTH);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.6; gbc.gridheight = 2;
        panel.add(pCarrito, gbc);

        // Eventos
        btnBuscarEstudiante.addActionListener(e -> buscarEstudiante());
        txtCodigoEstudiante.addActionListener(e -> buscarEstudiante());
        btnBuscarLibro.addActionListener(e -> buscarLibro());
        txtIdLibro.addActionListener(e -> buscarLibro());
        btnAgregarCarrito.addActionListener(e -> agregarAlCarrito());
        btnLimpiarCarrito.addActionListener(e -> limpiarCarrito());
        btnConfirmarPrestamo.addActionListener(e -> confirmarPrestamo());

        return panel;
    }

    private JPanel crearPanelDevolucion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        JLabel lblDevDesc = new JLabel("Ingrese el ID del Préstamo para procesar el retorno del libro:");
        lblDevDesc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = row++;
        panel.add(lblDevDesc, gbc);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pSearch.setOpaque(false);
        pSearch.add(new JLabel("ID Préstamo:"));
        txtIdPrestamoDev = new JTextField(15);
        pSearch.add(txtIdPrestamoDev);
        btnBuscarPrestamo = new JButton("Buscar Préstamo");
        btnBuscarPrestamo.setBackground(new Color(33, 37, 41));
        btnBuscarPrestamo.setForeground(Color.WHITE);
        btnBuscarPrestamo.putClientProperty("JButton.buttonType", "roundRect");
        pSearch.add(btnBuscarPrestamo);
        
        gbc.gridy = row++;
        panel.add(pSearch, gbc);

        lblDetallesPrestamoVal = new JLabel("<html>Detalles del préstamo:<br>- Estudiante:<br>- Libro:<br>- Fecha Préstamo:<br>- Estado:</html>");
        lblDetallesPrestamoVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetallesPrestamoVal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        gbc.gridy = row++;
        panel.add(lblDetallesPrestamoVal, gbc);

        btnConfirmarDevolucion = new JButton("Procesar Devolución y Retorno");
        btnConfirmarDevolucion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmarDevolucion.setBackground(new Color(13, 110, 253));
        btnConfirmarDevolucion.setForeground(Color.WHITE);
        btnConfirmarDevolucion.setEnabled(false);
        btnConfirmarDevolucion.putClientProperty("JButton.buttonType", "roundRect");
        btnConfirmarDevolucion.setPreferredSize(new Dimension(0, 40));
        
        gbc.gridy = row++;
        panel.add(btnConfirmarDevolucion, gbc);

        // Relleno inferior
        gbc.weighty = 1.0;
        gbc.gridy = row++;
        panel.add(new JLabel(), gbc);

        // Eventos
        btnBuscarPrestamo.addActionListener(e -> buscarPrestamoParaDevolver());
        txtIdPrestamoDev.addActionListener(e -> buscarPrestamoParaDevolver());
        btnConfirmarDevolucion.addActionListener(e -> confirmarDevolucion());

        return panel;
    }

    private JPanel crearPanelTablaGeneralPrestamos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                "Historial de Préstamos Registrados (Filtro por todos)",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(73, 80, 87)
        ));

        modeloPrestamos = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Préstamo", "Estudiante", "Bibliotecario", "Fecha Préstamo", "Fecha Devolución", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaPrestamos = new JTable(modeloPrestamos);
        tablaPrestamos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaPrestamos.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(tablaPrestamos);
        panel.add(scroll, BorderLayout.CENTER);

        // Al hacer doble clic en un préstamo, cargar su ID en el panel de devolución
        tablaPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPrestamos.getSelectedRow() != -1) {
                String id = tablaPrestamos.getValueAt(tablaPrestamos.getSelectedRow(), 0).toString();
                txtIdPrestamoDev.setText(id);
            }
        });

        return panel;
    }

    public void actualizarTablaPrestamos() {
        ListaEnlazada<Prestamo> prestamos = prestamoController.obtenerTodos();
        modeloPrestamos.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo p = prestamos.obtener(i);
            String fDev = (p.getFechaDevolucion() != null) ? p.getFechaDevolucion().format(formatter) : "-";
            modeloPrestamos.addRow(new Object[]{
                p.getId(),
                p.getEstudiante() != null ? p.getEstudiante().getNombreCompleto() : "Desconocido",
                p.getBibliotecario() != null ? p.getBibliotecario().getNombreCompleto() : "Desconocido",
                p.getFechaPrestamo() != null ? p.getFechaPrestamo().format(formatter) : "",
                fDev,
                p.getEstado() != null ? p.getEstado().name() : ""
            });
        }
    }

    private void buscarEstudiante() {
        String codigo = txtCodigoEstudiante.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un código de estudiante.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Estudiante> estOpt = prestamoController.buscarEstudiantePorCodigo(codigo);
        if (estOpt.isPresent()) {
            estudianteSeleccionado = estOpt.get();
            lblNombreEstudianteVal.setText("Estudiante: " + estudianteSeleccionado.getNombreCompleto());
            lblNombreEstudianteVal.setForeground(new Color(25, 135, 84));

            // Verificar si tiene multas pendientes
            var multas = new biblioteca.servicios.MultaService().obtenerMultasPendientes(estudianteSeleccionado.getId());
            if (!multas.isEmpty()) {
                lblEstadoEstudianteVal.setText("Estado: BLOQUEADO (Tiene multas pendientes)");
                lblEstadoEstudianteVal.setForeground(new Color(220, 53, 69));
                JOptionPane.showMessageDialog(this, 
                        "El estudiante seleccionado tiene multas pendientes. No es apto para nuevos préstamos.", 
                        "Estudiante Bloqueado", 
                        JOptionPane.ERROR_MESSAGE);
            } else {
                lblEstadoEstudianteVal.setText("Estado: APTO PARA PRÉSTAMO");
                lblEstadoEstudianteVal.setForeground(new Color(25, 135, 84));
            }
        } else {
            estudianteSeleccionado = null;
            lblNombreEstudianteVal.setText("Estudiante: (No encontrado)");
            lblNombreEstudianteVal.setForeground(new Color(220, 53, 69));
            lblEstadoEstudianteVal.setText("Estado: -");
            lblEstadoEstudianteVal.setForeground(Color.BLACK);
        }
    }

    private void buscarLibro() {
        String idStr = txtIdLibro.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID de un libro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Optional<Libro> libOpt = prestamoController.buscarLibroPorId(id);

            if (libOpt.isPresent()) {
                libroSeleccionado = libOpt.get();
                lblTituloLibroVal.setText("Título: " + libroSeleccionado.getTitulo());
                lblTituloLibroVal.setForeground(new Color(25, 135, 84));
                lblStockLibroVal.setText("Stock actual: " + libroSeleccionado.getStock() + " unidades");

                if (libroSeleccionado.getStock() <= 0) {
                    lblStockLibroVal.setForeground(new Color(220, 53, 69));
                    btnAgregarCarrito.setEnabled(false);
                    JOptionPane.showMessageDialog(this, 
                            "El libro seleccionado no cuenta con stock disponible en este momento.", 
                            "Sin Stock", 
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    lblStockLibroVal.setForeground(new Color(25, 135, 84));
                    btnAgregarCarrito.setEnabled(true);
                }
            } else {
                libroSeleccionado = null;
                lblTituloLibroVal.setText("Título: (No encontrado)");
                lblTituloLibroVal.setForeground(new Color(220, 53, 69));
                lblStockLibroVal.setText("Stock actual: -");
                lblStockLibroVal.setForeground(Color.BLACK);
                btnAgregarCarrito.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del libro debe ser numérico.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarAlCarrito() {
        if (libroSeleccionado == null) return;

        // Validar si ya está en el carrito
        for (int i = 0; i < carritoLibros.size(); i++) {
            DetallePrestamo dp = carritoLibros.obtener(i);
            if (dp.getLibro().getId() == libroSeleccionado.getId()) {
                JOptionPane.showMessageDialog(this, "El libro ya está en la lista de préstamo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        DetallePrestamo detalle = new DetallePrestamo();
        detalle.setLibro(libroSeleccionado);
        detalle.setCantidad(1); // Prestamos de a 1 unidad por libro

        carritoLibros.agregar(detalle);
        modeloCarrito.addRow(new Object[]{
            libroSeleccionado.getId(),
            libroSeleccionado.getTitulo(),
            1
        });

        // Limpiar sección libro
        libroSeleccionado = null;
        txtIdLibro.setText("");
        lblTituloLibroVal.setText("Título: (Ninguno)");
        lblTituloLibroVal.setForeground(Color.BLACK);
        lblStockLibroVal.setText("Stock actual: -");
        lblStockLibroVal.setForeground(Color.BLACK);
        btnAgregarCarrito.setEnabled(false);
    }

    private void limpiarCarrito() {
        carritoLibros.clear();
        modeloCarrito.setRowCount(0);
    }

    private void confirmarPrestamo() {
        if (estudianteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, busque y seleccione un estudiante apto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (carritoLibros.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No ha añadido ningún libro para prestar.", "Lista Vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exito = prestamoController.registrarPrestamo(estudianteSeleccionado, usuarioLogueado, carritoLibros);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Préstamo registrado correctamente en la base de datos.");
            limpiarCarrito();
            // Resetear sección estudiante
            estudianteSeleccionado = null;
            txtCodigoEstudiante.setText("");
            lblNombreEstudianteVal.setText("Estudiante: (No seleccionado)");
            lblNombreEstudianteVal.setForeground(Color.BLACK);
            lblEstadoEstudianteVal.setText("Estado: -");
            lblEstadoEstudianteVal.setForeground(Color.BLACK);
            actualizarTablaPrestamos();
        } else {
            JOptionPane.showMessageDialog(this, 
                    "No se pudo registrar el préstamo. Compruebe si el estudiante posee multas, libros atrasados o si se agotó el stock.", 
                    "Error de Registro", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int idPrestamoBuscadoDev = -1;

    private void buscarPrestamoParaDevolver() {
        String idStr = txtIdPrestamoDev.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del préstamo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Optional<Prestamo> prestamoOpt = prestamoController.buscarPrestamoPorId(id);

            if (prestamoOpt.isPresent()) {
                Prestamo p = prestamoOpt.get();
                idPrestamoBuscadoDev = p.getId();
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String fechaStr = p.getFechaPrestamo() != null ? p.getFechaPrestamo().format(formatter) : "-";
                
                String info = String.format("<html>Detalles del préstamo ID #%d:<br>"
                        + "<b>Estudiante:</b> %s<br>"
                        + "<b>Fecha Préstamo:</b> %s<br>"
                        + "<b>Estado actual:</b> <font color='%s'>%s</font></html>",
                        p.getId(),
                        p.getEstudiante() != null ? p.getEstudiante().getNombreCompleto() : "Desconocido",
                        fechaStr,
                        "DEVUELTO".equalsIgnoreCase(p.getEstado() != null ? p.getEstado().name() : "") ? "green" : "red",
                        p.getEstado() != null ? p.getEstado().name() : ""
                );

                lblDetallesPrestamoVal.setText(info);

                if (p.getEstado() == biblioteca.modelo.EstadoPrestamo.DEVUELTO) {
                    btnConfirmarDevolucion.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "Este préstamo ya figura como DEVUELTO.", "Devolución Previa", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    btnConfirmarDevolucion.setEnabled(true);
                }
            } else {
                idPrestamoBuscadoDev = -1;
                lblDetallesPrestamoVal.setText("<html>Detalles del préstamo:<br><font color='red'>No se encontró el préstamo</font></html>");
                btnConfirmarDevolucion.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del préstamo debe ser numérico.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmarDevolucion() {
        if (idPrestamoBuscadoDev == -1) return;

        boolean exito = prestamoController.registrarDevolucion(idPrestamoBuscadoDev);

        if (exito) {
            // Verificar si el sistema generó alguna multa en la base de datos justo ahora
            // Buscamos si el estudiante del préstamo tiene multas
            Optional<Prestamo> pOpt = prestamoController.buscarPrestamoPorId(idPrestamoBuscadoDev);
            String extraMsg = "";
            if (pOpt.isPresent() && pOpt.get().getEstado() == biblioteca.modelo.EstadoPrestamo.ATRASADO) {
                extraMsg = "\n⚠️ Se ha registrado la devolución con retraso. Se generó una multa diaria en PostgreSQL.";
            }

            JOptionPane.showMessageDialog(this, "Devolución registrada exitosamente. El stock de los libros ha sido repuesto." + extraMsg);
            
            // Limpiar
            txtIdPrestamoDev.setText("");
            lblDetallesPrestamoVal.setText("<html>Detalles del préstamo:<br>- Estudiante:<br>- Libro:<br>- Fecha Préstamo:<br>- Estado:</html>");
            btnConfirmarDevolucion.setEnabled(false);
            idPrestamoBuscadoDev = -1;
            
            actualizarTablaPrestamos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la devolución.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
