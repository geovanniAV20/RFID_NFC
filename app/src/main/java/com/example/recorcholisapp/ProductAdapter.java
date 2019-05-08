package com.example.recorcholisapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    private Context context;

    public ProductAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        Fresco.initialize(this.context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.product_layout, parent, false);
        }

        TextView name = convertView.findViewById(R.id.product_name);
        TextView description = convertView.findViewById(R.id.product_description);
        TextView price = convertView.findViewById(R.id.product_price);
        name.setText(product.getName());
        description.setText(product.getDescription());
        price.setText(product.getPrice() + " Tickets");
        Uri uri = Uri.parse(product.getPhoto());
        SimpleDraweeView draweeView = convertView.findViewById(R.id.product_image);
        draweeView.setImageURI(uri);


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}

