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

import com.calimero.knx.knxoncalimero.knxobject.KnxBooleanObject;
import com.calimero.knx.knxoncalimero.knxobject.KnxFloatObject;

import java.util.Observable;
import java.util.Observer;

import tuwien.auto.calimero.GroupAddress;


public class MainActivity extends Activity implements Observer {
    public static boolean first = true;
    public static KnxBusConnection testConnection;
    KnxBusConnection connectionRunnable;
    //Gui-Elemente
    //EditText tfGatewayIP, tfSendHaupt, tfSendMitte, tfSendSub, tfRcvHaupt, tfRcvMitte, tfRcvSub, tfRcvValue, tfSendValue;
    EditText tfGatewayIP,tfHaupt, tfMitte, tfSub;
    TextView lux1,lux2,temperatur,wind;
    TextView tvConnectionStatus;
    ImageView wetter;
    Button sendButton, connectButton, readButton;
    GroupAddress rcvAdress, sendAdress;
    MainActivity thisMainActivity;
    private Container resultContainer, busActionContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wetterstation_layout);
        thisMainActivity = this;
        //testConnection = new KnxBusConnection("", "192.168.10.28");
        busActionContainer = new Container();
        resultContainer = new Container();
        resultContainer.addObserver(this);

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
                connectionRunnable = new KnxBusConnection("192.168.10.123", tfGatewayIP.getText().toString(), busActionContainer, resultContainer);
                connectionRunnable.addObserver(thisMainActivity);
                Thread connectionThread = new Thread(connectionRunnable);
                connectionThread.start();
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
                rcvAdress = new GroupAddress(Integer.valueOf(tfHaupt.getText().toString()),
                        Integer.valueOf(tfMitte.getText().toString()),
                        Integer.valueOf(tfSub.getText().toString()));
                busActionContainer.push(new KnxBooleanObject(rcvAdress, true));
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
        System.out.println("Update from MainActivity called:" + observable);
        if (observable.equals(resultContainer)) {
            /*if (data instanceof KnxBooleanObject) {
                final boolean read = ((KnxBooleanObject) data).getValue();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("RunOnUiThread Run runned");
                        tfRcvValue.setText("Read " + read + " from Bus");
                    }
                });
            }*/
            if (data instanceof KnxFloatObject) {
                final float read = ((KnxFloatObject) data).getValue();
                if(((KnxFloatObject) data).getGroupAddress().equals(new GroupAddress(6,6,6))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("RunOnUiThread Run runned");

                            wind.setText("Read " + read + " from Bus");
                        }
                    });
                }
            }
        } else if (observable.equals(connectionRunnable)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("RunOnUiThread Run runned");
                    if (connectionRunnable.isConnected()) {
                        tvConnectionStatus.setText("Connected");
                    } else {
                        tvConnectionStatus.setText("Connection Error");
                    }
                }
            });
        }
    }
}
