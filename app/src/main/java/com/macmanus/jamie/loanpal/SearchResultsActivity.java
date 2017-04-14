package com.macmanus.jamie.loanpal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by jamie on 11/04/17.
 */

public class SearchResultsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        final ImageButton backButton = (ImageButton) findViewById(R.id.toolbar_back_button);
        backButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    backButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight));
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    backButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    finish();
                }
                return false;
            }
        });

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Search Results");


        String jsonSearchResults = getIntent().getExtras().getString("jsonSearchResults");

        try{
            JSONArray jsonResult = new JSONArray(jsonSearchResults);

            Log.e(jsonSearchResults, "<-------resultsHERE");

            ArrayList<FoodItem> items = new ArrayList<FoodItem>();

            for(int i = 0; i < jsonResult.length(); i++){
                Log.e(jsonResult.getString(i), "JSON ELEMENT");

                JSONArray innerArray = jsonResult.getJSONArray(i);
                items.add(new FoodItem(innerArray.getInt(0), innerArray.getString(1), innerArray.getString(2),
                            innerArray.getDouble(3), innerArray.getDouble(4), innerArray.getDouble(5), innerArray.getDouble(6)));
            }



            ListView foodList = (ListView) findViewById(R.id.search_item_list);

            foodList.setEmptyView(findViewById(R.id.empty));

            SearchItemAdapter fAdapter = new SearchItemAdapter(this, R.layout.search_result_item, R.id.search_result_item, items);

            foodList.setAdapter(fAdapter);

            foodList.setClickable(true);
            final ArrayList<FoodItem> finalItems = items;

            foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent openFoodItem = new Intent(SearchResultsActivity.this, FoodItemActivity.class);
                    String stringifiedFoodItem = finalItems.get(position).toString();
                    openFoodItem.putExtra("foodItem", stringifiedFoodItem);
                    openFoodItem.putExtra("calledFrom", "searchResults");
                    startActivity(openFoodItem);
                }
            });

        }
        catch (JSONException e){

        }

    }
}
