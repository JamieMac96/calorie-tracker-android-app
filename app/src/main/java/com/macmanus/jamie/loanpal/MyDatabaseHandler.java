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
        myDB.execSQL("CREATE TABLE User (" +
                "UserID INT NOT NULL PRIMARY KEY AUTOINCREMENT " +
                "EmailAddress VARCHAR(255) UNIQUE" +
                "UserName VARCHAR(45)" +
                "Password VARCHAR(45) );");


    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int oldVersion, int newVersion){
        //
    }
}
