package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jamie on 04/03/17.
 */

public class GoalsActivity extends Activity {

    private EditText startingBodyweight;
    private EditText currentBodyWeight;
    private EditText goalBodyWeight;
    private EditText calorieGoalEditText;
    private Spinner  fatPercentage;
    private Spinner  carbPercentage;
    private Spinner  proteinPercentage;
    private Spinner  weeklyGoalsDropdown;
    private Spinner  activityLevelDropDown;
    private Button submitButton;
    private final String READ_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/get-goals.php";
    private final String WRITE_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/update-goals.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_goals);

        Button backButton = (Button) findViewById(R.id.toolbar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Goals");


        weeklyGoalsDropdown = setDropdown(R.id.weekly_goal, R.array.weekly_goals_array);
        activityLevelDropDown = setDropdown(R.id.activity_level, R.array.activity_level_array);
        fatPercentage = setDropdown(R.id.fat_dropdown, R.array.percentages);
        carbPercentage = setDropdown(R.id.carbs_dropdown, R.array.percentages);
        proteinPercentage = setDropdown(R.id.protein_dropdown, R.array.percentages);
        startingBodyweight = (EditText) findViewById(R.id.initial_bodyweight);
        currentBodyWeight = (EditText) findViewById(R.id.current_bodyweight);
        goalBodyWeight = (EditText) findViewById(R.id.goal_bodyweight);
        calorieGoalEditText = (EditText) findViewById(R.id.calorie_goal);
        submitButton = (Button) findViewById(R.id.submit_button);
        //submitButton.setClickable(false);

        setOnChangeEvents();

        getCurrentGoals();

    }


    //We need to display the item that the user has already selected beforehand
    //To do this we could possibly pass another argument that represents this value
    public Spinner setDropdown(int spinnerID, int arrayID){
        Spinner dropdown = (Spinner) findViewById(spinnerID);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

        return dropdown;
    }

    public void getCurrentGoals(){
        SessionManager manager = SessionManager.getInstance(getApplicationContext());
        String userID = manager.getUserID();
        Map<String,String> params = new HashMap<String,String>();
        params.put("userID", userID);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("in try", "in try");
                    boolean requestOutcome = response.getBoolean("success");
                    if(requestOutcome){
                        Log.e("WE MADE IT", "WE MADE IT");
                        String weeklyGoal = response.getString("weeklyGoal");
                        String activityLevel = response.getString("activityLevel");
                        String initialBodyweight = response.getString("initialBodyweight");
                        String bodyweight = response.getString("bodyweight");
                        String goalBodyweight = response.getString("goalBodyWeight");
                        String calorieGoal = response.getString("calorieGoal");
                        String fatPercentage = response.getString("fatPercentage");
                        String proteinPercentage = response.getString("proteinPercentage");
                        String carbPercentage = response.getString("carbPercentage");

                        startingBodyweight.setText(initialBodyweight);
                        currentBodyWeight.setText(bodyweight);
                        goalBodyWeight.setText(goalBodyweight);
                        calorieGoalEditText.setText(calorieGoal);
                        setDropDownItem(weeklyGoalsDropdown, weeklyGoal);
                        setDropDownItem(activityLevelDropDown, activityLevel);
                        Log.e("fat %: " + fatPercentage,"  fat  ");
                        setDropDownItem(GoalsActivity.this.fatPercentage, fatPercentage);
                        setDropDownItem(GoalsActivity.this.carbPercentage, carbPercentage);
                        setDropDownItem(GoalsActivity.this.proteinPercentage, proteinPercentage);
                    }
                    else{
                        Log.e("response false" ,"false");
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
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(GoalsActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(GoalsActivity.this, "auth failure error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(GoalsActivity.this, "server error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(GoalsActivity.this, "network error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(GoalsActivity.this, "parse error", Toast.LENGTH_SHORT).show();
                }
            }
        };

        CustomRequest request = new CustomRequest(Request.Method.POST, READ_DESTINATION, params, listener, errorListener);

        RequestQueueHelper helper = RequestQueueHelper.getInstance();
        helper.add(request);
    }


    public void attemptUpdateGoals(View view) {
        if(isValidUpdateForm()){
            SessionManager manager = SessionManager.getInstance(getApplicationContext());
            String userID = manager.getUserID();

            String fat = fatPercentage.getSelectedItem().toString();
            fat = fat.substring(0, fat.length() -1);
            String protein = proteinPercentage.getSelectedItem().toString();
            protein = protein.substring(0,protein.length() -1);
            String carbs = carbPercentage.getSelectedItem().toString();
            carbs = carbs.substring(0,carbs.length() -1);


            Map<String,String> params = new HashMap<String,String>();
            params.put("userID", userID);
            params.put("initialBodyweight", startingBodyweight.getText().toString());
            params.put("bodyweight", currentBodyWeight.getText().toString());
            params.put("goalBodyweight", goalBodyWeight.getText().toString());
            params.put("calorieGoal", calorieGoalEditText.getText().toString());
            params.put("weeklyGoal", weeklyGoalsDropdown.getSelectedItem().toString());
            params.put("activityLevel", activityLevelDropDown.getSelectedItem().toString());
            params.put("fatPercentage", fat);
            params.put("carbPercentage", carbs);
            params.put("proteinPercentage", protein);


            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean requestOutcome = response.getBoolean("success");
                        Log.e("Response " + requestOutcome,"...response");
                        if(requestOutcome){
                            Toast.makeText(GoalsActivity.this, "goals updated", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(GoalsActivity.this, "failed to update goals", Toast.LENGTH_SHORT).show();
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
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(GoalsActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(GoalsActivity.this, "auth failure error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(GoalsActivity.this, "server error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(GoalsActivity.this, "network error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(GoalsActivity.this, "parse error", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            CustomRequest request = new CustomRequest(Request.Method.POST, WRITE_DESTINATION, params, listener, errorListener);

            RequestQueueHelper helper = RequestQueueHelper.getInstance();
            helper.add(request);
        }

    }

    public boolean isValidUpdateForm(){
        if(fatPercentage != null && fatPercentage.getSelectedItem() !=null &&
                proteinPercentage != null && proteinPercentage.getSelectedItem() !=null &&
                carbPercentage != null && carbPercentage.getSelectedItem() !=null){

            String fat = fatPercentage.getSelectedItem().toString();
            fat = fat.substring(0, fat.length() -1);
            String protein = proteinPercentage.getSelectedItem().toString();
            protein = protein.substring(0,protein.length() -1);
            String carbs = carbPercentage.getSelectedItem().toString();
            carbs = carbs.substring(0,carbs.length() -1);

            int fatInt = Integer.parseInt(fat);
            int proteinInt = Integer.parseInt(protein);
            int carbsInt = Integer.parseInt(carbs);


            if((fatInt + proteinInt + carbsInt) == 100){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }


    private void setDropDownItem(Spinner dropdown, String item){
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) dropdown.getAdapter();
        int spinnerPosition = adapter.getPosition(item);
        if(spinnerPosition > -1){
            dropdown.setSelection(spinnerPosition);
        }
        dropdown.setSelection(spinnerPosition);
    }

    private void setOnChangeEvents(){

    }
}
