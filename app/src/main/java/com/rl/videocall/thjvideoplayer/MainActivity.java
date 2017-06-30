package com.rl.videocall.thjvideoplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rl.videocall.thjvideoplayer.video.THJVideoPlayer;
import com.rl.videocall.thjvideoplayer.video.THJVideoPlayerController;

public class MainActivity extends AppCompatActivity {

    private THJVideoPlayer mThjVideoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mThjVideoPlayer = (THJVideoPlayer) findViewById(R.id.player);
        mThjVideoPlayer.setUp("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4", null);
        THJVideoPlayerController controller = new THJVideoPlayerController(this);
        controller.setTitle("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？");
        controller.setImage("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg");
        mThjVideoPlayer.setController(controller);
    }

    @Override
    protected void onStop() {

        super.onStop();
    }
}
