package biblioteca.vista;

import biblioteca.controlador.ReporteController;
import biblioteca.estructuras.AlgoritmosOrdenamiento;
import biblioteca.modelo.Multa;
import biblioteca.modelo.Prestamo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import javax.swing.BorderFactory;
import biblioteca.estructuras.ListaEnlazada;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class ReportesPanel extends JPanel {

    private final ReporteController reporteController;

    // Tablas de reporte
    private JTable tablaMasPrestados;
    private DefaultTableModel modeloMasPrestados;

    private JTable tablaActivos;
    private DefaultTableModel modeloActivos;

    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    private JTable tablaMultas;
    private DefaultTableModel modeloMultas;
    private JComboBox<String> cbEstadoMultas;
    private JComboBox<String> cbOrdenMultas;
    private JTextField txtEstudianteMultas;
    private ListaEnlazada<Multa> multasReporte = new ListaEnlazada<>();

    public ReportesPanel() {
        this.reporteController = new ReporteController();
        inicializarUI();
        recargarReportes();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);

        // Cabecera con título y botón de recarga
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("Estadísticas e Informes Administrativos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(33, 37, 41));
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        JButton btnRecargar = new JButton("Actualizar Datos");
        btnRecargar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRecargar.setBackground(new Color(33, 37, 41));
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.putClientProperty("JButton.buttonType", "roundRect");
        btnRecargar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelHeader.add(btnRecargar, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // Pestañas para cada tipo de reporte
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Libros Más Prestados", crearTabMasPrestados());
        tabbedPane.addTab("Préstamos Activos", crearTabActivos());
        tabbedPane.addTab("Historial General", crearTabHistorial());
        tabbedPane.addTab("Historial de Multas", crearTabMultas());

        add(tabbedPane, BorderLayout.CENTER);

        // Eventos
        btnRecargar.addActionListener(e -> recargarReportes());
    }

    private JPanel crearTabMasPrestados() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblInfo = new JLabel("Ranking de los 5 libros con mayor demanda y cantidad acumulada de préstamos:");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfo.setForeground(new Color(73, 80, 87));
        panel.add(lblInfo, BorderLayout.NORTH);

        modeloMasPrestados = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Posición", "Título del Libro", "Autor", "Cantidad de Préstamos"}
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaMasPrestados = new JTable(modeloMasPrestados);
        tablaMasPrestados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaMasPrestados.setRowHeight(25);
        tablaMasPrestados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tablaMasPrestados);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTabActivos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblInfo = new JLabel("Listado de libros actualmente en posesión de estudiantes (No devueltos):");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfo.setForeground(new Color(73, 80, 87));
        panel.add(lblInfo, BorderLayout.NORTH);

        modeloActivos = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Préstamo", "Estudiante", "Bibliotecario", "Fecha Préstamo", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaActivos = new JTable(modeloActivos);
        tablaActivos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaActivos.setRowHeight(25);
        tablaActivos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tablaActivos);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTabHistorial() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblInfo = new JLabel("Registro cronológico histórico de todas las transacciones de la biblioteca:");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfo.setForeground(new Color(73, 80, 87));
        panel.add(lblInfo, BorderLayout.NORTH);

        modeloHistorial = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Préstamo", "Estudiante", "Bibliotecario", "Fecha Préstamo", "Fecha Devolución", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaHistorial.setRowHeight(25);
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTabMultas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Estado:"));
        cbEstadoMultas = new JComboBox<>(new String[]{"TODAS", "PENDIENTE", "PAGADA"});
        filtros.add(cbEstadoMultas);
        filtros.add(new JLabel("Ordenar:"));
        cbOrdenMultas = new JComboBox<>(new String[]{
            "Fecha reciente",
            "Mayor monto",
            "Menor monto",
            "Estudiante con más multas"
        });
        filtros.add(cbOrdenMultas);
        filtros.add(new JLabel("Estudiante:"));
        txtEstudianteMultas = new JTextField(18);
        txtEstudianteMultas.putClientProperty("JTextField.placeholderText", "Nombre del estudiante");
        filtros.add(txtEstudianteMultas);
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.putClientProperty("JButton.buttonType", "roundRect");
        filtros.add(btnFiltrar);
        panel.add(filtros, BorderLayout.NORTH);

        modeloMultas = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID Multa",
                    "ID Préstamo",
                    "Estudiante",
                    "Monto",
                    "Motivo",
                    "Estado",
                    "Fecha Creación",
                    "Fecha Pago"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaMultas = new JTable(modeloMultas) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    int modelRow = convertRowIndexToModel(row);
                    String motivo = modeloMultas.getValueAt(modelRow, 4) != null
                            ? modeloMultas.getValueAt(modelRow, 4).toString().toLowerCase()
                            : "";
                    if (motivo.contains("inutilizable") || motivo.contains("perdido")) {
                        component.setBackground(new Color(255, 235, 238));
                    } else {
                        component.setBackground(Color.WHITE);
                    }
                }
                return component;
            }
        };
        tablaMultas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaMultas.setRowHeight(25);
        tablaMultas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaMultas.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer());

        panel.add(new JScrollPane(tablaMultas), BorderLayout.CENTER);

        cbEstadoMultas.addActionListener(e -> cargarTablaMultas());
        cbOrdenMultas.addActionListener(e -> cargarTablaMultas());
        txtEstudianteMultas.addActionListener(e -> cargarTablaMultas());
        btnFiltrar.addActionListener(e -> cargarTablaMultas());

        return panel;
    }

    public void recargarReportes() {
        new Thread(() -> {
            try {
                // 1. Libros más prestados
                ListaEnlazada<Object[]> ranking = reporteController.obtenerLibrosMasPrestados();
                
                // 2. Préstamos activos
                ListaEnlazada<Prestamo> activos = reporteController.obtenerPrestamosActivos();

                // 3. Historial general
                ListaEnlazada<Prestamo> historial = reporteController.obtenerHistorialGeneral();

                // 4. Historial de multas
                ListaEnlazada<Multa> multas = reporteController.obtenerHistorialMultas();

                // Actualizar la GUI en el EDT
                java.awt.EventQueue.invokeLater(() -> {
                    multasReporte = multas;
                    // Llenar más prestados
                    modeloMasPrestados.setRowCount(0);
                    int pos = 1;
                    for (int i = 0; i < ranking.size(); i++) {
                        Object[] row = ranking.obtener(i);
                        modeloMasPrestados.addRow(new Object[]{
                            pos++ + "°",
                            row[0],
                            row[1],
                            row[2] + " préstamos"
                        });
                    }

                    // Llenar préstamos activos
                    modeloActivos.setRowCount(0);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    for (int i = 0; i < activos.size(); i++) {
                        Prestamo p = activos.obtener(i);
                        modeloActivos.addRow(new Object[]{
                            p.getId(),
                            p.getEstudiante() != null ? p.getEstudiante().getNombreCompleto() : "Desconocido",
                            p.getBibliotecario() != null ? p.getBibliotecario().getNombreCompleto() : "Desconocido",
                            p.getFechaPrestamo() != null ? p.getFechaPrestamo().format(formatter) : "",
                            p.getEstado() != null ? p.getEstado().name() : ""
                        });
                    }

                    // Llenar historial general
                    modeloHistorial.setRowCount(0);
                    for (int i = 0; i < historial.size(); i++) {
                        Prestamo p = historial.obtener(i);
                        String fDev = (p.getFechaDevolucion() != null) ? p.getFechaDevolucion().format(formatter) : "-";
                        modeloHistorial.addRow(new Object[]{
                            p.getId(),
                            p.getEstudiante() != null ? p.getEstudiante().getNombreCompleto() : "Desconocido",
                            p.getBibliotecario() != null ? p.getBibliotecario().getNombreCompleto() : "Desconocido",
                            p.getFechaPrestamo() != null ? p.getFechaPrestamo().format(formatter) : "",
                            fDev,
                            p.getEstado() != null ? p.getEstado().name() : ""
                        });
                    }
                    cargarTablaMultas();
                });
            } catch (Exception e) {
                System.out.println("Error al recargar reportes: " + e.getMessage());
            }
        }).start();
    }

    private void cargarTablaMultas() {
        if (modeloMultas == null) {
            return;
        }

        String estadoFiltro = cbEstadoMultas != null
                ? cbEstadoMultas.getSelectedItem().toString()
                : "TODAS";
        String estudianteFiltro = txtEstudianteMultas != null
                ? txtEstudianteMultas.getText().trim().toLowerCase()
                : "";

        ListaEnlazada<Multa> filtradas = multasReporte.filtrar(multa -> {
            String estado = multa.getEstado() != null ? multa.getEstado() : "";
            String estudiante = obtenerNombreEstudiante(multa).toLowerCase();
            return ("TODAS".equals(estadoFiltro) || estadoFiltro.equalsIgnoreCase(estado))
                    && (estudianteFiltro.isEmpty() || estudiante.contains(estudianteFiltro));
        });

        Comparator<Multa> comparador = switch (cbOrdenMultas != null
                ? cbOrdenMultas.getSelectedItem().toString()
                : "Fecha reciente") {
            case "Mayor monto" -> Comparator.comparingDouble(Multa::getMonto).reversed();
            case "Menor monto" -> Comparator.comparingDouble(Multa::getMonto);
            case "Estudiante con más multas" -> (m1, m2) -> {
                int c1 = contarMultasEstudiante(filtradas, obtenerNombreEstudiante(m1));
                int c2 = contarMultasEstudiante(filtradas, obtenerNombreEstudiante(m2));
                int comparacion = Integer.compare(c2, c1);
                if (comparacion != 0) {
                    return comparacion;
                }
                return obtenerNombreEstudiante(m1).compareToIgnoreCase(obtenerNombreEstudiante(m2));
            };
            default -> Comparator.comparing(
                    Multa::getFechaCreacion,
                    Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        };

        ListaEnlazada<Multa> ordenadas = AlgoritmosOrdenamiento.ordenarQuickSort(
                filtradas,
                new Multa[0],
                comparador);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        modeloMultas.setRowCount(0);
        for (Multa multa : ordenadas) {
            modeloMultas.addRow(new Object[]{
                multa.getId(),
                multa.getPrestamo() != null ? multa.getPrestamo().getId() : 0,
                obtenerNombreEstudiante(multa),
                String.format("S/. %.2f", multa.getMonto()),
                multa.getMotivo(),
                multa.getEstado(),
                multa.getFechaCreacion() != null ? multa.getFechaCreacion().format(formatter) : "",
                multa.getFechaPago() != null ? multa.getFechaPago().format(formatter) : "-"
            });
        }
    }

    private String obtenerNombreEstudiante(Multa multa) {
        if (multa.getPrestamo() != null
                && multa.getPrestamo().getEstudiante() != null
                && multa.getPrestamo().getEstudiante().getNombreCompleto() != null) {
            return multa.getPrestamo().getEstudiante().getNombreCompleto();
        }
        return "Desconocido";
    }

    private int contarMultasEstudiante(ListaEnlazada<Multa> multas, String estudiante) {
        int total = 0;
        for (Multa multa : multas) {
            if (obtenerNombreEstudiante(multa).equalsIgnoreCase(estudiante)) {
                total++;
            }
        }
        return total;
    }
}
