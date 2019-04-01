package com.juzix.wallet.engine;

public class UpdateVersionManager {

    private UpdateVersionManager() {

    }

    public static UpdateVersionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public String getVersion(){
        return "";
    }


    private static class InstanceHolder {
        private static volatile UpdateVersionManager INSTANCE = new UpdateVersionManager();
    }
}
