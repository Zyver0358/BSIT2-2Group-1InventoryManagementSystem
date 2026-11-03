package ds.trys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class DashboardFrame extends JFrame implements ActionListener {
    private JButton btnOpenInventory, btnLogout;
    private JLabel lblWelcome, lblInfo;
    private String role, username;
    private InventoryManager inventoryWindow;

    public DashboardFrame(String role, String username) {
        this.role = role;
        this.username = username;
        boolean admin = "Admin".equalsIgnoreCase(role);

        setTitle(admin ? "Admin Dashboard" : "Inventory Staff Dashboard");
        setSize(400, 280);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        lblWelcome = new JLabel(admin ? "Welcome, Administrator " + username + "!"
                                      : "Welcome, Inventory Staff " + username + "!");
        lblWelcome.setBounds(110, 25, 260, 25);
        add(lblWelcome);

        lblInfo = new JLabel(admin ? "Products, suppliers, users, stocks and reports"
                                   : "Product, stock, supplier and report management");
        lblInfo.setBounds(65, 50, 340, 25);
        add(lblInfo);

        btnOpenInventory = new JButton(admin ? "Manage Inventory & Suppliers" : "View Products & Stock Reports");
        btnOpenInventory.setBounds(80, 90, 240, 35);
        add(btnOpenInventory);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(140, 150, 120, 30);
        add(btnLogout);

        btnOpenInventory.addActionListener(this);
        btnLogout.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOpenInventory) {
            if (inventoryWindow == null || !inventoryWindow.isDisplayable()) {
                inventoryWindow = new InventoryManager(role, username);
                inventoryWindow.setVisible(true);
            } else {
                inventoryWindow.toFront();
            }
        } else if (e.getSource() == btnLogout) {
            if (inventoryWindow != null) {
                inventoryWindow.dispose();
            }
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}