package com.example.javibotonera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

//import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    // Inicializaciones Bluetooth
    /*private static final String TAG = "MainActivity";
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 2;
    private ArrayList<String> mNameDevices = new ArrayList<>();*/
    private ConnectedThread MyConexionBT = ThreadManager.getInstance().getConnectedThread();
    //private BluetoothSocket btSocket = MyConexionBT.btSocket;
    //public BluetoothAdapter mBtAdapter;

    final int handlerState = 0;

    // Variables Botonera
    Button BtnEmergencia, BtnAxis, BtnControl, BtnMas, BtnMenos, BtnClear,
           BtnSpeed, BtnMove, BtnRecordP, BtnOpenClose, BtnAbort, BtnRun, BtnExecute,
           Btn1, Btn2, Btn3, Btn4, Btn5, Btn6, Btn7, Btn8, Btn9, Btn0;
    TextView tv1, tv2, tv3, tv4;
    boolean disabled = false;
    char group = 'A';
    int speedA = 50, speedB = 50, speedL = 550, move = 0, moveL = 0, resta = 0;
    String mode;
    //private boolean pressed = false;



    //private BluetoothAdapter btAdapter = null;
    //private StringBuilder DataStringIN = new StringBuilder();
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para direccion MAC
    //private static String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recepcion de mensajes a android
        MyConexionBT.bluetoothIn = new Handler() {
            public void handleMessage(@NonNull android.os.Message msg){
                if(msg.what == handlerState){
                    String Message = (String) msg.obj;
                    // Message handling code
                    tv1.setText(Message);
                }
            }
        };

        VerificarEstadoBT();

        // Interface en el layout
        // Botones Funciones:
        BtnEmergencia = findViewById(R.id.IdBtnEmergency);
        BtnAxis = findViewById(R.id.IdBtnAxis);
        BtnControl = findViewById(R.id.IdBtnControl);
        BtnMas = findViewById(R.id.IdBtnMas);
        BtnMenos = findViewById(R.id.IdBtnMenos);
        BtnClear = findViewById(R.id.IdBtnClear);

        BtnSpeed = findViewById(R.id.IdBtnSpeed);
        BtnMove = findViewById(R.id.IdBtnMove);
        BtnRecordP = findViewById(R.id.IdBtnRecordP);
        BtnOpenClose = findViewById(R.id.IdBtnOpenClose);
        BtnAbort = findViewById(R.id.IdBtnAbort);
        BtnRun = findViewById(R.id.IdBtnRun);
        BtnExecute = findViewById(R.id.IdBtnExecute);

        // Botones Numeros:
        Btn1 = findViewById(R.id.IdBtn1);
        Btn2 = findViewById(R.id.IdBtn2);
        Btn3 = findViewById(R.id.IdBtn3);
        Btn4 = findViewById(R.id.IdBtn4);
        Btn5 = findViewById(R.id.IdBtn5);
        Btn6 = findViewById(R.id.IdBtn6);
        Btn7 = findViewById(R.id.IdBtn7);
        Btn8 = findViewById(R.id.IdBtn8);
        Btn9 = findViewById(R.id.IdBtn9);
        Btn0 = findViewById(R.id.IdBtn0);

        // Textos:
        tv1 = findViewById(R.id.tv1);
        tv1.setText("");
        tv2 = findViewById(R.id.tv2);
        tv2.setText("");
        tv3 = findViewById(R.id.tv3);
        tv3.setText("");
        tv4 = findViewById(R.id.tv4);
        tv4.setText(String.format("group:%s   ax:%s   %s", group, "――", mode));

        mode = getString(R.string.joints);

        BtnEmergencia.setOnClickListener(view -> {
            // Codigo de lo que hace el boton de emergencia
        });
        BtnAxis.setOnClickListener(view -> {
            Button b = (Button)view;
            String text = b.getText().toString();
            state(text);
        });
        BtnControl.setOnClickListener(this::control);

        BtnMas.setOnTouchListener(new View.OnTouchListener() {
            private final Handler handler = new Handler();
            private Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    MyConexionBT.write('+'); // Your repeated function call//
                    handler.postDelayed(this, 250); // Repeat every 100ms } };
                }
            };
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        handler.post(runnable);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(runnable);
                        break;
                }
                return true;
            }
        });

        BtnMenos.setOnTouchListener(new View.OnTouchListener() {
            private final Handler handler = new Handler();
            private Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    MyConexionBT.write('-'); // Your repeated function call//
                    handler.postDelayed(this, 250); // Repeat every 100ms } };
                }
            };
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        handler.post(runnable);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(runnable);
                        break;
                }
                return true;
            }
        });

        BtnSpeed.setOnClickListener(this::speed);
        BtnMove.setOnClickListener(this::move);
        BtnRecordP.setOnClickListener(this::record);
        BtnOpenClose.setOnClickListener(view -> {
            if(true){ /* Chequear si esta abierto o cerrado */
                open(view);
            }else{
                close(view);
            }
        });
        BtnAbort.setOnClickListener(this::abort);
        BtnRun.setOnClickListener(this::run);
        BtnExecute.setOnClickListener(this::enter);
        BtnClear.setOnClickListener(this::clr);

        Btn1.setOnClickListener(this::sendNumber);
        Btn2.setOnClickListener(this::sendNumber);
        Btn3.setOnClickListener(this::sendNumber);
        Btn4.setOnClickListener(this::sendNumber);
        Btn5.setOnClickListener(this::sendNumber);
        Btn6.setOnClickListener(this::sendNumber);
        Btn7.setOnClickListener(this::sendNumber);
        Btn8.setOnClickListener(this::sendNumber);
        Btn9.setOnClickListener(this::sendNumber);
        Btn0.setOnClickListener(this::sendNumber);
    }

    public void state(String text)  {
        if(text.equals(getString(R.string.joints)) && tv3.getText().toString().isEmpty()){
            tv1.setText("");
            mode = getString(R.string.xyz);
            tv4.setText(String.format("group:%s   ax:%s   %s", group, "――", mode));
            BtnAxis.setText(getString(R.string.xyz));
            Btn1.setText(getString(R.string.x));
            Btn2.setText(getString(R.string.y));
            Btn3.setText(getString(R.string.z));
            Btn4.setText(getString(R.string.p));
            Btn5.setText(getString(R.string.r));
            BtnAxis.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            BtnControl.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            Btn1.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            Btn2.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            Btn3.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            Btn4.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            Btn5.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
        }else{
            tv1.setText("");
            mode = getString(R.string.joints);
            tv4.setText(String.format("group:%s   ax:%s   %s", group, "――", mode));
            BtnAxis.setText(getString(R.string.joints));
            Btn1.setText(getString(R.string.uno));
            Btn2.setText(getString(R.string.dos));
            Btn3.setText(getString(R.string.tres));
            Btn4.setText(getString(R.string.cuatro));
            Btn5.setText(getString(R.string.cinco));
            BtnAxis.setBackgroundColor(ContextCompat.getColor(this, R.color.cyan));
            BtnControl.setBackgroundColor(ContextCompat.getColor(this, R.color.cyan));
            Btn1.setBackgroundColor(ContextCompat.getColor(this, R.color.cyan));
            Btn2.setBackgroundColor(ContextCompat.getColor(this, R.color.cyan));
            Btn3.setBackgroundColor(ContextCompat.getColor(this, R.color.cyan));
            Btn4.setBackgroundColor(ContextCompat.getColor(this, R.color.cyan));
            Btn5.setBackgroundColor(ContextCompat.getColor(this, R.color.cyan));
        }
    }

    public void speed(View view){
        tv1.setText("");
        resta = 0;
        if(mode.equals("XYZ")){
            state("XYZ");
            tv2.setText(String.format("Speeds %s%% L", speedL));
            tv3.setText(String.format("Speeds (%s%%) L", speedL));
        }else if(group == 'A'){
            tv2.setText(String.format("Speeds %s %s%% ", group, speedA));
            tv3.setText(String.format("Speeds (%s %s%%)", group, speedA));
        }else if(group == 'B'){
            tv2.setText(String.format("Speeds %s %s%% ", group, speedB));
            tv3.setText(String.format("Speeds (%s %s%%)", group, speedB));
        }
    }

    public void move(View view){
        tv1.setText("");
        resta = 0;
        if(mode.equals("XYZ")){
            state("XYZ");
            tv3.setText(String.format("%s", "moveL "));
        }else{
            tv3.setText(String.format("%s", "MOVE "));
        }
    }

    public void run(View view){
        tv1.setText("");
        resta = 0;
        if(mode.equals("XYZ")){
            state("XYZ");
        }
        tv3.setText(String.format("%s", "run "));
    }

    public void record(View view){
        tv1.setText("");
        resta = 0;
        if(mode.equals("XYZ")){
            state("XYZ");
        }
        tv3.setText(String.format("%s", "Here "));
    }

    public void control(View view){
        tv1.setText("");
        resta = 0;
        String s3 = tv3.getText().toString();
        if(s3.isEmpty() || s3.equals("Con All <enter>")){
            tv3.setText(String.format("%s", "Coff All <enter>"));
        }else{
            tv3.setText(String.format("%s", "Con All <enter>"));
        }
    }

    public void abort(View view){
        MyConexionBT.write('a');
        tv1.setText(String.format("%s", "ALL PROGRAMS ABORTED "));
        resta = 0;
    }

    public void sendNumber(View view){
        Button b = (Button)view;
        String text = b.getText().toString();

        if(tv3.getText().toString().isEmpty()){
            // Si no se esta usando speed simplemente muestra el numero por pantalla
            if(!text.matches("[XYZPR]")){
                mode = getString(R.string.joints);
                if(text.equals("7")) group = 'B';
                else group = 'A';
            }else{
                mode = getString(R.string.xyz);
                group = 'A';
            }

            tv1.setText(String.format("Axis %s  Selected", text));
            tv4.setText(String.format("group:%s     ax:%s       %s", group, text, mode));

        }else if(tv3.getText().toString().matches("Speed.*")){

            // se añade el numero al final de string
            tv3.setText(String.format("%s%s", tv3.getText().toString(), text));
            resta++;

        }else if(tv3.getText().toString().matches("MOVE.*|moveL.*")){

            // se añade el numero al final de string
            tv3.setText(String.format("%s%s", tv3.getText().toString(), text));
            resta++;

        }else if(tv3.getText().toString().matches("run.*")){

            // se añade el numero al final de string
            tv3.setText(String.format("%s%s", tv3.getText().toString(), text));
            resta++;

        }else if(tv3.getText().toString().matches("Here.*")){

            // se añade el numero al final de string
            tv3.setText(String.format("%s%s", tv3.getText().toString(), text));
            resta++;

        }
    }

    public void enter(View view){
        String s3 = tv3.getText().toString();

        if(s3.matches("Speed.*")){

            String speed = String.format("%s", s3.substring(s3.length()-resta));
            resta = 0;

            if(!speed.isEmpty()) {
                int num = Integer.parseInt(speed);

                if (num > 100 || num < 1) {
                    // marcar error
                    tv1.setText(String.format("%s", "INVALID DATA"));
                } else {
                    tv1.setText("");
                    if(s3.matches("SpeedL.*")){
                        speedL = num;
                    }else if (group == 'A') {
                        speedA = num;
                    } else if (group == 'B') {
                        speedB = num;
                    }
                }

                if(s3.matches("SpeedL.*")){
                    tv2.setText(String.format("Speed%s %s%% ", "L", num));
                }else {
                    tv2.setText(String.format("Speed%s %s%% ", group, num));
                }
            }

        }else if(s3.matches("MOVE.*")){

            String movement = String.format("%s", s3.substring(s3.length()-resta));
            resta = 0;

            if(!movement.isEmpty()) {
                int num = Integer.parseInt(movement);

                if (num > 10 || num < 0) {
                    // marcar error
                    tv1.setText(String.format("%s", "POS NOT FOUND"));
                } else {
                    tv1.setText(String.format("%s", "MOVEMENT ABORTED"));
                    if(s3.matches("moveL.*")){
                        moveL = num;
                    }else if (group == 'A') {
                        move = num;
                    }
                }

                if(s3.matches("moveL.*")){
                    tv2.setText(String.format("moveL %s", num));
                }else {
                    tv2.setText(String.format("MOVE %s", num));
                }
            }

        }else if(s3.matches("run.*")){

            String run = String.format("%s", s3.substring(s3.length()-resta));
            resta = 0;

            if(!run.isEmpty()) {
                int num = Integer.parseInt(run);

                if (num==0){
                    tv1.setText(String.format("%s", "Homing..."));
                }else if (num > 10 || num < 0) {
                    // marcar error
                    tv1.setText(String.format("%s", "PRG NOT FOUND"));
                }else {
                    tv1.setText(String.format("%s", "Homing complete"));
                }

                tv2.setText(String.format("Run %s", num));
            }

        }else if(s3.matches("Here.*")){

            String record = String.format("%s", s3.substring(s3.length()-resta));
            resta = 0;

            if(!record.isEmpty()) {
                int num = Integer.parseInt(record);
                tv1.setText(String.format("%s", "DONE"));
                tv2.setText(String.format("Here %s", num));
            }

        }else if(s3.equals("Coff All <enter>")){
            resta = 0;
            disabled = true;
            tv1.setText(String.format("%s", "CONTROL DISABLED"));
            tv2.setText(String.format("%s", s3));

        }else if(s3.equals("Con All <enter>")){
            resta = 0;
            disabled = false;
            tv1.setText(String.format("%s", "CONTROL ENABLED"));
            tv2.setText(String.format("%s", s3));

        }else {
            tv1.setText("");
        }
        String GetData = String.format("%s",tv3.getText().toString());
        MyConexionBT.write(GetData);
        tv3.setText("");

    }

    public void open(View view){
        MyConexionBT.write("OPEN");
    }

    public void close(View view){
        MyConexionBT.write("CLOSE");
    }

    public void clr(View view){
        tv3.setText("");
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    /*@Override
    public void onResume() {    // Al expandir la aplicacion nuevamente
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(Dispositivos.EXTRA_DEVICE_ADDRESS);
        //Setea la direccion MAC
        BluetoothDevice device = MyConexionBT.mBtAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                btSocket.connect();
                //Toast.makeText(getBaseContext(), "CONEXION EXITOSA", Toast.LENGTH_SHORT).show();

                //return;
            }
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Toast.makeText(getBaseContext(), "Could not close Socket.", Toast.LENGTH_LONG).show();
            }
        }
        MyConexionBT = new ConnectedThread(btSocket, mBtAdapter, MainActivity.this, this);
        MyConexionBT.start();
    }
    */

    @Override
    public void onPause() {     // Al reducir la aplicación
        super.onPause();
        try { // Cuando se sale de la aplicación esta parte permite que no se deje abierto el socket
            MyConexionBT.btSocket.close();
        } catch (IOException e2) {
            Toast.makeText(getBaseContext(), "Could not close Socket.", Toast.LENGTH_LONG).show();
        }
    }

    //Comprueba que el dispositivo Bluetooth
    //está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if (MyConexionBT.mBtAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (MyConexionBT.mBtAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    startActivityForResult(enableBtIntent, 1);
                    //return;
                }

            }
        }
    }
}