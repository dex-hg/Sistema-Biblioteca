package biblioteca.vista;

import biblioteca.modelo.Usuario;
import biblioteca.servicios.LibroService;
import biblioteca.servicios.MultaService;
import biblioteca.servicios.PrestamoService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {

    private final Usuario usuarioLogueado;
    private JPanel panelLateral;
    private JPanel panelContenido;
    private CardLayout cardLayout;

    // Paneles de contenido
    private JPanel panelDashboard;
    private GestionLibrosPanel panelLibros;
    private PrestamosPanel panelPrestamos;
    private ReportesPanel panelReportes;

    // Servicios para el Dashboard
    private final LibroService libroService;
    private final PrestamoService prestamoService;
    private final MultaService multaService;

    // Etiquetas del Dashboard
    private JLabel lblTotalLibrosVal;
    private JLabel lblPrestamosActivosVal;
    private JLabel lblMultasPendientesVal;

    public MainFrame(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.libroService = new LibroService();
        this.prestamoService = new PrestamoService();
        this.multaService = new MultaService();
        inicializarUI();
        cargarEstadisticasDashboard();
    }

    private void inicializarUI() {
        setTitle("Sistema de Gestión de Biblioteca - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizado por defecto
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        JPanel panelRaiz = new JPanel(new BorderLayout());
        panelRaiz.setBackground(new Color(245, 247, 250));

        // 1. MENU LATERAL
        panelLateral = crearPanelLateral();
        panelRaiz.add(panelLateral, BorderLayout.WEST);

        // 2. PANEL DE CONTENIDO (CardLayout)
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelContenido.setOpaque(false);

        // Crear instancias de paneles
        panelDashboard = crearPanelDashboard();
        panelLibros = new GestionLibrosPanel(usuarioLogueado);
        
        // Agregar paneles al CardLayout
        panelContenido.add(panelDashboard, "DASHBOARD");
        panelContenido.add(panelLibros, "LIBROS");

        // Paneles condicionales según el rol (Administrador = 1, Bibliotecario = 2, Estudiante = 3)
        int rolId = (usuarioLogueado.getRol() != null) ? usuarioLogueado.getRol().getId() : 3;
        if (rolId == 1 || rolId == 2) {
            panelPrestamos = new PrestamosPanel(usuarioLogueado);
            panelContenido.add(panelPrestamos, "PRESTAMOS");
        }
        if (rolId == 1) {
            panelReportes = new ReportesPanel();
            panelContenido.add(panelReportes, "REPORTES");
        }

        panelRaiz.add(panelContenido, BorderLayout.CENTER);
        add(panelRaiz);

        // Mostrar por defecto el Dashboard
        cardLayout.show(panelContenido, "DASHBOARD");
    }

    private JPanel crearPanelLateral() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(new Color(33, 37, 41)); // Gris oscuro moderno

        // Encabezado del menú
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel lblBrandIcon = new JLabel("📚 ", SwingConstants.LEFT);
        lblBrandIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        lblBrandIcon.setForeground(Color.WHITE);
        
        JLabel lblBrandTitle = new JLabel("BIBLIO-SYS", SwingConstants.LEFT);
        lblBrandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBrandTitle.setForeground(Color.WHITE);

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        brandPanel.setOpaque(false);
        brandPanel.add(lblBrandIcon);
        brandPanel.add(lblBrandTitle);

        header.add(brandPanel, BorderLayout.NORTH);

        JLabel lblUser = new JLabel(usuarioLogueado.getNombreCompleto());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(new Color(222, 226, 230));
        lblUser.setBorder(BorderFactory.createEmptyBorder(15, 0, 2, 0));

        String rolText = switch ((usuarioLogueado.getRol() != null) ? usuarioLogueado.getRol().getId() : 3) {
            case 1 -> "Administrador";
            case 2 -> "Bibliotecario";
            default -> "Estudiante";
        };
        JLabel lblRol = new JLabel("Rol: " + rolText);
        lblRol.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRol.setForeground(new Color(173, 181, 189));

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);
        userPanel.add(lblUser, BorderLayout.NORTH);
        userPanel.add(lblRol, BorderLayout.SOUTH);
        header.add(userPanel, BorderLayout.CENTER);

        sidebar.add(header, BorderLayout.NORTH);

        // Cuerpo del menú (Botones de navegación)
        JPanel menu = new JPanel(new GridBagLayout());
        menu.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 15, 4, 15);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        JButton btnHome = crearBotonMenu("Dashboard", "🏠");
        gbc.gridy = row++;
        menu.add(btnHome, gbc);

        JButton btnLib = crearBotonMenu("Catálogo de Libros", "📘");
        gbc.gridy = row++;
        menu.add(btnLib, gbc);

        int rolId = (usuarioLogueado.getRol() != null) ? usuarioLogueado.getRol().getId() : 3;
        JButton btnPres = null;
        if (rolId == 1 || rolId == 2) {
            btnPres = crearBotonMenu("Préstamos y Dev.", "🔄");
            gbc.gridy = row++;
            menu.add(btnPres, gbc);
        }

        JButton btnRep = null;
        if (rolId == 1) {
            btnRep = crearBotonMenu("Reportes", "📈");
            gbc.gridy = row++;
            menu.add(btnRep, gbc);
        }

        // Espaciador vertical
        gbc.gridy = row++;
        gbc.weighty = 1.0;
        menu.add(new JLabel(), gbc);

        sidebar.add(menu, BorderLayout.CENTER);

        // Pie del menú (Cerrar Sesión)
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 15, 30, 15));

        JButton btnCerrar = crearBotonMenu("Cerrar Sesión", "🚪");
        btnCerrar.setBackground(new Color(220, 53, 69));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        footer.add(btnCerrar, BorderLayout.SOUTH);

        sidebar.add(footer, BorderLayout.SOUTH);

        // Eventos de Navegación
        btnHome.addActionListener(e -> {
            cargarEstadisticasDashboard();
            cardLayout.show(panelContenido, "DASHBOARD");
        });
        btnLib.addActionListener(e -> {
            panelLibros.actualizarTabla();
            cardLayout.show(panelContenido, "LIBROS");
        });
        if (btnPres != null) {
            btnPres.addActionListener(e -> {
                panelPrestamos.actualizarTablaPrestamos();
                cardLayout.show(panelContenido, "PRESTAMOS");
            });
        }
        if (btnRep != null) {
            btnRep.addActionListener(e -> {
                panelReportes.recargarReportes();
                cardLayout.show(panelContenido, "REPORTES");
            });
        }
        btnCerrar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Está seguro de cerrar sesión?", 
                    "Confirmar Salida", 
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });

        return sidebar;
    }

    private JButton crearBotonMenu(String texto, String icono) {
        JButton btn = new JButton(icono + "  " + texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(248, 249, 250));
        btn.setBackground(new Color(52, 58, 64));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        return btn;
    }

    private JPanel crearPanelDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(20, 20));
        dashboard.setOpaque(false);

        // Mensaje de Bienvenida superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel lblGreeting = new JLabel("¡Bienvenido, " + usuarioLogueado.getNombreCompleto() + "!");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblGreeting.setForeground(new Color(33, 37, 41));
        
        JLabel lblDate = new JLabel("Hoy es " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy")));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDate.setForeground(new Color(108, 117, 125));

        topPanel.add(lblGreeting, BorderLayout.NORTH);
        topPanel.add(lblDate, BorderLayout.SOUTH);
        dashboard.add(topPanel, BorderLayout.NORTH);

        // Tarjetas Estadísticas e Indicadores (Grid de 3 columnas)
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 10, 0, 10);

        // Tarjeta 1: Total Libros
        JPanel cardLibros = crearCardEstadistica("Total Libros en Sistema", "0", "📘", new Color(13, 110, 253));
        lblTotalLibrosVal = (JLabel) cardLibros.getClientProperty("valueLabel");
        gbc.gridx = 0;
        statsPanel.add(cardLibros, gbc);

        // Tarjeta 2: Préstamos Activos
        JPanel cardPrestamos = crearCardEstadistica("Préstamos Activos", "0", "🔄", new Color(25, 135, 84));
        lblPrestamosActivosVal = (JLabel) cardPrestamos.getClientProperty("valueLabel");
        gbc.gridx = 1;
        statsPanel.add(cardPrestamos, gbc);

        // Tarjeta 3: Multas Pendientes
        JPanel cardMultas = crearCardEstadistica("Multas Pendientes", "0", "⚠️", new Color(220, 53, 69));
        lblMultasPendientesVal = (JLabel) cardMultas.getClientProperty("valueLabel");
        gbc.gridx = 2;
        statsPanel.add(cardMultas, gbc);

        dashboard.add(statsPanel, BorderLayout.CENTER);

        // PANEL DE ACCESOS RÁPIDOS
        JPanel quickAccessPanel = new JPanel(new BorderLayout(10, 10));
        quickAccessPanel.setOpaque(false);
        quickAccessPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1, true),
                "Accesos Rápidos del Sistema",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(73, 80, 87)
        ));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonsPanel.setOpaque(false);

        int rolId = (usuarioLogueado.getRol() != null) ? usuarioLogueado.getRol().getId() : 3;

        if (rolId == 1 || rolId == 2) {
            JButton btnGoPrestamos = crearBotonAccesoRapido("Registrar Préstamo", "➕   Realizar un préstamo de libros a estudiantes.");
            btnGoPrestamos.addActionListener(e -> {
                panelPrestamos.actualizarTablaPrestamos();
                cardLayout.show(panelContenido, "PRESTAMOS");
            });
            buttonsPanel.add(btnGoPrestamos);
        }

        JButton btnGoCatalogo = crearBotonAccesoRapido("Ver Catálogo", "🔍   Buscar, filtrar y ordenar libros.");
        btnGoCatalogo.addActionListener(e -> {
            panelLibros.actualizarTabla();
            cardLayout.show(panelContenido, "LIBROS");
        });
        buttonsPanel.add(btnGoCatalogo);

        if (rolId == 1) {
            JButton btnGoReportes = crearBotonAccesoRapido("Ver Reportes", "📊   Análisis de préstamos y estadísticas.");
            btnGoReportes.addActionListener(e -> {
                panelReportes.recargarReportes();
                cardLayout.show(panelContenido, "REPORTES");
            });
            buttonsPanel.add(btnGoReportes);
        }

        quickAccessPanel.add(buttonsPanel, BorderLayout.CENTER);
        dashboard.add(quickAccessPanel, BorderLayout.SOUTH);

        return dashboard;
    }

    private JPanel crearCardEstadistica(String titulo, String valor, String icono, Color colorBorde) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, colorBorde),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblIcon = new JLabel(icono);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        lblIcon.setForeground(colorBorde);
        card.add(lblIcon, BorderLayout.EAST);

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(108, 117, 125));
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblVal.setForeground(new Color(33, 37, 41));
        card.add(lblVal, BorderLayout.CENTER);

        // Guardar la etiqueta del valor como propiedad cliente para poder actualizarla
        card.putClientProperty("valueLabel", lblVal);

        return card;
    }

    private JButton crearBotonAccesoRapido(String titulo, String descripcion) {
        JButton btn = new JButton("<html><b>" + titulo + "</b><br><font size='3' color='#6c757d'>" + descripcion + "</font></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(300, 70));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(33, 37, 41));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        return btn;
    }

    private void cargarEstadisticasDashboard() {
        new Thread(() -> {
            try {
                // Obtener datos
                int totalLibros = libroService.buscarPorTitulo("").size();
                int prestamosActivos = prestamoService.listarPrestamosActivos().size();
                
                // Para las multas, calculamos sumando las pendientes de todos los estudiantes
                int multasPendientes = 0;
                // Obtenemos todos los estudiantes de la base de datos
                var estudiantes = new biblioteca.servicios.EstudianteService().listarTodos();
                for (int i = 0; i < estudiantes.size(); i++) {
                    var est = estudiantes.obtener(i);
                    multasPendientes += multaService.obtenerMultasPendientes(est.getId()).size();
                }

                final int finalLibros = totalLibros;
                final int finalActivos = prestamosActivos;
                final int finalMultas = multasPendientes;

                // Actualizar interfaz gráfica en el hilo EDT
                java.awt.EventQueue.invokeLater(() -> {
                    if (lblTotalLibrosVal != null) {
                        lblTotalLibrosVal.setText(String.valueOf(finalLibros));
                    }
                    if (lblPrestamosActivosVal != null) {
                        lblPrestamosActivosVal.setText(String.valueOf(finalActivos));
                    }
                    if (lblMultasPendientesVal != null) {
                        lblMultasPendientesVal.setText(String.valueOf(finalMultas));
                    }
                });
            } catch (Exception e) {
                System.out.println("Error al cargar estadísticas en el Dashboard: " + e.getMessage());
            }
        }).start();
    }
}
