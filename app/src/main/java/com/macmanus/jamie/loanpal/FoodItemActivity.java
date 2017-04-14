package com.macmanus.jamie.loanpal;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jamie on 11/04/17.
 */

public class FoodItemActivity extends AppCompatActivity {
    private Button submitfoodButton;
    private TextView foodTitle;
    private TextView foodDescription;
    private TextView fatAmountText;
    private TextView carbAmountText;
    private TextView proteinAmountText;
    private TextView servingSize;
    private TextView totalCals;
    private EditText numServings;

    private final String UPDATE_FOOD_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/update-daily-food.php";
    private final String ADD_FOOD_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/add-food.php";

    //private final String UPDATE_FOOD_DESTINATION = "http://34.251.31.162/update-daily-food.php";
    //private final String ADD_FOOD_DESTINATION = "http://34.251.31.162/add-food.php";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_food_item);


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

        initializeViews();

        String stringFoodItem = getIntent().getExtras().getString("foodItem");
        String context = getIntent().getExtras().getString("calledFrom");


        String [] foodItemSplit = stringFoodItem.split(",");
        if(foodItemSplit.length == 9){
            try{
                FoodItem fItem = initializeFoodItem(foodItemSplit);
                setNumServingsMonitor(fItem);

                if(context.equals("searchResults")){
                    toolbarTitle.setText("Search Results");
                    submitfoodButton.setText("Add Food");

                    final FoodItem fItemCopy = fItem;

                    submitfoodButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Double.parseDouble(numServings.getText().toString()) > 0){
                                addDailyFood(fItemCopy);
                            }
                        }
                    });
                }
                else if(context.equals("mainPage")){
                    toolbarTitle.setText("Edit Food");
                    submitfoodButton.setText("Edit Food");

                    final FoodItem fItemCopy = fItem;

                    submitfoodButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Double.parseDouble(numServings.getText().toString()) > 0){
                                updateDailyFood(fItemCopy);
                            }
                        }
                    });
                }
            }
            catch (NumberFormatException e){
                Toast.makeText(this, "Invalid food", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "Error: food item invalid", Toast.LENGTH_SHORT).show();
        }





    }

    public FoodItem initializeFoodItem(String [] foodItemSplit){
        int nID =  Integer.parseInt(foodItemSplit[0]);
        int dailyFoodGlobalID =  Integer.parseInt(foodItemSplit[1]);
        String nTitle = foodItemSplit[2];
        String nDescription = foodItemSplit[3];
        double nServingSize = Double.parseDouble(foodItemSplit[4]);
        double nNumServings = Double.parseDouble(foodItemSplit[5]);;
        double nFatPerServing = Double.parseDouble(foodItemSplit[6]);;
        double nProteinPerServing = Double.parseDouble(foodItemSplit[7]);;
        double nCarbsPerServing = Double.parseDouble(foodItemSplit[8]);;
        final FoodItem fItem = new  FoodItem(nID, dailyFoodGlobalID, nTitle, nDescription, nServingSize, nNumServings, nFatPerServing, nCarbsPerServing, nProteinPerServing);

        fatAmountText.setText(fItem.getFatPerServing() + "");
        proteinAmountText.setText(fItem.getProteinPerServing() + "");
        carbAmountText.setText(fItem.getCarbsPerServing() + "");
        foodTitle.setText(fItem.getTitle());
        numServings.setText("" + nNumServings);
        foodDescription.setText(fItem.getDescription());
        servingSize.setText(fItem.getServingSize() + "");
        totalCals.setText(getCalories(fItem.getFatPerServing(), fItem.getCarbsPerServing(), fItem.getProteinPerServing()) + "");

        return fItem;
    }

    private void initializeViews(){
        submitfoodButton = (Button) findViewById(R.id.food_item_submit_button);
        fatAmountText = (TextView) findViewById(R.id.fat_grams);
        carbAmountText = (TextView) findViewById(R.id.carb_grams);
        proteinAmountText = (TextView) findViewById(R.id.protein_grams);
        foodTitle = (TextView) findViewById(R.id.food_item_title);
        foodDescription = (TextView) findViewById(R.id.food_item_description);
        servingSize = (TextView) findViewById(R.id.item_serving_size);
        numServings = (EditText) findViewById(R.id.number_of_servings);
        totalCals = (TextView) findViewById(R.id.total_calories);
    }

    //when user changes the number of servings we update the macros and calories displayed
    private void setNumServingsMonitor(final FoodItem fItem){
        numServings.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(numServings.getText().toString().length() > 0){
                    try{
                        double servings = Double.parseDouble(numServings.getText().toString());
                        fatAmountText.setText((fItem.getFatPerServing() * servings) + "");
                        proteinAmountText.setText((fItem.getProteinPerServing() * servings) + "");
                        carbAmountText.setText((fItem.getCarbsPerServing() * servings) + "");
                        double totalFat = fItem.getFatPerServing() * servings;
                        double totalCarbs = fItem.getCarbsPerServing() * servings;
                        double totalProtein = fItem.getProteinPerServing() * servings;
                        Log.e("Num servings ", "" + servings);
                        totalCals.setText(getCalories(totalFat, totalCarbs, totalProtein) + "");
                    }
                    catch(NumberFormatException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    public int getCalories(double fat, double carbs, double protein){
        return (int) ((fat * 9) + (carbs * 4) + (protein * 4));
    }

    private void addDailyFood(FoodItem fItem){
        SessionManager manager = SessionManager.getInstance(this);
        String userID = manager.getUserID();


        double numServingsNumeric = Double.parseDouble(this.numServings.getText().toString());

        Log.e("FOOD ITEM ON ADD", fItem.toString() + "");

        Map<String,String> params = new HashMap<String,String>();
        params.put("foodID", fItem.getId() + "");
        params.put("numServings",  + numServingsNumeric + "");
        params.put("userID", userID);


        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestOutcome = response.getBoolean("success");
                    Log.e(requestOutcome + " food outcome", "outcome here");

                    if(requestOutcome){
                        Log.e("FOOD ADDED", "FOOD ADDED");
                        Toast.makeText(FoodItemActivity.this, "Food Added To Diary", Toast.LENGTH_SHORT).show();
                        pullUpdatesToLocalDB();
                        finish();
                    }
                    else{
                        Toast.makeText(FoodItemActivity.this, "Failed to add food to diary", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FoodItemActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(FoodItemActivity.this, "auth failure error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(FoodItemActivity.this, "server error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(FoodItemActivity.this, "network error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(FoodItemActivity.this, "parse error", Toast.LENGTH_SHORT).show();
                }
            }
        };

        CustomRequest request = new CustomRequest(Request.Method.POST, ADD_FOOD_DESTINATION, params, listener, errorListener);

        RequestQueueHelper helper = RequestQueueHelper.getInstance();
        helper.add(request);
    }

    private void updateDailyFood(FoodItem foodItem){
        Toast.makeText(this, "updating food", Toast.LENGTH_SHORT).show();


        SessionManager manager = SessionManager.getInstance(this);
        String userID = manager.getUserID();
        double numServingsNumeric = Double.parseDouble(this.numServings.getText().toString());

        Log.e("FOOD ITEM ON ADD", foodItem.toString() + "");

        Map<String,String> params = new HashMap<String,String>();
        params.put("foodID", foodItem.getDailyFoodGlobalID() + "");
        params.put("numServings",  + numServingsNumeric + "");
        params.put("userID", userID);


        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e(response.toString(), "<----UPDATE RESPONSE");
                    boolean requestOutcome = response.getBoolean("success");
                    Log.e(requestOutcome + " food outcome", "outcome here");

                    if(requestOutcome){
                        Log.e("FOOD UPDATED", "FOOD UPDATED");
                        Toast.makeText(FoodItemActivity.this, "Food Updated", Toast.LENGTH_SHORT).show();
                        pullUpdatesToLocalDB();
                    }
                    else{
                        Toast.makeText(FoodItemActivity.this, "Failed to update food", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(FoodItemActivity.this, "Failed to update food", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(FoodItemActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(FoodItemActivity.this, "auth failure error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(FoodItemActivity.this, "server error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(FoodItemActivity.this, "network error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(FoodItemActivity.this, "parse error", Toast.LENGTH_SHORT).show();
                }
            }
        };

        CustomRequest request = new CustomRequest(Request.Method.POST, UPDATE_FOOD_DESTINATION, params, listener, errorListener);

        RequestQueueHelper helper = RequestQueueHelper.getInstance();
        helper.add(request);
    }

    private void pullUpdatesToLocalDB(){
        SessionManager manager = SessionManager.getInstance(this);
        String userID = manager.getUserID();


        //If we are not connected to a network then we will not be able to read from the remote database and thus we
        //won't be able to update the local database.
        //
        //By doing this check we will prevent the eventuality where we delete the local database entries and then fail to
        //pull the updated entries from the remote database.
        //
        //In an ideal scenario we would create a new service class for updating the local database but since time is a constraint
        //here we simply overwrite the local database rather than update it (and reuse our already implemented class).
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            //before we can insert the local database with the updated values we must clear the pertinent DB tables
            MyDatabaseHandler.getInstance(this).getWritableDatabase().execSQL("DELETE FROM UserFood");
            MyDatabaseHandler.getInstance(this).getWritableDatabase().execSQL("DELETE FROM DailyFood");

            //Now we call the DataRetrieverService to update the local database
            Intent userFoodsIntent = new Intent(this, DataRetrieverService.class);
            userFoodsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, "update-user-foods");
            userFoodsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
            startService(userFoodsIntent);

            Intent dailyFoodsIntent = new Intent(this, DataRetrieverService.class);
            dailyFoodsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, "update-daily-foods");
            dailyFoodsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
            startService(dailyFoodsIntent);
        }
    }
}
