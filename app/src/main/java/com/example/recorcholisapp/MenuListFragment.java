package com.example.recorcholisapp;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


public class MenuListFragment extends ListFragment {

    private ProductAdapter productArrayAdapter;

    private ArrayList<Product> commonProducts = new ArrayList<Product>();

    public MenuListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.commonProducts = ResourcesSingleton.getInstance().getCommonFood();
        productArrayAdapter = new ProductAdapter(this.getActivity(), R.layout.fragment_menu_list, new ArrayList<>());
        setListAdapter(productArrayAdapter);
        for (Product p: commonProducts) {
            productArrayAdapter.add(p);
        }
        productArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), ProductDescriptionActivity.class);
        i.putExtra(ProductDescriptionActivity.PRODUCT_INTENT_KEY, this.commonProducts.get(position));
        startActivity(i);
    }
}

