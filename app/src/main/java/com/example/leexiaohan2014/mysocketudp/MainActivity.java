package com.example.leexiaohan2014.mysocketudp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends ActionBarActivity {

    EditText ip;
    EditText port;
    EditText editText;
    String ipaddress;
    String portaddress;
    String stringToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        editText = (EditText) findViewById(R.id.editText);


        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ipaddress = ip.getText().toString();
                portaddress = port.getText().toString();
                stringToSend = editText.getText().toString();
                if (!((ipaddress.isEmpty())||(portaddress.isEmpty()))) {
                    send();
                }
                else {
                    //text.append("-------IP or PORT empty-------\n");
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void send() {
        AsyncTask<Void, String,Void> read = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //首先创建一个DatagramSocket对象
                    DatagramSocket socket=null;
                    if (socket == null){
                        socket = new DatagramSocket(null);
                        socket.setReuseAddress(true);
                        socket.bind(new InetSocketAddress(12222));
                    }

                    //创建一个InetAddree
                    InetAddress serverAddress = InetAddress.getByName(ipaddress);
                    //String str = editText.getText().toString();  //这是要传输的数据
                    byte data [] = stringToSend.getBytes();  //把传输内容分解成字节
                    //创建一个DatagramPacket对象，并指定要讲这个数据包发送到网络当中的哪个、地址，以及端口号
                    DatagramPacket packet = new DatagramPacket(data,data.length,serverAddress, Integer.parseInt(portaddress));
                    //调用socket对象的send方法，发送数据
                    socket.send(packet);
                    socket.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {

                super.onProgressUpdate(values);

            }
        };
        read.execute();

    }
}
