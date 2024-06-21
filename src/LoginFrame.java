import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private HashMap<String, Staff> staffMap;
    private HashMap<String, StoreItems> computers;
    private JLabel logoLabel;
    private ImageIcon customIcon;

    public LoginFrame(HashMap<String, Staff> staffMap, HashMap<String, StoreItems> computers) {
        this.staffMap = staffMap;
        this.computers = computers;

        setTitle("Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        // Loads and scale the logo image
        try {
            BufferedImage logoImage = ImageIO.read(new File("storeLogoImage.jpg"));
            Image scaledImage = logoImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledImage));
            customIcon = new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create components
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Computer Store Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.CYAN);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.BLACK);

        //Positioning:

        //Title
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleLabel, gbc);

        //Logo Label
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(logoLabel, gbc);

        //Username Label
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(usernameLabel, gbc);

        //username Field
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, gbc);

        //password Label
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, gbc);

        //Password Field
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(passwordField, gbc);

        //The Login Button
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        add(panel, new GridBagConstraints());

        // action listener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (authenticate(username, password)) {
                    SwingUtilities.invokeLater(() -> new MainFrame(staffMap.get(username), computers, staffMap));
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
                }
            }
        });

        setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        Staff staff = staffMap.get(username);
        return staff != null && staff.getPassword().equals(password);
    }


}
