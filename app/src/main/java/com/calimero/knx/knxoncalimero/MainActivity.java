package com.calimero.knx.knxoncalimero;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.calimero.knx.knxoncalimero.knxobject.KnxBooleanObject;

import tuwien.auto.calimero.GroupAddress;


public class MainActivity extends Activity {
    public static boolean first = true;
    public static KnxBusConnection testConnection;
    KnxBusConnection connectionThread;
    private Container readContainer, readResultContainer, writeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //testConnection = new KnxBusConnection("", "192.168.10.28");
        readContainer = new Container();
        writeContainer = new Container();
        readResultContainer = new Container();
        connectionThread = new KnxBusConnection("192.168.10.0", "192.168.10.28", readContainer, writeContainer, readResultContainer);
        connectionThread.start();

        Button sendButton = (Button) findViewById(R.id.btnSend);
        sendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeContainer.push(new KnxBooleanObject(new GroupAddress(0, 0, 1), true));
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("Bus was written");
            }
        });


        Button connectButton = (Button) findViewById(R.id.btnConnect);
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

        Button readButton = (Button) findViewById(R.id.btnReceive);
        readButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) findViewById(R.id.tfRcvValue);
                readContainer.push(new KnxBooleanObject(new GroupAddress(0, 0, 1)));
                try {
                    Thread.sleep(1000); //Muss später durch Benachrichtigung wenn gelesen ersetzt werden, damit die ANzeige aktualisiert werden kann
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean read = ((KnxBooleanObject) readResultContainer.getByGroupAdress(new GroupAddress(0, 0, 1))).getValue();
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
}
