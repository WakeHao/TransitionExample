# 前言
早在Android 4.4，Transition 就已经引入，但在5.0才得以真正的实现。而究竟Transition是用来干嘛的呢。接下来我将通过实例和原理解析来分析下Google这个强大的动画框架。
先来张效果图镇住场面

![Google Play上的Newsstand app(v3.3)](https://github.com/WakeHao/TransitionExample/blob/master/gif/first.gif)

这个效果下文会介绍如何实现，不过要先理解透这个框架的一些基础概念。
Transition Framework 核心就是根据Scene(场景,下文解释)的不同帮助开发者们自动生成动画。通常主要是通过以下几个方法开启动画。

- `TransitionManager.go()`
- `beginDelayedTransition()`
- `setEnterTransition()`/`setSharedElementEnterTransition()`


我们来逐一解释以上各种情况

## `TransitionManager.go()`
首先，先介绍下Scene这个类，看看官方的解释
>A scene represents the collection of values that various properties in the View hierarchy will have when the scene is applied. A Scene can be configured to automatically run a Transition when it is applied, which will animate the various property changes that take place during the scene change.

通俗的解释就是这个类存储着一个根view下的各种view的属性。通常由`getSceneForLayout (ViewGroup sceneRoot,int layoutId,Context context)`获取实例。
- sceneRoot
scene发生改变和动画执行的位置
- layoutId
即上文所说的根view

可能这样解释有点无力，下面我举个例子。
![栗子](http://upload-images.jianshu.io/upload_images/2539828-dc6f7f125533d4a9.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
private Scene scene1;
private Scene scene2;
private boolean isScene2;
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scene);
    initToolbar();
    initScene();
}

private void initScene() {
    ViewGroup sceneRoot= (ViewGroup) findViewById(R.id.scene_root);
    scene1=Scene.getSceneForLayout(sceneRoot,R.layout.scene_1,this);
    scene2=Scene.getSceneForLayout(sceneRoot,R.layout.scene_2,this);
    TransitionManager.go(scene1);
}

/**
 * scene1和scene2相互切换，播放动画 * @param view
  */
public void change(View view){
    TransitionManager.go(isScene2?scene1:scene2,new ChangeBounds());
    isScene2=!isScene2;
}
```
**scene1**:
![scene1](http://upload-images.jianshu.io/upload_images/2539828-68ffd1a3e7f63c46.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
**scene2**:
![scene2](http://upload-images.jianshu.io/upload_images/2539828-68803d52d727ea50.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

注意，两个scene布局中1和4，2和3除了图片位置大小不一样，其id是一样的。可以当成一个view.因为分析比较起始scene 的不同创建动画是针对于同一个view的。

上述简单的例子是通过第一种方式`TransitionManager.go()`触发动画。即在进入Activity的时候，手动将start scene通过
`TransitionManager.go(scene1)`设置为scene1。点击button通过`TransitionManager.go(scene2，new ChangeBounds())`切换到end scene状态:scene2.Transition 框架通过`ChangeBounds`类分析start scene和end scene的不同创建并播放动画。由于`ChangeBounds`类是分析比较两个scene中view的位置边界创建移动和缩放动画。发现从scene1->scene2其实是1->4,2->3。于是就执行相应的动画，即是如下效果:

![scene_simple.gif](https://github.com/WakeHao/TransitionExample/blob/master/gif/scene_simple.gif)

类似于`ChangeBounds`类的还有以下几种，他们都是继承Transiton类
- ChangeBounds
检测view的位置边界创建移动和缩放动画
- ChangeTransform
检测view的scale和rotation创建缩放和旋转动画
- ChangeClipBounds
检测view的剪切区域的位置边界，和ChangeBounds类似。不过ChangeBounds针对的是view而ChangeClipBounds针对的是view的剪切区域(`setClipBound(Rect rect)` 中的rect)。如果没有设置则没有动画效果
- ChangeImageTransform
检测**ImageView**（这里是专指ImageView）的尺寸，位置以及ScaleType，并创建相应动画。
- Fade,Slide,Explode
这三个都是根据view的visibility的不同分别创建渐入，滑动，爆炸动画。
以上各个动画类的实现效果如下：

![scene_all.gif](https://github.com/WakeHao/TransitionExample/blob/master/gif/scene_all.gif)


- AutoTransition
如果`TransitionManager.go(scene1)`不指定动画，则默认动画是AutoTransition类。它其实是一个动画集合，查看源码可知其实是动画集合中添加了Fade和ChangeBounds类。
```
private void init() {
    setOrdering(ORDERING_SEQUENTIAL);
    addTransition(new Fade(Fade.OUT)).
            addTransition(new ChangeBounds()).
            addTransition(new Fade(Fade.IN));
}
```
说到动画集合，其实动画类不仅可以通过类似`new ChangeBounds()`方法创建，也可以通过xml文件创建。且如果对于动画集合，xml方式可能会更加方便。
只需要两步，第一步在res/transition创建一个xml文件
如下:
**res/transition/changebounds_and_fade.xml**:
```
<?xml version="1.0" encoding="utf-8"?>
<transitionSet xmlns:android="http://schemas.android.com/apk/res/android">
<changeBounds />
<fade />
</transitionSet>
```

然后再代码中调用:
```
Transition sets=TransitionInflater.from(this).inflateTransition(R.transition.changebounds_and_fade);
```
最后补充一点，关于和`TransitionManager.go(scene2)`其实是调用当前的scene(scene1)的`scene1.exit()`以及下一个scene(scene2)的`scene2.enter()`
而它们又分别会触发`scene1.setExitAction()`和`scene1.setEnterAction()`.可以在这两个方法中定制一些特别的效果.



##  beginDelayedTransition()
接下来介绍下一个触发方式，如果上面的理解透了话下面的就很简单了。之前的那种`TransitionManager.go()`一直都是根据xml文件创造start scene和end scene,这样未免有些麻烦。
而`beginDelayedTransition()`原理则是通过代码改变view的属性，然后通过之前介绍的ChangeBounds等类分析start scene和end Scene不同来创建动画。

依然举个例子：

![栗子x2](http://upload-images.jianshu.io/upload_images/2539828-8fe85d697113a580.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


```
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
 * view的宽高1.5倍和原尺寸大小切换 * 配合ChangeBounds实现缩放效果 * @param view
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
 * VISIBLE和INVISIBLE状态切换 * @param views
  */
private void changeVisibility(View ...views){
    for (View view:views){
        view.setVisibility(view.getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
    }
}
```

当触发点击事件时候，此时记录下当前scene status，然后改变被点击view的尺寸，并改变其他view的visibility，再记录下改变后的scene status。而本例中`beginDelayedTransition()`第二个参数传的是一个`ChangeBounds`和`Explode`动画集合，所以这个集合的中改变尺寸的执行缩放动画,改变visibility的执行爆炸效果。整体效果如下:

![beginDelayed.gif](https://github.com/WakeHao/TransitionExample/blob/master/gif/beginDelayed.gif)


## 界面切换动画
前面说了那么多终于到了重头戏了:Activity/Fragment之前的切换效果。界面切换有两种，一种是不带共享元素的Content Transition一种是带有共享元素的Shared Element Transition。
### Content Transition
先解释下几个重要概念：

![transition_A_to_B.png](http://upload-images.jianshu.io/upload_images/2539828-d6f6029fea5657be.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- `A.exitTransition(transition)`
Transition框架会先遍历A界面确定要执行动画的view(非共享元素view)，执行`A.exitTransition()`前A界面会获取界面的start scene(view 处于VISIBLE状态)，然后将所有的要执行动画的view设置为INVISIBLE，并获取此时的end scene(view 处于INVISIBLE状态).根据transition分析差异的不同创建执行动画。
- `B.enterTransition()`
Transition框架会先遍历B界面，确定要执行动画的view，设置为INVISIBLE。执行`B.enterTransition()`前获取此时的start scene(view 处于INVISIBLE状态)，然后将所有的要执行动画的view设置为VISIBLE，并获取此时的end scene(view 处于VISIBLE状态).根据transition分析差异的不同创建执行动画。



![transition_B_to_A.png](http://upload-images.jianshu.io/upload_images/2539828-edd033b5b850a33e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



根据上文解释，界面切换动画是建立在visibility的改变的基础上的，所以`getWindow().setEnterTransition(transition);`中的参数一般传的是`Fade`,`Slide`,`Explode`类的实例（因为这三个类是通过分析visibility不同创建动画的）。通常写一个完整的Activity Content Transiton有以下几个步骤：
- 在style中添加
```
<item name="android:windowActivityTransitions">true</item>
```
Material主题的应用自动设置为true.
- 设置相应的A离开/B进入/B离开/A重新进入动画。
```
//A 不设置默认为null
getWindow().setExitTransition(transition);
//B 不设置默认为Fade
getWindow().setEnterTransition(transition);
//B 不设置默认为EnterTransition
getWindow().setReturnTransition(transition);
//A 不设置默认为ExitTransition
getWindow().setReenterTransition(transition);
```
当然也可以在主题中设置
```
<item name="android:windowEnterTransition">@transition/slide_and_fade</item>
<item name="android:windowReturnTransition">@transition/return_slide</item>
```
- 跳转界面
这里的跳转界面不能仅仅`startActivity(intent)`,
需要
```
Bundle bundle=ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle;
startActivity(intent,bundle)
```


ok到这里为止既可以运行activity之间的切换动画了。
但是你会发现，在界面切换的时候，A退出时，过了一小会，B就进入了，（真是过分,不给A完全展示ExitTransition）如果你是想等A完全退出后B再进入可以通过设置`setAllowEnterTransitionOverlap(false)`(默认是true)，同样可以在xml中设置：
```
<item name="android:windowAllowEnterTransitionOverlap">false</item>
<item name="android:windowAllowReturnTransitionOverlap">false</item>
```
说了这么多我觉得又得举个简单例子。
**A.Activity:**
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initToolBar();
    getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide));
    //未设置setReenterTransition()默认和setExitTransition一样
}

public void goContentTransitions(View view){
    Intent intent = new Intent(this, ContentTransitionsActivity.class);
    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
    startActivity(intent,activityOptionsCompat.toBundle());
}
```
**res/translation/slide.xml:**
```
<transitionSet xmlns:android="http://schemas.android.com/apk/res/android">
<slide android:duration="1000"></slide>
</transitionSet>
```
**B.Activity:**
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_content_transitions);
    initToolbar();

    Slide slide=new Slide();
    slide.setDuration(500);
    slide.setSlideEdge(Gravity.LEFT);
    getWindow().setEnterTransition(slide);
    getWindow().setReenterTransition(new Explode().setDuration(600));

}
```

实现的效果如下:

![contentTransition.gif](https://github.com/WakeHao/TransitionExample/blob/master/gif/contentTransition.gif)


仔细看着动画你其实可以发现A的状态栏也跟着下拉上拉了，而且和下面的视图有一定的间距。处女座表示不能忍。
其实从原理上来解释，Activity的切换动画针对的是整个界面的view的visibility，而有没有什么方法能让Transition框架只关注某一个view或者不关注某个view呢。当然，`transition.addTarget()`和`transition.excludeTarget()`可以分别实现上述功能。
方便的是也可以在xml设置该属性，那么我们现在要做的是将statusBar排除掉，可以在slide.xml这样写:
```

<transitionSet xmlns:android="http://schemas.android.com/apk/res/android">
<slide android:duration="1000">
    <targets >
        <!--表示除了状态栏-->
        <target android:excludeId="@android:id/statusBarBackground"/>
        <!--表示只针对状态栏-->
 <!--<target android:targetId="@android:id/statusBarBackground"/>-->  </targets>
</slide>
</transitionSet>
```
大功告成，效果我就不贴了，各位可以脑补一下...

### Shared Element Transition

![shared_element.png](http://upload-images.jianshu.io/upload_images/2539828-3f5e885594d36d30.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

界面切换中往往Content Transition和Shared Element Transition是同时存在的，区别于Content Transition,主要有以下几个不同点:
- `startActivity()`
```
Bundle bundle=ActivityOptionsCompat.makeSceneTransitionAnimation(activity，pairs).toBundle;
startActivity(intent,bundle)
```
这里的pairs是`Pair<View, String>`类的实例集合，存储着两个activity之间共享view和name。这里的name要和B界面的共享view的`transitionName`一致。就像这样：

```
Intent intent = new Intent(this, WithSharedElementTransitionsActivity.class);
ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this
  ,new Pair<View, String>(shared_image,"shared_image_")
        ,new Pair<View, String>(shared_text,"shared_text_"));
startActivity(intent,activityOptionsCompat.toBundle());

//xml
<TextView
  android:text="withShared"
  android:transitionName="shared_text_"
 style="@style/MaterialAnimations.TextAppearance.Title.Inverse"
  android:layout_width="wrap_content"
  android:layout_height="wrap_content" />


><de.hdodenhof.circleimageview.CircleImageView
  android:id="@+id/icon_gg"
  android:layout_centerInParent="true"
  android:src="@mipmap/xkl"
  android:transitionName="shared_image_"
  android:layout_width="150dp"
  android:layout_height="150dp" />

```

- `setSharedElementEnterTransition()`/`setSharedElementReturnTransition()`

不设置的话默认是`@android:transition/move`动画。而`setExitTransition()`和`setEnterTransition()`默认为null和Fade.

其实Shared Element Transition原理和Content Transition类似都是根据始末scene status的不同创建动画。
不同的是Content Transition是通过改变view的visibility来改变scene状态从而进一步创建动画，而Shared Element Transition是分析A B界面共享view的尺寸，位置，样式的不同创建动画化的。所以前者通常设置Fade等Transition后者通常设置ChangeBounds等Transition.


最后的最后让我们来分析如何实现文章一开始的那个gif图效果。
1. 整个动画包括Content Transition和Shared Element Transition。而A界面的`setExitTransition()`并没有设置为null。
1. 当进入B界面，这里的共享view只是单纯的移动所以`setSharedElementEnterTransition(transition)`可以不用设置，默认为move。同时会执行一个水纹展开动画，这个可以通过`ViewAnimationUtils.createCircularReveal()`方法实现。在Shared Element Transition结束之后执行Content Transition,可以看出是Slide动画。所以可以通过设置`setExitTransition(new Slide())`完成。注意这里Slide只作用于底部的item(要设置target)，否则就作用于一整个视图了。
1. 最关键的来了，在B退出时候，可以看到屏幕上半部分向上滑过，下半部分向下滑过。一种从中间撕开的视觉效果。所以可以将布局一分为二并指定为用两个不同方向的Slide的Target,差不多像这样:
```
<transitionSet
android:duration="800" xmlns:android="http://schemas.android.com/apk/res/android">
<slide android:slideEdge="top">
    <targets >
        <target android:targetId="@id/viewGroup_top"></target>
    </targets>
</slide>
<slide android:slideEdge="bottom">
    <targets >
        <target android:targetId="@id/viewGroup_bottom"></target>
    </targets>
</slide>
</transitionSet>
```
这里其实有个坑，我们先来看看`isTransitionGroup()`这个方法:
```
public boolean isTransitionGroup() {
    if ((mGroupFlags & FLAG_IS_TRANSITION_GROUP_SET) != 0) {
        return ((mGroupFlags & FLAG_IS_TRANSITION_GROUP) != 0);
    } else {
        final ViewOutlineProvider outlineProvider = getOutlineProvider();
        return getBackground() != null || getTransitionName() != null ||
                (outlineProvider != null && outlineProvider != ViewOutlineProvider.BACKGROUND);
    }
}
```
返回值为true表示这个ViewGroup作为一个整体执行Activity Transition，false表示这个ViewGroup中子view各自执行各自的。如果这个ViewGroup设置了background或者TransitionName，或者`setTransitionGroup(true)`则返回值为true表示作为一个整体执行动画.
所以这里的`viewGroup_bottom`和`viewGroup_top`最好设置下`setTransitionGroup(true)`.

实现效果如下，自己加了点其他特效

![finish](https://github.com/WakeHao/TransitionExample/blob/master/gif/last.gif)




# 参考
- [Getting Started with Activity & Fragment Transitions](http://www.androiddesignpatterns.com/2014/12/activity-fragment-transitions-in-android-lollipop-part1.html "Getting Started with Activity & Fragment Transitions")
- [https://github.com/lgvalle/Material-Animations](https://github.com/lgvalle/Material-Animations "https://github.com/lgvalle/Material-Animations")
