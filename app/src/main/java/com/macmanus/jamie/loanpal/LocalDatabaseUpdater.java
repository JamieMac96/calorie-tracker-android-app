package com.macmanus.jamie.loanpal;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by jamie on 15/04/17.
 */

public class LocalDatabaseUpdater {
    private Context contextOfCallingActivity;

    public LocalDatabaseUpdater(Context context){
        this.contextOfCallingActivity = context;
    }

    public void updateDailyFoods(){
        SessionManager manager = SessionManager.getInstance(contextOfCallingActivity);
        String userID = manager.getUserID();


        //If we are not connected to a network then we will not be able to read from the remote database and thus we
        //won't be able to update the local database.
        //
        //By doing this check we will prevent the eventuality where we delete the local database entries and then fail to
        //pull the updated entries from the remote database.
        ConnectivityManager cm = (ConnectivityManager) contextOfCallingActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            //before we can insert the local database with the updated values we must clear the pertinent DB tables
            MyDatabaseHandler.getInstance(contextOfCallingActivity).getWritableDatabase().execSQL("DELETE FROM DailyFood");

            //Now we call the DataRetrieverService to update the local database
            Intent dailyFoodsIntent = new Intent(contextOfCallingActivity, DataRetrieverService.class);
            dailyFoodsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, "update-daily-foods");
            dailyFoodsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
            contextOfCallingActivity.startService(dailyFoodsIntent);
        }
    }

    public void updateUserFoods(){
        SessionManager manager = SessionManager.getInstance(contextOfCallingActivity);
        String userID = manager.getUserID();

        ConnectivityManager cm = (ConnectivityManager) contextOfCallingActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            //before we can insert the local database with the updated values we must clear the pertinent DB tables
            MyDatabaseHandler.getInstance(contextOfCallingActivity).getWritableDatabase().execSQL("DELETE FROM UserFood");

            //Now we call the DataRetrieverService to update the local database
            Intent userFoodsIntent = new Intent(contextOfCallingActivity, DataRetrieverService.class);
            userFoodsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, "update-user-foods");
            userFoodsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
            contextOfCallingActivity.startService(userFoodsIntent);
        }
    }



    public void updateUserDetails(){
        SessionManager manager = SessionManager.getInstance(contextOfCallingActivity);
        String userID = manager.getUserID();


        //If we are not connected to a network then we will not be able to read from the remote database and thus we
        //won't be able to update the local database.
        //
        //By doing this check we will prevent the eventuality where we delete the local database entries and then fail to
        //pull the updated entries from the remote database.
        ConnectivityManager cm = (ConnectivityManager)contextOfCallingActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            //before we can insert the local database with the updated values we must clear the pertinent DB tables
            MyDatabaseHandler.getInstance(contextOfCallingActivity).getWritableDatabase().execSQL("DELETE FROM UserDetails");
            //Now we call the DataRetrieverService to update the local database
            Intent detailsIntent = new Intent(contextOfCallingActivity, DataRetrieverService.class);
            detailsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, "update-user-details");
            detailsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
            contextOfCallingActivity.startService(detailsIntent);

        }
    }

    public void updateBodywegihtEntries(){
        SessionManager manager = SessionManager.getInstance(contextOfCallingActivity);
        String userID = manager.getUserID();


        //If we are not connected to a network then we will not be able to read from the remote database and thus we
        //won't be able to update the local database.
        //
        //By doing this check we will prevent the eventuality where we delete the local database entries and then fail to
        //pull the updated entries from the remote database.
        ConnectivityManager cm = (ConnectivityManager)contextOfCallingActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            //before we can insert the local database with the updated values we must clear the pertinent DB tables
            MyDatabaseHandler.getInstance(contextOfCallingActivity).getWritableDatabase().execSQL("DELETE FROM BodyweightEntry");

            Intent progressIntent = new Intent(contextOfCallingActivity, DataRetrieverService.class);
            progressIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, "update-progress-entries");
            progressIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
            contextOfCallingActivity.startService(progressIntent);
        }
    }
}
