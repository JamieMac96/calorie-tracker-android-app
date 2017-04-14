package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by jamie on 04/03/17.
 */

public class ProgressActivity extends Activity {
    private double [] testWeights;
    private String [] testDates;

    private final String DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/get-progress.php";

    //private final String DESTINATION = "http://34.251.31.162/get-progress.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_progress);

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
        toolbarTitle.setText("Progress");
        displayProgressEntries();
    }

    public void displayProgressEntries(){
        Log.e("here", "here");


        MyDatabaseHandler myDB = MyDatabaseHandler.getInstance(this);

        List<String> progressDatabaseString = myDB.getTableInfoAsString(MyDatabaseHandler.BODYWEIGHT_ENTRY_TABLE_NAME);

        ArrayList<ProgressItem> items = new ArrayList<ProgressItem>();

        for(int i = 1 ; i < progressDatabaseString.size(); i++){
            Log.e("row", progressDatabaseString.get(i));
            String [] rowSplit = progressDatabaseString.get(i).split(",");
            items.add(new ProgressItem(rowSplit[2], rowSplit[3]));
        }

        ListView progressList = (ListView) findViewById(R.id.progress_list);

        ProgressItemAdapter pAdapter = new ProgressItemAdapter(this,R.id.progress_list, R.id.progress_item, items);

        progressList.setAdapter(pAdapter);
    }


}
