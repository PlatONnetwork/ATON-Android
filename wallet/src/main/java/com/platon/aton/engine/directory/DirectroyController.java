package com.platon.aton.engine.directory;

import android.Manifest;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.functions.Consumer;

public class DirectroyController {

   private DirectroyController(){ }

    private static class InstanceHolder {
        private static volatile DirectroyController INSTANCE = new DirectroyController();
    }

    public static DirectroyController getInstance(){
        return InstanceHolder.INSTANCE;
    }

   private DirectoryManager directoryManager;

    /**
     * 初始化文件目录
     * @param context
     */
   public void init(Context context){
        directoryManager = new DirectoryManager(new DirectroyContext(context));
   }

    public DirectoryManager getDirectoryManager(){
       return directoryManager;
   }

    public void getDir(final FragmentActivity activity, final String dirType, final DirCallback callback) {

        new RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            boolean dirStatue = getDirectoryManager().buildAndClean();
                            if (callback != null && dirStatue) {
                                callback.callback(getDirectoryManager().getDir(dirType));
                            }
                        } else {
                            if (callback != null) {
                                callback.callback(null);
                            }
                        }
                    }
                });
    }

    public interface DirCallback {
        void callback(File dir);
    }
}
