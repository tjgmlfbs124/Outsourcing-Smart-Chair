package com.example.COP;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.COP.Utils.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PHYSIONICS_PERMISSIONS_REQUEST_CODE = 20002;

    float off_x = 180;
    float off_y = 600;
    double LC_1, LC_2, LC_3, LC_4;
    double LC_5, LC_6;

    double pre_LC_1, pre_LC_2, pre_LC_3, pre_LC_4;
    double pre_LC_5, pre_LC_6;

    int[] aa = new int[100];
    int a = 0;

    double Vref = 2.5;
    int Gain = 1;

    Chart myChart;

    ImageView ivBt;
    Button btReset;
    Button btStart;
    Button btStop;
    Button btCalibration;

    TextView TextBat;

    TextView tvLC_11;
    TextView tvLC_1;
    TextView tvLC_7;
    TextView tvLC_5;
    ImageView IVCIR;
    TextView tvLC_2_11;
    TextView tvLC_2_1;
    TextView tvCOPX;
    TextView tvCOPY;



    private float ph_offset = 0.0f;
    private float ph_slope = 59.16f;
    private final float PH_ADC_LSB_RESOLUTION = 0.188f;

    private boolean mPhysionicsPermissionsGranted = false;


    private BluetoothBridge mBluetoothBridge = BluetoothBridge.getInstance();


    /**
     * @ckw 객체 설정
     */
    private AppCompatImageView left_monitor, right_monitor;
    private AppCompatImageView btn_set, btn_ble;
    private TextView txt_alram, btn_start;
    private Stopwatch stopWatch = new Stopwatch();
    private CircleProgressBar circleProgress_01, circleProgress_02, circleProgress_03, circleProgress_04;

    private PendingIntent pendingIntent;

    private Integer alertTime = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* btStart = findViewById(R.id.btn_start);
        btStop = findViewById(R.id.btn_stop);
        btCalibration = findViewById(R.id.btn_calibration);

        TextBat = findViewById(R.id.tvBattery);

        tvLC_11 = findViewById(R.id.tv_11);
        tvLC_1 = findViewById(R.id.tv_1);
        tvLC_7 = findViewById(R.id.tv_7);
        tvLC_5 = findViewById(R.id.tv_5);

        tvLC_2_11 = findViewById(R.id.tv_2_11);
        tvLC_2_1 = findViewById(R.id.tv_2_1);

        tvCOPX = findViewById(R.id.COPX);
        tvCOPY = findViewById(R.id.COPY);

        btReset = findViewById(R.id.btn_reset);

        IVCIR = findViewById(R.id.ivCIR);
        IVCIR.setX(0);
        IVCIR.setY(0);
        IVCIR.setVisibility(View.INVISIBLE); */


/*
        scatterEntries = new ArrayList<>();
        scatterEntries.add(new BarEntry(2, 5));
        scatterDataSet = new ScatterDataSet(scatterEntries, "");
        scatterData = new ScatterData(scatterDataSet);
        scatterChart.setData(scatterData);
        scatterDataSet.setValueTextSize(0f);
*/
        //@ckw
        left_monitor = (AppCompatImageView)findViewById(R.id.svg_left_monitor);
        right_monitor = (AppCompatImageView)findViewById(R.id.svg_right_monitor);
        btn_set = (AppCompatImageView)findViewById(R.id.btn_set);
        btn_ble = (AppCompatImageView)findViewById(R.id.btn_ble);
        txt_alram = (TextView)findViewById(R.id.txt_alram);
        btn_start = (TextView)findViewById(R.id.btn_start);

        circleProgress_01 = (CircleProgressBar)findViewById(R.id.circleProgress_01);

        btn_start.setOnClickListener(new ButtonClickListener());
        btn_set.setOnClickListener(new ButtonClickListener());

        /*btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  // START
                Log.d("@ckw", "onClick");
                if(mBluetoothBridge.mService.isConnected()) {
                    mBluetoothBridge.mService.sendCommand(0x53, null);
                }
            }
        });*/
        /*btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  // STOP
                Log.d("@ckw", "stopClick");
                if(mBluetoothBridge.mService.isConnected()) {
                    mBluetoothBridge.mService.sendCommand(0x50, null);
                }
            }
        });*/
        /*btCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBluetoothBridge.mService.isConnected()) {
                    mBluetoothBridge.mService.sendCommand(0x42, null);
                }
            }
        });

        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pre_LC_1  = LC_1;
                pre_LC_2  = LC_2;
                pre_LC_3  = LC_3;
                pre_LC_4  = LC_4;

                pre_LC_5  = LC_5;
                pre_LC_6  = LC_6;
            }
        });


        bluetoothDevicePairingInit();

        if(!checkPermissions()) {
            Log.d(TAG, "onCreate: permission denied, require all permission");
        }

        //myChart.addNewEntry(mLineChartL, 1);
        //myChart.addNewEntry(mLineChartL, 2);
        //myChart.addNewEntry(mLineChartL, 3);

        //mLineChartL.clearValues();*/

    }
    /**
     * @ckw 이벤트 처리
     */

    private class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_set :
                    Log.d("@ckw", "option");
                    showTimeEditDialog();
                    break;
                case R.id.txt_alram :
                    // 설정창 이동할것
                    break;
                case R.id.btn_start :
                case R.id.btn_ble :
                    Log.d("@ckw", "btn ble Click");
                    showAlertDialog();
                    /**
                     * 처음 눌렀을때 : 해야할일
                     *  1. 블루투스 연결 -> 성공시 -> btn_ble.setVisible(View.GONE); (BLE 이미지 없앰) -> btn_start.setText("START");
                     * 연결 성공후 눌렀을때 :
                     *  1. 스톱워치 작동 -> stopWatch.start(); or stopWatch.stop();
                     */
                    break;
            }
        }
    }

    /**
     * @ckw 알람
     * */
    private void myAlarm() {
        Log.d("@ckw", "alarm set");
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("alarm", 0);
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //Calendar calendar =
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }

    private void cancelMyAlarm() {
        Log.d("@ckw", "alarm cancel");
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        alarmManager.cancel(pendingIntent);
    }

    /** @ckw AlertDialog
     * */

    // ~ 잘못된 자세 알람
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("자세가 바르지 않습니다.");//.setMessage("")
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("@ckw", "close");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showTimeEditDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("시간을 설정해 주세요. (분)");

        builder.setView(editText);
        builder.setPositiveButton("입력",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertTime = Integer.parseInt(editText.getText().toString());
                    //Toast.makeText(getApplicationContext(),editText.getText().toString()+"분 설정" ,Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),alertTime.toString()+"분 설정" ,Toast.LENGTH_LONG).show();
                }
            });
        builder.setNegativeButton("취소",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * @ckw 진동 설정
     */

    //private void

    /**
     * --------------------- Bluetooth Pairing -------------------------------
     */

    private void bluetoothDevicePairingInit() {

        mBluetoothBridge.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothBridge.mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ble_service_init();

        //ivBt = findViewById(R.id.ivBt);

        // Handler Disconnect & Connect button
        /*ivBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothBridge.mBtAdapter.isEnabled()) {
                    Log.i(TAG, "onClick - BT not enabled yet");
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, mBluetoothBridge.REQUEST_ENABLE_BT);
                } else {
                    if (mBluetoothBridge.mService.isConnected()) {
                        //Disconnect button pressed
                        if (mBluetoothBridge.mDevice != null) {
                            mBluetoothBridge.mService.disconnect();
                        }
                    } else {
                        //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices

                        Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                        startActivityForResult(newIntent, mBluetoothBridge.REQUEST_SELECT_DEVICE);
                    }
                }
            }
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BluetoothBridge.REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mBluetoothBridge.mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    Log.d(TAG, "... onActivityResultdevice.address==" + mBluetoothBridge.mDevice + "mserviceValue" + mBluetoothBridge.mService);
                    //((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName()+ " - connecting");
                    mBluetoothBridge.mService.connect(deviceAddress);
                    //mConnectionProgDialog.show();
                }
                break;

            case BluetoothBridge.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    private void bleStateImageChange() {
        if(mBluetoothBridge.mService.isConnected()){
            ivBt.setImageResource(R.drawable.bluetooth_on);
        } else {
            ivBt.setImageResource(R.drawable.bluetooth_off);
        }
    }



    /**
     * ----------------------- Bluetooth Service -------------------------------
     */

    private void ble_service_init() {
        Intent bindIntent = new Intent(this, BleDataTransferService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mBluetoothBridge.mService = ((BleDataTransferService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mBluetoothBridge.mService);
            if (!mBluetoothBridge.mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mBluetoothBridge.mService = null;
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleDataTransferService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleDataTransferService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleDataTransferService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleDataTransferService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleDataTransferService.ACTION_IMG_INFO_AVAILABLE);
        intentFilter.addAction(BleDataTransferService.DEVICE_DOES_NOT_SUPPORT_IMAGE_TRANSFER);
        return intentFilter;
    }


    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        byte buuu;
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(BleDataTransferService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mBluetoothBridge.mMtuRequested = false;
                        new CountDownTimer(1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                mBluetoothBridge.mConnectionSuccess = true;
                                bleStateImageChange();
                                Log.d("@ckw", "onFinish!");
                            }
                        }.start();


                        Log.d(TAG, "UART_CONNECT_MSG");
                    }
                });
            }

            //*********************//
            if (action.equals(BleDataTransferService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        mBluetoothBridge.mState = BluetoothBridge.UART_PROFILE_DISCONNECTED;
                        mBluetoothBridge.mConnectionSuccess = false;
                        //mUartData[0] = mUartData[1] = mUartData[2] = mUartData[3] = mUartData[4] = mUartData[5] = 0;
                        Arrays.fill(mBluetoothBridge.mUartData, (byte)0);
                        mBluetoothBridge.mService.close();
                        //mTextViewMtu.setText("-");
                        //mTextViewConInt.setText("-");
                        //mConnectionProgDialog.hide();
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        bleStateImageChange();
                    }
                });
            }

            //*********************//
            if (action.equals(BleDataTransferService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mBluetoothBridge.mService.enableTXNotification();
                mBluetoothBridge.mService.sendCommand(BluetoothBridge.BleCommand.GetBleParams.ordinal(), null);

            }

            //*********************//
            if (action.equals(BleDataTransferService.ACTION_DATA_AVAILABLE)) {

                double Fz = 0;
                double oo = 0;
                double xo = 0;
                double oy = 0;
                double xy = 0;
                double COPx = 0;
                double COPy = 0;
                final byte[] txValue = intent.getByteArrayExtra(BleDataTransferService.EXTRA_DATA);

                Log.d("@ckw","Ble Data:"+Arrays.toString(txValue)); //@ckw

                if (txValue.length == 16) {

                    double raw_LC1,raw_LC2,raw_LC3,raw_LC4;

                    LC_1 = ((txValue[0] & 0x00FF)<<16) | ((txValue[1] & 0x00FF)<<8) | ((txValue[2] & 0x00FF)) ;
                    LC_1 = bit_shift(LC_1);
                    LC_1 = (double)(LC_1 * Vref/ Gain / 8388606 *1000);
                    raw_LC1 = (double)(LC_1 - pre_LC_1);


                    LC_2 = ((txValue[4] & 0x00FF)<<16) | ((txValue[5] & 0x00FF)<<8) | ((txValue[6] & 0x00FF)) ;
                    LC_2 = bit_shift(LC_2);
                    LC_2 = (double)(LC_2 * Vref/ Gain / 8388606 *1000);
                    raw_LC2 = (double)(LC_2 - pre_LC_2);

                    LC_3 = ((txValue[8] & 0x00FF)<<16) | ((txValue[9] & 0x00FF)<<8) | ((txValue[10] & 0x00FF)) ;
                    LC_3 = bit_shift(LC_3);
                    LC_3 = (double)(LC_3 * Vref/ Gain / 8388606 *1000);
                    raw_LC3 = (double)(LC_3 - pre_LC_3);

                    LC_4 = ((txValue[12] & 0x00FF)<<16) | ((txValue[13] & 0x00FF)<<8) | ((txValue[14] & 0x00FF)) ;
                    LC_4 = bit_shift(LC_4);
                    LC_4 = (double)(LC_4 * Vref/ Gain / 8388606 *1000);
                    raw_LC4 = (double)(LC_4 - pre_LC_4);

                    DecimalFormat decimalFormat = new DecimalFormat("#0.0000");
                    String str = decimalFormat.format(raw_LC1);
                    tvLC_7.setText(str+" mV");

                    str = decimalFormat.format(raw_LC2);
                    tvLC_5.setText(str+" mV");

                    str = decimalFormat.format(raw_LC3);
                    tvLC_11.setText(str+" mV");

                    str = decimalFormat.format(raw_LC4);
                    tvLC_1.setText(str+" mV");





                    oo = Math.abs(raw_LC3);
                    xo = Math.abs(raw_LC4);
                    oy = Math.abs(raw_LC1);
                    xy = Math.abs(raw_LC2);

                    Fz = oo + xo + oy + xy;

                    COPx = ( 1 + ((xo +xy) - (oo + oy))/Fz ) * 650 / 2;
                    COPy = ( 1 + ((oy +xy) - (oo + xo))/Fz ) * 650 / 2;

                    str = decimalFormat.format(COPx);
                    tvCOPX.setText(str);
                    str = decimalFormat.format(COPy);
                    tvCOPY.setText(str);

                    if(Fz < 0.5){
                        IVCIR.setVisibility(View.INVISIBLE);
                    }else{
                        IVCIR.setVisibility(View.VISIBLE);
                    }

                    IVCIR.setX((float)COPx+off_x);
                    IVCIR.setY((float)COPy+off_y);

                }

                if (txValue.length == 8) {

                    double raw_LC5,raw_LC6;

                    LC_5 = ((txValue[0] & 0x00FF)<<16) | ((txValue[1] & 0x00FF)<<8) | ((txValue[2] & 0x00FF)) ;
                    LC_5 = bit_shift(LC_5);
                    LC_5 = (double)(LC_5 * Vref/ Gain / 8388606 *1000);
                    raw_LC5 = (double)(LC_5 - pre_LC_5);

                    LC_6 = ((txValue[4] & 0x00FF)<<16) | ((txValue[5] & 0x00FF)<<8) | ((txValue[6] & 0x00FF)) ;
                    LC_6 = bit_shift(LC_6);
                    LC_6 = (double)(LC_6 * Vref/ Gain / 8388606 *1000);
                    raw_LC6 = (double)(LC_6 - pre_LC_6);

                    DecimalFormat decimalFormat = new DecimalFormat("#0.0000");
                    String str = decimalFormat.format(raw_LC5);
                    tvLC_2_11.setText(str+" mV");

                    str = decimalFormat.format(raw_LC6);
                    tvLC_2_1.setText(str+" mV");
                }
            }

            //*********************//
            if (action.equals(BleDataTransferService.ACTION_IMG_INFO_AVAILABLE)) {
                final byte[] txValue = intent.getByteArrayExtra(BleDataTransferService.EXTRA_DATA);

                short MSB = (short)(txValue[0] & 0x00FF);
                short LSB = (short)(txValue[1] & 0x00FF);

                double Battery = ( (MSB << 8 | LSB ) >> 4 ) ;
                Battery = Battery * 0.00125;
                DecimalFormat decimalFormat = new DecimalFormat("#0.000");
                String str = decimalFormat.format(Battery);

                TextBat.setText("Battery " + str + " V");
                Log.d("@ckw", "hello");
            }
            //*********************//
            if (action.equals(BleDataTransferService.DEVICE_DOES_NOT_SUPPORT_IMAGE_TRANSFER)){
                //showMessage("Device doesn't support UART. Disconnecting");
                mBluetoothBridge.mService.disconnect();
            }

        }
    };



/*
    public void debugMsg(String msg) {
        final String str = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                scatterEntries = new ArrayList<>();
                scatterEntries.add(new BarEntry(2, 10));
                scatterDataSet = new ScatterDataSet(scatterEntries, "");
                scatterData = new ScatterData(scatterDataSet);
                scatterChart.setData(scatterData);
                scatterDataSet.setValueTextSize(0f);

                TextBat.setText("THREAD");
            }
        });
    }*/

    /*

    private void runThread() {

        new Thread() {
            public void run() {

                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                tvLC_2_1.setText(test_val++ +" mV");
                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            }
        }.start();
    }*/




    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        if(mBluetoothBridge.mService!=null) {
            mBluetoothBridge.mService.stopSelf();
            mBluetoothBridge.mService = null;
        }

    }

    /**
     * --------------------- Permissions -------------------------------
     */

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private double bit_shift(double a){

        int MODULO = 1 << 24;
        int MAX_VALUE = (1 << 23) - 1;

        double b;

        b = a;
        if (a > MAX_VALUE) {
            a -= MODULO;
            b = a;
        }
        return a;
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PHYSIONICS_PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mPhysionicsPermissionsGranted = false;
        switch(requestCode) {
            case PHYSIONICS_PERMISSIONS_REQUEST_CODE: {
                if(grantResults.length > 0) {
                    for (int i = 0; i<grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mPhysionicsPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed, app terminated");
                            Toast.makeText(this, "사용권한 승인 필요", Toast.LENGTH_SHORT).show();
                            finish();

                            //finish();
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mPhysionicsPermissionsGranted = true;
                }
            }
        }
    }
}
