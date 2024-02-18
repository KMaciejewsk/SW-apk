package com.example.myapplication;

import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.MotionEvent;
import android.os.Bundle;
import android.widget.Button;

import java.io.DataOutputStream;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Button button_forward;
    Button button_backward;
    Button button_left;
    Button button_right;
    Button button_go_back;
    Button button_reconnect;
    TextView text;
    Socket socket;
    OutputStream out;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_forward = findViewById(R.id.button);
        button_backward = findViewById(R.id.button2);
        button_left = findViewById(R.id.button3);
        button_right = findViewById(R.id.button4);
        button_go_back = findViewById(R.id.button5);
        button_reconnect = findViewById(R.id.button6);
        text = findViewById(R.id.textView);

        // Initialize TCP connection
        Log.d("TCP", "Initializing TCP connection");
        initializeTCPConnection();
        Log.d("TCP", "TCP connection initialized");

        button_forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    text.setText("Forward");
                    sendCommand("F>" + System.currentTimeMillis());
                    Log.d("Sent", "F>" + System.currentTimeMillis());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    text.setText("Stop");
                    sendCommand("S>" + System.currentTimeMillis());
                    Log.d("Sent", "S>" + System.currentTimeMillis());
                }
                return false;
            }
        });

        button_backward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    text.setText("Backward");
                    sendCommand("B>" + System.currentTimeMillis());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    text.setText("Stop");
                    sendCommand("S>" + System.currentTimeMillis());
                }
                return false;
            }
        });

        button_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    text.setText("Left");
                    sendCommand("L>" + System.currentTimeMillis());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    text.setText("Stop");
                    sendCommand("S>" + System.currentTimeMillis());
                }
                return false;
            }
        });

        button_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    text.setText("Right");
                    sendCommand("R>" + System.currentTimeMillis());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    text.setText("Stop");
                    sendCommand("S>" + System.currentTimeMillis());
                }
                return false;
            }
        });

        button_go_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    text.setText("Go back");
                    sendCommand("back");
                }

                return false;
            }
        });

        button_reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeTCPConnection();
            }
        });
    }

    private void initializeTCPConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress("192.168.1.42", 10000));
                    out = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendCommand(final String command) {
       //send command to server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.write(command.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}