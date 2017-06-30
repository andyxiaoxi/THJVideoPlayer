package com.rl.videocall.getscreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    int width;
    int height;

    private RelativeLayout mParent;
    private FrameLayout mContainer;

    private ViewGroup.LayoutParams oldParams;

    private boolean smallFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        width = getResources().getDisplayMetrics().widthPixels;

        height = getResources().getDisplayMetrics().heightPixels;

        Log.e("tt","宽="+width+"---------高="+height);

        mParent = (RelativeLayout) findViewById(R.id.parent);
        mContainer = (FrameLayout) findViewById(R.id.container);
        oldParams = mContainer.getLayoutParams();
    }

    public void small(View v){
        ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
        if(!smallFlag){
            smallFlag =true;
            //小窗口播放
            ViewGroup parent = (ViewGroup) mContainer.getParent();
            if(parent!=null){
                parent.removeView(mContainer);
            }


            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(width*0.6f),(int)(width*0.6f*9f/16f));

            params.gravity = Gravity.TOP;

            contentView.addView(mContainer,params);

        }else{
            smallFlag =false;
            //大窗口播放
            contentView.removeView(mContainer);
            mParent.addView(mContainer,oldParams);
        }
    }
}
