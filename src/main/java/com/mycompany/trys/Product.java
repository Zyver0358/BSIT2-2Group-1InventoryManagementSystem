/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trys;

/**
 *
 * @author Rudy Anonuevo
 */
public class Product {
    private int id;
    private String name;
    private int quantity;
    private String supplier;
    private double price;
 
    public Product next;
    public Product left, right;
 
    public Product(int id, String name, int quantity, String supplier, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.supplier = supplier;
        this.price = price;
        this.next = null;
        this.left = null;
        this.right = null;
    }
 
    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public String getSupplier() { return supplier; }
    public double getPrice() { return price; }
 
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public void setPrice(double price) { this.price = price; }
}
