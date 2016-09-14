package com.example.david.takeatrip.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.david.takeatrip.R;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro2 {

    private static final int VIBRATE_INTENSITY = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // Add your slide's fragments here.
//        // AppIntro will automatically generate the dots indicator and buttons.
//        // (NOTE: non cancellare questo commento, può essere utile in futuro)
//        addSlide(IntroSampleSlideFragment.newInstance(R.layout.first_slide_here));
//        addSlide(IntroSampleSlideFragment.newInstance(R.layout.second_slide_here));

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //TODO inserire immagini, descrizioni e background appropriati
        addSlide(AppIntroFragment.newInstance("first slide",
                "loooooooooooooooooooooooooooooooooooong description", R.drawable.empty_image, Color.parseColor("#6F51B5")));
        addSlide(AppIntroFragment.newInstance("second slide",
                "loooooooooooooooooooooooooooooooooooong description", R.drawable.empty_image, Color.parseColor("#8F51B5")));
        addSlide(AppIntroFragment.newInstance("third slide",
                "loooooooooooooooooooooooooooooooooooong description", R.drawable.empty_image, Color.parseColor("#8F51B5")));

        // OPTIONAL METHODS
        // Override bar/separator color (not available for AppIntro2).
        //setBarColor(Color.parseColor("#3F51B5"));
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        //showSkipButton(true);     //(not available for AppIntro2)
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
}
