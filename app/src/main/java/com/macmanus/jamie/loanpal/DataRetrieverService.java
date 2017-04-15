package com.macmanus.jamie.loanpal;

import android.app.IntentService;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jamie on 11/04/17.
 *
 * This is a Class that retrieves data from the remote database and prints it to the local database.
 */

public class DataRetrieverService extends IntentService {
    public static final String PARAM_IN_MSG = "imsg";
    public static final String USER_ID_MSG = "-1";
    public static final String PARAM_OUT_MSG = "omsg";
    public static final String USER_DETAILS_MESSAGE = "uDetails";
    public static final String PROGRESS_ENTRIES_MESSAGE= "pEntries";
    public static final String USER_FOODS_MESSAGE= "uFoods";
    public static final String DAILY_FOODS_MESSAGE = "dFoods";

    //private final String USER_DETAILS_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/get-goals.php";
    //private final String PROGRESS_ENTRIES_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/get-progress.php";
    //private final String USER_FOODS_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/get-user-foods.php";
    //private final String DAILY_FOODS_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/get-daily-foods.php";

    private final String USER_DETAILS_DESTINATION = "http://34.251.31.162/get-goals.php";
    private final String PROGRESS_ENTRIES_DESTINATION = "http://34.251.31.162/get-progress.php";
    private final String USER_FOODS_DESTINATION = "http://34.251.31.162/get-user-foods.php";
    private final String DAILY_FOODS_DESTINATION = "http://34.251.31.162/get-daily-foods.php";

    //when we pull the daily foods down from the remote database to the local database
    //then the data in the local database becomes the data we use.
    //If it it becomes a new day then we want to update the local database from the remote database.
    //To do this we check the systemDate against the actual date.
    public static String SYSTEM_DATE;



    public DataRetrieverService(){
        super("DataRetrieverService");
        Log.i("in constructor", "for sevice");
    }


    //This method is called when we call startService() on an intent directed at this class.
    @Override
    protected void onHandleIntent(Intent intent) {
        //Depending on the message sent with the intent we update a diffrent table in the local database.

        String msg = intent.getStringExtra(PARAM_IN_MSG);
        String userID = intent.getStringExtra(USER_ID_MSG);
        Log.i("In onHandleIntent ",msg);
        Log.i("USER ID IN ONHANDLE ",userID + "");

        try{
            int userIdentifier = Integer.parseInt(userID);

            //determine which method to call based on extra contents of intent.
            //If we are using one of the predefined final strings (eg USER_DETAILS_MESSAGE)
            //then we are retrieving the data at login and we will need to use the string to
            //determine when to log the user in, otherwise we are simply updating the info and so we do
            //not need to wait for the update.
            if (userIdentifier > 0) {
                if (msg.equals(USER_DETAILS_MESSAGE) || msg.equals("update-user-details")) {
                    Log.e("in if1","in if1");
                    retrieveUserDetails(userIdentifier, msg);
                } else if (msg.equals(PROGRESS_ENTRIES_MESSAGE) || msg.equals("update-progress-entries")) {
                    Log.e("in if2","in if2");
                    retrieveProgressEntries(userIdentifier, msg);
                } else if (msg.equals(USER_FOODS_MESSAGE) || msg.equals("update-user-foods")) {
                    Log.e("in if3","in if3");
                    retrieveUserFoods(userIdentifier, msg);
                } else if (msg.equals(DAILY_FOODS_MESSAGE) || msg.equals("update-daily-foods")) {
                    Log.e("in if4","in if4");
                    retrieveDailyFoods(userIdentifier, msg);
                }
            }
        }
        catch (NumberFormatException e) {
            Log.e("in catch","in catch");
        }


    }

    public void retrieveUserDetails(final int userID, final String message){

        Map<String,String> params = new HashMap<String,String>();
        params.put("userID", userID + "");

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestOutcome = response.getBoolean("success");
                    Log.e(requestOutcome + " u details outcome", "outcome here");

                    if(requestOutcome){
                        String userIDFromDB = response.getString("userID");
                        String weeklyGoal = response.getString("weeklyGoal");
                        String activityLevel = response.getString("activityLevel");
                        String initialBodyweight = response.getString("initialBodyweight");
                        String bodyweight = response.getString("bodyweight");
                        String goalBodyweight = response.getString("goalBodyWeight");
                        String calorieGoal = response.getString("calorieGoal");
                        String fatPercentage = response.getString("fatPercentage");
                        String proteinPercentage = response.getString("proteinPercentage");
                        String carbPercentage = response.getString("carbPercentage");

                        MyDatabaseHandler dbHandler = MyDatabaseHandler.getInstance(getApplicationContext());

                        if(! (userID == Integer.parseInt(userIDFromDB))){
                            Log.e("ERROR ERROR !!!!!", "UserIDs do not match");
                        }

                        dbHandler.getWritableDatabase().execSQL(
                        "INSERT INTO UserDetails(User_UserID, WeeklyGoal, ActivityLevel, InitialBodyweight, Bodyweight, " +
                                "GoalWeight, CalorieGoal, ProteinGoalPercent, CarbGoalPercent, FatGoalPercent)" +
                                "VALUES(" + userID + ",\"" +weeklyGoal + "\", \""+ activityLevel +"\", "+initialBodyweight +
                                ","+bodyweight+", "+goalBodyweight+", "+calorieGoal+", "+proteinPercentage+", "+carbPercentage+", "+fatPercentage+");");

                        sendBroadCast(message);
                    }
                    else{

                    }

                } catch (JSONException e) {
                    Log.e("in onResponse catch","in catch");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               error.printStackTrace();
            }
        };

        CustomRequest request = new CustomRequest(Request.Method.POST, USER_DETAILS_DESTINATION, params, listener, errorListener);

        RequestQueueHelper helper = RequestQueueHelper.getInstance();
        helper.add(request);
    }

    public void retrieveProgressEntries(final int userID, final String message){


        Map<String,String> params = new HashMap<String,String>();
        params.put("userID", userID + "");

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                boolean requestOutcome = false;
                try {
                    requestOutcome = response.getBoolean("success");
                    Log.e(requestOutcome + " progress outcome", "outcome here");
                    if (requestOutcome) {
                        MyDatabaseHandler dbHandler = MyDatabaseHandler.getInstance(getApplicationContext());

                        if(response.has("result")) {
                            JSONArray result = response.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONArray innerArray = result.getJSONArray(i);
                                String bodyweight = innerArray.getString(1);
                                String date = innerArray.getString(0);

                                Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String parsedDate = formatter.format(initDate);

                                dbHandler.getWritableDatabase().execSQL(
                                        "INSERT INTO BodyweightEntry(EntryID, User_UserID, Weight, WeighInDate)" +
                                                "VALUES(" + null + "," + userID + ", " + bodyweight + ", \"" + parsedDate + "\");");


                            }
                        }
                        sendBroadCast(message);
                    } else {
                        Log.e("response false", "false");
                    }
                }
                 catch (JSONException e) {
                    Log.e("in onResponse catch","in catch");
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        CustomRequest request = new CustomRequest(Request.Method.POST, PROGRESS_ENTRIES_DESTINATION, params, listener, errorListener);

        RequestQueueHelper helper = RequestQueueHelper.getInstance();
        helper.add(request);

    }

    public void retrieveUserFoods(final int userID, final String message){

        Map<String,String> params = new HashMap<String,String>();
        params.put("userID", userID + "");

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestOutcome = response.getBoolean("success");
                    Log.e(requestOutcome + " user food outcome", "outcome here");

                    if(requestOutcome){
                        MyDatabaseHandler dbHandler = MyDatabaseHandler.getInstance(getApplicationContext());
                        if(response.has("result")) {

                            JSONArray result = response.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONArray innerArray = result.getJSONArray(i);
                                String name = innerArray.getString(0);
                                String description = innerArray.getString(1);
                                String servingSize = innerArray.getString(2);
                                String fatPerServing = innerArray.getString(3);
                                String proteinPerServing = innerArray.getString(4);
                                String carbPerServing = innerArray.getString(5);
                                String globalFoodID = innerArray.getString(6);


                                int calsPerServing = (int) ((Double.parseDouble(fatPerServing) * 9) + (Double.parseDouble(carbPerServing) * 4) + (Double.parseDouble(proteinPerServing) * 4));//calculate

                                dbHandler.getWritableDatabase().execSQL(
                                        "INSERT INTO UserFood(FoodID, User_UserID, Name, Description, ServingSize, CaloriesPerServing, ProteinPerServing, FatPerServing, CarbsPerServing)" +
                                                "VALUES(" + globalFoodID + "," + userID + ", \"" + name + "\", \"" + description + "\", " + servingSize + ", " + calsPerServing + ", " +
                                                proteinPerServing + ", " + fatPerServing + ", " + carbPerServing + ");");
                            }
                        }

                        sendBroadCast(message);
                    }
                    else{

                    }

                } catch (JSONException e) {
                    Log.e("in onResponse catch","in catch");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        CustomRequest request = new CustomRequest(Request.Method.POST, USER_FOODS_DESTINATION, params, listener, errorListener);

        RequestQueueHelper helper = RequestQueueHelper.getInstance();
        helper.add(request);
    }

    public void retrieveDailyFoods(final int userID, final String message){
        Date date = new Date();
        SYSTEM_DATE = new SimpleDateFormat("yyyy-MM-dd").format(date);


        Map<String,String> params = new HashMap<String,String>();
        params.put("userID", userID + "");

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestOutcome = response.getBoolean("success");
                    Log.e(requestOutcome + "daily food outcome", "outcome here daily fds");

                    MyDatabaseHandler dbHandler = MyDatabaseHandler.getInstance(getApplicationContext());

                    if(requestOutcome){
                        if(response.has("result")) {
                            Log.e(response.getString("result"),"<--------DATA BACK daily");
                            JSONArray result = response.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONArray innerArray = result.getJSONArray(i);
                                String name = innerArray.getString(0);
                                String description = innerArray.getString(1);
                                String servingSize = innerArray.getString(2);
                                String numServings = innerArray.getString(3);
                                String fatPerServing = innerArray.getString(4);
                                String proteinPerServing = innerArray.getString(5);
                                String carbPerServing = innerArray.getString(6);
                                String dailyFoodRemoteID = innerArray.getString(7);



                                int calsPerServing = (int) ((Double.parseDouble(fatPerServing) * 9) + (Double.parseDouble(carbPerServing) * 4) + (Double.parseDouble(proteinPerServing) * 4));//calculate

                                dbHandler.getWritableDatabase().execSQL(
                                        "INSERT INTO DailyFood(FoodID, GlobalFoodID, User_UserID, Name, Description, ServingSize, NumServings, CaloriesPerServing, ProteinPerServing, FatPerServing, CarbsPerServing)" +
                                                "VALUES(" + null + "," + dailyFoodRemoteID + "," +userID + ", \"" + name + "\", \"" + description + "\", " + servingSize + ", " + numServings + ", " + calsPerServing + ", " +
                                                proteinPerServing + ", " + fatPerServing + ", " + carbPerServing + ");");
                            }
                        }

                        sendBroadCast(message);
                    }
                    else{

                    }

                } catch (JSONException e) {
                    Log.e("in onResponse catch","in catch");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        CustomRequest request = new CustomRequest(Request.Method.POST, DAILY_FOODS_DESTINATION, params, listener, errorListener);

        RequestQueueHelper helper = RequestQueueHelper.getInstance();
        helper.add(request);
    }

    private void sendBroadCast(String message){
        Log.e("DataRetrieverService",message);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(LoginActivity.MyResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, message);
        sendBroadcast(broadcastIntent);
    }
}
