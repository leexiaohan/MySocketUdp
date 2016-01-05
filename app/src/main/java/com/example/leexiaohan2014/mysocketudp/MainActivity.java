package com.example.leexiaohan2014.mysocketudp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
    TextView textListen;
    EditText port2;
    String ipaddress;
    String portaddress;
    String portaddress2;
    String stringToSend;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        port2 = (EditText) findViewById(R.id.port2);
        editText = (EditText) findViewById(R.id.editText);
        textListen = (TextView) findViewById(R.id.textListen);

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
                    text.append("-------IP or PORT empty-------\n");
                }

            }
        });


        findViewById(R.id.listen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portaddress2 = port2.getText().toString();
                listen();
                //textListen.append("text received from server" + result);
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
                        socket = new DatagramSocket(12222);
                        socket.setReuseAddress(true);
                        //socket.bind(new InetSocketAddress(12222));
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

    public void listen() {

        AsyncTask<Void, String,Void> read = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //创建一个DatagramSocket对象，并指定监听的端口号
                    DatagramSocket socket=null;
                    if (socket == null){
                        socket = new DatagramSocket(null);
                        socket.setReuseAddress(true);
                        socket.bind(new InetSocketAddress(Integer.parseInt(portaddress2)));
                    }
                    //DatagramSocket socket = new DatagramSocket(4567);

                    byte data [] = new byte[4096];

                    //创建一个空的DatagramPacket对象

                    DatagramPacket packet = new DatagramPacket(data,data.length);

                    //使用receive方法接收客户端所发送的数据，

                    //如果客户端没有发送数据，该进程就停滞在这里
                    for (int i = 0; i < 10; i++) {
                        socket.receive(packet);
                        result = new String(packet.getData(),packet.getOffset(), packet.getLength());
                        publishProgress(result);
                    }
                    socket.close();

                    //System.out.println("result--->" + result);
                    //textListen.append("text received from server" + result + "\n");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                textListen.append("text received: "+values[0]);
                //textListen.append("text received from server" + result);
                super.onProgressUpdate(values);

            }
        };
        read.execute();
    }
}
