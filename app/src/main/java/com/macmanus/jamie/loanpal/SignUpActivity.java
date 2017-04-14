package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by jamie on 16/01/17.
 */

public class SignUpActivity extends Activity {

    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private EditText passwordConfirmView;
    private Button submitButton;
    private RequestQueueHelper helper = RequestQueueHelper.getInstance();
    private final String REQUEST_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/register.php";

    //private final String REQUEST_DESTINATION = "http://34.251.31.162/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        emailView = (AutoCompleteTextView) findViewById(R.id.emailSignUp);
        passwordView = (EditText) findViewById(R.id.passwordSignUp);
        passwordConfirmView = (EditText) findViewById(R.id.passwordConfirmSignUp);
        submitButton = (Button) findViewById(R.id.sign_up_button);



    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionManager manager = SessionManager.getInstance(getApplicationContext());
        if(manager.isLoggedIn()){
            goToMainActivity();
        }
    }

    public void registerUser(View v){

        //validate sign up elements
        if(validateEmail() && validatePassword() && validateConfirmPassword()){


            final String emailAddr = emailView.getText().toString();
            final String password = passwordView.getText().toString();

            final Toast attemptSignUpToast = Toast.makeText(SignUpActivity.this, "Attempting Sign Up...", Toast.LENGTH_LONG);
            attemptSignUpToast.show();

            //set response listener
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        attemptSignUpToast.cancel();
                        Log.e("in onResponse section","in onResponse");

                        boolean success = response.getBoolean("success");
                        String userID = response.getString("UserID");
                        Log.e(userID + ": id", "User ID here");

                        if(success){
                            Toast.makeText(SignUpActivity.this, "The sign up succeeded!", Toast.LENGTH_SHORT).show();
                            SessionManager manager = SessionManager.getInstance(getApplicationContext());
                            manager.createLoginSession(userID, emailAddr);
                            goToGoalsActivity();
                        }
                        else{
                            Toast.makeText(SignUpActivity.this, "The sign up failed", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        Log.e("Failed try","in catch");
                        e.printStackTrace();
                    }
                }
            };
            HashMap<String, String> params = new HashMap<String,String>();
            params.put("email", emailAddr);
            params.put("password", password);

            CustomRequest registerRequest = new CustomRequest(Request.Method.POST, REQUEST_DESTINATION, params, responseListener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(SignUpActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(SignUpActivity.this, "auth failure error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(SignUpActivity.this, "server error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(SignUpActivity.this, "network error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(SignUpActivity.this, "parse error", Toast.LENGTH_SHORT).show();
                    };
                }
            });
            helper.add(registerRequest);
        }

    }

    private void goToMainActivity(){
        Intent mainActivity = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }

    private void goToGoalsActivity(){
        Intent goalsActivity = new Intent(SignUpActivity.this, GoalsActivity.class);
        startActivity(goalsActivity);
    }

    private boolean validateConfirmPassword() {
        String confirmPassword = passwordConfirmView.getText().toString();
        String password = passwordView.getText().toString();

        if(!confirmPassword.equals(password)){
            passwordConfirmView.setError("The passwords do not match");
            return false;
        }
        return true;
    }


    private boolean validateEmail(){
        String email = emailView.getText().toString();
        if(email.length() < 255) {
            EmailValidator validator = EmailValidator.getInstance();

            if (!validator.isValid(email)) {
                emailView.setError("Invalid email");
                return false;
            }
            else {
                return true;
            }
        }
        return false;
    }

    private boolean validatePassword() {
        String password = passwordView.getText().toString();
        if(password.length() < 255) {
            if (password.matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})")) {
                return true;
            } else {
                passwordView.setError("required: 8-20 characters with upper and lowecase letters and atleast one number.");
                return false;
            }
        }
        return false;
    }

}
