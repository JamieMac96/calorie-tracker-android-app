package com.macmanus.jamie.loanpal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_food_item);


        Button backButton = (Button) findViewById(R.id.toolbar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Search Results");

        initializeViews();

        String stringFoodItem = getIntent().getExtras().getString("foodItem");
        String context = getIntent().getExtras().getString("calledFrom");


        String [] foodItemSplit = stringFoodItem.split(",");
        if(foodItemSplit.length == 8){
            try{
                FoodItem fItem = initializeFoodItem(foodItemSplit);
                setNumServingsMonitor(fItem);
            }
            catch (NumberFormatException e){
                Toast.makeText(this, "parse error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "Error: food item invalid", Toast.LENGTH_SHORT).show();
        }


        if(context.equals("searchResults")){
            submitfoodButton.setText("Add Food");
        }




    }

    public FoodItem initializeFoodItem(String [] foodItemSplit){
        int nID =  Integer.parseInt(foodItemSplit[0]);
        String nTitle = foodItemSplit[1];
        String nDescription = foodItemSplit[2];
        double nServingSize = Double.parseDouble(foodItemSplit[3]);
        double nNumServings = Double.parseDouble(foodItemSplit[4]);;
        double nFatPerServing = Double.parseDouble(foodItemSplit[5]);;
        double nProteinPerServing = Double.parseDouble(foodItemSplit[6]);;
        double nCarbsPerServing = Double.parseDouble(foodItemSplit[7]);;
        final FoodItem fItem = new  FoodItem(nID, nTitle, nDescription, nServingSize, nNumServings, nFatPerServing, nCarbsPerServing, nProteinPerServing);

        fatAmountText.setText(fItem.getFatPerServing() + "");
        proteinAmountText.setText(fItem.getProteinPerServing() + "");
        carbAmountText.setText(fItem.getCarbsPerServing() + "");
        foodTitle.setText(fItem.getTitle());
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
                    double servings = Double.parseDouble(numServings.getText().toString());
                    fatAmountText.setText((fItem.getFatPerServing() * servings) + "");
                    proteinAmountText.setText((fItem.getProteinPerServing() * servings) + "");
                    carbAmountText.setText((fItem.getCarbsPerServing() * servings) + "");
                    double totalFat = fItem.getFatPerServing() * servings;
                    double totalCarbs = fItem.getCarbsPerServing() * servings;
                    double totalProtein = fItem.getProteinPerServing() * servings;
                    totalCals.setText(getCalories(totalFat, totalCarbs, totalProtein) + "");
                }
            }
        });
    }


    public int getCalories(double fat, double carbs, double protein){
        return (int) ((fat * 9) + (carbs * 4) + (protein * 4));
    }
}
