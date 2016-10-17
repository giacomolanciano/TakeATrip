package com.example.david.takeatrip.Activities;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;

import com.example.david.takeatrip.R;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro2 {

    private static final int VIBRATE_INTENSITY = 30;
    private static final int LAST_SLIDE = 5;
    private static final int PERMISSIONS_SLIDE = LAST_SLIDE - 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // Add your slide's fragments here.
//        // AppIntro will automatically generate the dots indicator and buttons.
//        // NOTE: Do not delete this comment, it could be useful
//        addSlide(IntroSampleSlideFragment.newInstance(R.layout.first_slide_here));
//        addSlide(IntroSampleSlideFragment.newInstance(R.layout.second_slide_here));

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance(getString(R.string.home_slide_title),
                getString(R.string.home_slide_description), R.drawable.home_slide,
                ResourcesCompat.getColor(getResources(), R.color.blue, null)));

        addSlide(AppIntroFragment.newInstance(getString(R.string.memories_slide_title),
                getString(R.string.memories_slide_description), R.drawable.memories_slide,
                ResourcesCompat.getColor(getResources(), R.color.blue, null)));

        addSlide(AppIntroFragment.newInstance(getString(R.string.help_slide_title),
                getString(R.string.help_slide_description), R.drawable.help_slide,
                ResourcesCompat.getColor(getResources(), R.color.blue, null)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addSlide(AppIntroFragment.newInstance(getString(R.string.permissions_slide_title),
                    getString(R.string.permissions_slide_description), R.drawable.permissions_slide,
                    ResourcesCompat.getColor(getResources(), R.color.blue, null)));

            // Ask user for permissions (all in one slide, when it is passed by).
            // NOTE: Do not place them in last slide. Slides numbers start from 1.
            askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS}, PERMISSIONS_SLIDE);
        }

        addSlide(AppIntroFragment.newInstance(getString(R.string.last_slide_title),
                getString(R.string.last_slide_description), R.drawable.last_slide,
                ResourcesCompat.getColor(getResources(), R.color.blue, null)));

        // OPTIONAL METHODS
        // Override bar/separator color (not available for AppIntro2).
        //setBarColor(Color.parseColor("#3F51B5"));
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        //showSkipButton(false);     //(not available for AppIntro2)
        setSkipButtonEnabled(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(VIBRATE_INTENSITY);

        // Available animations.
        setFadeAnimation();
        //setZoomAnimation();
        //setFlowAnimation();
        //setSlideOverAnimation();
        //setDepthAnimation();

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // no op
    }

    private void setSkipButtonEnabled(boolean skipButtonEnabled) {
        this.skipButtonEnabled = skipButtonEnabled;
    }
}
