package com.example.recorcholisapp;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "nfcinventory_simple";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private TabLayout tabLayout;
    private ViewPager vpContenido;
    private ViewPagerAdapter adapter;
    private ArrayList<Fragment> arrayFragments;
    private ArrayList<String> arrayTitulos;

    NfcAdapter mNfcAdapter;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mReadWriteTagFilters;
    public boolean mWriteMode = false;
    public boolean mAuthenticationMode = false;
    public boolean ReadUIDMode = true;
    String[][]mTechList;

    AlertDialog mTagDialog;
    RadioGroup mRadioGroup;

    private TextView mTextMessage;

    int sectorNum = 01;
    String blockNum="04";
    String inverseBlockNum = "FB";
    String keysNum = "07";

    public String hexKeyA = "FFFFFFFFFFFF";
    public String hexKeyB = "FFFFFFFFFFFF";
    public String newHexKeyA = "";
    public String newHexKeyB = "";
    public int depositMoney = 0;
    public int currentMoney=0;
    public boolean read = false;

    public CartFragment cartFragment;
    public boolean isInCurrentDeposit = false;


    public String selectedKey = "";

    boolean keys = false;

    EditText mHexKeyA;
    EditText mHexKeyB;
    EditText mDatatoWrite;

    public int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, new MenuListFragment());
        fragmentTransaction.commit();

        mHexKeyA = findViewById(R.id.keyAEditText);
        mHexKeyB = findViewById(R.id.keyBEditText);
        mRadioGroup = findViewById(R.id.rBtnGrp);
        mDatatoWrite = findViewById(R.id.chargeEditText);
        tabLayout = findViewById(R.id.navigation);
        vpContenido = findViewById(R.id.vpContenido);

        cargarFragmentos();
        cargarTitulos();
        viewPagerEnTabLayout();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        /*if (mNfcAdapter == null)
        {
            Toast.makeText(this,
                    "Su dispositivo no soporta NFC. No se puede correr la aplicación.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }*/

        //checkNfcEnabled();

        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        IntentFilter mifareDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        try {
            mifareDetected.addDataType("application/com.itecstraining.mifarecontrol");
        } catch (IntentFilter.MalformedMimeTypeException e)
        {
            throw new RuntimeException("No se pudo añadir un tipo MIME.", e);
        }

        mReadWriteTagFilters = new IntentFilter[] { mifareDetected };


        mTechList = new String[][] { new String[] { MifareClassic.class.getName() } };

        resolveReadIntent(getIntent());
    }


    void resolveReadIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mfc = MifareClassic.get(tagFromIntent);

            if (ReadUIDMode)
            {
                String tipotag;
                String tamano;
                byte[] tagUID = tagFromIntent.getId();
                String hexUID = getHexString(tagUID, tagUID.length);
                Log.i(TAG, "Tag UID: " + hexUID);


                switch(mfc.getType())
                {
                    case 0: tipotag = "Mifare Classic"; break;
                    case 1: tipotag = "Mifare Plus"; break;
                    case 2: tipotag = "Mifare Pro"; break;
                    default: tipotag = "Mifare Desconocido"; break;
                }

                switch(mfc.getSize())
                {
                    case 1024: tamano = " (1K Bytes)"; break;
                    case 2048: tamano = " (2K Bytes)"; break;
                    case 4096: tamano = " (4K Bytes)"; break;
                    case 320: tamano = " (MINI - 320 Bytes)"; break;
                    default: tamano = " (Tamaño desconocido)"; break;
                }

                Log.i(TAG, "Card Type: " + tipotag + tamano);


            } else
            {
                try {
                    mfc.connect();
                    boolean auth;
                    String hexkey;

                    int sector = mfc.blockToSector(Integer.parseInt(blockNum));
                    byte[] datakey;


                    if (selectedKey.equals("A")){
                        hexkey = hexKeyA;
                        datakey = hexStringToByteArray(hexkey);
                        auth = mfc.authenticateSectorWithKeyA(sector, datakey);
                    }
                    else if (selectedKey.equals("B")){
                        hexkey = hexKeyB;
                        datakey = hexStringToByteArray(hexkey);
                        auth = mfc.authenticateSectorWithKeyB(sector, datakey);
                    }
                    else {
                        Toast.makeText(this,
                                "Primero necesitas auntenticar el tag!",
                                Toast.LENGTH_LONG).show();
                        mfc.close();
                        return;
                    }

                    if(auth){
                        int bloque =Integer.parseInt(blockNum);
                        byte[] dataread = mfc.readBlock(bloque);

                        String blockread = getHexString(dataread, dataread.length);
                        Log.i(TAG, "Bloque Leido: " + blockread);
                        currentMoney = Integer.parseInt(hexToDecimal(blockread));
                        saveUserInformation(currentMoney);

                        mTagDialog.cancel();

                        if(cartFragment != null) {
                            cartFragment.currentMoneyText.setText(Integer.toString(Integer.parseInt(hexToDecimal(blockread))));
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                this)
                                .setTitle("Saldo")
                                .setMessage("Los tickets en esta tarjeta son: " + hexToDecimal(blockread))
                                .setCancelable(true)
                                .setNegativeButton("Cancelar",
                                        (dialog, id) -> dialog.cancel())
                                .setOnCancelListener(dialog -> {
                                });
                        read = true;
                        mTagDialog = builder.create();
                        mTagDialog.show();
                    }else{
                        Toast.makeText(this,
                                "FALLO en lectura de Bloque debido a autentificación fallida.",
                                Toast.LENGTH_LONG).show();
                    }
                    mfc.close();
                }catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }

        }
    }

    private void saveUserInformation( int updatedMoney){
        UserInformation userInformation = new UserInformation(0.0, updatedMoney);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(this, "Información guardada", Toast.LENGTH_SHORT).show();
    }

    void resolveWriteIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mfc = MifareClassic.get(tagFromIntent);

            try {
                mfc.connect();
                boolean auth;
                String hexkey;
                int bloque;
                int sector;
                if(keys){
                    bloque = Integer.parseInt(keysNum);
                }else {
                    bloque = Integer.parseInt(blockNum);
                }

                sector = mfc.blockToSector(bloque);
                byte[] datakey;

                if (selectedKey.equals("A")){
                    hexkey = hexKeyA;
                    datakey = hexStringToByteArray(hexkey);
                    auth = mfc.authenticateSectorWithKeyA(sector, datakey);
                }
                else if (selectedKey.equals("B")){
                    hexkey = hexKeyB;
                    datakey = hexStringToByteArray(hexkey);
                    auth = mfc.authenticateSectorWithKeyB(sector, datakey);
                }
                else {
                    Toast.makeText(this,
                            "Necesitas auntenticar el tag!",
                            Toast.LENGTH_LONG).show();
                    mfc.close();
                    return;
                }

                if(auth){
                    if(keys){
                        String strdata = newHexKeyA + "FF078069"+newHexKeyB;
                        byte[] datatowrite = hexStringToByteArray(strdata);
                        mfc.writeBlock(bloque, datatowrite);

                        Toast.makeText(this,
                                "Cambio de llaves exitoso.",
                                Toast.LENGTH_LONG).show();
                    }else {
                        if(read){
                            int moneyFinal = depositMoney + currentMoney ;
                            String strdata = decimalToHex(moneyFinal);
                            Log.e("DEBUG_TAG", "Final money: " + moneyFinal);

                            byte[] datatowrite = hexStringToByteArray(strdata);
                            mfc.writeBlock(bloque, datatowrite);
                            currentMoney = 0;

                            isInCurrentDeposit = false;
                            ResourcesSingleton.getInstance().clearCart();
                            if(cartFragment != null) {
                                cartFragment.removeProducts();
                            }

                            read = false;

                            Toast.makeText(this,
                                    "Deposito/Pago exitoso.",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(this,
                                    "Primero debes leer el saldo!.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }



                }else{
                    Toast.makeText(this,
                            "FALLO en escritura de Bloque debido a autentificación fallida.",
                            Toast.LENGTH_LONG).show();
                }

                mfc.close();
                mTagDialog.cancel();

            }catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        }
    }

    void resolveAuthIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mfc = MifareClassic.get(tagFromIntent);

            try {
                mfc.connect();
                boolean auth;
                String hexkey;
                int sector = sectorNum;
                byte[] datakey;


                if (selectedKey.equals("A")){
                    hexkey = hexKeyA;
                    datakey = hexStringToByteArray(hexkey);
                    auth = mfc.authenticateSectorWithKeyA(sector, datakey);
                }
                else if (selectedKey.equals("B")){
                    hexkey = hexKeyB;
                    datakey = hexStringToByteArray(hexkey);
                    auth = mfc.authenticateSectorWithKeyB(sector, datakey);
                }
                else {
                    Toast.makeText(this,
                            "°Seleccionar llave A o B!",
                            Toast.LENGTH_LONG).show();
                    mfc.close();
                    return;
                }

                if(auth){
                    Toast.makeText(this,
                            "Autentificación de sector EXITOSA.",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,
                            "Autentificación de sector FALLIDA.",
                            Toast.LENGTH_LONG).show();
                }
                mfc.close();
            }catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //checkNfcEnabled();

        Log.d(TAG, "onResume: " + getIntent());

        //mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mReadWriteTagFilters, mTechList);
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        Log.d(TAG, "onNewIntent: " + intent);
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);

        if (mAuthenticationMode)
        {
            resolveAuthIntent(intent);
            mTagDialog.cancel();
        }
        else if (!mWriteMode)
        {
            resolveReadIntent(intent);
        } else
        {
            resolveWriteIntent(intent);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause: " + getIntent());
        //mNfcAdapter.disableForegroundDispatch(this);

    }


    public void enableTagWriteMode()
    {
        mWriteMode = true;
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                mReadWriteTagFilters, mTechList);
    }

    public void enableTagReadMode()
    {
        mWriteMode = false;
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                mReadWriteTagFilters, mTechList);
    }

    public void enableTagAuthMode()
    {
        mAuthenticationMode = true;
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                mReadWriteTagFilters, mTechList);
    }

    private View.OnClickListener mTagAuthenticate = new View.OnClickListener()
    {
        @Override
        public void onClick(View arg0)
        {

            enableTagAuthMode();

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this)
                    .setTitle(getString(R.string.ready_to_authenticate))
                    .setMessage(getString(R.string.ready_to_authenticate_instructions))
                    .setCancelable(true)
                    .setNegativeButton("Cancelar",
                            (dialog, id) -> dialog.cancel())
                    .setOnCancelListener(dialog -> mAuthenticationMode = false);
            mTagDialog = builder.create();
            mTagDialog.show();
        }
    };

    /*private void checkNfcEnabled()
    {
        Boolean nfcEnabled = mNfcAdapter.isEnabled();
        if (!nfcEnabled)
        {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.warning_nfc_is_off))
                    .setMessage(getString(R.string.turn_on_nfc))
                    .setCancelable(false)
                    .setPositiveButton("Actualizar Settings",
                            (dialog, id) -> startActivity(new Intent(
                                    android.provider.Settings.ACTION_WIRELESS_SETTINGS))).create().show();
        }
    }*/

    public static String getHexString(byte[] b, int length)
    {
        String result = "";
        Locale loc = Locale.getDefault();

        for (int i = 0; i < length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
            result += " ";
        }
        return result.toUpperCase(loc);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public String hexToDecimal(String hex){
        String digits = "0123456789ABCDEF";
        hex = hex.replaceAll("\\s+","");
        hex = hex.substring(4,8);
        Log.e("SALDO",hex);
        int val = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val+"";
    }

    public String decimalToHex(int decimal){
        String numDec="";
        String hex = Integer.toHexString(decimal)+"";
        String zeros="";
        for(int i=0; i< (8-hex.length());i++){
            zeros+="0";
        }
        hex = zeros + hex;
        String inverseHex = "";
        if(decimal != 0){
            inverseHex = Integer.toHexString(-1*decimal-1);
        }else {
            inverseHex = "FFFFFFFF";
        }
        String sectorNumS = blockNum;
        String sectorNumInv = inverseBlockNum;
        numDec = hex + inverseHex + hex + sectorNumS + sectorNumInv + sectorNumS + sectorNumInv;
        return numDec;

    }

    public void onReadMoney(View arg0)
    {

        enableTagReadMode();
        ReadUIDMode = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(
                this)
                .setTitle(getString(R.string.ready_to_read))
                .setMessage(getString(R.string.ready_to_read_instructions))
                .setCancelable(true)
                .setNegativeButton("Cancelar",
                        (dialog, id) -> dialog.cancel())
                .setOnCancelListener(dialog -> {
                    enableTagReadMode();
                    ReadUIDMode = true;
                });
        mTagDialog = builder.create();
        mTagDialog.show();
    }


    public void onDeposit(View arg0)
    {

        enableTagWriteMode();
        keys = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this)
                .setTitle(getString(R.string.ready_to_write))
                .setMessage(getString(R.string.ready_to_write_instructions))
                .setCancelable(true)
                .setNegativeButton("Cancelar",
                        (dialog, id) -> dialog.cancel())
                .setOnCancelListener(dialog -> enableTagReadMode());
        mTagDialog = builder.create();
        mTagDialog.show();
    }

    public void setDepositMoney(int money){
        depositMoney = money;
    }

    public void changeKeys(View arg0)
    {
        keys = true;
        enableTagWriteMode();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this)
                .setTitle("Listo para cambiar las llaves!")
                .setMessage("Acerque el Tag para cambiar a las nuevas llaves de auntenticacion")
                .setCancelable(true)
                .setNegativeButton("Cancelar",
                        (dialog, id) -> dialog.cancel())
                .setOnCancelListener(dialog -> enableTagReadMode());
        mTagDialog = builder.create();
        mTagDialog.show();
    }


    public void Aunthenticate(View arg0)
    {

        enableTagAuthMode();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this)
                .setTitle(getString(R.string.ready_to_authenticate))
                .setMessage(getString(R.string.ready_to_authenticate_instructions))
                .setCancelable(true)
                .setNegativeButton("Cancelar",
                        (dialog, id) -> dialog.cancel())
                .setOnCancelListener(dialog -> mAuthenticationMode = false);
        mTagDialog = builder.create();
        mTagDialog.show();
    }

    private void cargarFragmentos(){
        arrayFragments = new ArrayList<>();
        arrayFragments.add(new MenuListFragment());
        arrayFragments.add(new CartFragment());
        arrayFragments.add(new AdminFragment());
        arrayFragments.add(new LogoutFragment());
    }

    private void cargarTitulos(){
        arrayTitulos = new ArrayList<>();
        arrayTitulos.add("Menú");
        arrayTitulos.add("Carrito");
        arrayTitulos.add("Recargar");
        arrayTitulos.add("Logout");
    }

    private void viewPagerEnTabLayout(){
        adapter = new ViewPagerAdapter(getFragmentManager(), arrayFragments, arrayTitulos);
        vpContenido.setAdapter(adapter);
    }

}

