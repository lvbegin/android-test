package com.example.laurent.myapplicationforexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    public final int lvbUid = 1;
    public final String topic = "awl-A167399-lvb";
    MqttAndroidClient client;
    AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appDatabase = AppDatabase.create(this);
        ensureTestDb();
        initField();

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883", clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show();
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void decrementBalance(View view) {
        TextView inputField = (TextView) findViewById(R.id.inputValue);

        String payload = inputField.getText().toString();
        if (payload.length() == 0) {
            Toast.makeText(MainActivity.this, "Please, fill the field", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            MqttMessage message = new MqttMessage(("-" + payload).getBytes());
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(View view) {
        TextView inputField = (TextView) findViewById(R.id.inputValue);

        String payload = inputField.getText().toString();
        if (payload.length() == 0) {
            Toast.makeText(MainActivity.this, "Please, fill the field", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe() {
        try {
            client.subscribe(topic, 0);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //reconnect?
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    changeBalance(1,Integer.parseInt(message.toString()));
                    TextView displayedText = (TextView) findViewById(R.id.editText);
                    displayedText.setText(Integer.toString(getBalance(1)));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    int getBalance(int uid) {
        return appDatabase.accountDao().findByUid(1).getBalance();
    }
    void changeBalance(int uid, int delta) {
        Account account = appDatabase.accountDao().findByUid(uid);
        account.setBalance(account.getBalance() + delta);
        appDatabase.accountDao().update(account);
    }

    void ensureTestDb() {
        if (null != appDatabase.accountDao().findByUid(1)) {
            Toast.makeText(MainActivity.this, "Test account already exists", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(MainActivity.this, "Create test account", Toast.LENGTH_LONG).show();
        Account account = new Account();
        account.setBalance(0);
        account.setFirstName("Laurent");
        account.setLastName("Van Begin");
        appDatabase.accountDao().insert(account);appDatabase.accountDao();

    }

    void initField() {
        TextView displayedText = (TextView) findViewById(R.id.editText);
        Account account = appDatabase.accountDao().findByUid(1);
        displayedText.setText(Integer.toString(account.getBalance()));
    }

}
