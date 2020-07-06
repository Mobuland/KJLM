package com.example.myapplication.exo;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import java.io.FileDescriptor;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer;
import tv.danmaku.ijk.media.exo2.demo.EventLogger;

public class ExoMediaPlayer extends IjkExo2MediaPlayer {
    private static final String TAG  = "ExoMediaPlayer";

    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;

    private final Timeline.Window window = new Timeline.Window();

    public static final int POSITION_DISCONTINUITY = 899;

    private int playIndex  = 0;

    public ExoMediaPlayer(Context context) {
        super(context);
    }

    @Override
    @Deprecated
    public void setDataSource(Context context, Uri uri) {
        throw new UnsupportedOperationException("Deprecated, try setDataSource(List<String> uris, Map<String, String> headers)");
    }

    @Override
    @Deprecated
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) {
        throw new UnsupportedOperationException("Deprecated, try setDataSource(List<String> uris, Map<String, String> headers)");
    }

    @Override
    @Deprecated
    public void setDataSource(String path) {
        throw new UnsupportedOperationException("Deprecated, try setDataSource(List<String> uris, Map<String, String> headers)");
    }

    @Override
    @Deprecated
    public void setDataSource(FileDescriptor fd) {
        throw new UnsupportedOperationException("Deprecated, try setDataSource(List<String> uris, Map<String, String> headers)");
    }

    public void setDataSource(List<String> uris,Map<String,String>headers,int index,boolean cache){
        mHeaders = headers;
        if(uris == null){
            return;
        }
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        for(String uri : uris){
            MediaSource mediaSource = mExoHelper.getMediaSource(uri,isPreview,cache,false,mCacheDir,getOverrideExtension());
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        playIndex = index;
        mMediaSource = concatenatingMediaSource;
    }

    /**
     * 上一集 测试用
     */
    public void previous(){
        if(mInternalPlayer == null){
            return;
        }
        Timeline timeline = mInternalPlayer.getCurrentTimeline();
        if(timeline.isEmpty())return;
        int windowIndex = mInternalPlayer.getCurrentWindowIndex();
        timeline.getWindow(windowIndex,window);
        int previousWindowIndex = mInternalPlayer.getPreviousWindowIndex();
        if(previousWindowIndex != C.INDEX_UNSET
              &&(mInternalPlayer.getCurrentPosition() <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS
              ||(window.isDynamic && !window.isSeekable))){
            mInternalPlayer.seekTo(previousWindowIndex,C.TIME_UNSET);
        } else {
            mInternalPlayer.seekTo(0);
        }
    }

    @Override
    protected void prepareAsyncInternal() {
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        if (mTrackSelector == null) {
                            mTrackSelector = new DefaultTrackSelector();
                        }
                        mEventLogger = new EventLogger(mTrackSelector);
                        boolean preferExtensionDecoders = true;
                        boolean useExtensionRenderers = true;//是否开启扩展
                        @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode = useExtensionRenderers
                                ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
                        if (mRendererFactory == null) {
                            mRendererFactory = new DefaultRenderersFactory(mAppContext);
                            mRendererFactory.setExtensionRendererMode(extensionRendererMode);
                        }
                        if (mLoadControl == null) {
                            mLoadControl = new DefaultLoadControl();
                        }
                        mInternalPlayer = ExoPlayerFactory.newSimpleInstance(mAppContext, mRendererFactory, mTrackSelector, mLoadControl, null, Looper.getMainLooper());
                        mInternalPlayer.addListener(ExoMediaPlayer.this);
                        mInternalPlayer.addAnalyticsListener(ExoMediaPlayer.this);
                        mInternalPlayer.addListener(mEventLogger);
                        if (mSpeedPlaybackParameters != null) {
                            mInternalPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
                        }
                        if (mSurface != null)
                            mInternalPlayer.setVideoSurface(mSurface);
                        ///fix start index
                        if (playIndex > 0) {
                            mInternalPlayer.seekTo(playIndex, C.INDEX_UNSET);
                        }
                        mInternalPlayer.prepare(mMediaSource, false, false);
                        mInternalPlayer.setPlayWhenReady(false);
                    }
                }
        );
    }

    /**
     * 下一集
     */
    public void next() {
        if (mInternalPlayer == null) {
            return;
        }
        Timeline timeline = mInternalPlayer.getCurrentTimeline();
        if (timeline.isEmpty()) {
            return;
        }
        int windowIndex = mInternalPlayer.getCurrentWindowIndex();
        int nextWindowIndex = mInternalPlayer.getNextWindowIndex();
        if (nextWindowIndex != C.INDEX_UNSET) {
            mInternalPlayer.seekTo(nextWindowIndex, C.TIME_UNSET);
        } else if (timeline.getWindow(windowIndex, window, false).isDynamic) {
            mInternalPlayer.seekTo(windowIndex, C.TIME_UNSET);
        }
    }

    public int getCurrentWindowIndex() {
        if (mInternalPlayer == null) {
            return 0;
        }
        return mInternalPlayer.getCurrentWindowIndex();
    }
}