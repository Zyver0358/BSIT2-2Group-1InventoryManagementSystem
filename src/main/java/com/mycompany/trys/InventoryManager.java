/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trys;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Rudy Anonuevo
 */
public class InventoryManager extends JFrame implements ActionListener {

    public static final int LOW_STOCK_THRESHOLD = 5;
    public static final int RESTOCK_TARGET = 20;

    private Product head;
    private Product root;
    private HashMap<String, Supplier> supplierMap;

    private DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;
    private JTextField txtFPID, txtFName, txtFQuantity, txtFSID, txtFPrice;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnVReport, btnVSupplier;
    private JLabel lbHeader, lbIM, lbPDT, lbPID, lbName, lbQuantity, lbSID, lbPrice, lbSReport, lbReport;

    InventoryManager() {

        head = null;
        root = null;
        supplierMap = new HashMap<>();

        String[] columns = {"Product ID", "Name", "Quantity", "Supplier ID", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTitle("Inventory Management System");
        setSize(900, 380);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(440, 70, 430, 190);
        add(scrollPane);

        txtFPID = new JTextField();
        txtFPID.setBounds(110, 70, 270, 30);
        add(txtFPID);

        txtFName = new JTextField();
        txtFName.setBounds(110, 110, 270, 30);
        add(txtFName);

        txtFQuantity = new JTextField();
        txtFQuantity.setBounds(110, 150, 270, 30);
        add(txtFQuantity);

        txtFSID = new JTextField();
        txtFSID.setBounds(110, 190, 270, 30);
        add(txtFSID);

        txtFPrice = new JTextField();
        txtFPrice.setBounds(110, 230, 270, 30);
        add(txtFPrice);

        lbHeader = new JLabel("Inventory Management System");
        lbHeader.setBounds(360, 0, 220, 30);
        add(lbHeader);

        lbIM = new JLabel("Inventory Manager");
        lbIM.setBounds(135, 35, 120, 30);
        add(lbIM);

        lbPDT = new JLabel("Product Data Table");
        lbPDT.setBounds(600, 35, 120, 30);
        add(lbPDT);

        lbPID = new JLabel("Product ID:");
        lbPID.setBounds(10, 70, 100, 30);
        add(lbPID);

        lbName = new JLabel("Name:");
        lbName.setBounds(10, 110, 100, 30);
        add(lbName);

        lbQuantity = new JLabel("Quantity:");
        lbQuantity.setBounds(10, 150, 100, 30);
        add(lbQuantity);

        lbSID = new JLabel("Supplier ID:");
        lbSID.setBounds(10, 190, 100, 30);
        add(lbSID);

        lbPrice = new JLabel("Price:");
        lbPrice.setBounds(10, 230, 100, 30);
        add(lbPrice);

        lbSReport = new JLabel("Stock Report:");
        lbSReport.setBounds(440, 280, 100, 30);
        add(lbSReport);

        lbReport = new JLabel(" [No data]");
        lbReport.setBounds(530, 280, 120, 30);
        add(lbReport);

        btnAdd = new JButton("ADD");
        btnAdd.setBounds(10, 280, 85, 30);
        add(btnAdd);

        btnUpdate = new JButton("UPDATE");
        btnUpdate.setBounds(105, 280, 85, 30);
        add(btnUpdate);

        btnDelete = new JButton("DELETE");
        btnDelete.setBounds(200, 280, 85, 30);
        add(btnDelete);

        btnSearch = new JButton("SEARCH");
        btnSearch.setBounds(295, 280, 85, 30);
        add(btnSearch);

        btnVReport = new JButton("View Report");
        btnVReport.setBounds(620, 280, 120, 30);
        add(btnVReport);

        btnVSupplier = new JButton("View Supplier");
        btnVSupplier.setBounds(750, 280, 120, 30);
        add(btnVSupplier);

        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnSearch.addActionListener(this);
        btnVReport.addActionListener(this);
        btnVSupplier.addActionListener(this);
    }
    
    private void validate(String name, int qty, String supplierId, double price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (supplierId == null || supplierId.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier ID cannot be empty.");
        }
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
    }

    public void addProduct(Product p) {
        validate(p.getName(), p.getQuantity(), p.getSupplier(), p.getPrice());
        if (searchById(p.getId()) != null) {
            throw new IllegalArgumentException("Product ID " + p.getId() + " already exists.");
        }
        if (head == null) {
            head = p;
        } else {
            Product temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = p;
        }
        p.left = null;
        p.right = null;
        root = insertBST(root, p);
    }

    private Product insertBST(Product current, Product p) {
        if (current == null) return p;
        if (p.getPrice() < current.getPrice()) {
            current.left = insertBST(current.left, p);
        } else {
            current.right = insertBST(current.right, p);
        }
        return current;
    }

    private void rebuildBST() {
        root = null;
        Product temp = head;
        while (temp != null) {
            temp.left = null;
            temp.right = null;
            root = insertBST(root, temp);
            temp = temp.next;
        }
    }

    public boolean updateProduct(int id, String name, int qty, String supplier, double price) {
        validate(name, qty, supplier, price);
        Product p = searchById(id);
        if (p != null) {
            p.setName(name);
            p.setQuantity(qty);
            p.setSupplier(supplier);
            p.setPrice(price);
            rebuildBST();
            return true;
        }
        return false;
    }

    public boolean deleteProduct(int id) {
        if (head == null) return false;

        if (head.getId() == id) {
            Product removed = head;
            head = head.next;
            removed.next = null;
            rebuildBST();
            return true;
        }

        Product curr = head;
        Product prev = null;
        while (curr != null && curr.getId() != id) {
            prev = curr;
            curr = curr.next;
        }

        if (curr != null) {
            prev.next = curr.next;
            curr.next = null;
            rebuildBST();
            return true;
        }
        return false;
    }

    public Product searchById(int id) {
        Product temp = head;
        while (temp != null) {
            if (temp.getId() == id) return temp;
            temp = temp.next;
        }
        return null;
    }

    public Product searchByName(String name) {
        Product temp = head;
        while (temp != null) {
            if (temp.getName().equalsIgnoreCase(name)) return temp;
            temp = temp.next;
        }
        return null;
    }
    
    public Product searchBySupplier(String supplierId) {
        Product temp = head;
        while (temp != null) {
            if (temp.getSupplier().equalsIgnoreCase(supplierId)) return temp;
            temp = temp.next;
        }
        return null;
    }
    
    public Product searchByPrice(double price) {
        Product current = root;
        while (current != null) {
            if (Double.compare(price, current.getPrice()) == 0) return current;
            if (price < current.getPrice()) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }
    
    public boolean hasLowStock(int threshold) {
        Product temp = head;
        while (temp != null) {
            if (temp.getQuantity() <= threshold) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }
    
    public void addSupplier(Supplier s) {
        supplierMap.put(s.getId(), s);
    }

    public boolean hasSupplier(String supplierId) {
        return supplierMap.containsKey(supplierId);
    }

    public void ensureSupplier(String supplierId) {
        if (!supplierMap.containsKey(supplierId)) {
            supplierMap.put(supplierId, new Supplier(supplierId, "N/A", "N/A"));
        }
    }

    public boolean updateSupplier(String supplierId, String name, String contactInfo) {
        Supplier s = supplierMap.get(supplierId);
        if (s != null) {
            s.setName(name);
            s.setContactInfo(contactInfo);
            return true;
        }
        return false;
    }

    public Supplier getSupplier(String supplierId) {
        return supplierMap.get(supplierId);
    }

    private String supplierNameOf(String supplierId) {
        Supplier s = supplierMap.get(supplierId);
        return (s == null) ? "N/A" : s.getName();
    }
    
    public String generateLowStockReport(int threshold) {
        StringBuilder report = new StringBuilder("=== LOW STOCK REPORT (Qty <= " + threshold + ") ===\n\n");
        Product temp = head;
        boolean found = false;
        while (temp != null) {
            if (temp.getQuantity() <= threshold) {
                report.append("ID: ").append(temp.getId())
                      .append(" | Name: ").append(temp.getName())
                      .append(" | Qty: ").append(temp.getQuantity())
                      .append(" | Supplier: ").append(temp.getSupplier())
                      .append(" (").append(supplierNameOf(temp.getSupplier())).append(")")
                      .append(" | Price: PHP ").append(String.format("%.2f", temp.getPrice()))
                      .append("\n");
                found = true;
            }
            temp = temp.next;
        }
        return found ? report.toString() : "All products are fully stocked (Quantity > " + threshold + ").";
    }

    public String generateRestockReport(int threshold, int targetLevel) {
        if (!hasLowStock(threshold)) {
            return "No products need restocking (Quantity > " + threshold + ").";
        }
        StringBuilder report = new StringBuilder("=== RESTOCK REPORT (Qty <= " + threshold
                + ", restock up to " + targetLevel + ") ===\n\n");
        double totalCost = restockInOrder(root, report, threshold, targetLevel);
        report.append("\nTotal Estimated Cost: PHP ").append(String.format("%.2f", totalCost));
        return report.toString();
    }
    
    private double restockInOrder(Product node, StringBuilder report, int threshold, int targetLevel) {
        if (node == null) return 0;

        double total = restockInOrder(node.left, report, threshold, targetLevel);

        if (node.getQuantity() <= threshold) {
            int toOrder = targetLevel - node.getQuantity();
            double cost = toOrder * node.getPrice();
            total += cost;
            report.append("ID: ").append(node.getId())
                  .append(" | Name: ").append(node.getName())
                  .append(" | Qty: ").append(node.getQuantity())
                  .append(" | Order: ").append(toOrder)
                  .append(" | Supplier: ").append(node.getSupplier())
                  .append(" (").append(supplierNameOf(node.getSupplier())).append(")")
                  .append(" | Est. Cost: PHP ").append(String.format("%.2f", cost))
                  .append("\n");
        }

        total += restockInOrder(node.right, report, threshold, targetLevel);
        return total;
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        Product temp = head;
        while (temp != null) {
            tableModel.addRow(new Object[]{temp.getId(), temp.getName(), temp.getQuantity(), temp.getSupplier(), temp.getPrice()});
            temp = temp.next;
        }
        refreshReportLabel();
    }

    private void refreshReportLabel() {
        if (head == null) {
            lbReport.setText(" [No data]");
        } else if (hasLowStock(LOW_STOCK_THRESHOLD)) {
            lbReport.setText(" [low-stock]");
        } else {
            lbReport.setText(" [Fully Stocked]");
        }
    }

    private void clearFields() {
        txtFPID.setText("");
        txtFName.setText("");
        txtFQuantity.setText("");
        txtFSID.setText("");
        txtFPrice.setText("");
    }

    private void selectRowById(int id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).toString().equals(String.valueOf(id))) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                return;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            String PID = txtFPID.getText().trim();
            String Name = txtFName.getText().trim();
            String Quanty = txtFQuantity.getText().trim();
            String SID = txtFSID.getText().trim();
            String Price = txtFPrice.getText().trim();

            if (!PID.isEmpty() && !Name.isEmpty() && !Quanty.isEmpty() && !SID.isEmpty() && !Price.isEmpty()) {
                try {
                    int id = Integer.parseInt(PID);
                    int qty = Integer.parseInt(Quanty);
                    double price = Double.parseDouble(Price);

                    addProduct(new Product(id, Name, qty, SID, price));
                    ensureSupplier(SID);
                    refreshTable();
                    clearFields();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Product ID and Quantity must be whole numbers, Price must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all product details first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnUpdate){
            int rowSelected = table.getSelectedRow();
            if (rowSelected != -1) {
                String PID = txtFPID.getText().trim();
                String Name = txtFName.getText().trim();
                String Quanty = txtFQuantity.getText().trim();
                String SID = txtFSID.getText().trim();
                String Price = txtFPrice.getText().trim();

                if (!PID.isEmpty() && !Name.isEmpty() && !Quanty.isEmpty() && !SID.isEmpty() && !Price.isEmpty()) {
                    try {
                        int selectedId = Integer.parseInt(tableModel.getValueAt(rowSelected, 0).toString());
                        int id = Integer.parseInt(PID);
                        int qty = Integer.parseInt(Quanty);
                        double price = Double.parseDouble(Price);

                        if (id != selectedId) {
                            JOptionPane.showMessageDialog(this, "Product ID cannot be changed. Use the ID of the selected row", "Input Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            updateProduct(id, Name, qty, SID, price);
                            ensureSupplier(SID);
                            refreshTable();
                            clearFields();
                            JOptionPane.showMessageDialog(this, "Product Info has been updated");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Product ID and Quantity must be whole numbers, Price must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please fill in all product details first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product first before updating", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnDelete) {
            int indexSelected = table.getSelectedRow();
            if (indexSelected != -1) {
                int id = Integer.parseInt(tableModel.getValueAt(indexSelected, 0).toString());
                deleteProduct(id);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Product Info has been deleted");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product first before deleting", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnSearch){
            String PID = txtFPID.getText().trim();
            String Name = txtFName.getText().trim();
            String SID = txtFSID.getText().trim();
            String Price = txtFPrice.getText().trim();
            
            if (!PID.isEmpty() || !Name.isEmpty() || !SID.isEmpty() || !Price.isEmpty()){
                try {
                    Product found = null;
                    if (!PID.isEmpty()) {
                        found = searchById(Integer.parseInt(PID));
                        txtFPID.setText("");
                    }else if(!Name.isEmpty()){
                        found = searchByName(Name);
                        txtFName.setText("");
                    }else if (!SID.isEmpty()) {
                        found = searchBySupplier(SID);
                        txtFSID.setText("");
                    }else if (!Price.isEmpty()) {
                        found = searchByPrice(Double.parseDouble(Price));
                        txtFPrice.setText("");
                    }

                    if (found != null) {
                        selectRowById(found.getId());
                        JOptionPane.showMessageDialog(this, "Match found!");
                    } else {
                        table.clearSelection();
                        JOptionPane.showMessageDialog(this, "No matching product found!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Product ID must be a whole number and Price must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Enter a Product ID, Name,or Supplier ID to search", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnVReport) {
            if (head != null) {
                new ReportWindow().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please add a product first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnVSupplier) {
            new SupplierWindow().setVisible(true);
        }
    }
    
    private class ReportWindow extends JFrame implements ActionListener {

        private JTextArea txtReport;
        private JScrollPane reportScroll;
        private JButton btnLowStock, btnRestock;

        ReportWindow() {
            setTitle("Stock Report");
            setSize(650, 400);
            setLayout(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            btnLowStock = new JButton("Low-Stock Report");
            btnLowStock.setBounds(10, 10, 150, 30);
            add(btnLowStock);

            btnRestock = new JButton("Restock Report");
            btnRestock.setBounds(170, 10, 150, 30);
            add(btnRestock);

            txtReport = new JTextArea();
            txtReport.setEditable(false);
            txtReport.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            reportScroll = new JScrollPane(txtReport);
            reportScroll.setBounds(10, 50, 615, 300);
            add(reportScroll);

            txtReport.setText(generateLowStockReport(LOW_STOCK_THRESHOLD));

            btnLowStock.addActionListener(this);
            btnRestock.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnLowStock) {
                txtReport.setText(generateLowStockReport(LOW_STOCK_THRESHOLD));
            } else if (e.getSource() == btnRestock) {
                txtReport.setText(generateRestockReport(LOW_STOCK_THRESHOLD, RESTOCK_TARGET));
            }
        }
    }
    
    private class SupplierWindow extends JFrame implements ActionListener {

        private DefaultTableModel supplierModel;
        private JTable supplierTable;
        private JScrollPane supplierScroll;
        private JTextField txtSID, txtName, txtContact;
        private JLabel lbSupSID, lbSupName, lbSupContact;
        private JButton btnAddSupplier, btnUpdateSupplier;

        SupplierWindow() {
            String[] columns = {"Supplier ID", "Supplier Name", "Contact Info"};
            supplierModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            setTitle("View & Manage Suppliers");
            setSize(650, 400);
            setLayout(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            supplierTable = new JTable(supplierModel);
            supplierScroll = new JScrollPane(supplierTable);
            supplierScroll.setBounds(10, 10, 615, 200);
            add(supplierScroll);

            lbSupSID = new JLabel("Supplier ID:");
            lbSupSID.setBounds(10, 225, 80, 30);
            add(lbSupSID);

            txtSID = new JTextField();
            txtSID.setBounds(90, 225, 100, 30);
            add(txtSID);

            lbSupName = new JLabel("Name:");
            lbSupName.setBounds(205, 225, 45, 30);
            add(lbSupName);

            txtName = new JTextField();
            txtName.setBounds(250, 225, 150, 30);
            add(txtName);

            lbSupContact = new JLabel("Contact:");
            lbSupContact.setBounds(415, 225, 60, 30);
            add(lbSupContact);

            txtContact = new JTextField();
            txtContact.setBounds(475, 225, 150, 30);
            add(txtContact);

            btnAddSupplier = new JButton("Add Supplier");
            btnAddSupplier.setBounds(150, 280, 150, 30);
            add(btnAddSupplier);

            btnUpdateSupplier = new JButton("Update Supplier Info");
            btnUpdateSupplier.setBounds(320, 280, 180, 30);
            add(btnUpdateSupplier);

            btnAddSupplier.addActionListener(this);
            btnUpdateSupplier.addActionListener(this);

            refreshSupplierTable();
        }

        private void refreshSupplierTable() {
            supplierModel.setRowCount(0);
            for (Supplier s : supplierMap.values()) {
                supplierModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactInfo()});
            }
        }

        private void clearSupplierFields() {
            txtSID.setText("");
            txtName.setText("");
            txtContact.setText("");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnAddSupplier) {
                String SID = txtSID.getText().trim();
                String sName = txtName.getText().trim();
                String sContact = txtContact.getText().trim();

                if (!SID.isEmpty() && !sName.isEmpty() && !sContact.isEmpty()) {
                    if (hasSupplier(SID)) {
                        JOptionPane.showMessageDialog(this, "Supplier ID already exists. Select it and use Update instead", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                    } else {
                        addSupplier(new Supplier(SID, sName, sContact));
                        refreshSupplierTable();
                        clearSupplierFields();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter all supplier details first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource() == btnUpdateSupplier) {
                int selectedRow = supplierTable.getSelectedRow();
                if (selectedRow != -1) {
                    String sName = txtName.getText().trim();
                    String sContact = txtContact.getText().trim();

                    if (!sName.isEmpty() && !sContact.isEmpty()) {
                        String SID = supplierModel.getValueAt(selectedRow, 0).toString();
                        updateSupplier(SID, sName, sContact);
                        refreshSupplierTable();
                        clearSupplierFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Please enter the new name and contact info first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a supplier from the table first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}