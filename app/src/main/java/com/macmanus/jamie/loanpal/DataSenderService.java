package com.macmanus.jamie.loanpal;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jamie on 15/04/17.
 */

public class DataSenderService extends IntentService{

    //private final String ADD_FOOD_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/add-food.php";

    private final String ADD_FOOD_DESTINATION = "http://34.251.31.162/add-food.php";



    public DataSenderService(){
        super("DataSenderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MyDatabaseHandler myDB = MyDatabaseHandler.getInstance(getApplicationContext());

        double numServings = intent.getExtras().getDouble("numServings");
        int foodID  = intent.getExtras().getInt("foodID");
        int id = intent.getExtras().getInt("pendingIntentID");

        Log.e("INFO RECEIVED: ", numServings+ ", "+ foodID + ", "+ id);

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            int userID = Integer.parseInt(SessionManager.getInstance(this).getUserID());

            addDailyFood(userID, numServings, foodID, id);
        }
        else{
            Log.e("No CONNECTION YET", "No CONNECTION YET");
        }
    }

    private void addDailyFood(int userID, double numServings, int globalFoodID, final int pendingIntentID){

        Map<String,String> params = new HashMap<String,String>();
        params.put("foodID", globalFoodID + "");
        params.put("numServings",  + numServings + "");
        params.put("userID", userID + "");

        Log.e("DATA SENT TO PHP ",globalFoodID +","+numServings + "," +userID);


        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e(response.toString(), "<----UPDATE RESPONSE");
                    boolean requestOutcome = response.getBoolean("success");
                    Log.e(requestOutcome + " food outcome", "outcome here");

                    if(requestOutcome){
                        Log.e("FOOD ADDED", "FOOD ADDED");
                        LocalDatabaseUpdater updater = new LocalDatabaseUpdater(getApplicationContext());
                        updater.updateDailyFoods();
                    }
                    else{
                        Log.e("Failed to send","food to remote DB");
                    }

                } catch (JSONException e) {
                    Log.e("Failed to send","food to remote DB *catch*");
                    e.printStackTrace();
                }

                Intent updateRemoteDBIntent = new Intent(getApplicationContext(), DataSenderService.class);
                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), pendingIntentID, updateRemoteDBIntent, 0);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DATA SENDER ERROR",error.getCause() + "");
                error.printStackTrace();
            }
        };

        CustomRequest request = new CustomRequest(Request.Method.POST, ADD_FOOD_DESTINATION, params, listener, errorListener);

        RequestQueueHelper helper = RequestQueueHelper.getInstance();
        helper.add(request);
    }
}
