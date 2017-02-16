package com.wakehao.transitionexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import com.wakehao.transitionexample.scene.BaseSceneActivity;
import com.wakehao.transitionexample.scene.SceneChangeBoundsActivity;
import com.wakehao.transitionexample.scene.SceneChangeClipBoundsActivity;
import com.wakehao.transitionexample.scene.SceneChangeImageTransformActivity;
import com.wakehao.transitionexample.scene.SceneChangeTransformActivity;
import com.wakehao.transitionexample.scene.SceneFadeSlideExplodeActivity;

public class SceneActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        initToolbar();
    }

    private void initToolbar() {
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

    public void changeBounds(View view){
        startActivity(new Intent(this, SceneChangeBoundsActivity.class));
    }
    public void changeTransform(View view){
        startActivity(new Intent(this, SceneChangeTransformActivity.class));
    }
    public void changeClipBounds(View view){
        startActivity(new Intent(this, SceneChangeClipBoundsActivity.class));
    }
    public void changeImageTransform(View view){
        startActivity(new Intent(this, SceneChangeImageTransformActivity.class));
    }
    public void fade(View view){
        startActivity(new Intent(this, SceneFadeSlideExplodeActivity.class));
    }

}
