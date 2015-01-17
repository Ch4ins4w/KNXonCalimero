package com.calimero.knx.knxoncalimero;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.calimero.knx.connection.knxobject.KnxComparableObject;
import com.calimero.knx.connection.knxobject.KnxFloatObject;
import com.calimero.knx.connection.sys.KnxCommunicationObject;

import java.util.Observable;
import java.util.Observer;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXAckTimeoutException;


public class MainActivity extends Activity implements Observer {
    public static boolean first = true;
    private KnxCommunicationObject knxComObj;

    //Gui-Elemente
    //EditText tfGatewayIP, tfSendHaupt, tfSendMitte, tfSendSub, tfRcvHaupt, tfRcvMitte, tfRcvSub, tfRcvValue, tfSendValue;
    EditText tfGatewayIP,tfHaupt, tfMitte, tfSub;
    TextView lux1,lux2,temperatur,wind;
    TextView tvConnectionStatus;
    ImageView wetter;
    Button sendButton, connectButton, readButton;
    GroupAddress rcvAdress, sendAdress;
    MainActivity thisMainActivity;
    GroupAddress windadr;
    GroupAddress tempadr;
    GroupAddress lichtadr;
    GroupAddress adr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wetterstation_layout);
        //setContentView(R.layout.start);
        thisMainActivity = this;
        //testConnection = new KnxBusConnection("", "192.168.10.28");


        wetter = (ImageView) findViewById(R.id.wetter);
        lux1 = (TextView) findViewById(R.id.lux1);
        lux2 = (TextView) findViewById(R.id.lux2);
        temperatur = (TextView) findViewById(R.id.temperatur);
        wind = (TextView) findViewById(R.id.wind);
        tvConnectionStatus = (TextView) findViewById(R.id.tvConnectionStatus);
        tfGatewayIP = (EditText) findViewById(R.id.tfGatewayIP);
        tfHaupt = (EditText) findViewById(R.id.tfHaupt);
        tfMitte = (EditText) findViewById(R.id.tfMitte);
        tfSub = (EditText) findViewById(R.id.tfSub);
        /*
        tfSendHaupt = (EditText) findViewById(R.id.tfSendHaupt);
        tfSendMitte = (EditText) findViewById(R.id.tfSendMittel);
        tfSendSub = (EditText) findViewById(R.id.tfSendSub);
        tfRcvHaupt = (EditText) findViewById(R.id.tfRcvHaupt);
        tfRcvMitte = (EditText) findViewById(R.id.tfRcvMittel);
        tfRcvSub = (EditText) findViewById(R.id.tfRcvSub);
        tfRcvValue = (EditText) findViewById(R.id.tfRcvValue);
        tfSendValue = (EditText) findViewById(R.id.tfSendValue);
        tvConnectionStatus = (TextView) findViewById(R.id.tvConnectionStatus);

        sendButton = (Button) findViewById(R.id.btnSend);
        sendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAdress = new GroupAddress(Integer.valueOf(tfSendHaupt.getText().toString()),
                        Integer.valueOf(tfSendMitte.getText().toString()),
                        Integer.valueOf(tfSendSub.getText().toString()));
                busActionContainer.push(new KnxBooleanObject(sendAdress, tfSendValue.getText().toString().equals("1"), false));
            }
        });

        */
        connectButton = (Button) findViewById(R.id.btnConnect);
        connectButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: gateway ip überprüfung
                //todo: Host Ip dynamisch bestimmen oder Gui element dafür implementieren
                try {
                    knxComObj = KnxCommunicationObject.getInstance("192.168.10.183",tfGatewayIP.getText().toString());
                    knxComObj.addObserver(thisMainActivity);
                    windadr = new GroupAddress(Integer.valueOf("3"),
                            Integer.valueOf("3"),
                            Integer.valueOf("2"));
                    tempadr = new GroupAddress(Integer.valueOf("3"),
                            Integer.valueOf("3"),
                            Integer.valueOf("1"));
                    lichtadr = new GroupAddress(Integer.valueOf("3"),
                            Integer.valueOf("3"),
                            Integer.valueOf("0"));
                    knxComObj.readPeriodicFloat(windadr, 1000);
                    knxComObj.readPeriodicFloat(tempadr, 1000);
                    knxComObj.readPeriodicFloat(lichtadr, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*Button closeButton = (Button) findViewById(R.id.btnConnect);
        closeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                testConnection.closeBus();
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("Bus closed");
            }
        });
        */
        readButton = (Button) findViewById(R.id.btnRead);
        readButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*rcvAdress = new GroupAddress(Integer.valueOf(tfHaupt.getText().toString()),
                        Integer.valueOf(tfMitte.getText().toString()),
                        Integer.valueOf(tfSub.getText().toString()));
                    */
                windadr = new GroupAddress(Integer.valueOf("3"),
                        Integer.valueOf("3"),
                        Integer.valueOf("2"));
                tempadr = new GroupAddress(Integer.valueOf("3"),
                        Integer.valueOf("3"),
                        Integer.valueOf("1"));
                lichtadr = new GroupAddress(Integer.valueOf("3"),
                        Integer.valueOf("3"),
                        Integer.valueOf("0"));
                    knxComObj.readFloat(windadr);
                    knxComObj.readFloat(tempadr);
                    knxComObj.readFloat(lichtadr);
                    //knxComObj.readBoolean(rcvAdress);
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

    @Override
    public void update(Observable observable, Object data) {
        System.out.println("Update from MainActivity called by: " + observable);
        System.out.println("with data: " + observable);
        if (data instanceof KnxComparableObject) {
            adr = ((KnxComparableObject) data).getGroupAddress();
            if (data instanceof KnxFloatObject) {
                final float read = ((KnxFloatObject) data).getValue();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("RunOnUiThread Run runned");
                        if(adr.equals(windadr)) {
                            wind.setText("Windstärke:" + read);
                        }
                        if(adr.equals(tempadr)) {
                            temperatur.setText("Temperatur:" + read);
                        }
                        if(adr.equals(lichtadr)) {
                            lux1.setText("Lux:" + read);
                        }
                    }
                });
            }
        } else if (knxComObj != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("RunOnUiThread Run runned");
                    if (knxComObj.isConnected()) {
                        tvConnectionStatus.setText("Connected");
                    } else {
                        tvConnectionStatus.setText("Connection Error");
                    }
                }
            });
        }
    }
}
