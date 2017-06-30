package com.rl.videocall.thjvideoplayer.video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/8.
 */
public class THJVideoPlayer extends FrameLayout implements TextureView.SurfaceTextureListener{


    public static final int STATE_ERROR = -1;          // 播放错误
    public static final int STATE_IDLE = 0;            // 播放未开始
    public static final int STATE_PREPARING = 1;       // 播放准备中
    public static final int STATE_PREPARED = 2;        // 播放准备就绪
    public static final int STATE_PLAYING = 3;         // 正在播放
    public static final int STATE_PAUSED = 4;          // 暂停播放
    // 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
    public static final int STATE_BUFFERING_PLAYING = 5;
    // 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停)
    public static final int STATE_BUFFERING_PAUSED = 6;
    public static final int STATE_COMPLETED = 7;       // 播放完成


    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    public static final int PLAYER_TINY_WINDOW = 12;   // 小窗口播放器


    private Context mContext;

    /**
     * 容器
     */
    private FrameLayout mContainer;
    /**
     * 控制器
     */
    private THJVideoPlayerController mController;
    private MediaPlayer mMediaPlayer;

    private int mBufferPercentage;
    private int mCurrentState = STATE_IDLE;
    private int mPlayerState = PLAYER_NORMAL;
    /**
     * 设置播放视频的地址
     */
    private String mUrl;
    private Map<String,String> mHeaders;
    private SurfaceTexture mSurfaceTexture;
    private TextureView mTextureView;





    public THJVideoPlayer(Context context) {
        this(context,null);
    }

    public THJVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * 初始化，把容器加入到控件上
     */
    private void init(){
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        this.addView(mContainer,params);
    }

    /**
     * 设置播放的数据源
     * @param url
     * @param headers
     */
    public void setUp(String url,Map<String,String> headers){
        mUrl = url;
        mHeaders = headers;
    }

    /**
     * 关联控制器
     * @param controller
     */
    public void setController(THJVideoPlayerController controller){
        mController = controller;
        //设置控制的播放器对象，互相绑定
        mController.setTHJVideoPlayer(this);

        mContainer.removeView(mController);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        mContainer.addView(mController,params);
    }

    /**
     * 初始化播放器，添加视频视图
     */
    public void start(){
        if(mCurrentState==STATE_IDLE||mCurrentState==STATE_ERROR||mCurrentState==STATE_COMPLETED) {
            initMediaPlayer();     //初始化播放器
            initTextureView();     //初始化展示视频内容的TextureView
            addTextureView();      //将TexttureView增加到容器中
        }
    }

    public void restart(){
        if(mCurrentState==STATE_PAUSED){
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mController.setControllerState(mPlayerState,mCurrentState);
            LogUtil.d("STATE_PLAYING");
        }
        if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("STATE_BUFFERING_PLAYING");
        }
    }

    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("STATE_PAUSED");
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("STATE_BUFFERING_PAUSED");
        }
    }

    public void seekTo(int pos) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(pos);
        }
    }

    /**
     * 初始化播放器，注册好监听事件的方法
     */
    private void initMediaPlayer() {
        if(mMediaPlayer==null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);

            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        }
    }

    /**
     * 初始化视屏界面承载界面
     */
    private void initTextureView() {
        if(mTextureView==null){
            mTextureView = new TextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    /**
     * 增加到容器中
     */
    private void addTextureView() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mTextureView,0,params);
    }


    private MediaPlayer.OnPreparedListener mOnPreparedListener=new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
           //准备好，开始播放
            mp.start();
            mCurrentState = STATE_PREPARED;
            mController.setControllerState(mPlayerState,mCurrentState);
            LogUtil.d("onPrepared ——> STATE_PREPARED");
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            LogUtil.d("onVideoSizeChanged ——> width：" + width + "，height：" + height);
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_COMPLETED;
            mController.setControllerState(mPlayerState,mCurrentState);
            LogUtil.d("onCompletion ——> STATE_COMPLETED");
        }
    };
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mCurrentState = STATE_ERROR;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("onError ——> STATE_ERROR ———— what：" + what);
            return false;
        }
    };
    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if(what== MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                //播放器渲染第一帧
                mCurrentState = STATE_PLAYING;
                mController.setControllerState(mPlayerState,mCurrentState);
                LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
            }else if(what==MediaPlayer.MEDIA_INFO_BUFFERING_START){
                //播放器暂时不播放，以缓冲更多的数据
                if(mCurrentState==STATE_PAUSED||mCurrentState==STATE_BUFFERING_PAUSED){
                    mCurrentState = STATE_BUFFERING_PAUSED;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                }else{
                    mCurrentState = STATE_BUFFERING_PLAYING;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
                mController.setControllerState(mPlayerState,mCurrentState);
            }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    mController.setControllerState(mPlayerState, mCurrentState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED;
                    mController.setControllerState(mPlayerState, mCurrentState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
                }
            }else{
                LogUtil.d("onInfo ——> what：" + what);
            }
            return true;     //返回true,表示该方法处理了mediaplayer的 info消息
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        //缓冲进度
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mBufferPercentage = percent;
        }
    };


    /**
     * surfaceTexture 的回掉方法
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //SurfaceTexture数据通道准备好，打开播放器
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            openMediaPlayer();
        } else {

            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }


    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void openMediaPlayer() {
        try {
            mMediaPlayer.setDataSource( mUrl);
            mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            mController.setControllerState(mPlayerState, mCurrentState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterFullScreen() {
        if (mPlayerState == PLAYER_FULL_SCREEN) return;

        // 隐藏ActionBar、状态栏，并横屏
        NiceUtil.hideActionBar(mContext);
        NiceUtil.scanForActivity(mContext)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.removeView(mContainer);

        ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(mContext)
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        contentView.addView(mContainer, params);

        mPlayerState = PLAYER_FULL_SCREEN;
        mController.setControllerState(mPlayerState, mCurrentState);
        LogUtil.d("PLAYER_FULL_SCREEN");
    }

    public boolean exitFullScreen() {
        if (mPlayerState == PLAYER_FULL_SCREEN) {
            NiceUtil.showActionBar(mContext);
            NiceUtil.scanForActivity(mContext)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mPlayerState = PLAYER_NORMAL;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("PLAYER_NORMAL");
            return true;
        }
        return false;
    }

    public void enterTinyWindow() {
        if (mPlayerState == PLAYER_TINY_WINDOW) return;

        this.removeView(mContainer);

        ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(mContext)
                .findViewById(android.R.id.content);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) (NiceUtil.getScreenWidth(mContext) * 0.6f),
                (int) (NiceUtil.getScreenWidth(mContext) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = NiceUtil.dp2px(mContext, 8f);
        params.bottomMargin = NiceUtil.dp2px(mContext, 8f);

        contentView.addView(mContainer, params);

        mPlayerState = PLAYER_TINY_WINDOW;
        mController.setControllerState(mPlayerState, mCurrentState);
        LogUtil.d("PLAYER_TINY_WINDOW");
    }


    public boolean exitTinyWindow() {
        if (mPlayerState == PLAYER_TINY_WINDOW) {
            ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mPlayerState = PLAYER_NORMAL;
            mController.setControllerState(mPlayerState, mCurrentState);
            LogUtil.d("PLAYER_NORMAL");
            return true;
        }
        return false;
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mContainer.removeView(mTextureView);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mController != null) {
            mController.reset();
        }
        mCurrentState = STATE_IDLE;
        mPlayerState = PLAYER_NORMAL;
    }


    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }


    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }


    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }


    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }


    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }


    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }


    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }


    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }


    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }


    public boolean isFullScreen() {
        return mPlayerState == PLAYER_FULL_SCREEN;
    }


    public boolean isTinyWindow() {
        return mPlayerState == PLAYER_TINY_WINDOW;
    }


    public boolean isNormal() {
        return mPlayerState == PLAYER_NORMAL;
    }


    public long getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }


    public long getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }


    public int getBufferPercentage() {
        return mBufferPercentage;
    }
}
