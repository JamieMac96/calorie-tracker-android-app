package com.macmanus.jamie.loanpal;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.GraphicalView;

import java.util.List;

/**
 * Created by jamie on 15/04/17.
 */

public class NutritionActivity extends AppCompatActivity {

    private TextView fatText;
    private TextView carbsText;
    private TextView proteinText;

    private double fatGrams;
    private double carbGrams;
    private double proteinGrams;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_nutrition);

        fatText = (TextView) findViewById(R.id.nutrition_fat);
        carbsText = (TextView) findViewById(R.id.nutrition_carbs);
        proteinText = (TextView) findViewById(R.id.nutrition_protein);

        initializeTextViews();

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

        toolbarTitle.setText("Nutrition");

        // create instance of a PieGraph we just created
        PieChart nutritionPie = new PieChart();
        GraphicalView graphicalView = nutritionPie.getGraphicalView(this, (int)(proteinGrams * 4), (int)(carbGrams * 4), (int)(fatGrams * 4));

        LinearLayout pieGraph = (LinearLayout) findViewById(R.id.pie_chart);
// pieGraph is just a regular LinearLayout you need to create in .xml file

        pieGraph.addView(graphicalView);

    }

    private void initializeTextViews(){
        fatText.setTextColor(Color.BLUE);
        proteinText.setTextColor(Color.RED);
        carbsText.setTextColor(Color.GREEN);

        fatGrams = 0;
        carbGrams = 0;
        fatGrams = 0;

        MyDatabaseHandler myDB = MyDatabaseHandler.getInstance(this);

        List<String> dailyFoodsDB = myDB.getTableInfoAsString(MyDatabaseHandler.DAILY_FOOD_TABLE_NAME);

        for(int i = 1; i < dailyFoodsDB.size(); i++){
            String [] rowSplit = dailyFoodsDB.get(i).split(",");
            double numServings = Double.parseDouble(rowSplit[6]);
            Log.e(rowSplit[8], "<--protein");
            Log.e(rowSplit[9], "<--carbs");
            Log.e(rowSplit[10], "<--fat");
            proteinGrams += Double.parseDouble(rowSplit[8]) * numServings;
            fatGrams += Double.parseDouble(rowSplit[9]) * numServings;
            carbGrams += Double.parseDouble(rowSplit[10]) * numServings;
        }

        fatText.append("" + fatGrams + "g");
        proteinText.append("" + proteinGrams+ "g");
        carbsText.append("" + carbGrams+ "g");

    }













}
