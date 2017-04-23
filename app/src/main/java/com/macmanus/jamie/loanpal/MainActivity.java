package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    private TextView fatGoal;
    private TextView proteinGoal;
    private TextView carbGoal;
    private TextView caloriesGoal;


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

        fatGoal = (TextView) findViewById(R.id.goal_fat);
        proteinGoal = (TextView) findViewById(R.id.goal_protein);
        carbGoal = (TextView) findViewById(R.id.goal_carbs);
        caloriesGoal = (TextView) findViewById(R.id.goal_cals);

    }



    //We want to update
    @Override
    protected void onStart() {
        super.onStart();
        SessionManager manager = SessionManager.getInstance(getApplicationContext());
        manager.checkLogin();

        //Each day we want to start the food diary over again and remove any food from the local database that is not todays and load the new stuff if there is any.
        //to do this we check sharedpreference that we created in the dataretrieverservice and if there is a disparity between the current date and the date of the last
        //update then we update the local database
        Date date = new Date();
        String systemDate;
        String currentDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
        try {
            systemDate = getSharedPreferences("CalorieTrackerPref", 0).getString("systemDate", null);
        }
        catch(NullPointerException e){
            systemDate = null;
        }
        if((!currentDate.equals(systemDate)) && (systemDate != null)){
            pullUpdatesToLocalDB();
        }


        getTodaysFoods();

        displayDailyFoods();

        setNutritionTotals();

        setNutritionalGoals();

        setNutritionTopBar();

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

        Intent updateRemoteDBIntent = new Intent(getApplicationContext(), DataSenderService.class);

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, updateRemoteDBIntent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);

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
        String fatString = String.format("%.1f", fatSum) + "g ";
        String proteinString = String.format("%.1f", proteinSum) + "g ";
        String carbString = String.format("%.1f", carbSum) + "g ";
        String calString = String.format("%d", (int)calorieSum);

        //finally, set views text to hold the formatted strings
        fatTotal.setText(fatString);
        proteinTotal.setText(proteinString);
        carbTotal.setText(carbString);
        caloriesTotal.setText(calString);
    }

    private void setNutritionalGoals(){
        MyDatabaseHandler myDB = MyDatabaseHandler.getInstance(this);

        List<String> tableString = myDB.getTableInfoAsString(MyDatabaseHandler.USER_DETAILS_TABLE_NAME);

        if(tableString.size() > 0) {
            String [] rowSplit = tableString.get(1).split(",");

            String calorieGoal = rowSplit[6];
            String proteinGoalPercent = rowSplit[7];
            String carbGoalPercent = rowSplit[8];
            String fatGoalPercent = rowSplit[9];

            double calorieGoalDouble = Double.parseDouble(calorieGoal);
            double caloriesFromProtein = (Integer.parseInt(proteinGoalPercent) * calorieGoalDouble) / 100;
            double caloriesFromFat = (Integer.parseInt(fatGoalPercent) * calorieGoalDouble) / 100;
            double caloriesFromCarb = (Integer.parseInt(carbGoalPercent) * calorieGoalDouble) / 100;

            int goalProtien = (int) caloriesFromProtein / 4;
            int goalCarbs = (int) caloriesFromCarb / 4;
            int goalFat = (int) caloriesFromFat / 9;

            fatGoal.setText(goalFat + "");
            proteinGoal.setText(goalProtien + "");
            carbGoal.setText(goalCarbs + "");
            caloriesGoal.setText(calorieGoal);
        }
        else{
            Toast.makeText(this, "Set your goals utilize all the app's features", Toast.LENGTH_SHORT).show();
        }

    }


    private void setNutritionTopBar(){
        //if we update any foods that are displayed we also want to update the header of the main page
        TextView goalCaloriesTop = (TextView) findViewById(R.id.calorie_goal_top);
        TextView consumedCaloriesTop = (TextView) findViewById(R.id.calories_consumed_top);
        TextView netCaloriesTop = (TextView) findViewById(R.id.net_calories_top);

        //set consumed calories
        consumedCaloriesTop.setText(caloriesTotal.getText().toString());

        //get calorie goal from UserDetails table and set the corresponding element
        Cursor userDetailsTable = MyDatabaseHandler.getInstance(this).getReadableDatabase().rawQuery("SELECT * FROM " + MyDatabaseHandler.USER_DETAILS_TABLE_NAME, null);
        if(userDetailsTable.moveToNext()) {
            String goalCalories = userDetailsTable.getString(6);
            goalCaloriesTop.setText(goalCalories);

            //calculate the net calories
            String netCalories = String.format("%.1f", (Double.parseDouble(goalCalories) - Double.parseDouble(caloriesTotal.getText().toString())));
            netCaloriesTop.setText(netCalories);
            if(Double.parseDouble(netCalories) < 0){
                netCaloriesTop.setTextColor(ContextCompat.getColor(this, R.color.myRed));
            }
            else{
                netCaloriesTop.setTextColor(ContextCompat.getColor(this, R.color.green));
            }
        }
    }

    private void pullUpdatesToLocalDB(){
        LocalDatabaseUpdater myUpdater = new LocalDatabaseUpdater(getApplicationContext());

        myUpdater.updateUserFoods();
        myUpdater.updateDailyFoods();
    }
}