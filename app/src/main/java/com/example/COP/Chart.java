package com.example.COP;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import static android.graphics.Color.BLUE;


public class Chart {

    public LineChart initChartConfig(LineChart mChart, String tag) {

        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText(tag);


        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        mChart.setVisibleXRangeMaximum(20);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);


        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);  //

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        //leftAxis.setAxisMaximum(10);
        //leftAxis.setAxisMinimum(0);
        leftAxis.setDrawGridLines(true);


        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(true);
        mChart.setDrawBorders(true);

        return mChart;
    }

    public LineChart initChartConfig_2(LineChart mChart, String tag) {

        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText(tag);


        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        mChart.setVisibleXRangeMaximum(25);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        //leftAxis.setAxisMaximum(550);
        //leftAxis.setAxisMinimum(350);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(true);
        mChart.setDrawBorders(true);

        return mChart;
    }

    public LineChart initChartConfig_3(LineChart mChart, String tag) {

        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText(tag);


        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        mChart.setVisibleXRangeMaximum(25);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        //leftAxis.setAxisMaximum(550);
        //leftAxis.setAxisMinimum(350);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(true);
        mChart.setDrawBorders(true);

        return mChart;
    }


    private long removalCounter = 0;
    private static final int VISIBLE_COUNT = 100;

    public void addNewEntry(LineChart mChart, float value1) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount() + removalCounter, value1), 0);
            data.notifyDataChanged();

            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(VISIBLE_COUNT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());


            mChart.setVisibleXRangeMaximum(VISIBLE_COUNT);


            // move to the latest entry
            mChart.moveViewToX(set.getEntryCount());


            /*if (set.getEntryCount() >= VISIBLE_COUNT) {
                set.removeFirst();
                for (int i=0; i<set.getEntryCount(); i++) {
                    Entry entryToChange = set.getEntryForIndex(i);
                    entryToChange.setX(entryToChange.getX() - 1);
                }
            }*/

            mChart.moveViewToX(data.getEntryCount());
        }


    }

    public void addEntry(LineChart mChart, float value1, float value2, float value3) {

        LineData data = mChart.getData();


        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);
            ILineDataSet set3 = data.getDataSetByIndex(2);

            if (set == null) {
                set = createSet();
                set2 = createSet2();
                set3 = createSet3();
                data.addDataSet(set);
                data.addDataSet(set2);
                data.addDataSet(set3);
            }

            data.addEntry(new Entry(set.getEntryCount() + removalCounter, value1), 0);
            data.addEntry(new Entry(set2.getEntryCount() + removalCounter, value2), 1);
            data.addEntry(new Entry(set3.getEntryCount() + removalCounter, value3), 2);
            data.notifyDataChanged();

            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(VISIBLE_COUNT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());


            mChart.setVisibleXRangeMaximum(VISIBLE_COUNT);


            // move to the latest entry
            mChart.moveViewToX(set.getEntryCount());
            mChart.moveViewToX(set2.getEntryCount());
            mChart.moveViewToX(set3.getEntryCount());


            /*if (set.getEntryCount() >= VISIBLE_COUNT) {
                set.removeFirst();
                for (int i=0; i<set.getEntryCount(); i++) {
                    Entry entryToChange = set.getEntryForIndex(i);
                    entryToChange.setX(entryToChange.getX() - 1);
                }
            }*/

            mChart.moveViewToX(data.getEntryCount());
        }
    }

    public void magaddEntry(LineChart mChart, float value1, float value2, float value3) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);
            ILineDataSet set3 = data.getDataSetByIndex(2);

            if (set == null) {
                set = createSet();
                set2 = createSet2();
                set3 = createSet3();
                data.addDataSet(set);
                data.addDataSet(set2);
                data.addDataSet(set3);
            }

            data.addEntry(new Entry(set.getEntryCount() + removalCounter, value1), 0);
            data.addEntry(new Entry(set2.getEntryCount() + removalCounter, value2), 1);
            data.addEntry(new Entry(set3.getEntryCount() + removalCounter, value3), 2);
            data.notifyDataChanged();

            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(VISIBLE_COUNT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());


            mChart.setVisibleXRangeMaximum(VISIBLE_COUNT);


            // move to the latest entry
            mChart.moveViewToX(set.getEntryCount());
            mChart.moveViewToX(set2.getEntryCount());
            mChart.moveViewToX(set3.getEntryCount());

            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private ILineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "X-AXIS");

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.GREEN);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setValueTextColor(Color.BLACK);


        return set;
    }

    private ILineDataSet createSet2() {
        LineDataSet set = new LineDataSet(null, "Y-AXIS");

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.RED);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setValueTextColor(Color.BLACK);
        return set;
    }

    private ILineDataSet createSet3() {
        LineDataSet set = new LineDataSet(null, "Z-AXIS");

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(BLUE);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setValueTextColor(Color.BLACK);
        return set;
    }











}

