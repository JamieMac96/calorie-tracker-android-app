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
 * Created by jamie on 13/04/17.
 */

public class HomeFoodItemAdpter extends ArrayAdapter {

    private ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();
    private Context context;

    HomeFoodItemAdpter(Context context,int resource, int textViewResourceId, ArrayList<FoodItem> items){
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

        convertView = myInflator.inflate(R.layout.food_item, null);

        TextView title = (TextView) convertView.findViewById(R.id.food_item_name);
        TextView fat = (TextView) convertView.findViewById((R.id.food_item_fat));
        TextView carbs = (TextView) convertView.findViewById((R.id.food_item_carbs));
        TextView protein = (TextView) convertView.findViewById((R.id.food_item_protein));
        TextView cals = (TextView) convertView.findViewById((R.id.food_item_cals));

        double numServings = foodItems.get(position).getNumServings();

        String fatString = String.format("%.1f", foodItems.get(position).getFatPerServing() * numServings) + "g ";
        String proteinString = String.format("%.1f", (foodItems.get(position).getProteinPerServing() * numServings)) + "g ";
        String carbString = String.format("%.1f", (foodItems.get(position).getCarbsPerServing() * numServings)) + "g ";
        String calString = String.format("%.1f", (foodItems.get(position).getCaloriesPerServing() * numServings));

        title.setText(foodItems.get(position).getTitle());
        fat.setText(fatString);
        carbs.setText(carbString);
        protein.setText(proteinString);
        cals.setText(calString);


        return convertView;
    }
}
