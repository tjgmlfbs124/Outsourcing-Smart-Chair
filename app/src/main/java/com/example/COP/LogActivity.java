package com.example.COP;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;

public class LogActivity extends AppCompatActivity {
    private TextView txt_posit_01,txt_posit_02,txt_posit_03,txt_posit_04,txt_posit_05,txt_posit_06;
    private SegmentedButtonGroup btnGroup;
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
    }

    class onChangeButtonGroup implements SegmentedButtonGroup.OnPositionChangedListener{
        @Override
        public void onPositionChanged(int position) {
            switch (position){
                case 0 : // 오늘
                    break;
                case 1 : // 최근 일주일
                    break;

                case 2 : // 최근 한달
                    break;
            }
        }
    }
}
