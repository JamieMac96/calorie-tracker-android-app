package com.macmanus.jamie.loanpal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jamie on 10/04/17.
 */

public class ProgressItemAdapter extends ArrayAdapter<ProgressItem> {
    private ArrayList<ProgressItem> progressItems = new ArrayList<ProgressItem>();
    private Context context;


    ProgressItemAdapter(Context context,int resource, int textViewResourceId, ArrayList<ProgressItem> items){
        super(context, resource, textViewResourceId, items);
        Log.e("construct", "construct");
        this.context = context;
        progressItems = items;
    }

    @Override
    public int getCount() {
        return this.progressItems.size();
    }

    @Override @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("executed", "executes");
        LayoutInflater myInflator = LayoutInflater.from(this.context);

        convertView = myInflator.inflate(R.layout.progress_item, null);

        TextView dateItem = (TextView) convertView.findViewById(R.id.date_item);
        TextView weightItem = (TextView) convertView.findViewById((R.id.bodyweight_item));

        dateItem.setText(progressItems.get(position).getWeighInDate());

        weightItem.setText(progressItems.get(position).getWeight());

        return convertView;
    }

}
