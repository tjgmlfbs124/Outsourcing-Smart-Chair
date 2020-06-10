package com.example.COP;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.example.COP.Utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class LogActivity extends AppCompatActivity {
    private TextView txt_posit_01,txt_posit_02,txt_posit_03,txt_posit_04,txt_posit_05,txt_posit_06;
    private SegmentedButtonGroup btnGroup;

    private JSONArray jsonArrayData;

    private Integer verticalFront=0, verticalCenter=0, verticalBack=0;
    private Integer horizontalLeft=0, horizontalRight=0, horizontalCenter=0;
    private Integer dataLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        txt_posit_01 = (TextView)findViewById(R.id.txt_posit_01); // 앞
        txt_posit_02 = (TextView)findViewById(R.id.txt_posit_02); // 중
        txt_posit_03 = (TextView)findViewById(R.id.txt_posit_03); // 뒤
        txt_posit_04 = (TextView)findViewById(R.id.txt_posit_04); // 왼
        txt_posit_05 = (TextView)findViewById(R.id.txt_posit_05); // 중
        txt_posit_06 = (TextView)findViewById(R.id.txt_posit_06); // 우

        btnGroup = (SegmentedButtonGroup)findViewById(R.id.btnGroup);
        btnGroup.setOnPositionChangedListener(new onChangeButtonGroup());


        /* 일간 데이터 처리*/
        dataSet(1);

        /*
        Date date = new Date(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date date2 = calendar.getTime();

        //Log.d("@ckw", Integer.toString(date.compareTo(date2)) );
        Log.d("@ckw", "data:"+(String)DateFormat.format("yyMMdd-HHmmss", date));

        Log.d("@ckw", "data2:"+(String)DateFormat.format("yyMMdd-HHmmss", date2));

        Long curTime = (System.currentTimeMillis());
        String now = "curT:"+(String)DateFormat.format("yyMMdd-HHmmss", curTime);
        Log.d("@ckw", now);


        try {
            JSONObject obj = new JSONObject();
            obj.put("date", System.currentTimeMillis());
            obj.put("verPos", 1); // -1:앞/0:센터/1:뒤
            obj.put("horPos", -1); // -1:왼/0:센터/1:우
        } catch (JSONException e) {
            Log.d("@ckw", e.getMessage());
        }*/
    }

    class onChangeButtonGroup implements SegmentedButtonGroup.OnPositionChangedListener{
        @Override
        public void onPositionChanged(int position) {
            switch (position){
                case 0 : // 오늘
                    dataSet(1);
                    break;
                case 1 : // 최근 일주일
                    dataSet(7);
                    break;

                case 2 : // 최근 한달
                    dataSet(30);
                    break;
            }
        }
    }

    /***
     * 몇일 전 까지 자료를 볼껀지
     * @param amount 일 전 부터 현재까지 데이터
     */
    private void dataSet(int amount) {
        verticalFront = verticalCenter = verticalBack = 0;
        horizontalLeft= horizontalCenter=horizontalRight = 0;
        dataLength = 0;

        try {
            jsonArrayData = new JSONArray();
            jsonArrayData = PreferenceManager.getJsonArray(getApplicationContext(), "sitting_position");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1*amount);
            Long tempL = calendar.getTimeInMillis();
            String result = "";
            for (int i = 0; i < jsonArrayData.length(); i++) {
                JSONObject tempObj = jsonArrayData.getJSONObject(i);
                Integer verData, horData;
                verData = tempObj.getInt("verPos");
                horData = tempObj.getInt("horPos");
                if (tempObj.getLong("date") < tempL) continue;
                result += "\ndate:" + tempObj.getString("date") + ", verPos:" + tempObj.getString("verPos") + ", horPos:" + tempObj.getString("horPos");

                switch (verData) {
                    case -1:
                        verticalFront++;
                        break;
                    case 0:
                        verticalCenter++;
                        break;
                    case 1:
                        verticalBack++;
                        break;
                    default:
                        break;
                }
                switch (horData) {
                    case -1:
                        horizontalLeft++;
                        break;
                    case 0:
                        horizontalCenter++;
                        break;
                    case 1:
                        horizontalRight++;
                        break;
                    default:
                        break;
                }
                dataLength++;

            }
            Log.d("@ckw", result);
        } catch (JSONException e) {
            Log.d("@ckw", e.getMessage());
        }

        /*Log.d("@ckw","1:"+horizontalLeft.toString());
        Log.d("@ckw","2:"+horizontalCenter.toString());
        Log.d("@ckw","3:"+horizontalRight.toString());
        Log.d("@ckw","4:"+dataLength.toString());*/

        double tempPercent = (double) verticalFront / (double) dataLength * 100;
        txt_posit_01.setText(String.format("%.2f", tempPercent) + "%");
        tempPercent = (double) verticalCenter / (double) dataLength * 100;
        txt_posit_02.setText(String.format("%.2f", tempPercent) + "%");
        tempPercent = (double) verticalBack / (double) dataLength * 100;
        txt_posit_03.setText(String.format("%.2f", tempPercent) + "%");

        tempPercent = (double) horizontalLeft / (double) dataLength * 100;
        txt_posit_04.setText(String.format("%.2f", tempPercent) + "%");
        tempPercent = (double) horizontalCenter / (double) dataLength * 100;
        txt_posit_05.setText(String.format("%.2f", tempPercent) + "%");
        tempPercent = (double) horizontalRight / (double) dataLength * 100;
        txt_posit_06.setText(String.format("%.2f", tempPercent) + "%");
    }
}
