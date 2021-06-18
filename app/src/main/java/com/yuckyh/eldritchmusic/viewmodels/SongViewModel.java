package com.yuckyh.eldritchmusic.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yuckyh.eldritchmusic.models.Song;

import java.util.ArrayList;

public class SongViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Song>> mListLiveData;

    public MutableLiveData<ArrayList<Song>> getListLiveData() {
        if (mListLiveData == null) {
            mListLiveData = new MutableLiveData<ArrayList<Song>>() {
            };
        }
        return mListLiveData;
    }
}
