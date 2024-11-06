package com.example.javibotonera;

import com.example.javibotonera.ThreadManager;
import com.example.javibotonera.ConnectedThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Dispositivos extends AppCompatActivity {
    private static final String TAG = "Dispositivos";
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 2;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private ArrayList<String> mNameDevices = new ArrayList<>();
    private ArrayAdapter<String> deviceAdapter;
    private static BluetoothAdapter mBtAdapter;
    private static BluetoothSocket btSocket;
    private BluetoothDevice DispositivoSeleccionado;
    private ConnectedThread MyConexionBT;


    Button IdBtnBuscar, IdBtnConectar, IdBtnDesconectar, IdBtnGoBotonera;
    Spinner IdDisEncontrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos);

        requestBluetoothConnectPermission();
        requestLocationPermission();

        IdBtnBuscar = findViewById(R.id.IdBtnBuscar);
        IdBtnConectar = findViewById(R.id.IdBtnConectar);
        IdBtnDesconectar = findViewById(R.id.IdBtnDesconectar);
        IdBtnGoBotonera = findViewById(R.id.IdBtnGoBotonera);
        IdDisEncontrados = findViewById(R.id.IdMySpinner);

        deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mNameDevices);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IdDisEncontrados.setAdapter(deviceAdapter);

        IdBtnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DispositivosVinculados();
            }
        });
        IdBtnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConectarDispBT();
            }
        });

        IdBtnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btSocket!=null){
                    try{btSocket.close();}
                    catch(IOException e) {
                        Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }
        });
        IdBtnGoBotonera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Dispositivos.this, MainActivity.class);
                Dispositivos.this.startActivity(myIntent);
            }
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Dispositivos.REQUEST_ENABLE_BT) {
                        Log.d(TAG, "ACTIVIDAD REGISTRADA");
                        //Toast.makeText(getBaseContext(), "ACTIVIDAD REGISTRADA", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    public void DispositivosVinculados() {
        if(!mNameDevices.isEmpty()){    // Si la lista tiene dispositivos encontrados
            mNameDevices.clear();       // Limpiar lista
        }
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            showToast("Bluetooth no disponible en este dispositivo.");
            finish();
            return;
        }

        if (!mBtAdapter.isEnabled()) {  // Si el adaptador bluetooth no esta habilitado
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            someActivityResultLauncher.launch(enableBtIntent);  // Trata de habilitar el bluetooth en el smartphone
        }

        IdDisEncontrados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                DispositivoSeleccionado = getBluetoothDeviceByName(mNameDevices.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {  // Llamado cuando no se selecciona nada en la lista
                DispositivoSeleccionado = null;
            }
        });

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                mNameDevices.add(device.getName());
            }
            deviceAdapter.notifyDataSetChanged();
        } else {
            showToast("No hay dispositivos Bluetooth emparejados.");
        }
    }

    // Agrega este método para solicitar el permiso
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
    }

    // Agrega este método para solicitar el permiso
    private void requestBluetoothConnectPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_CONNECT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso concedido, ahora puedes utilizar funciones de Bluetooth que requieran BLUETOOTH_CONNECT");
            } else {
                Log.d(TAG, "Permiso denegado, debes manejar este caso según tus necesidades");
            }
        }
    }

    private BluetoothDevice getBluetoothDeviceByName(String name) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, " ----->>>>> ActivityCompat.checkSelfPermission");
        }
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals(name)) {
                return device;
            }
        }
        return null;
    }

    private void ConectarDispBT() {
        if (DispositivoSeleccionado == null) {
            showToast("Selecciona un dispositivo Bluetooth.");
            return;
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            btSocket = DispositivoSeleccionado.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
            btSocket.connect();
            MyConexionBT = new ConnectedThread(btSocket, mBtAdapter, Dispositivos.this, this);
            ThreadManager.getInstance().setConnectedThread(MyConexionBT);
            MyConexionBT.start();
            showToast("Conexión exitosa.");
            IdBtnGoBotonera.setEnabled(true);
        } catch (IOException e) {
            showToast("Error al conectar con el dispositivo.");
        }
    }


    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

