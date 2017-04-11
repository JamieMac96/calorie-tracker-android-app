package com.macmanus.jamie.loanpal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

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

            SearchItemAdapter fAdapter = new SearchItemAdapter(this,R.layout.search_result_item, R.id.search_result_item, items);

            foodList.setAdapter(fAdapter);
        }
        catch (JSONException e){

        }

    }
}
