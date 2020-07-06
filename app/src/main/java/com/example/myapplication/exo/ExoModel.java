package com.example.myapplication.exo;

import com.shuyu.gsyvideoplayer.model.GSYModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExoModel extends GSYModel {
    List<String> urls = new ArrayList<>();
    int index;
    public ExoModel(List<String> url, Map<String, String> mapHeadData,int index, boolean loop, float speed, boolean isCache, File cachePath, String overrideExtension) {
        super("", mapHeadData, loop, speed, isCache, cachePath, overrideExtension);
        this.urls = url;
        this.index = index;
    }

    public List<String> getUrls(){return urls;}

    public void setUrls(List<String> urls) {this.urls = urls;}
}
