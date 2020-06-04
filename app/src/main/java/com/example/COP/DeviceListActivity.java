package com.example.COP;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class DeviceListActivity extends Activity {

    // Dependency (Layout)
    //   : title_bar.xml
    //   : device_list.xml
    //   : device_element.xml
    // Dependency (code)
    //   : BleDataTransferService.java
    //   : BluetoothBridge.java
    // Dependency (AndroidManifest.xml)
    //   : Add Code
    // <activity android:name=".DeviceListActivity" android:label="Scanning Devices" android:theme="@android:style/Theme.Dialog"/>
    // <service android:enabled="true" android:name=".BleDataTransferService" />

    public static final String TAG = "BleListActivity";

    // Android Bluetooth Class
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    List<BluetoothDevice> deviceList;
    Map<String, Integer> devRssiValues;
    private static final long SCAN_PERIOD = 10000; //10 seconds
    private Handler mHandler;
    private boolean mScanning;
    private int MY_PERMISSIONS_REQUEST_LOCATION = 2000;


    private static final String BLE_DEVICE_NAME = "COP";

    //
    private DeviceAdapter deviceAdapter;

    private TextView mEmptyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        setContentView(R.layout.device_list);
        android.view.WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.gravity=Gravity.TOP;
        layoutParams.y = 200;
        mHandler = new Handler();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth Low Energy not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth Low Energy not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        populateList();
        mEmptyList = (TextView) findViewById(R.id.empty);
        Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mScanning==false) scanLeDevice(true);
                else finish();
            }
        });

    }

    private void populateList() {
        /* Initialize device list container */
        Log.d(TAG, "populateList");
        deviceList = new ArrayList<BluetoothDevice>();
        deviceAdapter = new DeviceAdapter(this, deviceList);
        devRssiValues = new HashMap<String, Integer>();

        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(deviceAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        scanLeDevice(true);

    }

    private void scanLeDevice(final boolean enable) {
        final Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        if (enable) {
            ScanFilter beaconFilter = new ScanFilter.Builder()
                    .build();
            ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
            //filters.add(beaconFilter);

            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();


            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mScanning = false;
                    //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mBluetoothLeScanner.stopScan(mScanCallback);

                    cancelButton.setText("Scan");

                }
            }, SCAN_PERIOD);

            mScanning = true;
            //mBluetoothAdapter.startLeScan(mLeScanCallback);
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
            cancelButton.setText("Cancel");
        } else {
            mScanning = false;
            //mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mBluetoothLeScanner.stopScan(mScanCallback);
            cancelButton.setText("Scan");
        }

    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "onScanResult");
            processResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d(TAG, "onBatchScanResults: "+results.size()+" results");
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(TAG, "LE Scan Failed: "+errorCode);
        }

        private void processResult(ScanResult result) {
            Log.i(TAG, "New LE Device: " + result.getDevice().getName() + " @ " + result.getRssi());

            /*
             * Create a new beacon from the list of obtains AD structures
             * and pass it up to the main thread
             */
            /*TemperatureBeacon beacon = new TemperatureBeacon(result.getScanRecord(),
                    result.getDevice().getAddress(),
                    result.getRssi());
            mHandler.sendMessage(Message.obtain(null, 0, beacon));*/

            if(result.getDevice().getName()!=null) {
                if(result.getDevice().getName().equals(BLE_DEVICE_NAME)) {
                    Log.i(TAG, "New LE Device: pH-Device Matching");
                    addDevice(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes(), 0);
                }
            }



            //addDevice(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes(), 1);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addDevice(device,rssi, scanRecord, 1);
                                }
                            });

                        }
                    });
                }
            };

    private UUID findServiceUuidInScanRecord(byte[] scanRecord){
        final char[] hexArray = "0123456789abcdef".toCharArray();
        for(int index = 0; (index >= 0 && index < scanRecord.length-1); ) {
            int length = scanRecord[index];
            int header = scanRecord[index+1];
            if(length == 0 || header == 0) {
                return null;
            }
            if(length == 17 && header == 7){
                String stringUUID = "";
                for(int uIndex = 0; uIndex < 16; uIndex++){
                    int hexVal = scanRecord[index + 2 + (15-uIndex)] & 0xFF;
                    stringUUID += hexArray[hexVal / 16];
                    stringUUID += hexArray[hexVal % 16];
                    if(uIndex == 3 || uIndex == 5 || uIndex == 7 || uIndex == 9){
                        stringUUID += "-";
                    }
                }
                UUID returnUUID = UUID.fromString(stringUUID);
                return returnUUID;
            }
            index += ((int)length + 1);
        }
        return null;
    }

    private void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord, int option) {

        if (option == 1) {
            boolean deviceFound = false;

            UUID serviceUUID = findServiceUuidInScanRecord(scanRecord);
            if (serviceUUID != null && serviceUUID.equals(BleDataTransferService.DATA_AUDIO_TRANSFER_SERVICE_UUID)) {
                for (BluetoothDevice listDev : deviceList) {
                    if (listDev.getAddress().equals(device.getAddress())) {
                        deviceFound = true;
                        break;
                    }
                }

                devRssiValues.put(device.getAddress(), rssi);
                if (!deviceFound) {
                    deviceList.add(device);
                    mEmptyList.setVisibility(View.GONE);

                    deviceAdapter.notifyDataSetChanged();
                }
            }
        } else if(option==0) {
            boolean deviceFound = false;
            for (BluetoothDevice listDev : deviceList) {
                if (listDev.getAddress().equals(device.getAddress())) {
                    deviceFound = true;
                    break;
                }
            }

            devRssiValues.put(device.getAddress(), rssi);
            if (!deviceFound) {
                deviceList.add(device);
                mEmptyList.setVisibility(View.GONE);
                deviceAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @Override
    public void onStop() {
        super.onStop();
        //mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mBluetoothLeScanner.stopScan(mScanCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mBluetoothLeScanner.stopScan(mScanCallback);
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = deviceList.get(position);
            //mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mBluetoothLeScanner.stopScan(mScanCallback);

            Bundle b = new Bundle();
            b.putString(BluetoothDevice.EXTRA_DEVICE, deviceList.get(position).getAddress());

            Intent result = new Intent();
            result.putExtras(b);
            setResult(Activity.RESULT_OK, result);
            finish();

        }
    };



    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    class DeviceAdapter extends BaseAdapter {
        Context context;
        List<BluetoothDevice> devices;
        LayoutInflater inflater;

        public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup vg;

            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) inflater.inflate(R.layout.device_element, null);
            }

            BluetoothDevice device = devices.get(position);
            final TextView tvadd = ((TextView) vg.findViewById(R.id.address));
            final TextView tvname = ((TextView) vg.findViewById(R.id.name));
            final TextView tvpaired = (TextView) vg.findViewById(R.id.paired);
            final TextView tvrssi = (TextView) vg.findViewById(R.id.rssi);

            tvrssi.setVisibility(View.VISIBLE);
            byte rssival = (byte) devRssiValues.get(device.getAddress()).intValue();
            if (rssival != 0) {
                tvrssi.setText("Rssi = " + String.valueOf(rssival));
            }

            tvname.setText(device.getName());
            tvadd.setText(device.getAddress());
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.i(TAG, "device::"+device.getName());
                tvname.setTextColor(Color.WHITE);
                tvadd.setTextColor(Color.WHITE);
                tvpaired.setTextColor(Color.GRAY);
                tvpaired.setVisibility(View.VISIBLE);
                tvpaired.setText("paired");
                tvrssi.setVisibility(View.VISIBLE);
                tvrssi.setTextColor(Color.WHITE);

            } else {
                tvname.setTextColor(Color.WHITE);
                tvadd.setTextColor(Color.WHITE);
                tvpaired.setVisibility(View.GONE);
                tvrssi.setVisibility(View.VISIBLE);
                tvrssi.setTextColor(Color.WHITE);
            }
            return vg;
        }
    }
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
