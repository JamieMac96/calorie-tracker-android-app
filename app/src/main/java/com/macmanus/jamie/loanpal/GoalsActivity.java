package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.macmanus.jamie.loanpal.DataRetrieverService.USER_DETAILS_MESSAGE;

/**
 * Created by jamie on 04/03/17.
 */

public class GoalsActivity extends Activity {

    private TextView startingBodyweight;
    private EditText currentBodyWeight;
    private EditText goalBodyWeight;
    private EditText calorieGoalEditText;
    private Spinner  fatPercentage;
    private Spinner  carbPercentage;
    private Spinner  proteinPercentage;
    private Spinner  weeklyGoalsDropdown;
    private Spinner  activityLevelDropDown;
    private Button submitButton;

    private String loadedCurrentBodyWeight;
    private String loadedGoalBodyWeight;
    private String loadedCalorieGoalEditText;
    private String  loadedFatPercentage;
    private String  loadedCarbPercentage;
    private String  loadedProteinPercentage;
    private String  loadedWeeklyGoalsDropdown;
    private String  loadedActivityLevelDropDown;
    //private final String WRITE_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/update-goals.php";

    private final String WRITE_DESTINATION = "http://34.251.31.162/update-goals.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_goals);

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
        toolbarTitle.setText("Goals");

        initializeViews();

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

    //populate the goals page with the users current goals.
    public void getCurrentGoals(){
        if(MyDatabaseHandler.hasInstance()) {
            MyDatabaseHandler myDB = MyDatabaseHandler.getInstance(this);
            List<String> details = myDB.getTableInfoAsString(MyDatabaseHandler.USER_DETAILS_TABLE_NAME);
            if(details.size() > 1){
                //first row of details is the table's column names so we get(1)
                String[] rowSplit = details.get(1).split(",");

                String weeklyGoal = rowSplit[1];
                String activityLevel = rowSplit[2];
                String initialBodyweight = rowSplit[3];
                String bodyweight = rowSplit[4];
                String goalBodyweight = rowSplit[5];
                String calorieGoal = rowSplit[6];
                String fatPercentage = rowSplit[9] + "%";
                String proteinPercentage = rowSplit[7] + "%";
                String carbPercentage = rowSplit[8] + "%";

                startingBodyweight.setText(initialBodyweight);
                currentBodyWeight.setText(bodyweight);
                goalBodyWeight.setText(goalBodyweight);
                calorieGoalEditText.setText(calorieGoal);
                setDropDownItem(weeklyGoalsDropdown, weeklyGoal);
                setDropDownItem(activityLevelDropDown, activityLevel);
                Log.e("fat %: " + fatPercentage, "  fat  ");
                setDropDownItem(GoalsActivity.this.fatPercentage, fatPercentage);
                setDropDownItem(GoalsActivity.this.carbPercentage, carbPercentage);
                setDropDownItem(GoalsActivity.this.proteinPercentage, proteinPercentage);

                setInitialFieldValues();
            }
        }
    }


    public void attemptUpdateGoals(View view) {
        if(isValidUpdateForm()){
            submitButton.setClickable(false);
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

                            pullUpdatesToLocalDB();
                            setInitialFieldValues();
                        }
                        else{
                            Toast.makeText(GoalsActivity.this, "failed to update goals", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Log.e("in onResponse catch","in catch");
                        e.printStackTrace();
                    }
                    submitButton.setClickable(true);
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    submitButton.setClickable(true);
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
        String cBodyWeight = currentBodyWeight.getText().toString();
        String gBodyWeight = goalBodyWeight.getText().toString();
        String calorieGoal = calorieGoalEditText.getText().toString();

        if(cBodyWeight.length() != 0 && gBodyWeight.length() != 0 && calorieGoal.length() != 0){
            if(fatPercentage != null && fatPercentage.getSelectedItem() !=null &&
                    proteinPercentage != null && proteinPercentage.getSelectedItem() !=null &&
                    carbPercentage != null && carbPercentage.getSelectedItem() !=null){

                if(fieldsHaveBeenEdited()){
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
                        Toast.makeText(this, "Fat, carbohydrate and protein must total 100%.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else{
                    Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
            else{
                Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else{
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
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

    //check if fields have changed since the activity was loaded.
    private boolean fieldsHaveBeenEdited(){
        try {
            if (loadedCurrentBodyWeight.equals(currentBodyWeight.getText().toString()) &&
                    loadedGoalBodyWeight.equals(goalBodyWeight.getText().toString()) &&
                    loadedCalorieGoalEditText.equals(calorieGoalEditText.getText().toString()) &&
                    loadedFatPercentage.equals(fatPercentage.getSelectedItem().toString()) &&
                    loadedCarbPercentage.equals(carbPercentage.getSelectedItem().toString()) &&
                    loadedProteinPercentage.equals(proteinPercentage.getSelectedItem().toString()) &&
                    loadedWeeklyGoalsDropdown.equals(weeklyGoalsDropdown.getSelectedItem().toString()) &&
                    loadedActivityLevelDropDown.equals(activityLevelDropDown.getSelectedItem().toString())
                    ) {
                return false;
            }
        }
        catch(NullPointerException e){

        }


        return true;
    }

    private void setInitialFieldValues(){
        loadedCurrentBodyWeight = currentBodyWeight.getText().toString();
        loadedGoalBodyWeight = goalBodyWeight.getText().toString();
        loadedCalorieGoalEditText = calorieGoalEditText.getText().toString();
        loadedFatPercentage = fatPercentage.getSelectedItem().toString();
        loadedCarbPercentage = carbPercentage.getSelectedItem().toString();
        loadedProteinPercentage = proteinPercentage.getSelectedItem().toString();
        loadedWeeklyGoalsDropdown = weeklyGoalsDropdown.getSelectedItem().toString();
        loadedActivityLevelDropDown = activityLevelDropDown.getSelectedItem().toString();
    }

    private void initializeViews(){
        weeklyGoalsDropdown = setDropdown(R.id.weekly_goal, R.array.weekly_goals_array);
        activityLevelDropDown = setDropdown(R.id.activity_level, R.array.activity_level_array);
        fatPercentage = setDropdown(R.id.fat_dropdown, R.array.percentages);
        carbPercentage = setDropdown(R.id.carbs_dropdown, R.array.percentages);
        proteinPercentage = setDropdown(R.id.protein_dropdown, R.array.percentages);
        startingBodyweight = (TextView) findViewById(R.id.initial_bodyweight);
        currentBodyWeight = (EditText) findViewById(R.id.current_bodyweight);
        goalBodyWeight = (EditText) findViewById(R.id.goal_bodyweight);
        calorieGoalEditText = (EditText) findViewById(R.id.calorie_goal);
        submitButton = (Button) findViewById(R.id.goals_submit_button);
    }

    private void pullUpdatesToLocalDB(){
        LocalDatabaseUpdater myUpdater = new LocalDatabaseUpdater(this);
        myUpdater.updateBodywegihtEntries();
        myUpdater.updateUserDetails();
    }
}
