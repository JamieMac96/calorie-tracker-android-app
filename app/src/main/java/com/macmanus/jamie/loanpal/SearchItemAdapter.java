package com.macmanus.jamie.loanpal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jamie on 11/04/17.
 */

public class SearchItemAdapter extends ArrayAdapter<FoodItem> {
    private ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();
    private Context context;


    SearchItemAdapter(Context context,int resource, int textViewResourceId, ArrayList<FoodItem> items){
        super(context, resource, textViewResourceId, items);
        this.context = context;
        foodItems = items;
    }

    @Override
    public int getCount() {
        return this.foodItems.size();
    }

    @Override @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("executed", "executes");
        LayoutInflater myInflator = LayoutInflater.from(this.context);

        convertView = myInflator.inflate(R.layout.search_result_item, null);

        TextView title = (TextView) convertView.findViewById(R.id.search_result_food_name);
        TextView description = (TextView) convertView.findViewById((R.id.search_result_food_description));
        TextView servingSize = (TextView) convertView.findViewById((R.id.search_result_serving_size));

        title.setText(foodItems.get(position).getTitle());

        description.setText(foodItems.get(position).getDescription());

        servingSize.setText(", " + foodItems.get(position).getServingSize());

        return convertView;
    }

}