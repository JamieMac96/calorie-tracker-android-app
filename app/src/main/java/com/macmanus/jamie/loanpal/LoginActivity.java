package com.macmanus.jamie.loanpal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import static com.macmanus.jamie.loanpal.DataRetrieverService.DAILY_FOODS_MESSAGE;
import static com.macmanus.jamie.loanpal.DataRetrieverService.PROGRESS_ENTRIES_MESSAGE;
import static com.macmanus.jamie.loanpal.DataRetrieverService.USER_DETAILS_MESSAGE;
import static com.macmanus.jamie.loanpal.DataRetrieverService.USER_FOODS_MESSAGE;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {



    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView forgottenPassword;
    private final String REQUEST_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/login.php";
    MyResponseReceiver receiver;


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



        IntentFilter filter = new IntentFilter(MyResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyResponseReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyResponseReceiver receiver = new MyResponseReceiver();
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
                        retrieveUserData(userID);
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

    public void retrieveUserData(String userID){

        String getUserDetailsMessage = "uDetails";
        String getProgressEntriesMessage = "pEntries";
        String getUserFoodsMessage = "uFoods";
        String getDailyFoodsMessage = "dFoods";

        Intent detailsIntent = new Intent(this, DataRetrieverService.class);
        detailsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, getUserDetailsMessage);
        detailsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
        startService(detailsIntent);

        Intent progressIntent = new Intent(this, DataRetrieverService.class);
        progressIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, getProgressEntriesMessage);
        Log.e("USERID IN SENDER1",userID + "");
        progressIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
        startService(progressIntent);

        Intent uFoodsIntent = new Intent(this, DataRetrieverService.class);
        uFoodsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, getUserFoodsMessage);
        Log.e("USERID IN SENDER2",userID + "");
        uFoodsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
        startService(uFoodsIntent);

        Intent dFoodsIntent = new Intent(this, DataRetrieverService.class);
        dFoodsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, getDailyFoodsMessage);
        Log.e("USERID IN SENDER3",userID + "");
        dFoodsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
        startService(dFoodsIntent);


    }

    public class MyResponseReceiver extends BroadcastReceiver {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        public static final String ACTION_RESP =
                "com.mamlambo.intent.action.MESSAGE_PROCESSED";
        private boolean retrievedUserDetails = false;
        private boolean retrievedProgressEntries = false;
        private boolean retrievedUserFoods = false;
        private boolean retrievedDailyFoods = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra(DataRetrieverService.PARAM_OUT_MSG);

            Log.e("IN RECEIVER", "IN RECEIVER");

            switch (text) {
                case USER_DETAILS_MESSAGE:
                    retrievedUserDetails = true;
                    break;
                case PROGRESS_ENTRIES_MESSAGE:
                    retrievedProgressEntries = true;
                    break;
                case USER_FOODS_MESSAGE:
                    retrievedUserFoods = true;
                    break;
                case DAILY_FOODS_MESSAGE:
                    retrievedDailyFoods = true;
                    break;
            }
            if(retrievedUserDetails && retrievedProgressEntries && retrievedUserFoods && retrievedDailyFoods){
                retrievedUserDetails = false;
                retrievedDailyFoods = false;
                retrievedUserFoods = false;
                retrievedProgressEntries = false;
                goToMainActivity();
            }
        }
    }


    private void goToMainActivity(){
        unregisterReceiver(receiver);
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }
}

