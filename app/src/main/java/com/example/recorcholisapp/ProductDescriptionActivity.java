package com.example.recorcholisapp;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ProductDescriptionActivity extends Activity {

    public static final String PRODUCT_INTENT_KEY = "PRODUCT_INTENT_KEY";

    private TextView textProductPrice;
    private TextView textProductQuantityCounter;

    private Product product;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        product = getIntent().getParcelableExtra(PRODUCT_INTENT_KEY);

        TextView productName = findViewById(R.id.product_name);
        productName.setText(product.getName());

        TextView productDescription = findViewById(R.id.product_description);
        productDescription.setText(product.getDescription());

        textProductPrice = findViewById(R.id.text_product_price);
        textProductPrice.setText(Integer.toString(product.getPrice()));

        textProductQuantityCounter = findViewById(R.id.text_product_quantity_counter);

        ImageView imageProductRemove = findViewById(R.id.image_product_remove);
        imageProductRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProductPrice();
            }
        });
        ImageView imageProductAdd = findViewById(R.id.image_product_add);
        imageProductAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductPrice();
            }
        });

        Button buttonAddToCart = findViewById(R.id.button_add_to_cart);
        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToCart();
            }
        });
    }

    private void removeProductPrice(){
        int quantity = Integer.parseInt(textProductQuantityCounter.getText().toString()) - 1;
        if (quantity > 0) {
            double newPrice = quantity * product.getPrice();
            textProductQuantityCounter.setText(String.valueOf(quantity));
            textProductPrice.setText(String.format("$%.2f", newPrice));
        }
    }

    private void addProductPrice(){
        int quantity = Integer.parseInt(textProductQuantityCounter.getText().toString()) + 1;
        double newPrice = quantity * product.getPrice();
        textProductQuantityCounter.setText(String.valueOf(quantity));
        textProductPrice.setText(String.format("$%.2f", newPrice));
    }

    private void addProductToCart(){
        /*db.addProduct(product.id, product.productType, product.name, product.description,
                Integer.parseInt(textProductQuantityCounter.getText().toString()),
                product.price,
                textProductComments.getText().toString(),
                InClubSingleton.getInstance().getClubId());
        getActivity().onBackPressed();*/

        ResourcesSingleton.getInstance().addProduct(product.getId(), Integer.parseInt(textProductQuantityCounter.getText().toString()));
        Toast.makeText(this, "Se agregó al carrito", Toast.LENGTH_LONG).show();
        this.onBackPressed();
    }
}

