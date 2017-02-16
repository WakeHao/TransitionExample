package com.wakehao.transitionexample.scene;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import com.wakehao.transitionexample.R;

public class SceneChangeBoundsActivity extends BaseSceneActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_change_bounds);
        initToolbar();
        initScene(R.id.scene_root,R.layout.scene_1_changebounds,R.layout.scene_2_changebounds);
    }

    @Override
    Transition getTransition() {
        return new ChangeBounds();
//        return new ChangeClipBounds();
    }


}
