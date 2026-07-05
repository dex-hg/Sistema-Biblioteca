package biblioteca.vista;

import biblioteca.controlador.LoginController;
import biblioteca.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginFrame extends JFrame {

    private final LoginController loginController;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        this.loginController = new LoginController();
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Sistema de Biblioteca - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 500);
        setLocationRelativeTo(null); // Centrar en pantalla

        // Panel Principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Panel de Título (Encabezado)
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setOpaque(false);

        JLabel lblLogo = new JLabel("📚", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        panelTitulo.add(lblLogo, BorderLayout.NORTH);

        JLabel lblTitulo = new JLabel("BIBLIOTECA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(33, 37, 41));
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);

        JLabel lblSubtitulo = new JLabel("Acceso al Sistema", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(108, 117, 125));
        panelTitulo.add(lblSubtitulo, BorderLayout.SOUTH);

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);

        // Panel de Formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        // Campo Usuario
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsuario.setForeground(new Color(73, 80, 87));
        gbc.gridy = 0;
        panelFormulario.add(lblUsuario, gbc);

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(300, 35));
        txtUsuario.putClientProperty("JTextField.placeholderText", "Ingrese su nombre de usuario");
        txtUsuario.putClientProperty("JTextField.showClearButton", true);
        gbc.gridy = 1;
        panelFormulario.add(txtUsuario, gbc);

        // Campo Contraseña
        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(new Color(73, 80, 87));
        gbc.gridy = 2;
        panelFormulario.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(300, 35));
        txtPassword.putClientProperty("JTextField.placeholderText", "Ingrese su contraseña");
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);
        gbc.gridy = 3;
        panelFormulario.add(txtPassword, gbc);

        // Espaciador antes del botón
        gbc.insets = new Insets(24, 0, 8, 0);

        // Botón Login
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(13, 110, 253));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(300, 40));
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        gbc.gridy = 4;
        panelFormulario.add(btnLogin, gbc);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        add(panelPrincipal);

        // Eventos
        btnLogin.addActionListener(e -> realizarLogin());
        txtUsuario.addActionListener(e -> realizarLogin());
        txtPassword.addActionListener(e -> realizarLogin());
    }

    private void realizarLogin() {
        String username = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Por favor, complete todos los campos.", 
                    "Campos Vacíos", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Usuario> usuarioOpt = loginController.procesarLogin(username, password);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Abrir MainFrame con el usuario autenticado
            MainFrame mainFrame = new MainFrame(usuario);
            mainFrame.setVisible(true);
            this.dispose(); // Cerrar login
        } else {
            JOptionPane.showMessageDialog(this, 
                    "Usuario o contraseña incorrectos.", 
                    "Error de Autenticación", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
