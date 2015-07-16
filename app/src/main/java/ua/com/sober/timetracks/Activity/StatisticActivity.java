package ua.com.sober.timetracks.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.PieChartView;
import ua.com.sober.timetracks.R;
import ua.com.sober.timetracks.provider.ContractClass;

/**
 * Created by dmitry.hmel on 18.03.2015.
 */
public class StatisticActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

//        Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.statisticToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        backToMain();
    }

    private void backToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    /**
     * A fragment containing a pie chart.
     */
    public static class PlaceholderFragment extends Fragment {

        private PieChartView chart;
        private PieChartData data;

        private boolean hasLabels = false;
        private boolean hasLabelsOutside = false;
        private boolean hasCenterCircle = false;
        private boolean hasCenterText1 = false;
        private boolean hasCenterText2 = false;
        private boolean isExploded = false;
        private boolean hasLabelForSelected = false;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_statistic, container, false);

            chart = (PieChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            generateData();

            return rootView;
        }

        private List<SliceValue> getData() {
            Long totalTime;
            String sortOrder = ContractClass.Tasks.COLUMN_NAME_TOTAL_TIME + " ASC";
            Context context = this.getActivity().getApplicationContext();
            Cursor cursor = context.getContentResolver().query(ContractClass.Tasks.CONTENT_URI, null, null, null, sortOrder);
            cursor.moveToFirst();
            List<SliceValue> values = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                totalTime = cursor.getLong(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_TOTAL_TIME));
                if (totalTime < 0) {
                    totalTime = totalTime + System.currentTimeMillis();
                }
                SliceValue sliceValue = new SliceValue((float) totalTime, ChartUtils.pickColor());
                values.add(sliceValue);
                cursor.moveToNext();
            }
            cursor.close();
            return values;
        }

        private void generateData() {
//            int numValues = 6;
//
//            List<SliceValue> values = new ArrayList<>();
//            for (int i = 0; i < numValues; ++i) {
//                SliceValue sliceValue = new SliceValue((float) Math.random() * 30 + 15, ChartUtils.pickColor());
//                values.add(sliceValue);
//            }

            data = new PieChartData(getData());
            data.setHasLabels(hasLabels);
            data.setHasLabelsOnlyForSelected(hasLabelForSelected);
            data.setHasLabelsOutside(hasLabelsOutside);
            data.setHasCenterCircle(hasCenterCircle);

            if (isExploded) {
                data.setSlicesSpacing(24);
            }

            if (hasCenterText1) {
                data.setCenterText1("Hello!");

                // Get roboto-italic font.
                Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
                data.setCenterText1Typeface(tf);

                // Get font size from dimens.xml and convert it to sp(library uses sp values).
                data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                        (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
            }

            if (hasCenterText2) {
                data.setCenterText2("Charts (Roboto Italic)");

                Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");

                data.setCenterText2Typeface(tf);
                data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                        (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
            }

            chart.setPieChartData(data);
        }

        private void explodeChart() {
            isExploded = !isExploded;
            generateData();

        }

        private void toggleLabelsOutside() {
            // has labels have to be true:P
            hasLabelsOutside = !hasLabelsOutside;
            if (hasLabelsOutside) {
                hasLabels = true;
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);
            }

            if (hasLabelsOutside) {
                chart.setCircleFillRatio(0.7f);
            } else {
                chart.setCircleFillRatio(1.0f);
            }

            generateData();

        }

        private void toggleLabels() {
            hasLabels = !hasLabels;

            if (hasLabels) {
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);

                if (hasLabelsOutside) {
                    chart.setCircleFillRatio(0.7f);
                } else {
                    chart.setCircleFillRatio(1.0f);
                }
            }

            generateData();
        }

        private void toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected;

            chart.setValueSelectionEnabled(hasLabelForSelected);

            if (hasLabelForSelected) {
                hasLabels = false;
                hasLabelsOutside = false;

                if (hasLabelsOutside) {
                    chart.setCircleFillRatio(0.7f);
                } else {
                    chart.setCircleFillRatio(1.0f);
                }
            }

            generateData();
        }

        /**
         * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
         * method(don't confuse with View.animate()).
         */
        private void prepareDataAnimation() {
            for (SliceValue value : data.getValues()) {
                value.setTarget((float) Math.random() * 30 + 15);
            }
        }

        private class ValueTouchListener implements PieChartOnValueSelectListener {

            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }
    }

}
