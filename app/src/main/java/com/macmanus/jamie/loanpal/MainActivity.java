package com.macmanus.jamie.loanpal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle toggle;


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

    }


    //If user has already been on the main page and they logout then the can simply press back to
    //get back to the main page again without logging in. Calling checkLogin() in the onstart prevents this.
    @Override
    protected void onStart() {
        super.onStart();
        SessionManager manager = SessionManager.getInstance(getApplicationContext());
        manager.checkLogin();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToLogin(MenuItem item) {
        Intent signIn = new Intent(this, LoginActivity.class);
        startActivity(signIn);
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
        Intent progress = new Intent(this, AddFoodActivity.class);
        startActivity(progress);
    }

    public void goToCreateFood(MenuItem item) {
        Intent progress = new Intent(this, CreateFoodActivity.class);
        startActivity(progress);
    }
}