package com.juzix.wallet.config;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.juzhen.framework.fs.Directory;
import com.juzhen.framework.fs.DirectroyContext;
import com.juzhen.framework.util.TimeConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JZDirectroyContext extends DirectroyContext {

    public JZDirectroyContext(Context context) {
        super(context);
    }

    @Override
    protected String getRootPath() {

        String rootPath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            File fileDir = getExternalFilesDir(mContext);
            rootPath = fileDir.getAbsolutePath() + File.separator + JZDirType.root;
        } else {
            rootPath = mContext.getFilesDir().getPath() + File.separator + JZDirType.root;
        }

        return rootPath;
    }

    private File getExternalFilesDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File path = context.getExternalFilesDir(null);
            if (path != null) {
                return path;
            }
        }
        final String filesDir = "/Android/data/" + context.getPackageName() + "/files/";
        return new File(Environment.getExternalStorageDirectory().getPath() + filesDir);
    }

    @Override
    protected Collection<Directory> initDirectories() {
        List<Directory> children = new ArrayList<Directory>();
        //添加edge目录
        Directory dir = newDirectory(JZDirType.plat);
        children.add(dir);
        //添加log目录
        dir = newDirectory(JZDirType.log);
        children.add(dir);
        //添加crash目录
        dir = newDirectory(JZDirType.crash);
        children.add(dir);
        //添加cache目录
        dir = newDirectory(JZDirType.cache);
        children.add(dir);
        //添加raw目录
        dir = newDirectory(JZDirType.raw);
        children.add(dir);
        //添加user目录
        dir = newDirectory(JZDirType.user);
        children.add(dir);


//        //添加image目录
//        dir = newDirectory(JZDirType.image);
//        children.add(dir);
//
//        //image目录下添加thumb目录
//        dir.addChild(new Directory(JZDirType.image_thumb, null));

//        //添加raw目录
//        dir = newDirectory(JZDirType.wallet);
//        children.add(dir);

        return children;
    }

    private Directory newDirectory(String type) {
        Directory child = new Directory(type, null);
        child.setType(type);
        if (type.equals(JZDirType.cache)) {
            child.setForCache(true);
            child.setExpiredTime(TimeConstants.ONE_DAY_MS);
        }
        return child;
    }

}
