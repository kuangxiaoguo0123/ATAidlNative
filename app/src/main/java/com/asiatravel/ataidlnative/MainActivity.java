package com.asiatravel.ataidlnative;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asiatravel.ataidlnative.util.IntentUtil;

import service.DataService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView resultTextView;
    private MyServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button serviceButton = (Button) findViewById(R.id.service_button);
        resultTextView = (TextView) findViewById(R.id.result_textView);

        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });
    }

    private void startService() {
        Intent intent = new Intent();
        intent.setAction("service.DataService");
        Intent explicitIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            explicitIntent = IntentUtil.getExplicitIntent(this, intent);
        } else {
            explicitIntent = intent;
        }
        conn = new MyServiceConnection();
        bindService(explicitIntent, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
        }
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataService dataService = DataService.Stub.asInterface(service);
            try {
                int result = dataService.add(10, 20);
                String info = dataService.getName();
                resultTextView.setText("result is" + result + " " + " info is" + info);
            } catch (RemoteException e) {
                Log.d(TAG, "onServiceConnected: e-->" + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
