package com.macmanus.jamie.loanpal;

/**
 * Created by jamie on 10/04/17.
 */

public class ProgressItem {
    private String weight;
    private String weighInDate;

    ProgressItem(String weight, String weightInDate){
        this.weight = weight;
        this.weighInDate = weightInDate;
    }

    public String getWeight() {
        return weight;
    }

    public String getWeighInDate() {
        return weighInDate;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setWeighInDate(String weightInDate) {
        this.weighInDate = weightInDate;
    }

    public String toString(){
        return weight + "," + weighInDate;
    }
}
