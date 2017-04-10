package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by jamie on 04/03/17.
 */

public class ProgressActivity extends Activity {
    private double [] testWeights = {230,220,210,200,190,185,230,220,210,200,190,185};
    private String [] testDates = {"1/1/2016", "3/3/2016", "4/5/2016", "4/7/2016", "8/9/2017", "1/1/2017", "1/1/2016", "3/3/2016", "4/5/2016", "4/7/2016", "8/9/2017", "1/1/2017"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_progress);

        Button backButton = (Button) findViewById(R.id.toolbar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Progress");

        ListView progressList = (ListView) findViewById(R.id.progress_list);

        CustomAdapter cAdapter = new CustomAdapter();

        progressList.setAdapter(cAdapter);

    }

    class CustomAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return testWeights.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.progress_item, null);

            TextView dateItem = (TextView) convertView.findViewById(R.id.date_item);
            TextView weightItem = (TextView) convertView.findViewById((R.id.bodyweight_item));

            dateItem.setText(testDates[position]);

            weightItem.setText(testWeights[position] + "");
            return convertView;
        }
    }



}
