package com.tantrik.desktopnotifier;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.telephony.*;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SendNotificationRequest extends Activity {
    private int PORT = 1234;
    ServerSocket httpServerSocket;
    GoogleCloudMessaging gcm;
    String regid = "";
    String SENDER_ID = "337222096179";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification_request);
        Context ctx = SendNotificationRequest.this;
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        GcmRegister gcmRegister = new GcmRegister();
        gcmRegister.start();
        Log.d("IP Address", Integer.toString(ipAddress));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_notification_request, menu);
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
    class PhoneListener extends PhoneStateListener{
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // called when someone is ringing to this phone
                    Thread t = new Thread(new SendPost(incomingNumber));
                    t.start();
                    Toast.makeText(SendNotificationRequest.this,
                            "Incoming: " + incomingNumber,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    class GcmRegister extends Thread{
        public void run() {
            try {
                gcm = GoogleCloudMessaging.getInstance(SendNotificationRequest.this);
                regid = gcm.register(SENDER_ID);
                new SendPost(regid).start();
                Log.d("Registration Id", regid);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }


}
class SendPost extends Thread{
    private String incomingNumber = null;
    private String postUrl = "http://192.168.1.7:8888/gcmId";
    private HttpClient httpClient = null;
    private HttpPost httpPost = null;
    SendPost(String incomingNumber){
        this.incomingNumber = incomingNumber;
    }
    public void run(){
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(postUrl);
        String attribute = "gcmId";
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair(attribute, incomingNumber));
        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(postData);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("Response", httpResponse.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }
}


