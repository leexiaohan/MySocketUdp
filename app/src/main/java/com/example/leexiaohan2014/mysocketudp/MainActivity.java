package com.example.leexiaohan2014.mysocketudp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class MainActivity extends ActionBarActivity {

    EditText ip;
    TextView textListen;
    String ipAddress;
    static int portLocal = 8888;
    static int portServer = 12345;
    String stringToSend;
    //String result;
    DatagramSocket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = (EditText) findViewById(R.id.ip);
        textListen = (TextView) findViewById(R.id.textListen);
        //Switch relay1 = (Switch)findViewById(R.id.switch1);
        //Switch relay2 = (Switch)findViewById(R.id.switch2);
        textListen.setMovementMethod(ScrollingMovementMethod.getInstance());
        //request----------------------------------------------------------------
        findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipAddress = ip.getText().toString();
                stringToSend = "request";
                textListen.append("send request to " + ipAddress + "\n");
                if (!(ipAddress.isEmpty())) {
                    send();
                    listen();
                }
                //else
                //   textListen.append("ip empty");

            }
        });
        findViewById(R.id.relaySet).setOnClickListener(new View.OnClickListener() {
            Switch relay1 = (Switch) findViewById(R.id.switch1);
            Switch relay2 = (Switch) findViewById(R.id.switch2);

            @Override
            public void onClick(View v) {
                ipAddress = ip.getText().toString();
                if (relay1.isChecked() && relay2.isChecked()) {
                    textListen.append("relay1,2 selected\n");
                    stringToSend = "1";
                } else if (!(relay1.isChecked() || relay2.isChecked())) {
                    textListen.append("relay1,2 not selected\n");
                    stringToSend = "2";
                } else if ((!relay1.isChecked()) && relay2.isChecked()) {
                    textListen.append("relay 1 not selected, relay2 selected\n");
                    stringToSend = "4";
                } else {
                    textListen.append("relay1 selected, relay2 not selected\n");
                    stringToSend = "3";
                }
                stringToSend = "relay_state," + stringToSend;
                textListen.append("send relay state to " + ipAddress + "\n");
                if (!(ipAddress.isEmpty())) {
                    send();
                    //listen();
                }
            }
        });
/*
        findViewById(R.id.listen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //portaddress2 = port2.getText().toString();
                listen();
                //textListen.append("text received from server" + result);
            }
        });
*/

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

                    socket = new DatagramSocket(8888);
                    socket.setReuseAddress(true);

                    //创建一个InetAddree
                    InetAddress serverAddress = InetAddress.getByName(ipAddress);
                    //String str = editText.getText().toString();  //这是要传输的数据
                    byte data [] = stringToSend.getBytes();  //把传输内容分解成字节
                    //创建一个DatagramPacket对象，并指定要讲这个数据包发送到网络当中的哪个、地址，以及端口号
                    DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, portServer);
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
                String result;
                try {
                    //创建一个DatagramSocket对象，并指定监听的端口号
                    //DatagramSocket socket=null;
                    socket = new DatagramSocket(null);
                    //  socket.setReuseAddress(true);
                    socket.bind(new InetSocketAddress(portLocal));

                    //DatagramSocket socket = new DatagramSocket(4567);

                    byte data [] = new byte[4096];

                    //创建一个空的DatagramPacket对象

                    DatagramPacket packet = new DatagramPacket(data,data.length);

                    //使用receive方法接收客户端所发送的数据，

                    //如果客户端没有发送数据，该进程就停滞在这里
                    // for (int i = 0; i < 10; i++) {
                        socket.receive(packet);
                        result = new String(packet.getData(),packet.getOffset(), packet.getLength());
                    result += "\n";
                        publishProgress(result);
                    //}
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
