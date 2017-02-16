package com.wakehao.transitionexample;

import android.animation.Animator;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class WithSharedElementTransitionsActivity extends AppCompatActivity {

    private View image_bg;
    private Toolbar toolbar;
    private CircleImageView icon_gg;
    private FloatingActionButton fab;
    private LinearLayout liney_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_shared_element_transitions);
        initView();
        initToolbar();
        getWindow().setEnterTransition(initContentEnterTransition());
        getWindow().setSharedElementEnterTransition(initSharedElementEnterTransition());
        getWindow().setReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.return_slide));

    }

    private Transition initSharedElementEnterTransition() {
        final Transition sharedTransition=TransitionInflater.from(this).inflateTransition(R.transition.changebounds_with_arcmotion);
        sharedTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Animator circularReveal = ViewAnimationUtils.createCircularReveal(image_bg, image_bg.getWidth() / 2, image_bg.getHeight() / 2
                        , icon_gg.getWidth()/2, Math.max(image_bg.getWidth(), image_bg.getHeight()));
                image_bg.setBackgroundColor(Color.BLACK);
                circularReveal.setDuration(600);
                circularReveal.start();
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                sharedTransition.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        return sharedTransition;
    }

    private Transition initContentEnterTransition() {
        Transition transition= TransitionInflater.from(this).inflateTransition(R.transition.slide_and_fade);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
//                liney_bottom.setTransitionGroup(true);
                fab.animate()
                        .scaleY(1)
                        .scaleX(1)
                        .start();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        return transition;
    }

    private void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        image_bg=  findViewById(R.id.image_bg);
        icon_gg= (CircleImageView) findViewById(R.id.icon_gg);
        liney_bottom = (LinearLayout) findViewById(R.id.liney_bottom);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if(!liney_bottom.isTransitionGroup())liney_bottom.setTransitionGroup(true);
        super.onBackPressed();
    }
}
