package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
    TextView text;
    long startTime;
    long endTime;
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
        text = findViewById(R.id.textView);

        // Initialize TCP connection
        initializeTCPConnection();

        button_forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    text.setText("Forward");
                    sendCommand("F");
                    startTime = System.currentTimeMillis();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    text.setText("Stop");
                    sendCommand("S" + (System.currentTimeMillis() - startTime));
                }
                return false;
            }
        });

        button_backward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    text.setText("Backward");
                    sendCommand("B");
                    startTime = System.currentTimeMillis();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    text.setText("Stop");
                    sendCommand("S" + (System.currentTimeMillis() - startTime));
                }
                return false;
            }
        });

        button_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    text.setText("Left");
                    sendCommand("L");
                    startTime = System.currentTimeMillis();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    text.setText("Stop");
                    sendCommand("S" + (System.currentTimeMillis() - startTime));
                }
                return false;
            }
        });

        button_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    text.setText("Right");
                    sendCommand("R");
                    startTime = System.currentTimeMillis();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    text.setText("Stop");
                    sendCommand("S" + (System.currentTimeMillis() - startTime));
                }
                return false;
            }
        });
    }

    private void initializeTCPConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress("127.0.0.1", 5000));
                    out = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendCommand(final String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (out != null) {
                        out.write(command.getBytes());
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}