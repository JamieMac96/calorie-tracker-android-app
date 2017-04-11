package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by jamie on 04/03/17.
 */

public class AddFoodActivity extends Activity {
    private SearchView searchView;
    private String DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/search-global-food.php";
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add_food);

        Button backButton = (Button) findViewById(R.id.toolbar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Add Food");

        searchView = (SearchView) findViewById(R.id.search_bar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                searchQuery = s;

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
                return false;
            }
        });
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





































}
