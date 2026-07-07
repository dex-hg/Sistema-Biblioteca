package biblioteca.vista;

import biblioteca.controlador.PrestamoController;
import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.EstadoPrestamo;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Usuario;
import biblioteca.servicios.PrestamoService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import biblioteca.estructuras.AlgoritmosOrdenamiento;
import biblioteca.estructuras.Cola;
import biblioteca.estructuras.ListaEnlazada;
import biblioteca.estructuras.Pila;

public class PrestamosPanel extends JPanel {

    private final Usuario usuarioLogueado;
    private final PrestamoController prestamoController;

    // Datos temporales para la transacción de préstamo
    private Estudiante estudianteSeleccionado;
    private Libro libroSeleccionado;
    private final ListaEnlazada<DetallePrestamo> carritoLibros = new ListaEnlazada<>();
    private final Pila<DetallePrestamo> historialCarrito = new Pila<>();
    private final Pila<DetallePrestamo> rehacerCarrito = new Pila<>();

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
    private JButton btnDeshacerCarrito;
    private JButton btnRehacerCarrito;
    private JTextField txtBuscarEstudianteReferencia;
    private JTable tablaEstudiantesReferencia;
    private DefaultTableModel modeloEstudiantesReferencia;
    private JTextField txtBuscarLibroReferencia;
    private JTable tablaLibrosReferencia;
    private DefaultTableModel modeloLibrosReferencia;

    // Componentes del Panel Devoluciones
    private JTextField txtIdPrestamoDev;
    private JButton btnBuscarPrestamo;
    private JButton btnSiguienteColaDevolucion;
    private JLabel lblDetallesPrestamoVal;
    private JButton btnConfirmarDevolucion;
    private JTable tablaPrestamosPendientes;
    private DefaultTableModel modeloPrestamosPendientes;

    // Tabla general de préstamos en el sistema
    private JTable tablaPrestamos;
    private DefaultTableModel modeloPrestamos;
    private JTextField txtBuscarHistorialEstudiante;
    private JTextField txtBuscarHistorialLibro;
    private JComboBox<String> cbFiltroEstadoHistorial;
    private JComboBox<String> cbFiltroDiasPrestamo;
    private JComboBox<String> cbOrdenDiasPrestamo;
    private JButton btnToggleHistorialPrestamos;
    private boolean mostrarHistorialCompletoPrestamos = false;

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
        JLabel lblTitulo = new JLabel(esEstudiante() ? "Mis Préstamos" : "Operaciones de Préstamos y Devoluciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(33, 37, 41));
        add(lblTitulo, BorderLayout.NORTH);

        if (esEstudiante()) {
            add(crearPanelConsultaEstudiante(), BorderLayout.CENTER);
            return;
        }

        // Crear Pestañas (TabbedPane)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel panelRegistro = crearPanelRegistroPrestamo();
        JPanel panelDevolucion = crearPanelDevolucion();
        JPanel panelHistorial = crearPanelTablaGeneralPrestamos();

        tabbedPane.addTab("Registrar Préstamo", panelRegistro);
        tabbedPane.addTab("Registrar Devolución", panelDevolucion);
        tabbedPane.addTab("Historial", panelHistorial);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private boolean esEstudiante() {
        return usuarioLogueado.getRol() != null && usuarioLogueado.getRol().getId() == 3;
    }

    private JPanel crearPanelConsultaEstudiante() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setOpaque(false);

        JLabel lblInfo = new JLabel("Consulta primero los libros pendientes de devolución y revisa qué debes regresar pronto.");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfo.setForeground(new Color(73, 80, 87));
        panelSuperior.add(lblInfo, BorderLayout.NORTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Filtrar:"));
        cbFiltroDiasPrestamo = new JComboBox<>(new String[]{"Pendientes", "Vencidos", "Vencen en 3 días", "Con más tiempo"});
        filtros.add(cbFiltroDiasPrestamo);
        filtros.add(new JLabel("Ordenar:"));
        cbOrdenDiasPrestamo = new JComboBox<>(new String[]{"Menos días primero", "Más días primero"});
        filtros.add(cbOrdenDiasPrestamo);
        btnToggleHistorialPrestamos = new JButton("Mostrar historial completo");
        btnToggleHistorialPrestamos.putClientProperty("JButton.buttonType", "roundRect");
        filtros.add(btnToggleHistorialPrestamos);
        panelSuperior.add(filtros, BorderLayout.CENTER);

        panel.add(panelSuperior, BorderLayout.NORTH);

        modeloPrestamos = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Préstamo", "Libros", "Fecha Préstamo", "Fecha Límite", "Fecha Devolución", "Estado", "Días restantes"}
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaPrestamos = new JTable(modeloPrestamos);
        tablaPrestamos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaPrestamos.setRowHeight(24);
        tablaPrestamos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblDetallesPrestamoVal = new JLabel("<html>Seleccione un préstamo para ver los libros asociados.</html>");
        lblDetallesPrestamoVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetallesPrestamoVal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(new JScrollPane(tablaPrestamos));
        split.setBottomComponent(new JScrollPane(lblDetallesPrestamoVal));
        split.setResizeWeight(0.72);
        split.setDividerLocation(360);
        split.setOpaque(false);
        panel.add(split, BorderLayout.CENTER);

        tablaPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPrestamos.getSelectedRow() != -1) {
                int row = tablaPrestamos.convertRowIndexToModel(tablaPrestamos.getSelectedRow());
                int idPrestamo = Integer.parseInt(modeloPrestamos.getValueAt(row, 0).toString());
                cargarDetallePrestamoEstudiante(idPrestamo);
            }
        });
        cbFiltroDiasPrestamo.addActionListener(e -> actualizarTablaPrestamos());
        cbOrdenDiasPrestamo.addActionListener(e -> actualizarTablaPrestamos());
        btnToggleHistorialPrestamos.addActionListener(e -> {
            mostrarHistorialCompletoPrestamos = !mostrarHistorialCompletoPrestamos;
            btnToggleHistorialPrestamos.setText(mostrarHistorialCompletoPrestamos
                    ? "Ocultar historial devuelto"
                    : "Mostrar historial completo");
            actualizarTablaPrestamos();
        });

        return panel;
    }

    private JPanel crearPanelRegistroPrestamo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);

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
        panelFormulario.add(pEstudiante, gbc);

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
        panelFormulario.add(pLibro, gbc);

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

        btnDeshacerCarrito = new JButton("Deshacer último");
        btnDeshacerCarrito.putClientProperty("JButton.buttonType", "roundRect");
        btnDeshacerCarrito.setEnabled(false);
        pAccionesCart.add(btnDeshacerCarrito);

        btnRehacerCarrito = new JButton("Rehacer");
        btnRehacerCarrito.putClientProperty("JButton.buttonType", "roundRect");
        btnRehacerCarrito.setEnabled(false);
        pAccionesCart.add(btnRehacerCarrito);
        
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
        panelFormulario.add(pCarrito, gbc);

        panel.add(panelFormulario, BorderLayout.CENTER);
        panel.add(crearPanelReferenciasRegistro(), BorderLayout.SOUTH);

        // Eventos
        btnBuscarEstudiante.addActionListener(e -> buscarEstudiante());
        txtCodigoEstudiante.addActionListener(e -> buscarEstudiante());
        btnBuscarLibro.addActionListener(e -> buscarLibro());
        txtIdLibro.addActionListener(e -> buscarLibro());
        btnAgregarCarrito.addActionListener(e -> agregarAlCarrito());
        btnDeshacerCarrito.addActionListener(e -> deshacerUltimoLibroCarrito());
        btnRehacerCarrito.addActionListener(e -> rehacerUltimoLibroCarrito());
        btnLimpiarCarrito.addActionListener(e -> limpiarCarrito());
        btnConfirmarPrestamo.addActionListener(e -> confirmarPrestamo());
        txtBuscarEstudianteReferencia.addActionListener(e -> cargarTablaEstudiantesReferencia());
        txtBuscarLibroReferencia.addActionListener(e -> cargarTablaLibrosReferencia());
        tablaEstudiantesReferencia.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaEstudiantesReferencia.getSelectedRow() != -1) {
                int row = tablaEstudiantesReferencia.convertRowIndexToModel(tablaEstudiantesReferencia.getSelectedRow());
                txtCodigoEstudiante.setText(modeloEstudiantesReferencia.getValueAt(row, 1).toString());
                buscarEstudiante();
            }
        });
        tablaLibrosReferencia.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaLibrosReferencia.getSelectedRow() != -1) {
                int row = tablaLibrosReferencia.convertRowIndexToModel(tablaLibrosReferencia.getSelectedRow());
                txtIdLibro.setText(modeloLibrosReferencia.getValueAt(row, 0).toString());
                buscarLibro();
            }
        });

        cargarTablaEstudiantesReferencia();
        cargarTablaLibrosReferencia();

        return panel;
    }

    private JPanel crearPanelReferenciasRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JPanel estudiantes = crearPanelReferenciaEstudiantes();
        gbc.gridx = 0;
        panel.add(estudiantes, gbc);

        JPanel libros = crearPanelReferenciaLibros();
        gbc.gridx = 1;
        panel.add(libros, gbc);

        return panel;
    }

    private JPanel crearPanelReferenciaEstudiantes() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Alumnos registrados"));

        JPanel buscador = new JPanel(new BorderLayout(6, 0));
        buscador.setOpaque(false);
        buscador.add(new JLabel("Nombre:"), BorderLayout.WEST);
        txtBuscarEstudianteReferencia = new JTextField();
        txtBuscarEstudianteReferencia.putClientProperty("JTextField.placeholderText", "Buscar alumno por nombre");
        buscador.add(txtBuscarEstudianteReferencia, BorderLayout.CENTER);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.putClientProperty("JButton.buttonType", "roundRect");
        btnBuscar.addActionListener(e -> cargarTablaEstudiantesReferencia());
        buscador.add(btnBuscar, BorderLayout.EAST);
        panel.add(buscador, BorderLayout.NORTH);

        modeloEstudiantesReferencia = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Código", "Nombre"}
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaEstudiantesReferencia = new JTable(modeloEstudiantesReferencia);
        tablaEstudiantesReferencia.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(tablaEstudiantesReferencia);
        scroll.setPreferredSize(new Dimension(0, 130));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelReferenciaLibros() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Libros registrados"));

        JPanel buscador = new JPanel(new BorderLayout(6, 0));
        buscador.setOpaque(false);
        buscador.add(new JLabel("Título:"), BorderLayout.WEST);
        txtBuscarLibroReferencia = new JTextField();
        txtBuscarLibroReferencia.putClientProperty("JTextField.placeholderText", "Buscar libro por título");
        buscador.add(txtBuscarLibroReferencia, BorderLayout.CENTER);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.putClientProperty("JButton.buttonType", "roundRect");
        btnBuscar.addActionListener(e -> cargarTablaLibrosReferencia());
        buscador.add(btnBuscar, BorderLayout.EAST);
        panel.add(buscador, BorderLayout.NORTH);

        modeloLibrosReferencia = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Título"}
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaLibrosReferencia = new JTable(modeloLibrosReferencia);
        tablaLibrosReferencia.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(tablaLibrosReferencia);
        scroll.setPreferredSize(new Dimension(0, 130));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void cargarTablaEstudiantesReferencia() {
        if (modeloEstudiantesReferencia == null) {
            return;
        }
        String criterio = txtBuscarEstudianteReferencia != null
                ? txtBuscarEstudianteReferencia.getText()
                : "";
        ListaEnlazada<Estudiante> estudiantes
                = prestamoController.buscarEstudiantesPorNombre(criterio);
        modeloEstudiantesReferencia.setRowCount(0);
        for (Estudiante estudiante : estudiantes) {
            modeloEstudiantesReferencia.addRow(new Object[]{
                estudiante.getId(),
                estudiante.getCodigo() != null ? estudiante.getCodigo() : "",
                estudiante.getNombreCompleto() != null ? estudiante.getNombreCompleto() : ""
            });
        }
    }

    private void cargarTablaLibrosReferencia() {
        if (modeloLibrosReferencia == null) {
            return;
        }
        String criterio = txtBuscarLibroReferencia != null
                ? txtBuscarLibroReferencia.getText()
                : "";
        ListaEnlazada<Libro> libros = prestamoController.buscarLibrosPorTitulo(criterio);
        modeloLibrosReferencia.setRowCount(0);
        for (Libro libro : libros) {
            modeloLibrosReferencia.addRow(new Object[]{
                libro.getId(),
                libro.getTitulo() != null ? libro.getTitulo() : ""
            });
        }
    }

    private JPanel crearPanelDevolucion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 440));

        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setOpaque(false);

        JLabel lblDevDesc = new JLabel("Seleccione un préstamo pendiente o ingrese su ID para procesar la devolución:");
        lblDevDesc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelSuperior.add(lblDevDesc, BorderLayout.NORTH);

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

        btnSiguienteColaDevolucion = new JButton("Siguiente en cola");
        btnSiguienteColaDevolucion.putClientProperty("JButton.buttonType", "roundRect");
        pSearch.add(btnSiguienteColaDevolucion);

        panelSuperior.add(pSearch, BorderLayout.CENTER);
        panel.add(panelSuperior, BorderLayout.NORTH);

        modeloPrestamosPendientes = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Préstamo", "Estudiante", "Fecha Préstamo", "Estado", "Días activo"}
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaPrestamosPendientes = new JTable(modeloPrestamosPendientes);
        tablaPrestamosPendientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaPrestamosPendientes.setRowHeight(24);
        tablaPrestamosPendientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel panelTablaPendientes = new JPanel(new BorderLayout(5, 5));
        panelTablaPendientes.setBackground(Color.WHITE);
        panelTablaPendientes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                "Préstamos pendientes de devolución",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(73, 80, 87)
        ));
        JScrollPane scrollPendientes = new JScrollPane(tablaPrestamosPendientes);
        scrollPendientes.setPreferredSize(new Dimension(0, 150));
        panelTablaPendientes.setMinimumSize(new Dimension(0, 140));
        panelTablaPendientes.add(scrollPendientes, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setOpaque(false);
        panelInferior.setMinimumSize(new Dimension(0, 190));
        panelInferior.setPreferredSize(new Dimension(0, 210));

        lblDetallesPrestamoVal = new JLabel("<html>Detalles del préstamo:<br>- Estudiante:<br>- Libro:<br>- Fecha Préstamo:<br>- Estado:</html>");
        lblDetallesPrestamoVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetallesPrestamoVal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JScrollPane scrollDetalles = new JScrollPane(lblDetallesPrestamoVal);
        scrollDetalles.setPreferredSize(new Dimension(0, 155));
        scrollDetalles.setMinimumSize(new Dimension(0, 135));
        panelInferior.add(scrollDetalles, BorderLayout.CENTER);

        btnConfirmarDevolucion = new JButton("Procesar Devolución y Retorno");
        btnConfirmarDevolucion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmarDevolucion.setBackground(new Color(13, 110, 253));
        btnConfirmarDevolucion.setForeground(Color.WHITE);
        btnConfirmarDevolucion.setEnabled(false);
        btnConfirmarDevolucion.putClientProperty("JButton.buttonType", "roundRect");
        btnConfirmarDevolucion.setPreferredSize(new Dimension(260, 42));

        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelAccion.setOpaque(false);
        panelAccion.add(btnConfirmarDevolucion);
        panelInferior.add(panelAccion, BorderLayout.SOUTH);

        JSplitPane splitDevolucion = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitDevolucion.setTopComponent(panelTablaPendientes);
        splitDevolucion.setBottomComponent(panelInferior);
        splitDevolucion.setResizeWeight(0.48);
        splitDevolucion.setDividerLocation(190);
        splitDevolucion.setBorder(null);
        splitDevolucion.setOpaque(false);
        panel.add(splitDevolucion, BorderLayout.CENTER);

        // Eventos
        btnBuscarPrestamo.addActionListener(e -> buscarPrestamoParaDevolver());
        txtIdPrestamoDev.addActionListener(e -> buscarPrestamoParaDevolver());
        btnSiguienteColaDevolucion.addActionListener(e -> cargarSiguientePrestamoEnCola());
        btnConfirmarDevolucion.addActionListener(e -> confirmarDevolucion());
        tablaPrestamosPendientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPrestamosPendientes.getSelectedRow() != -1) {
                int row = tablaPrestamosPendientes.convertRowIndexToModel(tablaPrestamosPendientes.getSelectedRow());
                String id = modeloPrestamosPendientes.getValueAt(row, 0).toString();
                txtIdPrestamoDev.setText(id);
                cargarPrestamoParaDevolver(Integer.parseInt(id), false);
            }
        });

        return panel;
    }

    private JPanel crearPanelTablaGeneralPrestamos() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                "Historial de préstamos registrados",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(73, 80, 87)
        ));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Estudiante:"));
        txtBuscarHistorialEstudiante = new JTextField(18);
        txtBuscarHistorialEstudiante.putClientProperty("JTextField.placeholderText", "Nombre de estudiante");
        filtros.add(txtBuscarHistorialEstudiante);
        filtros.add(new JLabel("Libro:"));
        txtBuscarHistorialLibro = new JTextField(18);
        txtBuscarHistorialLibro.putClientProperty("JTextField.placeholderText", "Título de libro");
        filtros.add(txtBuscarHistorialLibro);
        filtros.add(new JLabel("Estado:"));
        cbFiltroEstadoHistorial = new JComboBox<>(new String[]{"TODOS", "ACTIVO", "ATRASADO", "DEVUELTO"});
        filtros.add(cbFiltroEstadoHistorial);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.putClientProperty("JButton.buttonType", "roundRect");
        filtros.add(btnBuscar);
        panel.add(filtros, BorderLayout.NORTH);

        modeloPrestamos = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Préstamo", "Estudiante", "Libros", "Bibliotecario", "Fecha Préstamo", "Fecha Devolución", "Estado"}
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

        tablaPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPrestamos.getSelectedRow() != -1) {
                int row = tablaPrestamos.convertRowIndexToModel(tablaPrestamos.getSelectedRow());
                String id = modeloPrestamos.getValueAt(row, 0).toString();
                txtIdPrestamoDev.setText(id);
            }
        });
        btnBuscar.addActionListener(e -> actualizarTablaPrestamos());
        txtBuscarHistorialEstudiante.addActionListener(e -> actualizarTablaPrestamos());
        txtBuscarHistorialLibro.addActionListener(e -> actualizarTablaPrestamos());
        cbFiltroEstadoHistorial.addActionListener(e -> actualizarTablaPrestamos());

        return panel;
    }

    public void actualizarTablaPrestamos() {
        if (esEstudiante()) {
            actualizarTablaPrestamosEstudiante();
            return;
        }

        ListaEnlazada<Prestamo> prestamos = prestamoController.obtenerTodos();
        modeloPrestamos.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo p = prestamos.obtener(i);
            String resumenLibros = construirResumenLibrosTabla(p.getId());
            if (!cumpleFiltroHistorial(p, resumenLibros)) {
                continue;
            }
            String fDev = (p.getFechaDevolucion() != null) ? p.getFechaDevolucion().format(formatter) : "-";
            modeloPrestamos.addRow(new Object[]{
                p.getId(),
                p.getEstudiante() != null ? p.getEstudiante().getNombreCompleto() : "Desconocido",
                resumenLibros,
                p.getBibliotecario() != null ? p.getBibliotecario().getNombreCompleto() : "Desconocido",
                p.getFechaPrestamo() != null ? p.getFechaPrestamo().format(formatter) : "",
                fDev,
                p.getEstado() != null ? p.getEstado().name() : ""
            });
        }
        actualizarTablaPrestamosPendientes();
    }

    private boolean cumpleFiltroHistorial(Prestamo prestamo, String resumenLibros) {
        String filtroEstudiante = txtBuscarHistorialEstudiante != null
                ? txtBuscarHistorialEstudiante.getText().trim().toLowerCase()
                : "";
        String filtroLibro = txtBuscarHistorialLibro != null
                ? txtBuscarHistorialLibro.getText().trim().toLowerCase()
                : "";
        String filtroEstado = cbFiltroEstadoHistorial != null
                ? cbFiltroEstadoHistorial.getSelectedItem().toString()
                : "TODOS";

        String estudiante = prestamo.getEstudiante() != null
                && prestamo.getEstudiante().getNombreCompleto() != null
                        ? prestamo.getEstudiante().getNombreCompleto().toLowerCase()
                        : "";
        String libros = resumenLibros != null ? resumenLibros.toLowerCase() : "";
        String estado = prestamo.getEstado() != null ? prestamo.getEstado().name() : "";

        return (filtroEstudiante.isEmpty() || estudiante.contains(filtroEstudiante))
                && (filtroLibro.isEmpty() || libros.contains(filtroLibro))
                && ("TODOS".equals(filtroEstado) || filtroEstado.equalsIgnoreCase(estado));
    }

    private void actualizarTablaPrestamosEstudiante() {
        ListaEnlazada<Prestamo> prestamos = prestamoController.obtenerPrestamosPorEstudiante(usuarioLogueado.getId());
        ListaEnlazada<Prestamo> prestamosFiltrados = prestamos.filtrar(p
                -> (mostrarHistorialCompletoPrestamos
                || p.getEstado() != EstadoPrestamo.DEVUELTO)
                && cumpleFiltroDias(p));

        Comparator<Prestamo> comparadorDias = Comparator.comparingInt(this::calcularDiasRestantesOrden);
        if ("Más días primero".equals(cbOrdenDiasPrestamo != null ? cbOrdenDiasPrestamo.getSelectedItem() : "")) {
            comparadorDias = comparadorDias.reversed();
        }
        ListaEnlazada<Prestamo> prestamosOrdenados
                = AlgoritmosOrdenamiento.ordenarQuickSort(
                        prestamosFiltrados,
                        new Prestamo[0],
                        comparadorDias);

        modeloPrestamos.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Prestamo p : prestamosOrdenados) {
            LocalDate fechaPrestamo = p.getFechaPrestamo();
            LocalDate fechaLimite = fechaPrestamo != null
                    ? fechaPrestamo.plusDays(PrestamoService.MAX_DIAS_PRESTAMO)
                    : null;
            String estado = p.getEstado() != null ? p.getEstado().name() : "";
            String diasRestantes = "-";
            if (fechaLimite != null && p.getEstado() != EstadoPrestamo.DEVUELTO) {
                long dias = ChronoUnit.DAYS.between(LocalDate.now(), fechaLimite);
                diasRestantes = dias >= 0 ? String.valueOf(dias) : "Vencido hace " + Math.abs(dias) + " días";
            }
            modeloPrestamos.addRow(new Object[]{
                p.getId(),
                construirResumenLibrosTabla(p.getId()),
                fechaPrestamo != null ? fechaPrestamo.format(formatter) : "",
                fechaLimite != null ? fechaLimite.format(formatter) : "",
                p.getFechaDevolucion() != null ? p.getFechaDevolucion().format(formatter) : "-",
                estado,
                diasRestantes
            });
        }
    }

    private boolean cumpleFiltroDias(Prestamo prestamo) {
        String filtro = cbFiltroDiasPrestamo != null ? cbFiltroDiasPrestamo.getSelectedItem().toString() : "Pendientes";
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            return mostrarHistorialCompletoPrestamos;
        }

        int diasRestantes = calcularDiasRestantesOrden(prestamo);
        return switch (filtro) {
            case "Vencidos" -> diasRestantes < 0;
            case "Vencen en 3 días" -> diasRestantes >= 0 && diasRestantes <= 3;
            case "Con más tiempo" -> diasRestantes > 3;
            default -> true;
        };
    }

    private int calcularDiasRestantesOrden(Prestamo prestamo) {
        if (prestamo.getFechaPrestamo() == null) {
            return Integer.MAX_VALUE;
        }
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            return Integer.MAX_VALUE - 1;
        }
        LocalDate fechaLimite = prestamo.getFechaPrestamo().plusDays(PrestamoService.MAX_DIAS_PRESTAMO);
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), fechaLimite);
    }

    private void actualizarTablaPrestamosPendientes() {
        if (modeloPrestamosPendientes == null) {
            return;
        }
        Cola<Prestamo> colaDevoluciones = prestamoController.obtenerColaDevolucionesPendientes();
        modeloPrestamosPendientes.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (!colaDevoluciones.isEmpty()) {
            Prestamo p = colaDevoluciones.dequeue();
            LocalDate fechaPrestamo = p.getFechaPrestamo();
            long diasActivo = fechaPrestamo != null
                    ? ChronoUnit.DAYS.between(fechaPrestamo, LocalDate.now())
                    : 0;
            modeloPrestamosPendientes.addRow(new Object[]{
                p.getId(),
                p.getEstudiante() != null ? p.getEstudiante().getNombreCompleto() : "Desconocido",
                fechaPrestamo != null ? fechaPrestamo.format(formatter) : "",
                p.getEstado() != null ? p.getEstado().name() : "",
                diasActivo
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
        historialCarrito.push(detalle);
        rehacerCarrito.clear();
        modeloCarrito.addRow(new Object[]{
            libroSeleccionado.getId(),
            libroSeleccionado.getTitulo(),
            1
        });
        actualizarEstadoAccionesCarrito();

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
        historialCarrito.clear();
        rehacerCarrito.clear();
        modeloCarrito.setRowCount(0);
        actualizarEstadoAccionesCarrito();
    }

    private void deshacerUltimoLibroCarrito() {
        if (historialCarrito.isEmpty() || carritoLibros.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay libros para deshacer.", "Lista vacía", JOptionPane.INFORMATION_MESSAGE);
            actualizarEstadoAccionesCarrito();
            return;
        }

        DetallePrestamo detalleDeshecho = historialCarrito.pop();
        carritoLibros.removerUltimo();
        rehacerCarrito.push(detalleDeshecho);
        int ultimaFila = modeloCarrito.getRowCount() - 1;
        if (ultimaFila >= 0) {
            modeloCarrito.removeRow(ultimaFila);
        }
        actualizarEstadoAccionesCarrito();
    }

    private void rehacerUltimoLibroCarrito() {
        if (rehacerCarrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay libros para rehacer.", "Sin acciones", JOptionPane.INFORMATION_MESSAGE);
            actualizarEstadoAccionesCarrito();
            return;
        }

        DetallePrestamo detalle = rehacerCarrito.pop();
        carritoLibros.agregar(detalle);
        historialCarrito.push(detalle);
        Libro libro = detalle.getLibro();
        modeloCarrito.addRow(new Object[]{
            libro != null ? libro.getId() : 0,
            libro != null ? libro.getTitulo() : "Libro no disponible",
            detalle.getCantidad()
        });
        actualizarEstadoAccionesCarrito();
    }

    private void actualizarEstadoAccionesCarrito() {
        if (btnDeshacerCarrito != null) {
            btnDeshacerCarrito.setEnabled(!carritoLibros.isEmpty() && !historialCarrito.isEmpty());
        }
        if (btnRehacerCarrito != null) {
            btnRehacerCarrito.setEnabled(!rehacerCarrito.isEmpty());
        }
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

    private void cargarSiguientePrestamoEnCola() {
        Cola<Prestamo> colaDevoluciones = prestamoController.obtenerColaDevolucionesPendientes();
        if (colaDevoluciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay préstamos pendientes en la cola de devolución.", "Cola vacía", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Prestamo siguiente = colaDevoluciones.peek();
        txtIdPrestamoDev.setText(String.valueOf(siguiente.getId()));
        cargarPrestamoParaDevolver(siguiente.getId(), false);
    }

    private void buscarPrestamoParaDevolver() {
        String idStr = txtIdPrestamoDev.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del préstamo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            cargarPrestamoParaDevolver(id, true);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del préstamo debe ser numérico.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarPrestamoParaDevolver(int id, boolean mostrarAvisos) {
        Optional<Prestamo> prestamoOpt = prestamoController.buscarPrestamoPorId(id);

        if (prestamoOpt.isPresent()) {
            Prestamo p = prestamoOpt.get();
            idPrestamoBuscadoDev = p.getId();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaStr = p.getFechaPrestamo() != null ? p.getFechaPrestamo().format(formatter) : "-";
            long diasActivo = p.getFechaPrestamo() != null
                    ? ChronoUnit.DAYS.between(p.getFechaPrestamo(), LocalDate.now())
                    : 0;
            String estado = p.getEstado() != null ? p.getEstado().name() : "";
            String colorEstado = p.getEstado() == EstadoPrestamo.DEVUELTO ? "green" : "#dc3545";

            String info = String.format("<html>Detalles del préstamo ID #%d:<br>"
                    + "<b>Estudiante:</b> %s<br>"
                    + "<b>Fecha Préstamo:</b> %s<br>"
                    + "<b>Días transcurridos:</b> %d<br>"
                    + "<b>Estado actual:</b> <font color='%s'>%s</font><br>"
                    + "<b>Libros prestados:</b><br>%s</html>",
                    p.getId(),
                    p.getEstudiante() != null ? p.getEstudiante().getNombreCompleto() : "Desconocido",
                    fechaStr,
                    diasActivo,
                    colorEstado,
                    estado,
                    construirResumenLibrosPrestamo(p.getId())
            );

            lblDetallesPrestamoVal.setText(info);

            if (p.getEstado() == EstadoPrestamo.DEVUELTO) {
                btnConfirmarDevolucion.setEnabled(false);
                if (mostrarAvisos) {
                    JOptionPane.showMessageDialog(this, "Este préstamo ya figura como DEVUELTO.", "Devolución Previa", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                btnConfirmarDevolucion.setEnabled(true);
            }
        } else {
            idPrestamoBuscadoDev = -1;
            lblDetallesPrestamoVal.setText("<html>Detalles del préstamo:<br><font color='red'>No se encontró el préstamo</font></html>");
            btnConfirmarDevolucion.setEnabled(false);
        }
    }

    private String construirResumenLibrosPrestamo(int idPrestamo) {
        ListaEnlazada<DetallePrestamo> detalles = prestamoController.obtenerDetallesPrestamo(idPrestamo);
        if (detalles.isEmpty()) {
            return "- Sin detalle de libros registrado";
        }

        StringBuilder resumen = new StringBuilder();
        for (int i = 0; i < detalles.size(); i++) {
            DetallePrestamo detalle = detalles.obtener(i);
            int idLibro = detalle.getLibro() != null ? detalle.getLibro().getId() : 0;
            String titulo = obtenerTituloDetalle(detalle, idLibro);
            resumen.append("- ")
                    .append(titulo)
                    .append(" (cant. ")
                    .append(detalle.getCantidad())
                    .append(")");
            if (i < detalles.size() - 1) {
                resumen.append("<br>");
            }
        }
        return resumen.toString();
    }

    private String construirResumenLibrosTabla(int idPrestamo) {
        ListaEnlazada<DetallePrestamo> detalles = prestamoController.obtenerDetallesPrestamo(idPrestamo);
        if (detalles.isEmpty()) {
            return "Sin detalle";
        }

        StringBuilder resumen = new StringBuilder();
        for (int i = 0; i < detalles.size(); i++) {
            DetallePrestamo detalle = detalles.obtener(i);
            int idLibro = detalle.getLibro() != null ? detalle.getLibro().getId() : 0;
            String titulo = obtenerTituloDetalle(detalle, idLibro);
            if (i > 0) {
                resumen.append(", ");
            }
            resumen.append(titulo);
        }
        return resumen.toString();
    }

    private String obtenerTituloDetalle(DetallePrestamo detalle, int idLibro) {
        if (detalle.getLibro() != null
                && detalle.getLibro().getTitulo() != null
                && !detalle.getLibro().getTitulo().trim().isEmpty()) {
            return detalle.getLibro().getTitulo();
        }
        return prestamoController.buscarLibroPorId(idLibro)
                .map(Libro::getTitulo)
                .orElse("Libro ID " + idLibro);
    }

    private void cargarDetallePrestamoEstudiante(int idPrestamo) {
        Optional<Prestamo> prestamoOpt = prestamoController.buscarPrestamoPorId(idPrestamo);
        if (prestamoOpt.isEmpty()) {
            lblDetallesPrestamoVal.setText("<html>No se encontró el préstamo seleccionado.</html>");
            return;
        }

        Prestamo prestamo = prestamoOpt.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaPrestamo = prestamo.getFechaPrestamo() != null
                ? prestamo.getFechaPrestamo().format(formatter)
                : "-";
        String fechaLimite = prestamo.getFechaPrestamo() != null
                ? prestamo.getFechaPrestamo().plusDays(PrestamoService.MAX_DIAS_PRESTAMO).format(formatter)
                : "-";

        lblDetallesPrestamoVal.setText(String.format("<html><b>Préstamo ID #%d</b><br>"
                        + "<b>Fecha préstamo:</b> %s<br>"
                        + "<b>Fecha límite:</b> %s<br>"
                        + "<b>Estado:</b> %s<br>"
                        + "<b>Libros:</b><br>%s</html>",
                prestamo.getId(),
                fechaPrestamo,
                fechaLimite,
                prestamo.getEstado() != null ? prestamo.getEstado().name() : "",
                construirResumenLibrosPrestamo(idPrestamo)
        ));
    }

    private void confirmarDevolucion() {
        if (idPrestamoBuscadoDev == -1) return;

        Optional<Prestamo> prestamoAntesDevolver = prestamoController.buscarPrestamoPorId(idPrestamoBuscadoDev);
        boolean correspondeMultaPorRetraso = prestamoAntesDevolver.isPresent()
                && prestamoAntesDevolver.get().getFechaPrestamo() != null
                && ChronoUnit.DAYS.between(prestamoAntesDevolver.get().getFechaPrestamo(), LocalDate.now())
                > PrestamoService.MAX_DIAS_PRESTAMO;

        boolean exito = prestamoController.registrarDevolucion(idPrestamoBuscadoDev);

        if (exito) {
            String extraMsg = "";
            if (correspondeMultaPorRetraso) {
                extraMsg = "\nSe registró una multa pendiente por devolución tardía.";
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
