package com.example.recorcholisapp;

import java.util.ArrayList;
import java.util.HashMap;

public class ResourcesSingleton {

    private ArrayList<Product> commonFood = new ArrayList<Product>();
    private HashMap<Integer, Product> productHashMap = new HashMap<>();
    private int[] cart;

    private static ResourcesSingleton mInstance;

    private ResourcesSingleton() {
        Product p1 = new Product(1,"Bob esponja","Bob esponja de peluche" +
                " mediano",50,
                "https://cdn3.peluchilandia.es/3456-thickbox_default/peluche-bob-esponja-y-patricio.jpg");


        commonFood.add(p1);

        productHashMap.put(p1.getId(), p1);

        this.cart = new int[commonFood.size() + 1]; // +1 because the id starts at 1
    }


    public static ResourcesSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new ResourcesSingleton();
        }
        return mInstance;
    }

    public ArrayList<Product> getCommonFood() {
        return this.commonFood;
    }

    public void clearCart() {
        cart = new int[commonFood.size() + 1]; // +1 because the id starts at 1
    }

    public int[] getCart() {
        return this.cart;
    }

    public void addProduct(int id, int quantity) {
        cart[id] = cart[id] + quantity;
        if(cart[id] < 0) {
            cart[id] = 0;
        }
    }

    public void updateQuantity(int id, int quantity) {
        cart[id] =  quantity;
        if(cart[id] < 0) {
            cart[id] = 0;
        }
    }

    public int getTotalQuantity() {
        int quantity = 0;
        for(int p : cart) {
            quantity += p;
        }
        return quantity;
    }

    public void removeProduct(int id) {
        cart[id] = 0;
    }

    public Product getProduct(int id) {
        return productHashMap.get(id);
    }
}

