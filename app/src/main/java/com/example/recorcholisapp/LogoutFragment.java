package com.example.recorcholisapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;


public class LogoutFragment extends Fragment {

    Button btn_sign_out;

    public LogoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        btn_sign_out = view.findViewById(R.id.btn_sign_out);
        logout(view);
        return view;
    }

    private void logout(View view) {
        btn_sign_out.setOnClickListener(v -> AuthUI.getInstance().signOut(view.getContext()).addOnCompleteListener(task -> {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        }).addOnFailureListener(e -> Toast.makeText(view.getContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show()));
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

}
