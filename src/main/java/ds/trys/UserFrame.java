package ds.trys;

/**
 *
 * @author Rodnie Lastimoso
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserFrame extends JFrame implements ActionListener {
    private final InventoryManager manager;
    private DefaultTableModel userModel;
    private JTable userTable;
    private JScrollPane userScroll;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lbUsername, lbPassword, lbNote;
    private JButton btnAddStaff, btnUpdatePass, btnDeleteStaff, btnRefreshUser;

    UserFrame(InventoryManager manager) {
        this.manager = manager;
        String[] columns = {"Username", "Role"};
        userModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTitle("Manage User Accounts [Admin Only]");
        setSize(650, 400);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        userTable = new JTable(userModel);
        userScroll = new JScrollPane(userTable);
        userScroll.setBounds(10, 10, 615, 200);
        add(userScroll);

        lbUsername = new JLabel("Username:");
        lbUsername.setBounds(10, 225, 80, 30);
        add(lbUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(90, 225, 200, 30);
        add(txtUsername);

        lbPassword = new JLabel("Password:");
        lbPassword.setBounds(310, 225, 80, 30);
        add(lbPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(390, 225, 200, 30);
        add(txtPassword);

        lbNote = new JLabel("New accounts are created with the Inventory Staff role. Select a row to update/delete.");
        lbNote.setBounds(10, 262, 600, 20);
        add(lbNote);

        btnAddStaff = new JButton("Add Staff");
        btnAddStaff.setBounds(10, 295, 130, 30);
        add(btnAddStaff);

        btnUpdatePass = new JButton("Change Password");
        btnUpdatePass.setBounds(150, 295, 150, 30);
        add(btnUpdatePass);

        btnDeleteStaff = new JButton("Delete Staff");
        btnDeleteStaff.setBounds(310, 295, 130, 30);
        add(btnDeleteStaff);

        btnRefreshUser = new JButton("Refresh");
        btnRefreshUser.setBounds(450, 295, 100, 30);
        add(btnRefreshUser);

        btnAddStaff.addActionListener(this);
        btnUpdatePass.addActionListener(this);
        btnDeleteStaff.addActionListener(this);
        btnRefreshUser.addActionListener(this);

        userModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT username, role FROM users ORDER BY role, username");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                userModel.addRow(new Object[]{rs.getString("username"), rs.getString("role")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Load Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!manager.isAdmin()) return;

        if (e.getSource() == btnAddStaff) {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();
            if (!user.isEmpty() && !pass.isEmpty()) {
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement check = conn.prepareStatement("SELECT username FROM users WHERE username = ?");
                    check.setString(1, user);
                    ResultSet rs = check.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Username '" + user + "' already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'Staff')");
                        pstmt.setString(1, user);
                        pstmt.setString(2, pass);
                        pstmt.executeUpdate();
                        userModel.setRowCount(0);
                        try (PreparedStatement loadStmt = conn.prepareStatement("SELECT username, role FROM users ORDER BY role, username");
                             ResultSet loadRs = loadStmt.executeQuery()) {
                            while (loadRs.next()) {
                                userModel.addRow(new Object[]{loadRs.getString("username"), loadRs.getString("role")});
                            }
                        }
                        txtUsername.setText("");
                        txtPassword.setText("");
                        JOptionPane.showMessageDialog(this, "Inventory Staff account created successfully!");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnUpdatePass) {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                String role = userModel.getValueAt(selectedRow, 1).toString();
                String pass = new String(txtPassword.getPassword()).trim();
                if (!"Staff".equalsIgnoreCase(role)) {
                    JOptionPane.showMessageDialog(this, "The Admin account cannot be changed here.", "Not Allowed", JOptionPane.ERROR_MESSAGE);
                } else if (pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter the new password first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                } else {
                    String user = userModel.getValueAt(selectedRow, 0).toString();
                    try (Connection conn = DBConnection.getConnection()) {
                        PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ? AND role = 'Staff'");
                        pstmt.setString(1, pass);
                        pstmt.setString(2, user);
                        if (pstmt.executeUpdate() > 0) {
                            txtUsername.setText("");
                            txtPassword.setText("");
                            JOptionPane.showMessageDialog(this, "Password updated successfully!");
                        } else {
                            userModel.setRowCount(0);
                            try (PreparedStatement loadStmt = conn.prepareStatement("SELECT username, role FROM users ORDER BY role, username");
                                 ResultSet loadRs = loadStmt.executeQuery()) {
                                while (loadRs.next()) {
                                    userModel.addRow(new Object[]{loadRs.getString("username"), loadRs.getString("role")});
                                }
                            }
                            JOptionPane.showMessageDialog(this, "Account no longer exists.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a staff account from the table first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnDeleteStaff) {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                String role = userModel.getValueAt(selectedRow, 1).toString();
                if (!"Staff".equalsIgnoreCase(role)) {
                    JOptionPane.showMessageDialog(this, "The Admin account cannot be deleted.", "Not Allowed", JOptionPane.ERROR_MESSAGE);
                } else {
                    int confirm = JOptionPane.showConfirmDialog(this, "Delete the selected staff account?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        String user = userModel.getValueAt(selectedRow, 0).toString();
                        try (Connection conn = DBConnection.getConnection()) {
                            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE username = ? AND role = 'Staff'");
                            pstmt.setString(1, user);
                            pstmt.executeUpdate();
                            userModel.setRowCount(0);
                            try (PreparedStatement loadStmt = conn.prepareStatement("SELECT username, role FROM users ORDER BY role, username");
                                 ResultSet loadRs = loadStmt.executeQuery()) {
                                while (loadRs.next()) {
                                    userModel.addRow(new Object[]{loadRs.getString("username"), loadRs.getString("role")});
                                }
                            }
                            JOptionPane.showMessageDialog(this, "Staff account deleted from database");
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a staff account from the table first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnRefreshUser) {
            userModel.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT username, role FROM users ORDER BY role, username");
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    userModel.addRow(new Object[]{rs.getString("username"), rs.getString("role")});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Load Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
    

