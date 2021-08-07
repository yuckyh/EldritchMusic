package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.Artiste;

public class ArtisteRegistry extends Registry<Artiste> {
    private static final ArtisteRegistry INSTANCE = new ArtisteRegistry();

    protected ArtisteRegistry() {
        super("artistes");
    }

    public static ArtisteRegistry getInstance() {
        return INSTANCE;
    }
}
