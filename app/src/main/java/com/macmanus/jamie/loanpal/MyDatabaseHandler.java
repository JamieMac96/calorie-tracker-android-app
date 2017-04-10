package com.macmanus.jamie.loanpal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jamie on 04/03/17.
 *
 * This is a singleton child class of the class SQLiteOpenHelper.
 * The singleton pattern allows us to refernce the same instance of the class whenever it is used.
 */

public class MyDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABSE_VERSION = 1;
    private static final String NAME = "local_calorie_helper.db";
    private static MyDatabaseHandler myInstance;


    public static MyDatabaseHandler getInstance(Context context){
        if(myInstance == null){
            myInstance = new MyDatabaseHandler(context.getApplicationContext());
        }
        return myInstance;
    }

    private MyDatabaseHandler(Context context){
        super(context, NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase myDB){
        myDB.execSQL("CREATE TABLE User ("                                  +
                "UserID INT NOT NULL PRIMARY KEY AUTOINCREMENT,"            +
                "EmailAddress VARCHAR(255) UNIQUE,"                         +
                "Password VARCHAR(45));");

        myDB.execSQL("  CREATE TABLE BodyweightEntry("                      +
                    "   EntryID INT NOT NULL PRIMARY KEY AUTOINCREMENT,"    +
                    "   User_UserID INT NOT NULL FOREIGN KEY,"              +
                    "   Weight double NOT NULL,"                            +
                    "   WeighInDate NOT NULL,"                              +
                    "   FOREIGN KEY User_UserID REFERENCES User(UserID));");

        myDB.execSQL("  CREATE TABLE DailyFood( "                           +
                "       FoodID INT NOT NULL PRIMARY KEY AUTOINCREMENT,"     +
                "       User_UserID INT NOT NULL, "                         +
                "       Name VARCHAR(45) NOT NULL,"                         +
                "       ServingSize double NOT NULL,"                       +
                "       CaloriesPerServing double NOT NULL,"                +
                "       ProteinPerServing double NOT NULL,"                 +
                "       FatPerServing double NOT NULL,"                     +
                "       CarbsPerServing double NOT NULL,"                   +
                "       FOREIGN KEY User_UserID REFERNCES User(UserID));"
        );

        myDB.execSQL("  CREATE TABLE UserDetails("                          +
                "       User_UsrID INT NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "       WeeklyGoal VARCHAR(45) NOT NULL,"                   +
                "       InitialBodyweight double NOT NULL, "                +
                "       Bodyweight double NOT NULL,"                        +
                "       CalorieGoal INT NOT NULL,"                          +
                "       ProteinGoalPercent INT NOT NULL,"                   +
                "       CarbGoalPercent INT NOT NULL,"                      +
                "       FatGoalPercent INT NOT NULL,"                       +
                "       FOREIGN KEY User_UserID REFERENCES User(UserID)); ");

        myDB.execSQL("  CREATE TABLE UserFood( "                            +
                "       FoodID INT NOT NULL PRIMARY KEY AUTOINCREMENT,"     +
                "       User_UserID INT NOT NULL, "                         +
                "       Name VARCHAR(45) NOT NULL,"                         +
                "       ServingSize double NOT NULL,"                       +
                "       CaloriesPerServing double NOT NULL,"                +
                "       ProteinPerServing double NOT NULL,"                 +
                "       FatPerServing double NOT NULL,"                     +
                "       CarbsPerServing double NOT NULL,"                   +
                "       FOREIGN KEY User_UserID REFERNCES User(UserID));"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int oldVersion, int newVersion){
        //
    }
}
