package com.example.recorcholisapp;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class ProductDescriptionActivity extends Activity {

    public static final String PRODUCT_INTENT_KEY = "PRODUCT_INTENT_KEY";

    private TextView textProductPrice;
    private TextView textProductQuantityCounter;

    private Product product;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        product = getIntent().getParcelableExtra(PRODUCT_INTENT_KEY);

        TextView productName = findViewById(R.id.product_name);
        CollapsingToolbarLayout productNameB = findViewById(R.id.collapsing);
        productName.setText(product.getName());
        productNameB.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        productNameB.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        productNameB.setTitle(product.getName());

        ImageView productImage = findViewById(R.id.img_product);
        Picasso.with(getBaseContext()).load(product.getPhoto()).into(productImage);

        TextView productDescription = findViewById(R.id.product_description);
        productDescription.setText(product.getDescription());

        textProductPrice = findViewById(R.id.text_product_price);
        textProductPrice.setText(Integer.toString(product.getPrice()));

        textProductQuantityCounter = findViewById(R.id.text_product_quantity_counter);

        ImageView imageProductRemove = findViewById(R.id.image_product_remove);
        imageProductRemove.setOnClickListener(v -> removeProductPrice());
        ImageView imageProductAdd = findViewById(R.id.image_product_add);
        imageProductAdd.setOnClickListener(v -> addProductPrice());

        FloatingActionButton buttonAddToCart = findViewById(R.id.button_add_to_cart);
        buttonAddToCart.setOnClickListener(v -> addProductToCart());
    }

    private void removeProductPrice(){
        int quantity = Integer.parseInt(textProductQuantityCounter.getText().toString()) - 1;
        if (quantity > 0) {
            int newPrice = quantity * product.getPrice();
            textProductQuantityCounter.setText(String.valueOf(quantity));
            textProductPrice.setText(Integer.toString(newPrice));
        }
    }

    private void addProductPrice(){
        int quantity = Integer.parseInt(textProductQuantityCounter.getText().toString()) + 1;
        int newPrice = quantity * product.getPrice();
        textProductQuantityCounter.setText(String.valueOf(quantity));
        textProductPrice.setText(String.valueOf(newPrice));
    }

    private void addProductToCart(){
        ResourcesSingleton.getInstance().addProduct(product.getId(), Integer.parseInt(textProductQuantityCounter.getText().toString()));
        Toast.makeText(this, "Se agregó al carrito", Toast.LENGTH_LONG).show();
        this.onBackPressed();
    }
}

