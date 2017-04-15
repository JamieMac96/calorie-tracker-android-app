package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by jamie on 04/03/17.
 */

public class AddFoodActivity extends Activity {
    private SearchView searchView;
    //private String DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/search-global-food.php";
    private String DESTINATION = "http://34.251.31.162/search-global-food.php";
    private ImageButton speechButton;
    private String searchQuery;
    private List<FoodItem> userFoods;
    private List<FoodItem> filteredUserFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add_food);

        setBackButtonClickListener();

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Add Food");

        searchView = (SearchView) findViewById(R.id.search_bar);
        speechButton = (ImageButton) findViewById(R.id.speech_button);

        getUserFoods();

        filteredUserFoods = new ArrayList<FoodItem>(userFoods);

        displayUserFoods();

        handleSpeechInput();

        setSearchViewQueryListener();
    }



    public class SearchItemRetriever extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute(){
            Log.e("pre execute ", "pre execute");
            super.onPreExecute();
        }
        //make synchronous volley request to get users bodyweight entries.
        @Override
        protected String doInBackground(Void... params) {

            Map<String, String> requestParams = new HashMap<String, String>();
            requestParams.put("searchQuery", searchQuery);
            Log.e("searchQuery: ", searchQuery + "");

            //create synchronous request
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            CustomRequest request = new CustomRequest(Request.Method.POST, DESTINATION, requestParams, future, future);
            RequestQueueHelper helper = RequestQueueHelper.getInstance();
            helper.add(request);
            Log.e(request.toString(), "request");
            JSONArray result = new JSONArray();

            try {
                JSONObject response = future.get(); // this will block
                boolean requestOutcome;
                try {
                    Log.e("in try", "in try");
                    requestOutcome = response.getBoolean("success");
                    if (requestOutcome) {
                        Log.e("response true", "true");
                        result = response.getJSONArray("result");
                    } else {
                        Log.e("response false", "false");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            String stringifiedItems = result.toString();

            return stringifiedItems;
        }


        //parse result of doInBackground and send to displayResult method.
        @Override
        protected void onPostExecute(String resultFromDoInBackground) {
            sendToSearchResultActivity(resultFromDoInBackground);
        }


    }


    public void sendToSearchResultActivity(String items){
        if(items.equals("[]")){
            Toast.makeText(this, "No search results found", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent searchResults = new Intent(this, SearchResultsActivity.class);
            searchResults.putExtra("jsonSearchResults", items);
            searchResults.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(searchResults);
        }
    }


    private void getUserFoods(){
        userFoods = new ArrayList<FoodItem>();
        MyDatabaseHandler myDB = MyDatabaseHandler.getInstance(this);

        List<String> dbAsString = myDB.getTableInfoAsString(MyDatabaseHandler.USER_FOOD_TABLE_NAME);

        //convert db string to arraylist of FoodItems
        for(int i = 1; i < dbAsString.size(); i++){
            String [] splitRow = dbAsString.get(i).split(",");
            userFoods.add(new FoodItem(Integer.parseInt(splitRow[0]), splitRow[2], splitRow[3], Double.parseDouble(splitRow[4]),
                    Double.parseDouble(splitRow[7]), Double.parseDouble(splitRow[8]), Double.parseDouble(splitRow[6])));
        }
    }

    private void handleSpeechInput(){
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    public void promptSpeechInput(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        try {
            startActivityForResult(i, 100);
        }
        catch(ActivityNotFoundException e){
            Toast.makeText(this, "Sorry your device does not support speech language!", Toast.LENGTH_SHORT);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent i){
        super.onActivityResult(requestCode, resultCode, i);

        switch(requestCode) {
            case 100: if(resultCode == RESULT_OK && i != null){
                ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchView.setQuery(result.get(0), false);
                searchView.clearFocus();
            }
                break;
        }
    }

    private void displayUserFoods(){
        //retrieve listview
        ListView foodList = (ListView) findViewById(R.id.search_item_list_dynamic);

        //
        final ArrayList<FoodItem> finalItems = (ArrayList<FoodItem>) filteredUserFoods;

        SearchItemAdapter fAdapter = new SearchItemAdapter(this, R.layout.search_result_item, R.id.search_result_item, finalItems);

        foodList.setAdapter(fAdapter);

        foodList.setClickable(true);

        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openFoodItem = new Intent(AddFoodActivity.this, FoodItemActivity.class);
                String stringifiedFoodItem = finalItems.get(position).toString();
                openFoodItem.putExtra("foodItem", stringifiedFoodItem);

                //TODO: change the calledFrom flag to searchResultsOffline
                //TODO: and in the FoodItemActivity add functionality to add to the dailyfood local table.
                //TODO: Next we should add a service that continually checks for a network connection and when
                //TODO: the connection appears we should update the remote dailyfoods table from the local table.
                openFoodItem.putExtra("calledFrom", "searchResults");
                startActivity(openFoodItem);
            }
        });

    }

    private void applyFilterToFilteredFoods(){
        //in case we have already filtered the foods we want to re assign the full list and
        //essentially lift the previous filter
        filteredUserFoods = new ArrayList<FoodItem>(userFoods);

        //now we apply  the filter
        for(int i = 0; i < filteredUserFoods.size(); i++){
            Log.e(filteredUserFoods.get(i).toString(),"<-----ROW");
            if(! filteredUserFoods.get(i).getTitle().toLowerCase().contains(searchQuery.toLowerCase())){
                filteredUserFoods.remove(i);
                i--;
            }
        }
    }

    private void setBackButtonClickListener(){
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
    }

    private void setSearchViewQueryListener(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                searchQuery = s.toLowerCase();

                if(searchQuery != null) {
                    if (searchQuery.length() > 0) {
                        AsyncTask<Void, Void, String> task = new AddFoodActivity.SearchItemRetriever();

                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText.toLowerCase();

                applyFilterToFilteredFoods();

                displayUserFoods();
                return false;
            }
        });
    }
}
