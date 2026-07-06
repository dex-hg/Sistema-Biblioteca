package biblioteca.vista;

import biblioteca.controlador.PrestamoController;
import biblioteca.conexion.ConexionBD;
import biblioteca.estructuras.ListaEnlazada;
import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Multa;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Usuario;
import biblioteca.servicios.LibroService;
import biblioteca.servicios.MultaService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class MultasPanel extends JPanel {

    private final MultaService multaService;
    private final PrestamoController prestamoController;
    private final LibroService libroService;
    private final Usuario usuarioLogueado;
    private final boolean modoGestion;

    private JTable tablaMultas;
    private DefaultTableModel modeloMultas;
    private JComboBox<String> comboFiltroEstado;
    private JTextField txtIdPrestamo;
    private JTextField txtIdLibroAfectado;
    private JTextField txtMonto;
    private JTextArea txtMotivo;
    private JCheckBox chkLibroInutilizable;
    private JLabel lblMensajeMultas;
    private JComboBox<String> cbOrdenMultas;
    private JButton btnToggleHistorialMultas;
    private JButton btnRegistrarMulta;
    private JButton btnMarcarPagada;
    private JButton btnActualizar;
    private boolean mostrarHistorialCompletoMultas = false;

    public MultasPanel() {
        this(null);
    }

    public MultasPanel(Usuario usuario) {
        this.multaService = new MultaService();
        this.prestamoController = new PrestamoController();
        this.libroService = new LibroService();
        this.usuarioLogueado = usuario;
        this.modoGestion = usuario == null
                || usuario.getRol() == null
                || usuario.getRol().getId() == 1
                || usuario.getRol().getId() == 2;
        inicializarUI();
        recargarMultas();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitulo = new JLabel(modoGestion ? "Gestión de Multas" : "Mis Multas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(33, 37, 41));
        header.add(lblTitulo, BorderLayout.WEST);

        JPanel accionesHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        accionesHeader.setOpaque(false);
        accionesHeader.add(new JLabel("Filtro:"));
        comboFiltroEstado = new JComboBox<>(new String[]{"TODAS", "PENDIENTE", "PAGADA"});
        if (!modoGestion) {
            comboFiltroEstado.setSelectedItem("PENDIENTE");
        }
        accionesHeader.add(comboFiltroEstado);

        if (!modoGestion) {
            accionesHeader.add(new JLabel("Ordenar:"));
            cbOrdenMultas = new JComboBox<>(new String[]{"Fecha reciente", "Fecha antigua", "Monto mayor", "Monto menor"});
            accionesHeader.add(cbOrdenMultas);

            btnToggleHistorialMultas = new JButton("Mostrar historial completo");
            btnToggleHistorialMultas.putClientProperty("JButton.buttonType", "roundRect");
            accionesHeader.add(btnToggleHistorialMultas);
        }

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBackground(new Color(33, 37, 41));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.putClientProperty("JButton.buttonType", "roundRect");
        accionesHeader.add(btnActualizar);
        header.add(accionesHeader, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(15, 15));
        contenido.setOpaque(false);
        contenido.add(crearPanelTabla(), BorderLayout.CENTER);
        if (modoGestion) {
            contenido.add(crearPanelFormulario(), BorderLayout.EAST);
        }
        add(contenido, BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> recargarMultas());
        comboFiltroEstado.addActionListener(e -> recargarMultas());
        if (!modoGestion) {
            cbOrdenMultas.addActionListener(e -> recargarMultas());
            btnToggleHistorialMultas.addActionListener(e -> {
                mostrarHistorialCompletoMultas = !mostrarHistorialCompletoMultas;
                btnToggleHistorialMultas.setText(mostrarHistorialCompletoMultas
                        ? "Ocultar multas pagadas"
                        : "Mostrar historial completo");
                recargarMultas();
            });
        }
        if (modoGestion) {
            btnRegistrarMulta.addActionListener(e -> registrarMultaManual());
            btnMarcarPagada.addActionListener(e -> marcarMultaPagada());
            chkLibroInutilizable.addActionListener(e -> {
                txtIdLibroAfectado.setEnabled(chkLibroInutilizable.isSelected());
                if (!chkLibroInutilizable.isSelected()) {
                    txtIdLibroAfectado.setText("");
                }
            });
        }
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                "Multas registradas",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(73, 80, 87)
        ));

        modeloMultas = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Multa", "ID Préstamo", "Estudiante", "Monto", "Motivo", "Estado", "Fecha"}
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaMultas = new JTable(modeloMultas);
        tablaMultas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaMultas.setRowHeight(24);
        tablaMultas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblMensajeMultas = new JLabel("No tienes ninguna multa pendiente.");
        lblMensajeMultas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMensajeMultas.setForeground(new Color(25, 135, 84));
        lblMensajeMultas.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        lblMensajeMultas.setVisible(false);
        panel.add(lblMensajeMultas, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaMultas), BorderLayout.CENTER);

        if (modoGestion) {
            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            acciones.setOpaque(false);
            btnMarcarPagada = new JButton("Marcar como pagada");
            btnMarcarPagada.setBackground(new Color(25, 135, 84));
            btnMarcarPagada.setForeground(Color.WHITE);
            btnMarcarPagada.putClientProperty("JButton.buttonType", "roundRect");
            acciones.add(btnMarcarPagada);
            panel.add(acciones, BorderLayout.SOUTH);
        }

        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(400, 0));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                "Registrar multa manual",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(73, 80, 87)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        JLabel lblInfo = new JLabel("<html>Use este formulario para daños como hojas arrancadas, manchas u otros incidentes.</html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(73, 80, 87));
        gbc.gridy = row++;
        panel.add(lblInfo, gbc);

        gbc.gridy = row++;
        panel.add(new JLabel("ID del préstamo:"), gbc);
        txtIdPrestamo = new JTextField();
        gbc.gridy = row++;
        panel.add(txtIdPrestamo, gbc);

        chkLibroInutilizable = new JCheckBox("Libro inutilizable o perdido");
        chkLibroInutilizable.setOpaque(false);
        chkLibroInutilizable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = row++;
        panel.add(chkLibroInutilizable, gbc);

        gbc.gridy = row++;
        panel.add(new JLabel("ID del libro afectado:"), gbc);
        txtIdLibroAfectado = new JTextField();
        txtIdLibroAfectado.setEnabled(false);
        gbc.gridy = row++;
        panel.add(txtIdLibroAfectado, gbc);

        gbc.gridy = row++;
        panel.add(new JLabel("Monto:"), gbc);
        txtMonto = new JTextField();
        gbc.gridy = row++;
        panel.add(txtMonto, gbc);

        gbc.gridy = row++;
        panel.add(new JLabel("Motivo:"), gbc);
        txtMotivo = new JTextArea(8, 28);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        scrollMotivo.setPreferredSize(new Dimension(340, 150));
        scrollMotivo.setMinimumSize(new Dimension(300, 130));
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.45;
        panel.add(scrollMotivo, gbc);

        btnRegistrarMulta = new JButton("Registrar multa");
        btnRegistrarMulta.setBackground(new Color(13, 110, 253));
        btnRegistrarMulta.setForeground(Color.WHITE);
        btnRegistrarMulta.setHorizontalAlignment(SwingConstants.CENTER);
        btnRegistrarMulta.putClientProperty("JButton.buttonType", "roundRect");
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        panel.add(btnRegistrarMulta, gbc);

        gbc.gridy = row;
        gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);

        return panel;
    }

    public void recargarMultas() {
        ListaEnlazada<Multa> multas = modoGestion
                ? multaService.listarTodas()
                : multaService.obtenerMultasPorEstudiante(usuarioLogueado.getId());
        modeloMultas.setRowCount(0);
        String filtro = comboFiltroEstado != null ? comboFiltroEstado.getSelectedItem().toString() : "TODAS";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ArrayList<Multa> multasFiltradas = new ArrayList<>();

        for (int i = 0; i < multas.size(); i++) {
            Multa multa = multas.obtener(i);
            String estado = multa.getEstado() != null ? multa.getEstado() : "";
            if (!modoGestion && !mostrarHistorialCompletoMultas && "PAGADA".equalsIgnoreCase(estado)) {
                continue;
            }
            if (!"TODAS".equals(filtro) && !filtro.equalsIgnoreCase(estado)) {
                continue;
            }
            multasFiltradas.add(multa);
        }

        if (!modoGestion) {
            ordenarMultasEstudiante(multasFiltradas);
        }

        for (Multa multa : multasFiltradas) {
            String estado = multa.getEstado() != null ? multa.getEstado() : "";

            Prestamo prestamo = multa.getPrestamo();
            int idPrestamo = prestamo != null ? prestamo.getId() : 0;
            Optional<Prestamo> prestamoCompleto = prestamoController.buscarPrestamoPorId(idPrestamo);
            String estudiante = prestamoCompleto
                    .map(Prestamo::getEstudiante)
                    .map(e -> e != null ? e.getNombreCompleto() : "Desconocido")
                    .orElse("Desconocido");

            modeloMultas.addRow(new Object[]{
                multa.getId(),
                idPrestamo,
                estudiante,
                String.format("S/. %.2f", multa.getMonto()),
                multa.getMotivo(),
                estado,
                multa.getFechaCreacion() != null ? multa.getFechaCreacion().format(formatter) : ""
            });
        }

        if (lblMensajeMultas != null && !modoGestion) {
            boolean sinResultados = modeloMultas.getRowCount() == 0;
            lblMensajeMultas.setText(mostrarHistorialCompletoMultas
                    ? "No tienes multas registradas para el filtro seleccionado."
                    : "No tienes ninguna multa pendiente.");
            lblMensajeMultas.setVisible(sinResultados);
        }
    }

    private void ordenarMultasEstudiante(ArrayList<Multa> multas) {
        String orden = cbOrdenMultas != null ? cbOrdenMultas.getSelectedItem().toString() : "Fecha reciente";
        Comparator<Multa> comparador = switch (orden) {
            case "Fecha antigua" -> Comparator.comparing(
                    Multa::getFechaCreacion,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "Monto mayor" -> Comparator.comparingDouble(Multa::getMonto).reversed();
            case "Monto menor" -> Comparator.comparingDouble(Multa::getMonto);
            default -> Comparator.comparing(
                    Multa::getFechaCreacion,
                    Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        };
        multas.sort(comparador);
    }

    private void registrarMultaManual() {
        try {
            int idPrestamo = Integer.parseInt(txtIdPrestamo.getText().trim());
            double monto = Double.parseDouble(txtMonto.getText().trim());
            String motivo = txtMotivo.getText().trim();
            Integer idLibroAfectado = null;

            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor que cero.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el motivo de la multa.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (chkLibroInutilizable.isSelected()) {
                String idLibroTexto = txtIdLibroAfectado.getText().trim();
                if (idLibroTexto.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingrese el ID del libro afectado.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                idLibroAfectado = Integer.parseInt(idLibroTexto);
            }

            Optional<Prestamo> prestamoOpt = prestamoController.buscarPrestamoPorId(idPrestamo);
            if (prestamoOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No existe un préstamo con el ID indicado.", "Préstamo no encontrado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (idLibroAfectado != null && !prestamoContieneLibro(idPrestamo, idLibroAfectado)) {
                JOptionPane.showMessageDialog(this, "El libro afectado no pertenece al préstamo indicado.", "Libro no asociado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (idLibroAfectado != null) {
                Optional<Libro> libroOpt = libroService.buscarLibroPorId(idLibroAfectado);
                if (libroOpt.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No existe un libro con el ID afectado.", "Libro no encontrado", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (libroOpt.get().getStock() <= 0) {
                    JOptionPane.showMessageDialog(this, "El libro afectado no tiene stock disponible para descontar.", "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Multa multa = new Multa();
            multa.setPrestamo(prestamoOpt.get());
            multa.setMonto(monto);
            multa.setMotivo(construirMotivo(motivo, idLibroAfectado));
            multa.setEstado("PENDIENTE");

            if (registrarMultaYActualizarStock(multa, idLibroAfectado)) {
                String mensaje = idLibroAfectado != null
                        ? "Multa registrada correctamente. El stock del libro afectado fue descontado."
                        : "Multa registrada correctamente.";
                JOptionPane.showMessageDialog(this, mensaje);
                limpiarFormulario();
                recargarMultas();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar la multa.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del préstamo y el monto deben ser numéricos.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void marcarMultaPagada() {
        int selectedRow = tablaMultas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una multa de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tablaMultas.convertRowIndexToModel(selectedRow);
        int idMulta = Integer.parseInt(modeloMultas.getValueAt(modelRow, 0).toString());
        String estado = modeloMultas.getValueAt(modelRow, 5).toString();

        if ("PAGADA".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this, "La multa seleccionada ya está pagada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Marcar esta multa como pagada?",
                "Confirmar pago",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (multaService.pagarMulta(idMulta)) {
            JOptionPane.showMessageDialog(this, "Pago registrado correctamente.");
            recargarMultas();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar la multa.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtIdPrestamo.setText("");
        txtIdLibroAfectado.setText("");
        txtIdLibroAfectado.setEnabled(false);
        chkLibroInutilizable.setSelected(false);
        txtMonto.setText("");
        txtMotivo.setText("");
    }

    private boolean prestamoContieneLibro(int idPrestamo, int idLibro) {
        ListaEnlazada<DetallePrestamo> detalles = prestamoController.obtenerDetallesPrestamo(idPrestamo);
        for (int i = 0; i < detalles.size(); i++) {
            DetallePrestamo detalle = detalles.obtener(i);
            if (detalle.getLibro() != null && detalle.getLibro().getId() == idLibro) {
                return true;
            }
        }
        return false;
    }

    private String construirMotivo(String motivo, Integer idLibroAfectado) {
        if (idLibroAfectado == null) {
            return motivo;
        }
        return motivo + " | Libro inutilizable/perdido ID: " + idLibroAfectado;
    }

    private boolean registrarMultaYActualizarStock(Multa multa, Integer idLibroAfectado) {
        try {
            ConexionBD.beginTransaction();

            if (!multaService.registrarMulta(multa)) {
                throw new java.sql.SQLException("No se pudo registrar la multa.");
            }
            if (idLibroAfectado != null && !libroService.descontarUnidadStock(idLibroAfectado)) {
                throw new java.sql.SQLException("No se pudo descontar el stock del libro afectado.");
            }

            ConexionBD.commit();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Error al registrar multa manual: " + e.getMessage());
            ConexionBD.rollback();
            return false;
        } finally {
            ConexionBD.endTransaction();
        }
    }
}
