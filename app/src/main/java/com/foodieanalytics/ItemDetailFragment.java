package com.foodieanalytics;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.foodieanalytics.dummy.DummyContent;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static LinkedHashMap<String, String> CACHED_PREDS = null;
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem.content != null && mItem.content.equalsIgnoreCase("Dashboard")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://public.tableau.com/profile/erik.platt#!/vizhome/FoodieAnalyticsDashboardv2/LocationDashboard?publish=yes"));
            startActivity(browserIntent);
        } else {
            if (CACHED_PREDS == null) CACHED_PREDS = read();
            List<String> spinnerArray = new ArrayList<String>();
            Iterator iterator = CACHED_PREDS.keySet().iterator();
            String prev = null;
            while (iterator.hasNext()) {
                String val = (String) iterator.next();
                val = val.substring(0, val.lastIndexOf("_"));
                if (prev == null || !prev.equalsIgnoreCase(val))
                    spinnerArray.add(val);
                prev = val;
            }
            System.out.println(spinnerArray.size());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    super.getContext(), android.R.layout.simple_spinner_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sItems = (Spinner) rootView.findViewById(R.id.SpinnerFeedbackType);
            sItems.setAdapter(adapter);

            final Calendar myCalendar = Calendar.getInstance();

            final EditText edittext = (EditText) rootView.findViewById(R.id.startDay);
            final EditText endtext = (EditText) rootView.findViewById(R.id.endDay);
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(edittext, myCalendar);
                }

            };

            edittext.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(ItemDetailFragment.super.getContext(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            endtext.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    DatePickerDialog end = new DatePickerDialog(ItemDetailFragment.super.getContext(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    end.show();
                }

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(endtext, myCalendar);
                    }

                };
            });

            Button sub = (Button) rootView.findViewById(R.id.submit);
            sub.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Spinner mySpinner = (Spinner) rootView.findViewById(R.id.SpinnerFeedbackType);
                    String text = mySpinner.getSelectedItem().toString();

                    EditText edittext = (EditText) rootView.findViewById(R.id.startDay);
                    String start = edittext.getText().toString();

                    EditText endtext = (EditText) rootView.findViewById(R.id.endDay);
                    String end = endtext.getText().toString();

                    System.out.println(text + "," + start + "," + end);

                    if (CACHED_PREDS == null) CACHED_PREDS = read();
                    List<Integer> pred = new ArrayList<>();
                    String index = CACHED_PREDS.get(text + "_" + start);
                    index = index.substring(index.indexOf(":") + 1, index.length());
                    String endindex = CACHED_PREDS.get(text + "_" + end);
                    endindex = endindex.substring(endindex.indexOf(":") + 1, endindex.length());

                    List array = new ArrayList(CACHED_PREDS.keySet()).subList(Integer.parseInt(index), Integer.parseInt(endindex)+1);

                    for (int i = 0; i < array.size(); i++) {
                        String o = CACHED_PREDS.get(array.get(i));
                        o = o.substring(0, o.indexOf(":"));
                        pred.add(new Integer((int) Double.parseDouble(o)));
                    }
                    HorizontalBarChart chart = (HorizontalBarChart) rootView.findViewById(R.id.chart);
                    BarDataSet set1;
                    set1 = new BarDataSet(getDataSet(pred), "The year 2017");

                    set1.setColors(Color.parseColor("#F78B5D"), Color.parseColor("#FCB232"), Color.parseColor("#FDD930"), Color.parseColor("#ADD137"), Color.parseColor("#A0C25A"));

                    ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                    dataSets.add(set1);

                    BarData data = new BarData(dataSets);

                    // hide Y-axis
                    YAxis left = chart.getAxisLeft();
                    left.setDrawLabels(false);

                    // custom X-axis labels
                    List<String> values = array;
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

                    chart.setData(data);

                    // custom description
                    Description description = new Description();
                    description.setText("Visitors");
                    chart.setDescription(description);

                    // hide legend
                    chart.getLegend().setEnabled(false);

                    chart.animateY(1000);
                    chart.invalidate();
                }
        });


        return ((ScrollView) rootView.findViewById(R.id.ScrollView01));
    }

        return rootView;
}

public class MyXAxisValueFormatter implements IAxisValueFormatter {

    private List<String> mValues;

    public MyXAxisValueFormatter(List<String> values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String valret = String.valueOf(mValues.get((int) value));
        return valret.substring(valret.lastIndexOf("_")+1);
    }

}

    private ArrayList<BarEntry> getDataSet(List<Integer> data) {

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            Integer inte = data.get(i);
            BarEntry v1e2 = new BarEntry(i, inte);
            valueSet1.add(v1e2);
        }

        return valueSet1;
    }


    private void updateLabel(EditText edittext, Calendar myCalendar) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    public LinkedHashMap<String, String> read() {
        LinkedHashMap<String, String> resultList = new LinkedHashMap();
        BufferedReader reader = null;
        int count = -1;
        try {
            reader = new BufferedReader(new InputStreamReader(super.getContext().getAssets().open("pred.csv")));
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                if (count == -1) {
                    count++;
                    continue;
                }
                String row = csvLine;
                resultList.put(row.substring(0, row.indexOf(",")), row.substring(row.indexOf(",") + 1, row.length()) + ":" + count);
                count++;
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        return resultList;
    }

    public void onClickBtn(View v) {
        Toast.makeText(super.getContext(), "Clicked on Button", Toast.LENGTH_LONG).show();
    }
}
