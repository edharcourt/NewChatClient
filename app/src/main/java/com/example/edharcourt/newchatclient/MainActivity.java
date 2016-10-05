package com.example.edharcourt.newchatclient;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button connect = null;
    TextView ip_address = null;
    TextView receiving = null;

    ConnectionManager cm = null;
    Handler uiThreadHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect = (Button) findViewById(R.id.connect);
        ip_address = (TextView) findViewById(R.id.ip_address);
        receiving = (TextView) findViewById(R.id.receiving_text);
        uiThreadHandler = new Handler();

        ip_address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Fire off a thread that tries to connect to a server

                // Don't create a connection if we are already connected
                if (cm != null && cm.server_sock.isBound()) {
                    Toast.makeText(
                            MainActivity.this,
                            "Already connected",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (cm == null) {
                    cm = new ConnectionManager(MainActivity.this, uiThreadHandler,
                            ip_address.getText().toString());
                    new Thread(cm.client_thread).start();
                }
            }
        });




    }


}
