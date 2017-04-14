package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jamie on 04/03/17.
 */

public class CreateFoodActivity extends Activity {
    private EditText title;
    private EditText description;
    private EditText servingSize;
    private EditText fatPerServing;
    private EditText carbsPerServing;
    private EditText proteinPerServing;
    private Button submitFoodButton;
    private final String REQUEST_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/add-global-food.php";
    //private final String REQUEST_DESTINATION = "http://34.251.31.162/add-global-food.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_create_food);

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
        toolbarTitle.setText("New Food");

        title = (EditText) findViewById(R.id.food_title);
        description = (EditText) findViewById(R.id.food_description);
        servingSize = (EditText) findViewById(R.id.serving_size);
        fatPerServing = (EditText) findViewById(R.id.fat_per_serving);
        carbsPerServing = (EditText) findViewById(R.id.carbs_per_serving);
        proteinPerServing = (EditText) findViewById(R.id.protein_per_serving);
        submitFoodButton = (Button) findViewById(R.id.submit_food);
    }

    public void submitFood(View view) {

        if(validateForm()){
            Map<String, String> params = new HashMap<String, String>();

            params.put("Name", title.getText().toString());
            params.put("Description", description.getText().toString());
            params.put("ServingSize", servingSize.getText().toString());
            params.put("FatPerServing", fatPerServing.getText().toString());
            params.put("CarbsPerServing", carbsPerServing.getText().toString());
            params.put("ProteinPerServing", proteinPerServing.getText().toString());


            final Toast createFoodToast = Toast.makeText(CreateFoodActivity.this, "Creating food...", Toast.LENGTH_LONG);
            createFoodToast.show();

            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        createFoodToast.cancel();

                        boolean requestOutcome = response.getBoolean("success");

                        if(requestOutcome){
                            Toast.makeText(CreateFoodActivity.this, "Food created successfully", Toast.LENGTH_SHORT).show();
                            Intent mainActivity = new Intent(CreateFoodActivity.this, MainActivity.class);
                            startActivity(mainActivity);
                        }
                        else{
                            Toast.makeText(CreateFoodActivity.this, "Failed to create food", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CreateFoodActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(CreateFoodActivity.this, "auth failure error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(CreateFoodActivity.this, "server error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(CreateFoodActivity.this, "network error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(CreateFoodActivity.this, "parse error", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            CustomRequest request = new CustomRequest(Request.Method.POST, REQUEST_DESTINATION, params, listener, errorListener);

            RequestQueueHelper helper = RequestQueueHelper.getInstance();
            helper.add(request);
        }



    }

    public boolean validateForm(){
        if(isPosNum(servingSize.getText().toString()) && isPosNum(fatPerServing.getText().toString()) &&
                isPosNum(carbsPerServing.getText().toString()) && isPosNum(proteinPerServing.getText().toString())){
            if(title.getText().toString().length() <= 40 && description.getText().toString().length() <= 200 && servingSize.getText().toString().length() <= 5){
                return true;
            }
        }

        return false;
    }

    private boolean isPosNum(String numberString){
        try {
            double value = Double.parseDouble(numberString);
            if(value<0)
                return false;
            else
                return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
