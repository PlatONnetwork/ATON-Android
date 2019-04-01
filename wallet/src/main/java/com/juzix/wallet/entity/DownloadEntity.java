package com.juzix.wallet.entity;

import java.io.File;

public class DownloadEntity {
    private File      file;
    private String    fileName;
    private long      fileSize;//单位 byte
    private long      currentSize;
    private int       progress;
    private long       speed;
    private Throwable errorMsg;//下载异常信息


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public Throwable getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(Throwable errorMsg) {
        this.errorMsg = errorMsg;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
