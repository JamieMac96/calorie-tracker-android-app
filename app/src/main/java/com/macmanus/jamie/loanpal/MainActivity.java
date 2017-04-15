package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle toggle;
    private List<FoodItem> todaysFoods;
    public static String SystemDate;

    private TextView fatTotal;
    private TextView proteinTotal;
    private TextView carbTotal;
    private TextView caloriesTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, myDrawerLayout, 0, 0);

        myDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fatTotal = (TextView) findViewById(R.id.total_fat);
        proteinTotal = (TextView) findViewById(R.id.total_protein);
        carbTotal = (TextView) findViewById(R.id.total_carbs);
        caloriesTotal = (TextView) findViewById(R.id.total_cals);

    }



    //We want to update
    @Override
    protected void onStart() {
        super.onStart();
        SessionManager manager = SessionManager.getInstance(getApplicationContext());
        manager.checkLogin();

        Date date = new Date();
        String currentDate= new SimpleDateFormat("yyyy-MM-dd").format(date);

        //If it is a new day we want to get the data for the new day.
        if((!currentDate.equals(DataRetrieverService.SYSTEM_DATE)) && (DataRetrieverService.SYSTEM_DATE != null)){
            pullUpdatesToLocalDB();
        }

        Log.e("GETTING NEW FOODS", "GETTING NEW FOODS");

        getTodaysFoods();

        displayDailyFoods();

        setNutritionTotals();

        setCalorieTopBar();

        //if we update any foods that are displayed we also want to update the header of the main page
        TextView goalCaloriesTop = (TextView) findViewById(R.id.calorie_goal_top);
        TextView consumedCaloriesTop = (TextView) findViewById(R.id.calories_consumed_top);
        TextView netCaloriesTop = (TextView) findViewById(R.id.net_calories_top);

        //set consumed calories
        Log.e("CONSUMED CALORIES",caloriesTotal.getText().toString());
        consumedCaloriesTop.setText(caloriesTotal.getText().toString());

        //get calorie goal from UserDetails table and set the corresponding element
        Cursor userDetailsTable = MyDatabaseHandler.getInstance(this).getReadableDatabase().rawQuery("SELECT * FROM " + MyDatabaseHandler.USER_DETAILS_TABLE_NAME, null);
        if(userDetailsTable.moveToNext()) {
            Log.e(userDetailsTable.getString(6), "String at location 6");
            String goalCalories = userDetailsTable.getString(6);
            goalCaloriesTop.setText(goalCalories);

            //calculate the net calories
            String netCalories = String.format("%.1f", (Double.parseDouble(goalCalories) - Double.parseDouble(caloriesTotal.getText().toString())));
            netCaloriesTop.setText(netCalories);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void goToGoals(MenuItem item) {
        Intent goals = new Intent(this, GoalsActivity.class);
        startActivity(goals);
    }

    public void goToProgress(MenuItem item) {
        Intent progress = new Intent(this, ProgressActivity.class);
        startActivity(progress);
    }

    public void goToAddFood(MenuItem item) {
        Intent addFood = new Intent(this, AddFoodActivity.class);
        startActivity(addFood);
    }

    public void goToNutrition(MenuItem item) {
        Intent nutrition = new Intent(this, NutritionActivity.class);
        startActivity(nutrition);
    }

    public void goToCreateFood(MenuItem item) {
        Intent createFood = new Intent(this, CreateFoodActivity.class);
        startActivity(createFood);
    }

    public void goToLogin(MenuItem item) {
        //close and delete database
        MyDatabaseHandler.getInstance(getApplicationContext()).close();
        getApplicationContext().deleteDatabase(MyDatabaseHandler.NAME);

        //remove user session
        SessionManager manager = SessionManager.getInstance(this);
        manager.logoutUser();

        //go to login and add flags to remove back button history
        Intent signIn = new Intent(this, LoginActivity.class);
        signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signIn);
        finish();
    }

    public void closeDrawer(MenuItem item) {
        myDrawerLayout.closeDrawer(Gravity.LEFT);
    }


    private void displayDailyFoods(){
        ListView todaysFoodsListView = (ListView) findViewById(R.id.home_food_item_list);


        todaysFoodsListView.setEmptyView(findViewById(R.id.empty));

        //cast to arraylist to match constructor in ArrayAdapter
        ArrayList<FoodItem> items = (ArrayList<FoodItem>) todaysFoods;

        HomeFoodItemAdpter fAdapter = new HomeFoodItemAdpter(this, R.layout.food_item, R.id.home_food_item, items);

        todaysFoodsListView.setAdapter(fAdapter);

        todaysFoodsListView.setClickable(true);
        final List<FoodItem> finalItems = todaysFoods;

        todaysFoodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openFoodItem = new Intent(MainActivity.this, FoodItemActivity.class);
                String stringifiedFoodItem = finalItems.get(position).toString();
                openFoodItem.putExtra("foodItem", stringifiedFoodItem);
                openFoodItem.putExtra("calledFrom", "mainPage");
                startActivity(openFoodItem);
            }
        });
    }

    private void getTodaysFoods(){
        MyDatabaseHandler myDb = MyDatabaseHandler.getInstance(this);

        List<String> dbAsString = myDb.getTableInfoAsString(MyDatabaseHandler.DAILY_FOOD_TABLE_NAME);
        todaysFoods = new ArrayList<FoodItem>();

        for(int i = 1; i < dbAsString.size(); i++){
            Log.e(dbAsString.get(i),"<-----ROW");
            String [] splitRow = dbAsString.get(i).split(",");
            todaysFoods.add(new FoodItem(Integer.parseInt(splitRow[0]), Integer.parseInt(splitRow[1]), splitRow[3], splitRow[4], Double.parseDouble(splitRow[5]),
                    Double.parseDouble(splitRow[6]), Double.parseDouble(splitRow[9]), Double.parseDouble(splitRow[10]), Double.parseDouble(splitRow[8])));
        }
    }

    private void setNutritionTotals(){
        double fatSum = 0;
        double carbSum = 0;
        double proteinSum = 0;
        double calorieSum = 0;

        //get nutrient totals
        for(int i = 0; i < todaysFoods.size(); i++){
            double numServings = todaysFoods.get(i).getNumServings();
            Log.e("Num servings ", numServings + "");
            Log.e("Calories per serving ", todaysFoods.get(i).getCaloriesPerServing()+ "");
            fatSum += todaysFoods.get(i).getFatPerServing() * numServings;
            carbSum += todaysFoods.get(i).getCarbsPerServing() * numServings;
            proteinSum += todaysFoods.get(i).getProteinPerServing() * numServings;
            calorieSum += todaysFoods.get(i).getCaloriesPerServing() * numServings;
        }


        //format the doubles so onlly one decimal place is showing
        String fatString = String.format("%.1f", fatSum) + "g";
        String proteinString = String.format("%.1f", proteinSum) + "g";
        String carbString = String.format("%.1f", carbSum) + "g";
        String calString = String.format("%.1f", calorieSum);

        //finally, set views text to hold the formatted strings
        fatTotal.setText(fatString);
        proteinTotal.setText(proteinString);
        carbTotal.setText(carbString);
        caloriesTotal.setText(calString);
    }

    private void setCalorieTopBar(){

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