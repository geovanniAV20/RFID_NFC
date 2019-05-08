package com.example.recorcholisapp;

import java.util.ArrayList;
import java.util.HashMap;

public class ResourcesSingleton {

    private ArrayList<Product> products = new ArrayList<Product>();
    private HashMap<Integer, Product> productHashMap = new HashMap<>();
    private int[] cart;

    private static ResourcesSingleton mInstance;

    private ResourcesSingleton() {
        Product p1 = new Product(1,"Bob esponja","Bob esponja de peluche" +
                " mediano",50,
                "https://cdn3.peluchilandia.es/3456-thickbox_default/peluche-bob-esponja-y-patricio.jpg");
        Product p2 = new Product(2,"Patricio","Bob esponja de peluche" +
                " mediano",50,
                "http://h2577915.stratoserver.net/ImagenesERP/ANGAWA/patricio19cm.jpg");
        Product p3 = new Product(3,"Oso de peluche","Oso de peluche" +
                " chico",100,
                "https://www.sanborns.com.mx/imagenes-sanborns-ii/1200/7501909540602.jpg");
        Product p4 = new Product(4,"Rolly Puppy Dogs","Peluche de Rolly de Puppy Dogs" +
                " chico",100,
                "https://ss425.liverpool.com.mx/xl/1060502982.jpg");
        Product p5 = new Product(5,"Oso de peluche","Oso de peluche gris" +
                " grande",500,
                "https://www.costco.com.mx/medias/sys_master/products/hf1/h03/10755141206046.jpg");
        Product p6 = new Product(6,"Vulpiz Alola","Vulpix alola de peluche" +
                " mediano",1000,
                "http://www.raccoonplanet.com/1066-large_default/pokemon-peluche-vulpix-alola.jpg");
        Product p7 = new Product(7,"Iron Man","Figura de acción de Iron Man" +
                " chica",2000,
                "https://images-na.ssl-images-amazon.com/images/I/51HJ6-akSEL.jpg");
        Product p8 = new Product(8,"Xbox 360","Consola Xbox 360" +
                " slim",500000,
                "https://images-na.ssl-images-amazon.com/images/I/41God7KwSOL.jpg");
        Product p9 = new Product(9,"Nintendo Switch","Consola Nintento Switch" +
                "",1000000,
                "https://images-na.ssl-images-amazon.com/images/I/81uAOnMvAXL._SX679_.jpg");
        Product p10 = new Product(10,"Playstation 4","Consola Playstation 4" +
                " normal",1500000,
                "https://static-ca.ebgames.ca/images/products/710007/3max.jpg");

        products.add(p1);
        products.add(p2);
        products.add(p3);
        products.add(p4);
        products.add(p5);
        products.add(p6);
        products.add(p7);
        products.add(p8);
        products.add(p9);
        products.add(p10);

        productHashMap.put(p1.getId(), p1);
        productHashMap.put(p2.getId(), p2);
        productHashMap.put(p3.getId(), p3);
        productHashMap.put(p4.getId(), p4);
        productHashMap.put(p5.getId(), p5);
        productHashMap.put(p6.getId(), p6);
        productHashMap.put(p7.getId(), p7);
        productHashMap.put(p8.getId(), p8);
        productHashMap.put(p9.getId(), p9);
        productHashMap.put(p10.getId(), p10);

        this.cart = new int[products.size() + 1]; // +1 because the id starts at 1
    }


    public static ResourcesSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new ResourcesSingleton();
        }
        return mInstance;
    }

    public ArrayList<Product> getCommonFood() {
        return this.products;
    }

    public void clearCart() {
        cart = new int[products.size() + 1]; // +1 because the id starts at 1
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

