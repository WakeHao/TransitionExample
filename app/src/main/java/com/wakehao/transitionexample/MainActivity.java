package com.wakehao.transitionexample;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide));
//        getWindow().setExitTransition(new Fade());
        //未设置setReenterTransition()默认和setExitTransition一样
    }

    public void goContentTransitions(View view){
        Intent intent = new Intent(this, ContentTransitionsActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        startActivity(intent,activityOptionsCompat.toBundle());
    }


    private void initToolBar() {
        Toolbar toolbar=  (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void goScene(View view){
        startActivity(new Intent(this,SceneActivity.class));
    }
    public void goBeginDelayed(View view){
        startActivity(new Intent(this,BeginDelayedActivity.class));
    }

    public void goContent_and_Shared(View view){
        startActivity(new Intent(this,ContentAndSharedTransitionActivity.class));
    }




}
