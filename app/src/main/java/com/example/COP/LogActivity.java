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

public class LogActivity extends AppCompatActivity {
    private TextView txt_posit_01,txt_posit_02,txt_posit_03,txt_posit_04,txt_posit_05,txt_posit_06;
    private SegmentedButtonGroup btnGroup;

    private JSONArray jsonArrayData;

    //private

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        txt_posit_01 = (TextView)findViewById(R.id.txt_posit_01);
        txt_posit_02 = (TextView)findViewById(R.id.txt_posit_02);
        txt_posit_03 = (TextView)findViewById(R.id.txt_posit_03);
        txt_posit_04 = (TextView)findViewById(R.id.txt_posit_04);
        txt_posit_05 = (TextView)findViewById(R.id.txt_posit_05);
        txt_posit_06 = (TextView)findViewById(R.id.txt_posit_06);

        btnGroup = (SegmentedButtonGroup)findViewById(R.id.btnGroup);
        btnGroup.setOnPositionChangedListener(new onChangeButtonGroup());

        try {
            jsonArrayData = PreferenceManager.getJsonArray(getApplicationContext(), "TEST");
            String result = "";
            for(int i=0; i < jsonArrayData.length(); i++) {
                JSONObject tempObj = jsonArrayData.getJSONObject(i);
                result += "\ndate:"+tempObj.getString("date")+", verPos:"+tempObj.getString("verPos")+", horPos:"+tempObj.getString("horPos");
            }
            Log.d("@ckw", result);
        } catch (JSONException e) {
            Log.d("@ckw", e.getMessage());
        }

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
                    Log.d("@ckw", "position change");
                    break;
                case 1 : // 최근 일주일
                    break;

                case 2 : // 최근 한달
                    break;
            }
        }
    }
}
