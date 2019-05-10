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
    Button changeKeys;
    Button authBttn;
    RadioGroup mRadioGroup;

    int sectorNum = 29;

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
        changeKeys = view.findViewById(R.id.keyBttn);
        authBttn = view.findViewById(R.id.authBttn);
        deposit.setOnClickListener(this);
        changeKeys.setOnClickListener(this);
        authBttn.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
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
                    saveUserInformation(getActivity(), Double.valueOf(mDatatoWrite.getText().toString()));
                }
                break;

            case R.id.keyBttn:
                ((MainActivity)getActivity()).newHexKeyA = mHexKeyA.getText().toString();
                ((MainActivity)getActivity()).newHexKeyB = mHexKeyB.getText().toString();
                ((MainActivity)getActivity()).changeKeys(v);
                break;

            case R.id.authBttn:
                ((MainActivity)getActivity()).hexKeyA = mHexKeyA.getText().toString();
                ((MainActivity)getActivity()).hexKeyB = mHexKeyB.getText().toString();
                ((MainActivity)getActivity()).Aunthenticate(v);
                break;
        }

    }

    private void saveUserInformation(Context context, double updatedMoney){

        Map<String, String> data = new HashMap<>();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateF = formatter.format(date);

        data.put("Fecha", dateF);
        data.put("Dispositivo", "App");
        data.put("Tipo", "Deposito");
        data.put("valorSaldo", Double.toString(updatedMoney));
        data.put("valorTickets", Integer.toString(((MainActivity) getActivity()).currentTickets));

        UserInformation userInformation = new UserInformation(updatedMoney, ((MainActivity) getActivity()).currentTickets, data);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(context, "Información guardada", Toast.LENGTH_SHORT).show();
    }

}

