package com.example.joycombo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    EditText ipAddField,portNumberField;
    Button startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ipAddField=findViewById(R.id.ipAddField);
        portNumberField=findViewById(R.id.portNumberField);

        startBtn=findViewById(R.id.startBtn);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip= ipAddField.getText().toString();
                String port=portNumberField.getText().toString();

                Intent intent=new Intent(MainActivity.this,LoginAct.class);
                intent.putExtra("ip",ip);
                intent.putExtra("port",port);
                startActivity(intent);
            }
        });

    }
}
