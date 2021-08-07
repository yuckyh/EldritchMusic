package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.User;

public class UserRegistry extends Registry<User> {
    private static final UserRegistry INSTANCE = new UserRegistry();

    protected UserRegistry() {
        super("users");
    }

    public static UserRegistry getInstance() {
        return INSTANCE;
    }
}
