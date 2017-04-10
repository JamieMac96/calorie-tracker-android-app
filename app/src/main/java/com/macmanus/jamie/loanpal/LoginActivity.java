package com.macmanus.jamie.loanpal;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.common.api.GoogleApiClient;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {



    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView forgottenPassword;
    private final String REQUEST_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/login.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        forgottenPassword = (TextView) findViewById(R.id.forgotten_password);
        forgottenPassword.setPaintFlags(forgottenPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);



    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionManager manager = SessionManager.getInstance(getApplicationContext());
        if(manager.isLoggedIn()){
            goToMainActivity();
        }
    }

    public void attemptLogin(View view){
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (isEmailValid(email) && isPasswordValid(password)) {
            Log.e("valid details", "Details valid");
            loginUser(email, password);
        }
    }


    private void loginUser(final String email, String password){
        Map<String,String> params = new HashMap<String,String>();
        params.put("email", email);
        params.put("password", password);

        final Toast attemptLoginToast = Toast.makeText(LoginActivity.this, "Attempting Login...", Toast.LENGTH_LONG);
        attemptLoginToast.show();

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    attemptLoginToast.cancel();
                    boolean requestOutcome = response.getBoolean("success");
                    Log.e(requestOutcome + ": outcome", "outcome here");

                    if(requestOutcome){
                        String userID = response.getString("UserID");
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        SessionManager manager = SessionManager.getInstance(getApplicationContext());
                        manager.createLoginSession(userID, email);
                        goToMainActivity();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Email or password incorrect", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(LoginActivity.this, "auth failure error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(LoginActivity.this, "server error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(LoginActivity.this, "network error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(LoginActivity.this, "parse error", Toast.LENGTH_SHORT).show();
                }
            }
        };

        CustomRequest request = new CustomRequest(Request.Method.POST, REQUEST_DESTINATION, params, listener, errorListener);

        RequestQueueHelper helper = RequestQueueHelper.getInstance();
        helper.add(request);
    }

    private boolean isEmailValid(String email) {

        EmailValidator validator = EmailValidator.getInstance();

        if(validator.isValid(email)){
          return true;
        }
        else{
            mEmailView.setError("invalid email");
            return false;
        }
    }

    private boolean isPasswordValid(String password) {

        if(password.matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})")){
            return true;
        }
        else {
            mPasswordView.setError("required: 8 characters with upper and lowecase letters and atleast one number.");
            return false;
        }

    }


    public void startSignUpActivity(View view) {
        Intent signUpActivity = new Intent(this, SignUpActivity.class);
        startActivity(signUpActivity);
    }


    private void goToMainActivity(){
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }
}

