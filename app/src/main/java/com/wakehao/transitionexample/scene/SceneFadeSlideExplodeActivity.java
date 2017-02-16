package com.wakehao.transitionexample.scene;

import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;

import com.wakehao.transitionexample.R;

public class SceneFadeSlideExplodeActivity extends BaseSceneActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_fade_slide_explode);
        initToolbar();
        initScene(R.id.scene_root,R.layout.scene_1_explode,R.layout.scene_2_explode);
    }

    @Override
    Transition getTransition() {

        return new Slide();
//        return TransitionInflater.from(this)
//                .inflateTransition(R.transition.changebounds_and_fade);
    }
}
