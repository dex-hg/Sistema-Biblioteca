package biblioteca.vista;

import biblioteca.modelo.Usuario;
import biblioteca.servicios.LibroService;
import biblioteca.servicios.MultaService;
import biblioteca.servicios.PrestamoService;
import biblioteca.modelo.EstadoPrestamo;
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
    private MultasPanel panelMultas;
    private ReportesPanel panelReportes;

    // Servicios para el Dashboard
    private final LibroService libroService;
    private final PrestamoService prestamoService;
    private final MultaService multaService;

    // Etiquetas del Dashboard
    private JLabel lblTotalLibrosVal;
    private JLabel lblPrestamosActivosVal;
    private JLabel lblMultasPendientesVal;
    private JLabel lblAvisoPrestamosVal;
    private JLabel lblAvisoAtrasadosVal;
    private JLabel lblAvisoMultasVal;

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

        // Paneles según el rol (Administrador = 1, Bibliotecario = 2, Estudiante = 3)
        int rolId = (usuarioLogueado.getRol() != null) ? usuarioLogueado.getRol().getId() : 3;
        panelPrestamos = new PrestamosPanel(usuarioLogueado);
        panelMultas = new MultasPanel(usuarioLogueado);
        panelContenido.add(panelPrestamos, "PRESTAMOS");
        panelContenido.add(panelMultas, "MULTAS");
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

        JLabel lblBrandIcon = new JLabel("B", SwingConstants.LEFT);
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

        JButton btnHome = crearBotonMenu("Dashboard", "");
        gbc.gridy = row++;
        menu.add(btnHome, gbc);

        JButton btnLib = crearBotonMenu("Catálogo de Libros", "");
        gbc.gridy = row++;
        menu.add(btnLib, gbc);

        int rolId = (usuarioLogueado.getRol() != null) ? usuarioLogueado.getRol().getId() : 3;
        JButton btnPres = crearBotonMenu(rolId == 3 ? "Mis Préstamos" : "Préstamos y Dev.", "");
        gbc.gridy = row++;
        menu.add(btnPres, gbc);

        JButton btnMultas = crearBotonMenu(rolId == 3 ? "Mis Multas" : "Multas", "");
        gbc.gridy = row++;
        menu.add(btnMultas, gbc);

        JButton btnRep = null;
        if (rolId == 1) {
            btnRep = crearBotonMenu("Reportes", "");
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

        JButton btnCerrar = crearBotonMenu("Cerrar Sesión", "");
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
        btnPres.addActionListener(e -> {
            cardLayout.show(panelContenido, "PRESTAMOS");
        });
        btnMultas.addActionListener(e -> {
            cardLayout.show(panelContenido, "MULTAS");
        });
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
        JButton btn = new JButton(texto);
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
        int rolId = (usuarioLogueado.getRol() != null) ? usuarioLogueado.getRol().getId() : 3;

        // Tarjeta 1: Total Libros
        JPanel cardLibros = crearCardEstadistica("Total Libros en Sistema", "0", "LIB", new Color(13, 110, 253));
        lblTotalLibrosVal = (JLabel) cardLibros.getClientProperty("valueLabel");
        gbc.gridx = 0;
        statsPanel.add(cardLibros, gbc);

        // Tarjeta 2: Préstamos Activos
        JPanel cardPrestamos = crearCardEstadistica(
                rolId == 3 ? "Mis Préstamos Activos" : "Préstamos Activos",
                "0",
                "PRE",
                new Color(25, 135, 84));
        lblPrestamosActivosVal = (JLabel) cardPrestamos.getClientProperty("valueLabel");
        gbc.gridx = 1;
        statsPanel.add(cardPrestamos, gbc);

        // Tarjeta 3: Multas Pendientes
        JPanel cardMultas = crearCardEstadistica(
                rolId == 3 ? "Mis Multas Pendientes" : "Multas Pendientes",
                "0",
                "MUL",
                new Color(220, 53, 69));
        lblMultasPendientesVal = (JLabel) cardMultas.getClientProperty("valueLabel");
        gbc.gridx = 2;
        statsPanel.add(cardMultas, gbc);

        dashboard.add(statsPanel, BorderLayout.CENTER);

        JPanel avisosPanel = new JPanel(new GridBagLayout());
        avisosPanel.setOpaque(false);
        avisosPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1, true),
                "Avisos operativos",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(73, 80, 87)
        ));

        GridBagConstraints gbcAvisos = new GridBagConstraints();
        gbcAvisos.fill = GridBagConstraints.HORIZONTAL;
        gbcAvisos.insets = new Insets(8, 15, 8, 15);
        gbcAvisos.gridx = 0;
        gbcAvisos.weightx = 1.0;

        lblAvisoPrestamosVal = crearEtiquetaAviso("Cargando préstamos activos...");
        gbcAvisos.gridy = 0;
        avisosPanel.add(lblAvisoPrestamosVal, gbcAvisos);

        lblAvisoAtrasadosVal = crearEtiquetaAviso("Cargando préstamos atrasados...");
        gbcAvisos.gridy = 1;
        avisosPanel.add(lblAvisoAtrasadosVal, gbcAvisos);

        lblAvisoMultasVal = crearEtiquetaAviso("Cargando multas pendientes...");
        gbcAvisos.gridy = 2;
        avisosPanel.add(lblAvisoMultasVal, gbcAvisos);

        dashboard.add(avisosPanel, BorderLayout.SOUTH);

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
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
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

    private JLabel crearEtiquetaAviso(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(73, 80, 87));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(13, 110, 253)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        return label;
    }

    private void cargarEstadisticasDashboard() {
        new Thread(() -> {
            try {
                // Obtener datos
                int totalLibros = libroService.buscarPorTitulo("").size();
                int rolId = (usuarioLogueado.getRol() != null) ? usuarioLogueado.getRol().getId() : 3;
                var prestamosBase = rolId == 3
                        ? prestamoService.listarPrestamosPorEstudiante(usuarioLogueado.getId())
                        : prestamoService.listarPrestamosActivos();
                int prestamosActivos = 0;
                int prestamosAtrasados = 0;
                for (int i = 0; i < prestamosBase.size(); i++) {
                    var prestamo = prestamosBase.obtener(i);
                    if (prestamo.getEstado() == EstadoPrestamo.ACTIVO || prestamo.getEstado() == EstadoPrestamo.ATRASADO) {
                        prestamosActivos++;
                    }
                    if (prestamo.getEstado() == EstadoPrestamo.ATRASADO) {
                        prestamosAtrasados++;
                    }
                }
                
                int multasPendientes = 0;
                double montoMultasAcumuladas = 0.0;
                if (rolId == 3) {
                    var multasEstudiante = multaService.obtenerMultasPorEstudiante(usuarioLogueado.getId());
                    for (int i = 0; i < multasEstudiante.size(); i++) {
                        var multa = multasEstudiante.obtener(i);
                        montoMultasAcumuladas += multa.getMonto();
                        if ("PENDIENTE".equalsIgnoreCase(multa.getEstado())) {
                            multasPendientes++;
                        }
                    }
                } else {
                    var estudiantes = new biblioteca.servicios.EstudianteService().listarTodos();
                    for (int i = 0; i < estudiantes.size(); i++) {
                        var est = estudiantes.obtener(i);
                        multasPendientes += multaService.obtenerMultasPendientes(est.getId()).size();
                    }
                }

                final int finalLibros = totalLibros;
                final int finalActivos = prestamosActivos;
                final int finalMultas = multasPendientes;
                final int finalAtrasados = prestamosAtrasados;
                final int finalRolId = rolId;
                final int finalTotalPrestamosHistoricos = prestamosBase.size();
                final double finalMontoMultasAcumuladas = montoMultasAcumuladas;

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
                    if (lblAvisoPrestamosVal != null) {
                        lblAvisoPrestamosVal.setText("Préstamos no devueltos: " + finalActivos);
                    }
                    if (lblAvisoAtrasadosVal != null) {
                        lblAvisoAtrasadosVal.setText("Préstamos atrasados: " + finalAtrasados);
                    }
                    if (lblAvisoMultasVal != null) {
                        if (finalRolId == 3) {
                            lblAvisoMultasVal.setText(String.format("Monto total de multas acumuladas: S/. %.2f | Pendientes: %d",
                                    finalMontoMultasAcumuladas,
                                    finalMultas));
                        } else {
                            lblAvisoMultasVal.setText("Multas pendientes de pago: " + finalMultas);
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println("Error al cargar estadísticas en el Dashboard: " + e.getMessage());
            }
        }).start();
    }
}
