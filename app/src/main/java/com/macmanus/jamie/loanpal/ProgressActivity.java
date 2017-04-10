package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
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

public class ProgressActivity extends Activity {
    private double [] testWeights;
    private String [] testDates;

    private final String DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/get-progress.php";



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

        Log.e("now herre1","now herre1");

        AsyncTask<Void, Void, String> task= new ProgressItemRetriever();

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Log.e("now herre1","now herre1");


    }

    public class ProgressItemRetriever extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute(){
            Log.e("pre execute ", "pre execute");
            super.onPreExecute();
        }


        //make synchronous volley request to get users bodyweight entries.
        @Override
        protected String doInBackground(Void... params) {
            SessionManager manager = SessionManager.getInstance(getApplicationContext());
            String userID = manager.getUserID();
            Map<String, String> requestParams = new HashMap<String, String>();
            requestParams.put("userID", userID);
            Log.e("userID: ", userID + "");
            ArrayList<ProgressItem> progressItems = new ArrayList<ProgressItem>();

            //create synchronous request
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            CustomRequest request = new CustomRequest(Request.Method.POST, DESTINATION, requestParams, future, future);
            RequestQueueHelper helper = RequestQueueHelper.getInstance();
            helper.add(request);
            Log.e(request.toString(), "request");

            try {
                JSONObject response = future.get(); // this will block
                boolean requestOutcome = false;
                try {
                    requestOutcome = response.getBoolean("success");
                    if (requestOutcome) {
                        JSONArray result = response.getJSONArray("result");
                        for (int i = 0; i < result.length(); i++) {
                            JSONArray innerArray = result.getJSONArray(i);

                            progressItems.add(new ProgressItem(innerArray.getString(0), innerArray.getString(1)));
                            Log.e(progressItems.get(i).getWeight(), progressItems.get(i).getWeighInDate());
                        }
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

            String stringifiedItems = "";
            for(int i = 0; i < progressItems.size(); i++){
                if(i == progressItems.size() - 1){
                    stringifiedItems += progressItems.get(i).toString();
                }
                else{
                    stringifiedItems += progressItems.get(i).toString() + "#";
                }
            }

            return stringifiedItems;
        }


        //parse result of doInBackground and send to displayResult method.
        @Override
        protected void onPostExecute(String resultFromDoInBackground) {
            String [] splitResult = resultFromDoInBackground.split("#");
            ArrayList<ProgressItem> items = new ArrayList<ProgressItem>();

            for(int i = 0; i < splitResult.length; i++){
                String [] progressItemComponents = splitResult[i].split(",");
                if(progressItemComponents.length == 2) {
                    items.add(new ProgressItem(progressItemComponents[0], progressItemComponents[1]));
                }
            }

            displayResult(items);
        }

    }

    public void displayResult(ArrayList<ProgressItem> items){

        ListView progressList = (ListView) findViewById(R.id.progress_list);

        ProgressItemAdapter pAdapter = new ProgressItemAdapter(this,R.id.progress_list, R.id.progress_item, items);

        progressList.setAdapter(pAdapter);
    }


}
