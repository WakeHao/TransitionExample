package com.wakehao.transitionexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class BeginDelayedActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView cuteboy,cutegirl,hxy,lly;
    private boolean isImageBigger;
    private ViewGroup sceneRoot;
    private int primarySize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_delayed);
        initToolBar();
        initView();
    }

    @Override
    public void onClick(View v) {
        //start scene 是当前的scene
        TransitionManager.beginDelayedTransition(sceneRoot, TransitionInflater.from(this).inflateTransition(R.transition.explode_and_changebounds));
        //next scene 此时通过代码已改变了scene statue
        changeScene(v);
    }

    private void changeScene(View view) {
        changeSize(view);
        changeVisibility(cuteboy,cutegirl,hxy,lly);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * view的宽高1.5倍和原尺寸大小切换
     * 配合ChangeBounds实现缩放效果
     * @param view
     */
    private void changeSize(View view) {
        isImageBigger=!isImageBigger;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(isImageBigger){
            layoutParams.width=(int)(1.5*primarySize);
            layoutParams.height=(int)(1.5*primarySize);
        }else {
            layoutParams.width=primarySize;
            layoutParams.height=primarySize;
        }
        view.setLayoutParams(layoutParams);
    }

    /**
     * VISIBLE和INVISIBLE状态切换
     * @param views
     */
    private void changeVisibility(View ...views){
        for (View view:views){
            view.setVisibility(view.getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
        }
    }

    private void initView() {
        sceneRoot = (ViewGroup) findViewById(R.id.scene_root);
        cuteboy= (CircleImageView) findViewById(R.id.cuteboy);
        cutegirl= (CircleImageView) findViewById(R.id.cutegirl);
        hxy= (CircleImageView) findViewById(R.id.hxy);
        lly= (CircleImageView) findViewById(R.id.lly);
        primarySize=cuteboy.getLayoutParams().width;
        cuteboy.setOnClickListener(this);
        cutegirl.setOnClickListener(this);
        hxy.setOnClickListener(this);
        lly.setOnClickListener(this);
    }


    private void initToolBar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
