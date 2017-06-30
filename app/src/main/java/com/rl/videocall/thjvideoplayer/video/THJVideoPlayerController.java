package com.rl.videocall.thjvideoplayer.video;

import android.content.Context;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rl.videocall.thjvideoplayer.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/8.
 * 播放器控制器
 */
public class THJVideoPlayerController extends FrameLayout implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    private Context mContext;
    private THJVideoPlayer mTHJVideoPlayer;


    private ImageView mImage;
    private ImageView mCenterStart;

    private LinearLayout mTop;
    private ImageView mBack;
    private TextView mTitle;

    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private ImageView mFullScreen;

    private LinearLayout mLoading;
    private TextView mLoadText;

    private LinearLayout mError;
    private TextView mRetry;

    private LinearLayout mComplete;
    private TextView mReplay;
    private TextView mShare;


    private boolean topBottomVisible;

    private CountDownTimer mDismissTopBottomCountDownTimer;        //计时器
    private Timer mUpdateProgessTimer;
    private TimerTask mUpdateProgressTimerTask;



    public THJVideoPlayerController(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.video_controller, this, true);

        mImage = (ImageView) findViewById(R.id.image);
        mCenterStart = (ImageView) findViewById(R.id.center_start);

        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);

        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (ImageView) findViewById(R.id.full_screen);

        mLoading = (LinearLayout) findViewById(R.id.loading);
        mLoadText = (TextView) findViewById(R.id.load_text);


        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);

        mComplete = (LinearLayout) findViewById(R.id.complete);
        mReplay = (TextView) findViewById(R.id.replay);
        mShare = (TextView) findViewById(R.id.share);

        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mShare.setOnClickListener(this);

        this.setOnClickListener(this);
    }


    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setImage(String imageUrl) {
        RequestOptions options = new RequestOptions().placeholder(R.drawable.timg).centerCrop();

        Glide.with(mContext).load(imageUrl).apply(options).into(mImage);
    }

    public void setImage(int resId) {
        mImage.setImageResource(resId);
    }

    /**
     * 设置控制器控制的播放器
     *
     * @param thjVideoPlayer
     */
    public void setTHJVideoPlayer(THJVideoPlayer thjVideoPlayer) {
        mTHJVideoPlayer = thjVideoPlayer;
        if (mTHJVideoPlayer.isIdle()) {
            mTop.setVisibility(VISIBLE);
            mBack.setVisibility(GONE);
            mBottom.setVisibility(GONE);
        }
    }
    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
            if (mTHJVideoPlayer.isIdle()) {
                mTHJVideoPlayer.start();
            }
        } else if (v == mBack) {
            if (mTHJVideoPlayer.isFullScreen()) {
                mTHJVideoPlayer.exitFullScreen();
            } else if (mTHJVideoPlayer.isTinyWindow()) {
                mTHJVideoPlayer.exitFullScreen();
            }
        } else if (v == mRestartPause) {
            if (mTHJVideoPlayer.isPlaying() || mTHJVideoPlayer.isBufferingPlaying()) {
                mTHJVideoPlayer.pause();
            } else if (mTHJVideoPlayer.isPaused() || mTHJVideoPlayer.isBufferingPaused()) {
                mTHJVideoPlayer.restart();
            }
        } else if (v == mFullScreen) {
            if (mTHJVideoPlayer.isNormal()) {
                mTHJVideoPlayer.enterFullScreen();
            } else if (mTHJVideoPlayer.isFullScreen()) {
                mTHJVideoPlayer.exitFullScreen();
            }
        } else if (v == mRetry) {
            mTHJVideoPlayer.release();
            mTHJVideoPlayer.start();
        } else if (v == mReplay) {
            mRetry.performClick();      //什么意思
        } else if (v == mShare) {
            Toast.makeText(mContext, "分享", Toast.LENGTH_SHORT).show();
        } else if (v == this) {
            if (mTHJVideoPlayer.isPlaying() || mTHJVideoPlayer.isPaused() || mTHJVideoPlayer
                    .isBufferingPlaying() || mTHJVideoPlayer.isBufferingPaused()) {
                setTopBottomVisiable(!topBottomVisible);
            }
        }
    }

    public void setControllerState(int playerState, int playState) {
        switch (playerState) {
            case THJVideoPlayer.PLAYER_NORMAL:

                mBack.setVisibility(GONE);
                mFullScreen.setVisibility(VISIBLE);
                mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
                break;
            case THJVideoPlayer.PLAYER_FULL_SCREEN:


                mBack.setVisibility(VISIBLE);
                mFullScreen.setVisibility(VISIBLE);
                mFullScreen.setImageResource(R.drawable.ic_player_shrink);
                break;
            case THJVideoPlayer.PLAYER_TINY_WINDOW:    //小窗口
                mFullScreen.setVisibility(View.GONE);
                break;
        }

        switch (playState) {
            case THJVideoPlayer.STATE_IDLE:
                break;

            case THJVideoPlayer.STATE_PREPARING:
                //只显示动画，其他不显示
                mImage.setVisibility(View.GONE);
                mLoading.setVisibility(VISIBLE);
                mLoadText.setText("正在准备...");
                mComplete.setVisibility(GONE);
                mError.setVisibility(GONE);
                mTop.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                break;
            case THJVideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();
                break;
            case THJVideoPlayer.STATE_PLAYING:
                mLoading.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();
                break;
            case THJVideoPlayer.STATE_PAUSED:
                mLoading.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancelDismissTopBottomTimer();
                break;
            case THJVideoPlayer.STATE_BUFFERING_PLAYING:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                mLoadText.setText("正在缓冲...");
                startDismissTopBottomTimer();
                break;
            case THJVideoPlayer.STATE_BUFFERING_PAUSED:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                mLoadText.setText("正在缓冲...");
                cancelDismissTopBottomTimer();
                break;
            case THJVideoPlayer.STATE_COMPLETED:
                cancelUpdateProgressTimer();
                setTopBottomVisiable(false);
                mImage.setVisibility(View.VISIBLE);
                mComplete.setVisibility(View.VISIBLE);
                if (mTHJVideoPlayer.isFullScreen()) {
                    mTHJVideoPlayer.exitFullScreen();
                }
                if (mTHJVideoPlayer.isTinyWindow()) {
                    mTHJVideoPlayer.exitTinyWindow();
                }
                break;
            case THJVideoPlayer.STATE_ERROR:
                cancelUpdateProgressTimer();
                setTopBottomVisiable(false);
                mTop.setVisibility(View.VISIBLE);
                mError.setVisibility(View.VISIBLE);
                break;
        }

    }



    private void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgessTimer == null) {
            mUpdateProgessTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    THJVideoPlayerController.this.post(new Runnable()
                    {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgessTimer.schedule(mUpdateProgressTimerTask,0,300);
    }

    private void cancelUpdateProgressTimer() {
        if(mUpdateProgessTimer!=null){
            mUpdateProgessTimer.cancel();
            mUpdateProgessTimer = null;
        }
        if(mUpdateProgressTimerTask!=null){
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask =null;
        }
    }

    private void updateProgress() {
        long position = mTHJVideoPlayer.getCurrentPosition();
        long duration = mTHJVideoPlayer.getDuration();
        int bufferPercentage = mTHJVideoPlayer.getBufferPercentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int)(100f*position/duration);
        mSeek.setProgress(progress);
        mPosition.setText(NiceUtil.formatTime(position));
        mDuration.setText(NiceUtil.formatTime(duration));
    }


    private void setTopBottomVisiable(boolean visiable) {
        mTop.setVisibility((visiable ? VISIBLE : GONE));
        mBottom.setVisibility((visiable ? VISIBLE : GONE));
        topBottomVisible = visiable;
        if (visiable) {
            //当处于可见时，且不处于暂停的时候，开启隐藏顶部和底部的计时器
            if (!mTHJVideoPlayer.isPaused() && !mTHJVideoPlayer.isBufferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }



    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisiable(false);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();     //计时开始
    }

    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mTHJVideoPlayer.isBufferingPaused()||mTHJVideoPlayer.isPaused()){
            mTHJVideoPlayer.restart();
        }

        int position = (int)(mTHJVideoPlayer.getDuration()*seekBar.getProgress()/100f);
        mTHJVideoPlayer.seekTo(position);
        startDismissTopBottomTimer();
    }

    public void reset(){
        topBottomVisible = false;
        cancelDismissTopBottomTimer();
        cancelUpdateProgressTimer();
        mSeek.setSecondaryProgress(0);
        mSeek.setProgress(0);

        mCenterStart.setVisibility(VISIBLE);
        mImage.setVisibility(VISIBLE);

        mBottom.setVisibility(GONE);
        mFullScreen.setImageResource(R.drawable.ic_player_enlarge);

        mTop.setVisibility(VISIBLE);
        mBack.setVisibility(GONE);

        mLoading.setVisibility(GONE);
        mError.setVisibility(GONE);
        mComplete.setVisibility(GONE);

    }
}
