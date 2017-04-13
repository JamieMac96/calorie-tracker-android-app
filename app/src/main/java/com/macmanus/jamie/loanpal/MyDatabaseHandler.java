package com.macmanus.jamie.loanpal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamie on 04/03/17.
 *
 * This is a singleton child class of the class SQLiteOpenHelper.
 * The singleton pattern allows us to refernce the same instance of the class whenever it is used.
 */

public class MyDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABSE_VERSION = 1;
    public static final String NAME = "local_calorie_helper.db";
    public static final String BODYWEIGHT_ENTRY_TABLE_NAME = "BodyweightEntry";
    public static final String DAILY_FOOD_TABLE_NAME = "DailyFood";
    public static final String USER_FOOD_TABLE_NAME = "UserFood";
    public static final String USER_DETAILS_TABLE_NAME = "UserDetails";

    private static MyDatabaseHandler myInstance;


    public static MyDatabaseHandler getInstance(Context context){
        if(myInstance == null){
            myInstance = new MyDatabaseHandler(context.getApplicationContext());
        }
        return myInstance;
    }

    public static boolean hasInstance(){
        return myInstance != null;
    }

    private MyDatabaseHandler(Context context){
        super(context, NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase myDB){

        myDB.execSQL("  CREATE TABLE BodyweightEntry("                      +
                    "   EntryID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"    +
                    "   User_UserID INTEGER NOT NULL,"              +
                    "   Weight double NOT NULL,"                            +
                    "   WeighInDate NOT NULL);");

        myDB.execSQL("  CREATE TABLE DailyFood( "                           +
                "       FoodID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"     +
                "       User_UserID INTEGER NOT NULL, "                         +
                "       Name VARCHAR(45) NOT NULL,"                         +
                "       Description VARCHAR(200) NOT NULL,"                 +
                "       ServingSize double NOT NULL,"                       +
                "       NumServings double NOT NULL,"                       +
                "       CaloriesPerServing double NOT NULL,"                +
                "       ProteinPerServing double NOT NULL,"                 +
                "       FatPerServing double NOT NULL,"                     +
                "       CarbsPerServing double NOT NULL);"
        );

        myDB.execSQL("  CREATE TABLE UserDetails("                          +
                "       User_UserID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "       WeeklyGoal VARCHAR(45) NOT NULL,"                   +
                "       ActivityLevel VARCHAR(45) NOT NULL,"                   +
                "       InitialBodyweight double NOT NULL, "                +
                "       Bodyweight double NOT NULL,"                        +
                "       GoalWeight double NOT NULL,"                        +
                "       CalorieGoal INTEGER NOT NULL,"                          +
                "       ProteinGoalPercent INTEGER NOT NULL,"                   +
                "       CarbGoalPercent INTEGER NOT NULL,"                      +
                "       FatGoalPercent INTEGER NOT NULL); ");

        myDB.execSQL("  CREATE TABLE UserFood( "                            +
                "       FoodID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"     +
                "       User_UserID INTEGER NOT NULL, "                         +
                "       Name VARCHAR(45) NOT NULL,"                         +
                "       Description VARCHAR(200) NOT NULL,"                 +
                "       ServingSize double NOT NULL,"                       +
                "       CaloriesPerServing double NOT NULL,"                +
                "       ProteinPerServing double NOT NULL,"                 +
                "       FatPerServing double NOT NULL,"                     +
                "       CarbsPerServing double NOT NULL);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int oldVersion, int newVersion){
        //
    }


    //returns the content of a table as an arraylist of rows.
    //The rows are comma seperated values.
    public List<String> getTableInfoAsString(String tableName){
        List<String> tableData = new ArrayList<String>();


        String selectQuery = "SELECT  * FROM " + tableName;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        int counter = 0;

        while (cursor.moveToNext()) {
            if(counter == 0) {
                tableData.add("");
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    int currentIndex = tableData.size() - 1;
                    if (i != cursor.getColumnCount() - 1) {
                        tableData.set(currentIndex, tableData.get(currentIndex) + cursor.getColumnName(i) + ",");
                    } else {
                        tableData.set(currentIndex, tableData.get(currentIndex) + cursor.getColumnName(i));
                    }
                }
            }
            tableData.add("");
            for(int i = 0; i < cursor.getColumnCount(); i++){
                int currentIndex = tableData.size() - 1;
                if(i != cursor.getColumnCount() - 1) {
                    tableData.set(currentIndex, tableData.get(currentIndex) + cursor.getString(i) + ",");
                }
                else{
                    tableData.set(currentIndex, tableData.get(currentIndex) + cursor.getString(i));
                }
            }
            counter++;
        }


        cursor.close();

        return tableData;
    }
}
