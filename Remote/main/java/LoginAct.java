package com.example.joycombo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoginAct extends AppCompatActivity{

    EditText username,password;
    Button loginBtn,refreshBtn;
    Socket conn;
    Button fetchBtn;
    ListView loginListView;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent in=getIntent();
        String ip=in.getStringExtra("ip");
        String port=in.getStringExtra("port");
        int portNumber=Integer.parseInt(port);
        Handler  handler=new Handler();
        final ClientConnect cc=new ClientConnect(portNumber,ip);
        Thread t=new Thread(cc);
        t.start();
        loginListView=findViewById(R.id.LoginListView);
        loginListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(cc.getClientSocket().getOutputStream()));
                            bw.write(ClientConnect.songs.get(position)+"\n");
                            bw.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        loginBtn=findViewById(R.id.LoginBtn);
        fetchBtn=findViewById(R.id.fetchBtn);
        refreshBtn=findViewById(R.id.RefreshBtn);
        ProgressDialog pd=new ProgressDialog(LoginAct.this);
        pd.setMessage("Logging in");

        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,cc.songs);

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginAct.this, "connected to server", Toast.LENGTH_SHORT).show();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String un=username.getText().toString();
                String pass=password.getText().toString();

                try {

                    cc.writeToServer(un+","+pass);

                    //cc.flushWriter();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cc.songList();
                loginListView.setAdapter(adapter);
                Toast.makeText(LoginAct.this, ""+cc.songs.size(), Toast.LENGTH_SHORT).show();
                Log.e("sizeLogin", String.valueOf(cc.songs.size()));
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();
                //adapter.notify();
            }
        });


    }
}
