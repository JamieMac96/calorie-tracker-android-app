package com.macmanus.jamie.loanpal;

import android.util.Log;

/**
 * Created by jamie on 11/04/17.
 */

public class FoodItem {
    private int id;
    private int dailyFoodGlobalID;
    private String title;
    private String description;
    private double servingSize;
    private double numServings;
    private double fatPerServing;
    private double proteinPerServing;
    private double carbsPerServing;

    public FoodItem(int id, String title, String description, double servingSize, double numServings, double fatPerServing, double carbsPerServing, double proteinPerServing){
        this(id, 0, title, description,servingSize, numServings, fatPerServing, carbsPerServing, proteinPerServing);
    }

    public FoodItem(int id, String title, String description, double servingSize, double fatPerServing, double carbsPerServing, double proteinPerServing){
        this(id, 0, title, description,servingSize, 1, fatPerServing, carbsPerServing, proteinPerServing);
    }

    public FoodItem(int id, int globalID, String title, String description, double servingSize, double numServings, double fatPerServing, double carbsPerServing, double proteinPerServing){
        this.id =  id;
        this.dailyFoodGlobalID = globalID;
        this.title = title;
        this.description = description;
        this.servingSize = servingSize;
        this.numServings = numServings;
        this.fatPerServing = fatPerServing;
        this.proteinPerServing = proteinPerServing;
        this.carbsPerServing = carbsPerServing;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getServingSize() {
        return servingSize;
    }

    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }

    public double getNumServings() {
        return numServings;
    }

    public void setNumServings(double numServings) {
        this.numServings = numServings;
    }

    public double getFatPerServing() {
        return fatPerServing;
    }

    public void setFatPerServing(double fatPerServing) {
        this.fatPerServing = fatPerServing;
    }

    public double getProteinPerServing() {
        return proteinPerServing;
    }

    public void setProteinPerServing(double proteinPerServing) {
        this.proteinPerServing = proteinPerServing;
    }

    public double getCarbsPerServing() {
        return carbsPerServing;
    }

    public void setCarbsPerServing(double carbsPerServing) {
        this.carbsPerServing = carbsPerServing;
    }

    public int getDailyFoodGlobalID() {
        return dailyFoodGlobalID;
    }

    public void setDailyFoodGlobalID(int dailyFoodGlobalID) {
        this.dailyFoodGlobalID = dailyFoodGlobalID;
    }

    public int getCaloriesPerServing(){
        return (int) ((fatPerServing * 9) + (proteinPerServing * 4) + (carbsPerServing * 4));
    }

    public String toString(){
        return id + "," + dailyFoodGlobalID + "," + title + "," + description + "," + servingSize + "," + numServings + "," + fatPerServing + "," + proteinPerServing + "," + carbsPerServing;
    }
}
