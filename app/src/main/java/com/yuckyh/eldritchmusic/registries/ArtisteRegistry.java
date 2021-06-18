package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.Artiste;

public class ArtisteRegistry extends Registry<Artiste> {
    private static final ArtisteRegistry INSTANCE = new ArtisteRegistry();

    public static ArtisteRegistry getInstance() {
        return INSTANCE;
    }

    public void syncFromDb() {
        super.syncFromDb("artistes", Artiste.class);
    }
}
