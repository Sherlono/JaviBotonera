package com.example.javibotonera;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    public ConnectedThread MyConexionBT;
    public BluetoothSocket btSocket;
    public BluetoothAdapter mBtAdapter;
    public Handler bluetoothIn;
    private android.content.Context context;
    private android.app.Activity activity;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket, BluetoothAdapter adapter, android.content.Context context, android.app.Activity activity)
    {
        this.btSocket = socket;
        this.mBtAdapter = adapter;
        this.context = context;
        this.activity = activity;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try
        {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(context, "Stream issue.",Toast.LENGTH_LONG).show();
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void write(char input) {
        //byte msgBuffer = (byte)input;
        try {
            mmOutStream.write((byte)input);
        } catch (IOException e) {
            Toast.makeText(context , "La Conexión fallo", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }
    public void write(String input)
    {
        try {
            mmOutStream.write(input.getBytes());
        }
        catch (IOException e)   // Si no es posible enviar datos se cierra la conexión
        {
            Toast.makeText(context , "La Conexión fallo", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        // Se mantiene en modo escucha para determinar el ingreso de datos
        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                String recieved = new String(buffer, 0, bytes);
                bluetoothIn.obtainMessage(0, recieved).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }
}

