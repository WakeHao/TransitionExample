package com.wakehao.transitionexample.scene;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeImageTransform;
import android.transition.Transition;

import com.wakehao.transitionexample.R;

public class SceneChangeImageTransformActivity extends BaseSceneActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_image_transform);
        initToolbar();
        initScene(R.id.scene_root,R.layout.scene_1_changeimagetransform,R.layout.scene_2_changeimagetransform);
    }

    @Override
    Transition getTransition() {
        return new ChangeImageTransform();
    }
}
