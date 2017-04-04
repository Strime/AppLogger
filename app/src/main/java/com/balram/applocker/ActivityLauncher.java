package com.balram.applocker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by GSA13442 on 29/03/2017.
 */

public class ActivityLauncher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_tutorial),Context.MODE_PRIVATE);
        Intent intent;

        if(sharedPref.getBoolean(getString(R.string.already_launched),false)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, Intro.class);
        }
        startActivity(intent);
        finish();
    }
}
