package com.rl.videocall.fullscreen;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private FrameLayout contianer;
    private boolean fullFlag;
    private boolean firstFlag;

    private RelativeLayout parent;
    ViewGroup.LayoutParams oldparams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contianer = (FrameLayout) findViewById(R.id.container);
        parent = (RelativeLayout) findViewById(R.id.parent);

       oldparams = contianer.getLayoutParams();
    }

    public void full(View v){
        ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
        if(!fullFlag){
            firstFlag =true;

            fullFlag=true;
            parent.removeView(contianer);
            //1,隐藏状态栏，actionBar,

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            ActionBar ab = getSupportActionBar();
            if(ab!=null){
                ab.setShowHideAnimationEnabled(false);
                ab.hide();
            }

            //2,设置全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);



            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT );

            contentView.addView(contianer,params);


        }else{
            if(firstFlag) {
                fullFlag = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                ActionBar ab = getSupportActionBar();
                if (ab != null) {
                    ab.setShowHideAnimationEnabled(false);
                    ab.show();
                }

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                contentView.removeView(contianer);




                parent.addView(contianer, oldparams);
            }
        }
    }
}
