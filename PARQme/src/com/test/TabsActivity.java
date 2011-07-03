package com.test;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class TabsActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, ParqActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("parq").setIndicator("PARQ",
                          res.getDrawable(R.drawable.ic_tab_account))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, MapActivity.class);
        spec = tabHost.newTabSpec("map").setIndicator("Map",
                          res.getDrawable(R.drawable.ic_tab_account))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, AccActivity.class);
        spec = tabHost.newTabSpec("info").setIndicator("Account",
                          res.getDrawable(R.drawable.ic_tab_account))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, HelpActivity.class);
        spec = tabHost.newTabSpec("test").setIndicator("Help",
                          res.getDrawable(R.drawable.ic_tab_account))
                      .setContent(intent);
        tabHost.addTab(spec);
        tabHost.setCurrentTab(0);
    }
}