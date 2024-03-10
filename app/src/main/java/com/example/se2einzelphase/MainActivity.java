package com.example.se2einzelphase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btn = findViewById(R.id.button);
        TextInputEditText input = findViewById(R.id.textInputEditText);
        TextView text = findViewById(R.id.textView);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket("se2-submission.aau.at", 20080);
                            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            // Write the message to the server
                            out.write(input.getText().toString());
                            out.newLine(); // Important: BufferedWriter requires newline to flush
                            out.flush();

                            // Read the response
                            String messageReceived = in.readLine();

                            // Since we cannot update the UI from a different thread, we need to run on the main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    text.setText(messageReceived);
                                }
                            });

                            // Close streams and socket
                            out.close();
                            in.close();
                            socket.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }
}