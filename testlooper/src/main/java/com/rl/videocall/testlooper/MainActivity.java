package com.rl.videocall.testlooper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private Handler childHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                childHandler  = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        Log.e("child","子线程处理");
                    }
                };
                childHandler.sendMessage(Message.obtain());
                Looper.loop();
                Log.e("child","小样你还能执行");
            }
        };

        thread.setName("thj-thread");

        thread.start();


        new Thread(){
            @Override
            public void run() {
                while(true){
                    System.out.println("sssssss");
                }
            }
        }.start();

    }
}
