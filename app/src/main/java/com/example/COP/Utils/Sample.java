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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.COP.Utils.Stopwatch;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppCompatImageView left_monitor, right_monitor;
    private AppCompatImageView btn_set, btn_ble;
    private TextView txt_alram, btn_start;
    private Stopwatch stopWatch = new Stopwatch();
    private CircleProgressBar circleProgress_01, circleProgress_02, circleProgress_03, circleProgress_04;

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

        circleProgress_01 = (CircleProgressBar)findViewById(R.id.circleProgress_01);

        circleProgress_01.setProgressFormatter(new MyProgressFormatter()); // 포맷터
        circleProgress_01.setProgress(75); // 값설정하는방법

        /**
         * Vector 이미지 변경방법
         * left_monitor.setImageResource(R.drawable.svg파일명);
         *
         * 왼쪽 이미지 목록
         *  - res/drawable -> ic_svg_left_monitor_01, 02, 03.xml
         * 오른쪽 이미지 목록
         *  - res/drawable -> ic_svg_right_monitor_01, 02, 03.xml
         */
    }


    // 설정이미지, START 텍스트뷰 클릭시 제어
    private class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.txt_alram :
                    // 설정창 이동할것
                    break;
                case R.id.btn_start :
                case R.id.btn_ble :
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

    // 스톱워치 1초간격으로 호출되는 이벤트 클래스
    private class StopwatchListener implements Stopwatch.StopWatchListener{
        @Override
        public void onTick(String time) {
            /**
             * @param
             *  time : 00:00:00으로 들어옴
             */
            btn_start.setText(time);
        }
    }

    private class MyProgressFormatter implements CircleProgressBar.ProgressFormatter {
        private static final String DEFAULT_PATTERN = "%d%%";

        @Override
        public CharSequence format(int progress, int max) {
            return String.format(DEFAULT_PATTERN, (int) ((float) progress / (float) max * 100));
        }
    }
}
