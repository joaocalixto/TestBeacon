package simplelife.com.br.testbeacon;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Utils;
import com.estimote.sdk.cloud.CloudCallback;
import com.estimote.sdk.cloud.EstimoteCloud;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.exception.EstimoteServerException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
    private static final String TAG = "BEACONTESTE";

    private static final String MINT = "FC:5A:29:87:82:DF";
    private static final String blueberry = "EA:7A:88:67:FE:40";
    private static final String ice = "E1:56:9B:61:8E:CB";
    private BeaconManager beaconManager ;
    TextView beaconId;

    private boolean ice_enter = false;

    //  App ID & App Token can be taken from App section of Estimote Cloud.
    String appId = "testbeacon123";
    String appToken = "26c7e9bd5f93c23cfbc65991f5f16dcf";
    private TextView enterRegion;
    private TextView deviceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconManager = new BeaconManager(this);

        beaconId = (TextView) findViewById(R.id.txt_beacon1);
        enterRegion = (TextView) findViewById(R.id.txt_enter_region);
        deviceModel = (TextView) findViewById(R.id.txt_device_model);

        deviceModel.setText(getDeviceName());




        EstimoteSDK.initialize(this, appId, appToken);
        // Optional, debug logging.
         EstimoteSDK.enableDebugLogging(true);
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {


            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

                for (Beacon beaconTemp : beacons) {
                    String macAddress = beaconTemp.getMacAddress();
                    Log.d(TAG, "Ranged Name: " + macAddress);

                    try {
                        beaconManager.startMonitoring(region);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    //if (!ice_enter) {
                        if (macAddress.equalsIgnoreCase(ice)) {
                           // Toast.makeText(getBaseContext(), "Hello my name is ice", Toast.LENGTH_SHORT).show();
                            beaconId.setText("ICE: "+Utils.computeAccuracy(beaconTemp));
                        }

                        ice_enter = true;
                    //}
                }
            }
        });



        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                enterRegion.setText("ENTER");
//                Toast.makeText(getBaseContext(), "Enter region",Toast.LENGTH_SHORT).show();
                Log.d(TAG,  "Enter region");
            }

            @Override
            public void onExitedRegion(Region region) {
                enterRegion.setText("BYE BYE");
//                Toast.makeText(getBaseContext(), "EXIT REGION",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "EXIT REGION");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


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
    protected void onStop() {
        beaconManager.disconnect();

        super.onStop();
    }


    public void desconectarBeacon(View v){
        beaconManager.disconnect();
        Toast.makeText(getBaseContext(), "Bye bye", Toast.LENGTH_SHORT).show();
    }
    public void conectarBeacon(View v){

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }



}
