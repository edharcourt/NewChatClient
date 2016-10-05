package com.example.edharcourt.newchatclient;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    Button connect = null;
    TextView ip_address = null;
    TextView receiving = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect = (Button) findViewById(R.id.connect);
        ip_address = (TextView) findViewById(R.id.ip_address);
        receiving = (TextView) findViewById(R.id.receiving_text);

        ip_address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

    }


}
