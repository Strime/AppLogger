package com.balram.applocker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.bassaer.library.MDColor;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by GSA13442 on 27/03/2017.
 */

public class Intro extends AppIntro {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();



        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        CharSequence title ="Title";
        String description = "Descr";
        addSlide(AppIntroFragment.newInstance(title +" 1","desc", description, "desc", R.mipmap.chart, MDColor.BLUE_GREY_50, MDColor.BLUE_GREY_500, MDColor.GREY_500));
        addSlide(AppIntroFragment.newInstance(title +" 2","desc", description, "desc", R.mipmap.chart, MDColor.BLUE_GREY_50, MDColor.BLUE_GREY_500, MDColor.GREY_500));
        addSlide(AppIntroFragment.newInstance(title +" 3","desc", description, "desc", R.mipmap.chart, MDColor.BLUE_GREY_50, MDColor.BLUE_GREY_500, MDColor.GREY_500));
        addSlide(AppIntroFragment.newInstance(title +" 4","desc", description, "desc", R.mipmap.chart, MDColor.BLUE_GREY_50, MDColor.BLUE_GREY_500, MDColor.GREY_500));

        // OPTIONAL METHODS
        // Override bar/separator color.
        /*ActionBar actionbar = getActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable("color"));*/

        setBarColor(MDColor.BLUE_GREY_500);
        //setSeparatorColor(MDColor.BLUE_GREY_500);

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);


        setSlideOverAnimation();

        // Ask for CAMERA permission on the second slide
        askForPermissions(new String[]{Manifest.permission.READ_SMS,Manifest.permission.PROCESS_OUTGOING_CALLS}, 2); // OR

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferences.Editor sharedPrefEditor = getSharedPreferences(getString(R.string.shared_tutorial),Context.MODE_PRIVATE).edit();
        sharedPrefEditor.putBoolean(getString(R.string.already_launched),true);
        sharedPrefEditor.commit();

        Intent intent = new Intent(getApplication(), MainActivity.class);
        // set the new task and clear flags
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
