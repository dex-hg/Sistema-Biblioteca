package biblioteca.vista;

import biblioteca.controlador.LibroController;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import biblioteca.estructuras.ListaEnlazada;

public class GestionLibrosPanel extends JPanel {

    private final Usuario usuarioLogueado;
    private final LibroController libroController;

    // Componentes del Formulario
    private JTextField txtId;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JSpinner spinStock;
    private JButton btnRegistrar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Componentes de la Tabla y Filtros
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JComboBox<String> cbCriterioOrden;
    private JComboBox<String> cbMetodoBusqueda; // "Búsqueda Parcial (SQL)", "Búsqueda Binaria (Exacta)"
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;

    private ListaEnlazada<Libro> listaLibrosEnTabla = new ListaEnlazada<>();

    public GestionLibrosPanel(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.libroController = new LibroController();
        inicializarUI();
        actualizarTabla();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);

        // Título de la Sección
        JLabel lblTituloSeccion = new JLabel("Catálogo y Gestión de Libros");
        lblTituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloSeccion.setForeground(new Color(33, 37, 41));
        add(lblTituloSeccion, BorderLayout.NORTH);

        // Determinar si es modo Lectura (Estudiante = Rol ID 3)
        boolean esEstudiante = (usuarioLogueado.getRol() != null && usuarioLogueado.getRol().getId() == 3);

        // Panel de Operaciones (CRUD) - Solo se añade si no es estudiante
        if (!esEstudiante) {
            JPanel panelFormulario = crearPanelFormulario();
            add(panelFormulario, BorderLayout.WEST);
        }

        // Panel Central (Tabla, Ordenamiento y Búsqueda)
        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(320, 0));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1, true),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        JLabel lblForm = new JLabel("Detalles del Libro");
        lblForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblForm.setForeground(new Color(73, 80, 87));
        gbc.gridy = row++;
        form.add(lblForm, gbc);

        // Campo ID
        JLabel lblId = new JLabel("ID del Libro (Solo lectura)");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblId.setForeground(new Color(108, 117, 125));
        gbc.gridy = row++;
        form.add(lblId, gbc);

        txtId = new JTextField();
        txtId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtId.setEditable(false);
        txtId.setBackground(new Color(248, 249, 250));
        gbc.gridy = row++;
        form.add(txtId, gbc);

        // Campo Título
        JLabel lblTitulo = new JLabel("Título del Libro *");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(new Color(73, 80, 87));
        gbc.gridy = row++;
        form.add(lblTitulo, gbc);

        txtTitulo = new JTextField();
        txtTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTitulo.putClientProperty("JTextField.placeholderText", "Ingrese el título");
        gbc.gridy = row++;
        form.add(txtTitulo, gbc);

        // Campo Autor
        JLabel lblAutor = new JLabel("Autor *");
        lblAutor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblAutor.setForeground(new Color(73, 80, 87));
        gbc.gridy = row++;
        form.add(lblAutor, gbc);

        txtAutor = new JTextField();
        txtAutor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAutor.putClientProperty("JTextField.placeholderText", "Ingrese el autor");
        gbc.gridy = row++;
        form.add(txtAutor, gbc);

        // Campo Stock
        JLabel lblStock = new JLabel("Stock Disponible *");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStock.setForeground(new Color(73, 80, 87));
        gbc.gridy = row++;
        form.add(lblStock, gbc);

        spinStock = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        spinStock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = row++;
        form.add(spinStock, gbc);

        // Espaciador antes de botones
        gbc.insets = new Insets(15, 0, 6, 0);

        // Botón Registrar
        btnRegistrar = new JButton("Registrar Libro");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegistrar.setBackground(new Color(13, 110, 253));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.putClientProperty("JButton.buttonType", "roundRect");
        btnRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gbc.gridy = row++;
        form.add(btnRegistrar, gbc);

        // Botón Editar
        gbc.insets = new Insets(6, 0, 6, 0);
        btnEditar = new JButton("Actualizar Libro");
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEditar.setBackground(new Color(25, 135, 84));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.putClientProperty("JButton.buttonType", "roundRect");
        btnEditar.setEnabled(false);
        btnEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gbc.gridy = row++;
        form.add(btnEditar, gbc);

        // Botón Eliminar
        btnEliminar = new JButton("Eliminar Libro");
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminar.setEnabled(false);
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gbc.gridy = row++;
        form.add(btnEliminar, gbc);

        // Botón Limpiar
        btnLimpiar = new JButton("Limpiar Formulario");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gbc.gridy = row++;
        form.add(btnLimpiar, gbc);

        // Llenar espacio restante
        gbc.weighty = 1.0;
        gbc.gridy = row++;
        form.add(new JLabel(), gbc);

        // Eventos
        btnRegistrar.addActionListener(e -> registrarLibro());
        btnEditar.addActionListener(e -> actualizarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        return form;
    }

    private JPanel crearPanelCentral() {
        JPanel central = new JPanel(new BorderLayout(15, 15));
        central.setOpaque(false);

        // Barra Superior de Búsqueda y Ordenamiento
        JPanel barraFiltros = new JPanel(new GridBagLayout());
        barraFiltros.setBackground(Color.WHITE);
        barraFiltros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridy = 0;

        // Búsqueda
        gbc.gridx = 0;
        barraFiltros.add(new JLabel("Buscar por Título:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Escriba el título...");
        barraFiltros.add(txtBuscar, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        cbMetodoBusqueda = new JComboBox<>(new String[]{"Coincidencia SQL", "Búsqueda Binaria (Exacta)"});
        cbMetodoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        barraFiltros.add(cbMetodoBusqueda, gbc);

        gbc.gridx = 3;
        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBuscar.setBackground(new Color(33, 37, 41));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.putClientProperty("JButton.buttonType", "roundRect");
        btnBuscar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        barraFiltros.add(btnBuscar, gbc);

        // Ordenamiento
        gbc.gridx = 4;
        barraFiltros.add(new JLabel("Ordenar por:"), gbc);

        gbc.gridx = 5;
        cbCriterioOrden = new JComboBox<>(new String[]{"Título (A-Z)", "Stock (Ascendente)"});
        cbCriterioOrden.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        barraFiltros.add(cbCriterioOrden, gbc);

        central.add(barraFiltros, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Título", "Autor", "Stock Disponible"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Deshabilitar edición directa de celdas
            }
        };

        tablaLibros = new JTable(modeloTabla);
        tablaLibros.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaLibros.setRowHeight(25);
        tablaLibros.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaLibros.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tablaLibros);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        central.add(scroll, BorderLayout.CENTER);

        // Listeners y Eventos del panel central
        btnBuscar.addActionListener(e -> buscarLibro());
        txtBuscar.addActionListener(e -> buscarLibro());
        cbCriterioOrden.addActionListener(e -> ordenarLibros());

        // Evento al seleccionar fila
        tablaLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaLibros.getSelectedRow() != -1) {
                cargarFilaSeleccionada();
            }
        });

        return central;
    }

    public void actualizarTabla() {
        listaLibrosEnTabla = libroController.obtenerTodos();
        llenarTabla(listaLibrosEnTabla);
    }

    private void llenarTabla(ListaEnlazada<Libro> libros) {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < libros.size(); i++) {
            Libro l = libros.obtener(i);
            modeloTabla.addRow(new Object[]{
                l.getId(),
                l.getTitulo(),
                l.getAutor(),
                l.getStock()
            });
        }
    }

    private void buscarLibro() {
        String query = txtBuscar.getText().trim();
        String metodo = (String) cbMetodoBusqueda.getSelectedItem();

        if ("Búsqueda Binaria (Exacta)".equals(metodo)) {
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "Ingrese el título exacto para realizar la Búsqueda Binaria.", 
                        "Búsqueda Vacía", 
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Optional<Libro> libroOpt = libroController.buscarPorTituloBinario(query);
            if (libroOpt.isPresent()) {
                listaLibrosEnTabla = new ListaEnlazada<>();
                listaLibrosEnTabla.agregar(libroOpt.get());
                llenarTabla(listaLibrosEnTabla);
            } else {
                modeloTabla.setRowCount(0);
                JOptionPane.showMessageDialog(this, 
                        "No se encontró ningún libro con el título exacto: \"" + query + "\"", 
                        "Sin Resultados", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // SQL LIKE parcial
            listaLibrosEnTabla = libroController.buscarPorTitulo(query);
            llenarTabla(listaLibrosEnTabla);
        }
    }

    private void ordenarLibros() {
        String criterioCombo = (String) cbCriterioOrden.getSelectedItem();
        String criterio = "Título";
        if (criterioCombo != null && criterioCombo.contains("Stock")) {
            criterio = "Stock";
        }
        listaLibrosEnTabla = libroController.ordenarLibros(listaLibrosEnTabla, criterio);
        llenarTabla(listaLibrosEnTabla);
    }

    private void registrarLibro() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        int stock = (int) spinStock.getValue();

        if (titulo.isEmpty() || autor.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Por favor complete el título y el autor.", 
                    "Campos Vacíos", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exito = libroController.registrarLibro(titulo, autor, stock);
        if (exito) {
            JOptionPane.showMessageDialog(this, "Libro registrado exitosamente.");
            limpiarFormulario();
            actualizarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el libro. Verifique los datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarLibro() {
        if (txtId.getText().isEmpty()) return;
        int id = Integer.parseInt(txtId.getText());
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        int stock = (int) spinStock.getValue();

        if (titulo.isEmpty() || autor.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Por favor complete el título y el autor.", 
                    "Campos Vacíos", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exito = libroController.actualizarLibro(id, titulo, autor, stock);
        if (exito) {
            JOptionPane.showMessageDialog(this, "Libro actualizado correctamente.");
            limpiarFormulario();
            actualizarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarLibro() {
        if (txtId.getText().isEmpty()) return;
        int id = Integer.parseInt(txtId.getText());

        int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar este libro?", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = libroController.eliminarLibro(id);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.");
                limpiarFormulario();
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "No se puede eliminar el libro. Podría estar asociado a préstamos existentes.", 
                        "Error de Eliminación", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarFilaSeleccionada() {
        int selectedRow = tablaLibros.getSelectedRow();
        if (selectedRow != -1 && txtId != null) {
            txtId.setText(tablaLibros.getValueAt(selectedRow, 0).toString());
            txtTitulo.setText(tablaLibros.getValueAt(selectedRow, 1).toString());
            txtAutor.setText(tablaLibros.getValueAt(selectedRow, 2).toString());
            spinStock.setValue(Integer.parseInt(tablaLibros.getValueAt(selectedRow, 3).toString()));

            btnRegistrar.setEnabled(false);
            btnEditar.setEnabled(true);
            btnEliminar.setEnabled(true);
        }
    }

    private void limpiarFormulario() {
        if (txtId != null) {
            txtId.setText("");
            txtTitulo.setText("");
            txtAutor.setText("");
            spinStock.setValue(1);

            btnRegistrar.setEnabled(true);
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }
        tablaLibros.clearSelection();
    }
}
