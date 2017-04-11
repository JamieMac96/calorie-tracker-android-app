package com.macmanus.jamie.loanpal;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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

    private final String USER_DETAILS_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/getGoals.php";
    private final String PROGRESS_ENTRIES_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/getProgress.php";
    private final String USER_FOODS_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/getGoals.php";
    private final String DAILY_FOODS_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/getGoals.php";



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
            Log.e("in try","in try");
            //determine which method to call based on extra contents of intent.
            if (userIdentifier > 0) {
                if (msg.equals(USER_DETAILS_MESSAGE)) {
                    Log.e("in if1","in if1");
                    retrieveUserDetails(userIdentifier);
                } else if (msg.equals(PROGRESS_ENTRIES_MESSAGE)) {
                    Log.e("in if2","in if2");
                    retrieveProgressEntries(userIdentifier);
                } else if (msg.equals(USER_FOODS_MESSAGE)) {
                    Log.e("in if3","in if3");
                    retrieveUserFoods(userIdentifier);
                } else if (msg.equals(DAILY_FOODS_MESSAGE)) {
                    Log.e("in if4","in if4");
                    retrieveDailyFoods(userIdentifier);
                }
            }
        }
        catch (NumberFormatException e) {
            Log.e("in catch","in catch");
        }


    }

    public void retrieveUserDetails(int userID){
        //TODO: write implementation for data retrieval when we have implemented ability to search for and add foods.


        Log.e("DataRetrieverService","retrieveUserDetails");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(LoginActivity.MyResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, USER_DETAILS_MESSAGE);
        sendBroadcast(broadcastIntent);
    }

    public void retrieveProgressEntries(int userID){
        //TODO: write implementation for data retrieval when we have implemented ability to search for and add foods.

        Log.e("DataRetrieverService","retrieveProgressEntries");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(LoginActivity.MyResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, PROGRESS_ENTRIES_MESSAGE);
        sendBroadcast(broadcastIntent);
    }

    public void retrieveUserFoods(int userID){
        //TODO: write implementation for data retrieval when we have implemented ability to search for and add foods.

        Log.e("DataRetrieverService","retrieveUserFoods");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(LoginActivity.MyResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, USER_FOODS_MESSAGE);
        sendBroadcast(broadcastIntent);
    }

    public void retrieveDailyFoods(int userID){
        //TODO: write implementation for data retrieval when we have implemented ability to search for and add foods.

        Log.e("DataRetrieverService","retrieveDailyFoods");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(LoginActivity.MyResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, DAILY_FOODS_MESSAGE);
        sendBroadcast(broadcastIntent);
    }
}
