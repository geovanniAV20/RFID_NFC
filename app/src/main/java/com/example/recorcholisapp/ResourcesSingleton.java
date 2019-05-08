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
        Product p2 = new Product(2,"Tacos de arrachera","Tres tacos de arrachera, chiles toreados y guacamole,"
                ,70,
                "https://cuponassets.cuponatic-latam.com/backendMx/uploads/imagenes_descuentos/39951/a9e385e9df9f81199d402eca46172de05fcb120d.XL2.jpg");
        Product p3 = new Product(3,"Filete de Pescado","Acompañado de una ensalada,",
                130,
                "http://www.veintitantos.com/sites/default/files/styles/v2-img1000x563/public/articulo/2015-04/53447241c00c6_1.jpg?itok=vLyYclp4");
        Product p4 = new Product(4,"Milanesa","Milanesa de res o pollo acompañado de una ensalada,",
                129,
                "https://http2.mlstatic.com/milanesa-de-pollo-de-campo-argentino-carne-de-verdad-D_NQ_NP_219321-MLA20762032028_062016-F.jpg");
        Product p5 = new Product(5,"Carne Asada","Acompañado de una ensalada,",
                129,
                "https://cdn.kiwilimon.com/recetaimagen/28634/thumb400x300-28942.jpg");
        Product p6 = new Product(6,"Tacos Dorados de Pollo","Preparados con guacamole, lechuga, crema, queso," +
                " cebolla y jitomate. Orden de 3.",115,
                "https://i.ytimg.com/vi/0QKfqFW51pc/maxresdefault.jpg");
        Product p7 = new Product(7,"Enchiladas Verdes","3 enchiladas de pollo, queso o chorizo con queso rallado y crema",88,
                "https://cocina-casera.com/mx/wp-content/uploads/2017/09/enchiladas-verdes-1.jpg");
        Product p8 = new Product(8,"Carne a la Tampiqueña","Exquisita carne acompañada de chalupa con frijol, enchilada, enomlada y rajas con crema.",128,
                "http://cantinalaribera.com.mx/wp-content/uploads/2014/08/derancho-Carne-tampiquena.jpg");
        Product p9 = new Product(9,"Lasagna Clasica","Una deliciosa combinación de capas de pasta intercaladas con salsa de carne, quesos mozarella, ricotta," +
                "parmesano y romano.",170,
                "http://cdn2.cocinadelirante.com/sites/default/files/images/2016/08/lasagnadepimientos.jpg");
        Product p10 = new Product(10,"Pasta Carbonara","Pasta en una salsa cremosa de queso parmesano con tocino pancetta," +
                "pimientos rojos asados, horneados y cubiertos con pan molido." , 150,
                "http://cdn2.cocinadelirante.com/sites/default/files/styles/gallerie/public/images/2017/07/pastacarbonaraconpollo2.jpg");
        Product p11 = new Product(11,"Bagel de Jamón de Pierna","Emparedado con jamón de pierna de pavo" +
                "lechuga, jitomate y cebolla; elige tu picante favorito",65,
                "https://www.diabetesbienestarysalud.com/wp-content/uploads/2012/07/bagel-de-jamon-de-pavo.jpg");
        Product p12 = new Product(12,"Tortas de cochinita pibil","Receta impecable, en telera con frijol, con cebolla habanera"
                ,50,
                "https://www.cocinavital.mx/wp-content/uploads/2017/08/tortas-cochinita-pibil.jpg");
        Product p13 = new Product(13,"Ensalada de Pollo y Fresas","Rica ensalada con trozos de pollo y" +
                " fresas",70,
                "https://www.recetin.com/wp-content/uploads/2013/07/ensalada_fresas.jpg");
        Product p14 = new Product(14,"Pozole","Rojo o Blanco",80,
                "https://www.cocinavital.mx/wp-content/uploads/2017/09/como-hacer-pozole-rojo-de-puerco-mexicana.jpg");
        Product p15 = new Product(15,"Hamburguesa Clásica","Hamburguesa clásica con queso americano," +
                " agua de Jamaica y sopa de arroz con pollo",162,
                "https://cocina-casera.com/wp-content/uploads/2016/11/hamburguesa-queso-receta.jpg");

        commonFood.add(p1);
        commonFood.add(p2);
        commonFood.add(p3);
        commonFood.add(p4);
        commonFood.add(p5);
        commonFood.add(p6);
        commonFood.add(p7);
        commonFood.add(p8);
        commonFood.add(p9);
        commonFood.add(p10);
        commonFood.add(p11);
        commonFood.add(p12);
        commonFood.add(p13);
        commonFood.add(p14);
        commonFood.add(p15);

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
        productHashMap.put(p11.getId(), p11);
        productHashMap.put(p12.getId(), p12);
        productHashMap.put(p13.getId(), p13);
        productHashMap.put(p14.getId(), p14);
        productHashMap.put(p15.getId(), p15);

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

