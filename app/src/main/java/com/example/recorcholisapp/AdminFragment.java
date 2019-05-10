package com.example.recorcholisapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    EditText mHexKeyA;
    EditText mHexKeyB;
    EditText mDatatoWrite;
    Button deposit;
    Button readSaldo;
    Button authBttn;

    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatatoWrite = view.findViewById(R.id.chargeEditText);
        mHexKeyA = view.findViewById(R.id.keyAEditText);
        mHexKeyB = view.findViewById(R.id.keyBEditText);
        deposit = view.findViewById(R.id.depositBttn);
        authBttn = view.findViewById(R.id.authBttn);
        readSaldo = view.findViewById(R.id.moneyBttn);
        deposit.setOnClickListener(this);
        authBttn.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cards").child("data").push();
    }

    public void onReadMoney(View arg0)
    {
        ((MainActivity)getActivity()).onReadMoney(arg0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.depositBttn:
                if(mDatatoWrite.getText().toString().equals("")){
                    Toast.makeText(getActivity(),
                            "Introducir cantidad a depositar!",
                            Toast.LENGTH_LONG).show();
                }else {
                    ((MainActivity) getActivity()).setDepositMoney(Integer.parseInt(mDatatoWrite.getText().toString()));
                    ((MainActivity)getActivity()).onDeposit(v);
                    double aux = (((MainActivity) getActivity()).currentMoney + Integer.parseInt(mDatatoWrite.getText().toString()));
                    saveUserInformation(getActivity(), aux, Integer.parseInt(mDatatoWrite.getText().toString()));
                }
                break;

            case R.id.authBttn:
                ((MainActivity)getActivity()).hexKeyA = mHexKeyA.getText().toString();
                ((MainActivity)getActivity()).hexKeyB = mHexKeyB.getText().toString();
                ((MainActivity)getActivity()).Aunthenticate(v);
                break;

            case R.id.moneyBttn:
                ((MainActivity)getActivity()).onReadMoney(readSaldo);
                break;
        }

    }

    private void saveUserInformation(Context context, double updatedMoney, int cantidad){

        Map<String,String> data = new HashMap<String, String>();
        //Map<String, Map<String, String>> card = new HashMap<String, Map<String, String>>();


        String uuid = ((MainActivity) getActivity()).hexUID;

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateF = formatter.format(date);

        data.put("Descripcion", "Deposito");
        data.put("Fecha", dateF);
        data.put("Maquina", "Kiosco");
        data.put("Saldo", Integer.toString(cantidad));
        data.put("Tickets", "0");

        //card.put(uuid, data);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();
        UserInformation userInformation = new UserInformation(uuid, userID, updatedMoney, ((MainActivity) getActivity()).currentTickets, data);

        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(context, "Información guardada", Toast.LENGTH_SHORT).show();
    }
}

