package com.wakehao.transitionexample.scene;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.ViewGroup;

import com.wakehao.transitionexample.R;

public class SceneChangeTransformActivity extends BaseSceneActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_change_transform);
        initToolbar();
        initScene(R.id.scene_root,R.layout.scene_1_changetransform,R.layout.scene_2_changetransform);
    }

    @Override
    Transition getTransition() {
//        return new ChangeClipBounds();
        return new ChangeTransform();
    }


}
