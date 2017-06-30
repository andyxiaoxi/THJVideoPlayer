package com.rl.videocall.countdowntimer;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.text);

        CountDownTimer timer = new CountDownTimer(60000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                       // textView.setText(millisUntilFinished/1000+"S");
                        Log.e("time","主滴答"+millisUntilFinished/1000+"s");
                    }

                    @Override
                    public void onFinish() {
                        //textView.setText("重写验证");
                        Log.e("time","结束");
                    }
                };
                timer.start();
                long last =SystemClock.currentThreadTimeMillis();
                for (int i = 0;i<100000;i++){
                    Log.i("test","空转");
                }
                long elapsed = SystemClock.currentThreadTimeMillis()-last;
                Log.e("waste","耗时"+elapsed);

    }

}
