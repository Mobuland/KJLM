package com.example.myapplication.exo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.widget.NestedScrollView;


import com.example.myapplication.R;
import com.shuyu.gsyvideoplayer.GSYBaseADActivityDetail;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.video.GSYADVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;


public class ExoListPlayer extends GSYBaseADActivityDetail {

    NestedScrollView postDetailScroll;
    ExoPlayerView exoplayer;
    RelativeLayout relativeLayoutPlayer;
    View next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exo_list_player);

        postDetailScroll = findViewById(R.id.post_detail_nested_scroll);

        exoplayer = findViewById(R.id.detail_player);

        relativeLayoutPlayer = findViewById(R.id.activity_detail_player);

        next = findViewById(R.id.next);

        //初始化
        initVideo();

        //加载数据List

        List<GSYVideoModel> urls = new ArrayList<>();
        urls.add(new GSYVideoModel("http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4", "标题1"));
        urls.add(new GSYVideoModel("https://media6.smartstudy.com/ae/07/3997/2/dest.m3u8", "标题3"));
        urls.add(new GSYVideoModel("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4", "标题2"));

        //给播放器设置数据
        exoplayer.setUp(urls,0);

        //增加封面 防止还没加载出来的时候白屏
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //todo： 找图片封面
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        exoplayer.setThumbImageView(imageView);

        exoplayer.setIsTouchWiget(true);
        //关闭自动旋转
        exoplayer.setRotateViewAuto(false);
        exoplayer.setLockLand(false);
        exoplayer.setShowFullAnimation(false);
        exoplayer.setNeedLockFull(true);

        exoplayer.setVideoAllCallBack(this);

        exoplayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExoVideoManager.instance().next();
                ((ExoPlayerView)exoplayer.getCurrentPlayer()).nextUI();
            }
        });


    }


    @Override
    public GSYADVideoPlayer getGSYADVideoPlayer() {
        return null;
    }

    @Override
    public GSYVideoOptionBuilder getGSYADVideoOptionBuilder() {
        return null;
    }

    @Override
    public boolean isNeedAdOnStart() {
        return false;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * 重载为GSYExoVideoManager的方法处理
     */
    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (ExoVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    public ExoPlayerView getGSYVideoPlayer() {
        return exoplayer;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        //不用builder的模式
        return null;
    }

    @Override
    public void clickForFullScreen() {
    }

    /**
     * 是否启动旋转横屏，true表示启动
     * @return true
     */
    @Override
    public boolean getDetailOrientationRotateAuto() {
        return true;
    }

    @Override
    public void onEnterFullscreen(String url, Object... objects) {
        super.onEnterFullscreen(url, objects);
        //隐藏调全屏对象的返回按键
        GSYVideoPlayer gsyVideoPlayer = (GSYVideoPlayer)objects[1];
        gsyVideoPlayer.getBackButton().setVisibility(View.GONE);
    }


    private void resolveNormalVideoUI() {
        //增加title
        exoplayer.getTitleTextView().setVisibility(View.VISIBLE);
        exoplayer.getBackButton().setVisibility(View.VISIBLE);
    }

    private GSYVideoPlayer getCurPlay() {
        if (exoplayer.getFullWindowPlayer() != null) {
            return  exoplayer.getFullWindowPlayer();
        }
        return exoplayer;
    }

}
