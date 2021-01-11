package com.example.COP;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.COP.Utils.PreferenceManager;
import com.example.COP.Utils.Stopwatch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PHYSIONICS_PERMISSIONS_REQUEST_CODE = 20002;

    private static final int VERTICAL_PERCENT_STANDARD = 60; // 앞뒤 기울어짐 기준값 (> 앞으로 기울어짐)
    private static final int HORIZONTAL_PERCENT_STANDARD = 60; // 좌우 기울어짐 기준값 (> 오른쪽으로 기울어짐)
    private static final int PRESSURE_STANDARD = 30; // 의미있는 압력 기준값

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
     * @ckw 객체 선언
     */
    private AppCompatImageView left_monitor, right_monitor;
    private AppCompatImageView btn_set, btn_ble;
    private TextView txt_alram, btn_start;
    private Stopwatch stopWatch = new Stopwatch();
    private CircleProgressBar circleProgress_01, circleProgress_02, circleProgress_03, circleProgress_04;
    private ProgressBar bleProgress;

    private PendingIntent pendingIntent;

    private Integer alertTime = 20; // 시간 설정
    static Vibrator vibrator; // 진동설정

    private boolean resetAttempt = false;
    private boolean isReset = false;

    private boolean verticalPosition;
    private boolean horizontalPosition;
    private double wholeValue = 0;
    private double verticalPosPercent = 0;
    private double horizontalPosPercent = 0;

    private Handler mHandler;
    private Runnable mRuannble;
    private boolean handlerStarted;

    private Handler logHandler;
    private Runnable logRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        left_monitor = (AppCompatImageView)findViewById(R.id.svg_left_monitor);
        right_monitor = (AppCompatImageView)findViewById(R.id.svg_right_monitor);
        btn_set = (AppCompatImageView)findViewById(R.id.btn_set);
        btn_ble = (AppCompatImageView)findViewById(R.id.btn_ble);
        txt_alram = (TextView)findViewById(R.id.txt_alram);
        btn_start = (TextView)findViewById(R.id.btn_start);

        bleProgress = (ProgressBar)findViewById(R.id.progressBar);

        circleProgress_01 = (CircleProgressBar)findViewById(R.id.circleProgress_01);
        circleProgress_02 = (CircleProgressBar)findViewById(R.id.circleProgress_02);
        circleProgress_03 = (CircleProgressBar)findViewById(R.id.circleProgress_03);
        circleProgress_04 = (CircleProgressBar)findViewById(R.id.circleProgress_04);

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        btn_start.setOnClickListener(new ButtonClickListener());
        btn_set.setOnClickListener(new ButtonClickListener());

        mHandler = new Handler();
        mRuannble = new Runnable() {
            @Override
            public void run() {
                showAlertDialog();
            }
        };
        handlerStarted = false;

        logHandler = new Handler();
        logRunnable = new Runnable() {
            @Override
            public void run() {
                if(wholeValue > PRESSURE_STANDARD) {
                    // 의미있는 데이터
                    txt_alram.setText("자세를 측정중 입니다.");
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("date", System.currentTimeMillis());
                        Integer temp = 0;
                        if(verticalPosPercent > VERTICAL_PERCENT_STANDARD) {
                            temp = -1;
                        } else if(verticalPosPercent <= 100-VERTICAL_PERCENT_STANDARD) {
                            temp = 1;
                        } else { temp = 0; }
                        obj.put("verPos", temp);

                        if(horizontalPosPercent > HORIZONTAL_PERCENT_STANDARD) {
                            temp = 1;
                        } else if(horizontalPosPercent <= 100-VERTICAL_PERCENT_STANDARD) {
                            temp = -1;
                        } else { temp = 0; }
                        obj.put("horPos", temp);

                        PreferenceManager.setJsonArray(getApplicationContext(),"sitting_position", obj);
                    } catch (JSONException e) {

                    }
                } else {
                    txt_alram.setText("앉아있지 않습니다.");
                }
                logHandler.postDelayed(this, 5000);
            }
        };

        // 블루투스 초기화
        bluetoothDevicePairingInit();

    }

    private class StopwatchListener implements Stopwatch.StopWatchListener{
        @Override
        public void onTick(String time) {
            btn_start.setText(time);
        }
    }

    public class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_set :
                    showTimeEditDialog();
                    break;
                case R.id.txt_alram :
                    break;
                case R.id.btn_start :
                case R.id.btn_ble :
                    txt_alram.setText("초기화 중입니다.");

                    if(!isReset) { bleSwitch(); }
                    else {
                        if(!stopWatch.isRunning()) {
                            Log.d("@ckw", "stopwatch! start!");
                            stopWatch.setListener(new StopwatchListener());
                            stopWatch.start();

                        }
                        else {
                            stopWatch.stop();
                        }
                    }
                    break;
            }
        }
    }

    public void layoutClick(View view){
        Intent intent = new Intent(MainActivity.this, LogActivity.class);
        startActivity(intent);
    }

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

    // 잘못된 자세 알람
    private void showAlertDialog() {
        vibrator.vibrate(1000); // 진동 설정

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

    // 알람 시간 설정
    private void showTimeEditDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText(alertTime.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("시간을 설정해 주세요. (초)");

        builder.setView(editText);
        builder.setPositiveButton("입력",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertTime = Integer.parseInt(editText.getText().toString());
                    Toast.makeText(getApplicationContext(),alertTime.toString()+"초 설정" ,Toast.LENGTH_LONG).show();
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

    private void bleSwitch() {
        bleConnectionAttempt();
    }

    private void bleConnectionAttempt() {
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
                Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(newIntent, mBluetoothBridge.REQUEST_SELECT_DEVICE);
            }
        }
    }

    private void bluetoothDevicePairingInit() {

        mBluetoothBridge.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothBridge.mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
//            finish();
            return;
        }

        ble_service_init();
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
                                Log.d("@ckw", "onFinish!"); //@ckw

                                if(mBluetoothBridge.mService.isConnected()) {
                                    mBluetoothBridge.mService.sendCommand(0x53, null); // @ckw 데이터 송신 요청!
                                }

                            }
                        }.start();


                        Log.d(TAG, "UART_CONNECT_MSG");
                    }
                });
            }

            if (action.equals(BleDataTransferService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        mBluetoothBridge.mState = BluetoothBridge.UART_PROFILE_DISCONNECTED;
                        mBluetoothBridge.mConnectionSuccess = false;
                        Arrays.fill(mBluetoothBridge.mUartData, (byte)0);
                        mBluetoothBridge.mService.close();
                        Log.d(TAG, "UART_DISCONNECT_MSG");
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

                    str = decimalFormat.format(raw_LC2);

                    str = decimalFormat.format(raw_LC3);

                    str = decimalFormat.format(raw_LC4);

                    double LF = (Math.abs(raw_LC3)*100);
                    double RF = (Math.abs(raw_LC4)*100);
                    double LB = (Math.abs(raw_LC1)*100);
                    double RB = (Math.abs(raw_LC2)*100);

                    wholeValue = LF+RF+LB+RB;

                    int LF_percent = (int)((LF/wholeValue)*100);
                    int RF_percent = (int)((RF/wholeValue)*100);
                    int LB_percent = (int)((LB/wholeValue)*100);
                    int RB_percent = (int)((RB/wholeValue)*100);

                    verticalPosPercent = LF_percent + RF_percent;
                    horizontalPosPercent = RF_percent + RB_percent;

                    if(isReset && wholeValue > PRESSURE_STANDARD) { // 최소 데이터 크기 30 필요
                        circleProgress_01.setProgress( LF_percent );
                        circleProgress_02.setProgress( RF_percent );
                        circleProgress_03.setProgress( LB_percent );
                        circleProgress_04.setProgress( RB_percent );

                        if( verticalPosPercent >= VERTICAL_PERCENT_STANDARD) {
                            // 앞으로 기울어짐
                            left_monitor.setImageResource(R.drawable.ic_svg_left_monitor_02);
                            verticalPosition = false;
                        } else if (verticalPosPercent < 100-VERTICAL_PERCENT_STANDARD) {
                            // 뒤로 기울어짐
                            left_monitor.setImageResource(R.drawable.ic_svg_left_monitor_01);
                            verticalPosition = false;
                        } else {
                            // 정자세
                            left_monitor.setImageResource(R.drawable.ic_svg_left_monitor_03);
                            verticalPosition = true;
                        }


                        if( horizontalPosPercent >= HORIZONTAL_PERCENT_STANDARD) {
                            // 오른쪽으로 기울어짐
                            right_monitor.setImageResource(R.drawable.ic_svg_right_monitor_03);
                            horizontalPosition = false;
                        } else if ( horizontalPosPercent < 100-HORIZONTAL_PERCENT_STANDARD) {
                            // 왼쪽으로 기울어짐
                            right_monitor.setImageResource(R.drawable.ic_svg_right_monitor_02);
                            horizontalPosition = false;
                        } else {
                            // 정자세
                            right_monitor.setImageResource(R.drawable.ic_svg_right_monitor_01);
                            horizontalPosition = true;

                        }

                        if(!verticalPosition || !horizontalPosition) {
                            if(!handlerStarted) {
                                handlerStarted = true;
                                mHandler.postDelayed(mRuannble, alertTime*1000);
                            }
                        } else {
                            if(handlerStarted) {
                                handlerStarted = false;
                                mHandler.removeCallbacks(mRuannble);
                            }
                        }
                    } else { // 입력값이 작을 떄
                        circleProgress_01.setProgress( 0 );
                        circleProgress_02.setProgress( 0 );
                        circleProgress_03.setProgress( 0 );
                        circleProgress_04.setProgress( 0 );

                        left_monitor.setImageResource(R.drawable.ic_svg_left_monitor_03);
                        right_monitor.setImageResource(R.drawable.ic_svg_right_monitor_01);

                        if(handlerStarted) {
                            handlerStarted = false;
                            mHandler.removeCallbacks(mRuannble);
                        }
                    }


                    oo = Math.abs(raw_LC3);
                    xo = Math.abs(raw_LC4);
                    oy = Math.abs(raw_LC1);
                    xy = Math.abs(raw_LC2);

                    Fz = oo + xo + oy + xy;

                    COPx = ( 1 + ((xo +xy) - (oo + oy))/Fz ) * 650 / 2;
                    COPy = ( 1 + ((oy +xy) - (oo + xo))/Fz ) * 650 / 2;

                    str = decimalFormat.format(COPx);

                    str = decimalFormat.format(COPy);

                    if(!resetAttempt) { // 최초 초기화
                        resetAttempt = true;
                        Handler delayHandler = new Handler();
                        delayHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isReset= true;
                                btn_ble.setVisibility(View.GONE);
                                btn_start.setText("START");
                                vibrator.vibrate(1000); // 진동 설정
                                Log.d("@ckw", "sitting data RESET!!");

                                pre_LC_1  = LC_1;
                                pre_LC_2  = LC_2;
                                pre_LC_3  = LC_3;
                                pre_LC_4  = LC_4;

                                pre_LC_5  = LC_5;
                                pre_LC_6  = LC_6;

                                logHandler.post(logRunnable);
                                txt_alram.setText("이제 앉아주세요.");
                            }
                        }, 500);

                    }
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
                    //tvLC_2_11.setText(str+" mV");

                    str = decimalFormat.format(raw_LC6);
                    //tvLC_2_1.setText(str+" mV");
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
