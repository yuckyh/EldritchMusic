package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.User;

public class UserRegistry extends Registry<User> {
    private static final UserRegistry INSTANCE = new UserRegistry();

    public static UserRegistry getInstance() {
        return INSTANCE;
    }

    public UserRegistry syncFromDb() {
        super.syncFromDb("users", User.class);
        return INSTANCE;
    }
}
