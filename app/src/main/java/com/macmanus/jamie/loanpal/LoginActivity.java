package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.Button;
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
    private MyResponseReceiver receiver;
    private Button loginButton;
    //private final String REQUEST_DESTINATION = "http://10.0.2.2/calorie-tracker-app-server-scripts/login.php";
    private final String REQUEST_DESTINATION = "http://34.251.31.162/login.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent updateRemoteDBIntent = new Intent(getApplicationContext(), DataSenderService.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        loginButton = (Button) findViewById(R.id.sign_in_button);


        IntentFilter filter = new IntentFilter(MyResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyResponseReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        //if we get here and somehow a database exists, then attempting login will crash the app
        //thus we check if the database exists and if it does we close and delete it
        if(MyDatabaseHandler.hasInstance()){
            MyDatabaseHandler.getInstance(getApplicationContext()).close();
            getApplicationContext().deleteDatabase(MyDatabaseHandler.NAME);

        }

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
            loginButton.setClickable(false);
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
                    e.printStackTrace();
                }
                loginButton.setClickable(true);
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

        Intent detailsIntent = new Intent(this, DataRetrieverService.class);
        detailsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, USER_DETAILS_MESSAGE);
        detailsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
        startService(detailsIntent);

        Intent progressIntent = new Intent(this, DataRetrieverService.class);
        progressIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, PROGRESS_ENTRIES_MESSAGE);
        progressIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
        startService(progressIntent);

        Intent uFoodsIntent = new Intent(this, DataRetrieverService.class);
        uFoodsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, USER_FOODS_MESSAGE);
        uFoodsIntent.putExtra(DataRetrieverService.USER_ID_MSG, userID);
        startService(uFoodsIntent);

        Intent dFoodsIntent = new Intent(this, DataRetrieverService.class);
        dFoodsIntent.putExtra(DataRetrieverService.PARAM_IN_MSG, DAILY_FOODS_MESSAGE);
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
        Log.e("GO TO MAIN", "GO TO MAIN");
        try {
            if (receiver!=null){
                unregisterReceiver(receiver);
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
        finish();
    }
}

