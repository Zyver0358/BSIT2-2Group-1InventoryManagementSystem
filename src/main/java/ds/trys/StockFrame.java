/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ds.trys;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * 
 */
public class StockFrame extends JFrame implements ActionListener {
    private final InventoryManager manager;
    private DefaultTableModel stockProdModel, transModel;
    private JTable stockProdTable, transTable;
    private JScrollPane stockProdScroll, transScroll;
    private JTextField txtTPID, txtTQty;
    private JComboBox<String> cmbType;
    private JLabel lbTHeader, lbTPID, lbTType, lbTQty, lbTHistory, lbTHint;
    private JButton btnRecord, btnRefreshStock;

    StockFrame(InventoryManager manager) {
        this.manager = manager;
        String[] prodColumns = {"Product ID", "Name", "Quantity", "Supplier ID", "Price"};
        stockProdModel = new DefaultTableModel(prodColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] transColumns = {"Trans. ID", "Product ID", "Product Name", "Type", "Quantity", "Recorded By", "Date"};
        transModel = new DefaultTableModel(transColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTitle("Stock-In / Stock-Out Transactions");
        setSize(950, 560);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        lbTHeader = new JLabel("Record Stock Transaction");
        lbTHeader.setBounds(10, 5, 250, 30);
        add(lbTHeader);

        lbTPID = new JLabel("Product ID:");
        lbTPID.setBounds(10, 45, 100, 30);
        add(lbTPID);

        txtTPID = new JTextField();
        txtTPID.setBounds(110, 45, 200, 30);
        add(txtTPID);

        lbTType = new JLabel("Type:");
        lbTType.setBounds(10, 85, 100, 30);
        add(lbTType);

        cmbType = new JComboBox<>(new String[]{"Stock-In", "Stock-Out"});
        cmbType.setBounds(110, 85, 200, 30);
        add(cmbType);

        lbTQty = new JLabel("Quantity:");
        lbTQty.setBounds(10, 125, 100, 30);
        add(lbTQty);

        txtTQty = new JTextField();
        txtTQty.setBounds(110, 125, 200, 30);
        add(txtTQty);

        lbTHint = new JLabel("Tip: you can also select a product row.");
        lbTHint.setBounds(10, 160, 300, 20);
        add(lbTHint);

        btnRecord = new JButton("Record");
        btnRecord.setBounds(10, 190, 140, 30);
        add(btnRecord);

        btnRefreshStock = new JButton("Refresh");
        btnRefreshStock.setBounds(170, 190, 140, 30);
        add(btnRefreshStock);

        stockProdTable = new JTable(stockProdModel);
        stockProdScroll = new JScrollPane(stockProdTable);
        stockProdScroll.setBounds(330, 10, 600, 215);
        add(stockProdScroll);

        lbTHistory = new JLabel("Transaction History");
        lbTHistory.setBounds(10, 235, 200, 25);
        add(lbTHistory);

        transTable = new JTable(transModel);
        transScroll = new JScrollPane(transTable);
        transScroll.setBounds(10, 265, 920, 245);
        add(transScroll);

        btnRecord.addActionListener(this);
        btnRefreshStock.addActionListener(this);

        manager.loadDataFromDatabase();
        stockProdModel.setRowCount(0);
        for (Product p : manager.getProducts()) {
            stockProdModel.addRow(new Object[]{p.getId(), p.getName(), p.getQuantity(), p.getSupplier(), p.getPrice()});
        }

        transModel.setRowCount(0);
        String sql = "SELECT t.transaction_id, t.product_id, IFNULL(p.name, '(deleted)') AS pname, "
                + "t.type, t.quantity, t.username, t.created_at "
                + "FROM transactions t LEFT JOIN products p ON t.product_id = p.product_id "
                + "ORDER BY t.transaction_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                transModel.addRow(new Object[]{rs.getInt("transaction_id"), rs.getInt("product_id"), rs.getString("pname"),
                    rs.getString("type"), rs.getInt("quantity"), rs.getString("username"), rs.getTimestamp("created_at")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Load Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRecord) {
            String PID = txtTPID.getText().trim();
            String Qty = txtTQty.getText().trim();
            if (PID.isEmpty() && stockProdTable.getSelectedRow() != -1) {
                PID = stockProdModel.getValueAt(stockProdTable.getSelectedRow(), 0).toString();
            }
            if (!PID.isEmpty() && !Qty.isEmpty()) {
                try {
                    int id = Integer.parseInt(PID);
                    int qty = Integer.parseInt(Qty);
                    String type = cmbType.getSelectedIndex() == 0 ? "IN" : "OUT";

                    manager.recordTransaction(id, type, qty);
                    stockProdModel.setRowCount(0);
                    for (Product p : manager.getProducts()) {
                        stockProdModel.addRow(new Object[]{p.getId(), p.getName(), p.getQuantity(), p.getSupplier(), p.getPrice()});
                    }

                    transModel.setRowCount(0);
                    String sql = "SELECT t.transaction_id, t.product_id, IFNULL(p.name, '(deleted)') AS pname, "
                            + "t.type, t.quantity, t.username, t.created_at "
                            + "FROM transactions t LEFT JOIN products p ON t.product_id = p.product_id "
                            + "ORDER BY t.transaction_id DESC";
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql);
                         ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            transModel.addRow(new Object[]{rs.getInt("transaction_id"), rs.getInt("product_id"), rs.getString("pname"),
                                rs.getString("type"), rs.getInt("quantity"), rs.getString("username"), rs.getTimestamp("created_at")});
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Database Load Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    txtTPID.setText("");
                    txtTQty.setText("");
                    JOptionPane.showMessageDialog(this, "Transaction recorded and saved to database!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Product ID and Quantity must be whole numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter the Product ID (or select a row) and the Quantity", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnRefreshStock) {
            manager.loadDataFromDatabase();
            stockProdModel.setRowCount(0);
            for (Product p : manager.getProducts()) {
                stockProdModel.addRow(new Object[]{p.getId(), p.getName(), p.getQuantity(), p.getSupplier(), p.getPrice()});
            }

            transModel.setRowCount(0);
            String sql = "SELECT t.transaction_id, t.product_id, IFNULL(p.name, '(deleted)') AS pname, "
                    + "t.type, t.quantity, t.username, t.created_at "
                    + "FROM transactions t LEFT JOIN products p ON t.product_id = p.product_id "
                    + "ORDER BY t.transaction_id DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transModel.addRow(new Object[]{rs.getInt("transaction_id"), rs.getInt("product_id"), rs.getString("pname"),
                        rs.getString("type"), rs.getInt("quantity"), rs.getString("username"), rs.getTimestamp("created_at")});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Load Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}