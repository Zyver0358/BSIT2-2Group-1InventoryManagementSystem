package ds.trys;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class ReportFrame extends JFrame implements ActionListener {
    private final InventoryManager manager;
    private JTextArea txtReport;
    private JScrollPane reportScroll;
    private JButton btnLowStock, btnRestock;

    ReportFrame(InventoryManager manager) {
        this.manager = manager;
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
        reportScroll = new JScrollPane(txtReport);
        reportScroll.setBounds(10, 50, 615, 300);
        add(reportScroll);

        btnLowStock.addActionListener(this);
        btnRestock.addActionListener(this);

        if (!manager.isAdmin()) {
            btnRestock.setEnabled(false);
        }

        manager.loadDataFromDatabase();
        txtReport.setText(manager.generateLowStockReport(InventoryManager.LOW_STOCK_THRESHOLD));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        manager.loadDataFromDatabase();
        if (e.getSource() == btnLowStock) {
            txtReport.setText(manager.generateLowStockReport(InventoryManager.LOW_STOCK_THRESHOLD));
        } else if (e.getSource() == btnRestock) {
            if (!manager.isAdmin()) return;
            txtReport.setText(manager.generateRestockReport(InventoryManager.LOW_STOCK_THRESHOLD, InventoryManager.RESTOCK_TARGET));
        }
    }
}