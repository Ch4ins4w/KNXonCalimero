package com.calimero.knx.knxoncalimero;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.calimero.knx.knxoncalimero.knxobject.KnxBooleanObject;

import java.util.Observable;
import java.util.Observer;

import tuwien.auto.calimero.GroupAddress;


public class MainActivity extends Activity implements Observer {
    public static boolean first = true;
    //public static KnxBusConnection testConnection;
    KnxBusConnection connectionThread;
    //Gui-Elemente
    EditText tfGatewayIP, tfSendHaupt, tfSendMitte, tfSendSub, tfRcvHaupt, tfRcvMitte, tfRcvSub;
    Button sendButton, connectButton, readButton;
    private Container resultContainer, busActionContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //testConnection = new KnxBusConnection("", "192.168.10.28");
        busActionContainer = new Container();
        resultContainer = new Container();
        resultContainer.addObserver(this);

        tfGatewayIP = (EditText) findViewById(R.id.tfGatewayIP);
        tfSendHaupt = (EditText) findViewById(R.id.tfSendHaupt);
        tfSendMitte = (EditText) findViewById(R.id.tfSendMittel);
        tfSendSub = (EditText) findViewById(R.id.tfSendSub);
        tfRcvHaupt = (EditText) findViewById(R.id.tfRcvHaupt);
        tfRcvMitte = (EditText) findViewById(R.id.tfRcvMittel);
        tfRcvSub = (EditText) findViewById(R.id.tfRcvSub);

        sendButton = (Button) findViewById(R.id.btnSend);
        sendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                busActionContainer.push(new KnxBooleanObject(new GroupAddress(0, 0, 1), true));
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("Bus was written");
            }
        });


        connectButton = (Button) findViewById(R.id.btnConnect);
        connectButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (testConnection.initBus("", "192.168.10.28")) {
                    TextView textView = (TextView) findViewById(R.id.tvConnectionStatus);
                    textView.setText("Connected!");
                } else {
                    TextView textView = (TextView) findViewById(R.id.tvConnectionStatus);
                    textView.setText("Cannont open Connection");
                }*/

                //todo: gateway ip überprüfung
                //todo: Host Ip dynamisch bestimmen oder Gui element dafür implementieren
                connectionThread = new KnxBusConnection("192.168.10.0", tfGatewayIP.getText().toString(), busActionContainer, resultContainer);
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
        });*/

        readButton = (Button) findViewById(R.id.btnReceive);
        readButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) findViewById(R.id.tfRcvValue);
                busActionContainer.push(new KnxBooleanObject(new GroupAddress(0, 0, 1), true));
                try {
                    Thread.sleep(1000); //Muss später durch Benachrichtigung wenn gelesen ersetzt werden, damit die ANzeige aktualisiert werden kann
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean read = ((KnxBooleanObject) resultContainer.getByGroupAddress(new GroupAddress(0, 0, 1), true)).getValue();
                textView.setText("Read " + read + " from Bus");
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
        System.out.println("Update from MainActivity called");
        if (observable.equals(resultContainer)) {
            for (Object o : resultContainer.getAll()) {
                System.out.println(o);
            }
        }
    }
}
