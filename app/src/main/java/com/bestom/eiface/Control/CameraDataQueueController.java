package com.bestom.eiface.Control;

/**
 * 摄像头返回数据的 缓存，
 * 兼容 单目  双目 两种情况
 */
public class CameraDataQueueController {
    private static final String TAG = "CameraDataQueueController";

    private static final class Holder {
        private static final CameraDataQueueController INSTANCE = new CameraDataQueueController();
    }

    public static CameraDataQueueController getInstance() {
        return Holder.INSTANCE;
    }


    private YuvInfo infoColor = new YuvInfo();
    private YuvInfo infoFrared = new YuvInfo();


    public void putF(byte[] colorData, long time) {
        synchronized (this) {
            infoColor.setData(colorData, time);
        }
    }

    public byte[] getF(){
        return infoColor.data;
    }

    public void putB(byte[] fraredData, long time) {
        synchronized (this) {
            infoFrared.setData(fraredData, time);
        }
    }

    public byte[] getB(){
        return infoFrared.data;
    }

    public boolean getInfoColor(YuvInfo dstInfo) {
        synchronized (this) {
            dstInfo.isNews = infoColor.isNews;
            if (dstInfo.isNews) {
                dstInfo.time = infoColor.time;
                System.arraycopy(infoColor.data, 0, dstInfo.data, 0, dstInfo.data.length);
                infoColor.clear();
                return true;
            }
            return false;
        }
    }

    public byte[][] getDualCameraPreview(){
        synchronized (this){
            YuvInfo colorDstInfo = new YuvInfo();
            YuvInfo fraredDstInfo = new YuvInfo();
            if (getDualYuvInfo(colorDstInfo,fraredDstInfo)){
                return  new byte[][]{colorDstInfo.data,fraredDstInfo.data};
            } else {
                return  null;
            }
        }
    }

    public boolean getDualYuvInfo(YuvInfo colorDstInfo, YuvInfo fraredDstInfo) {
        synchronized (this) {
            if (isSameTime()) {
                //todo
                colorDstInfo.time = infoColor.time;
//                Log.i(TAG, "getDualYuvInfo:infoCLO "+infoColor.time);
                colorDstInfo.data = infoColor.data;
                infoColor.clear();

                fraredDstInfo.time = infoFrared.time;
//                Log.i(TAG, "getDualYuvInfo:infoIR  "+infoFrared.time);
                fraredDstInfo.data = infoFrared.data;
                infoFrared.clear();
                return true;
            }
            return false;
        }
    }

    private boolean isSameTime() {
        if (infoColor.isNews && infoFrared.isNews && Math.abs(infoColor.time - infoFrared.time) < 10) {
            return true;
        }
        return false;
    }

    //相机出来的yuv数据对象
    public static class YuvInfo {
        public final int width = CameraController.CAMERA_WIDTH;  // 存放的是YUV的源数据的宽
        public final int height = CameraController.CAMERA_HEIGHT; // 存放的是YUV的源数据的高
        public byte[] data; // 存放的是YUV的源数据
        public long time; //数据更新的时间
        public boolean isNews = false;//数据是否为空

        public void setData(byte[] srcData, long time) {
            this.time = time;
            this.data = srcData;
            isNews = true;
        }

        public void clear() {
            isNews = false;
        }
    }

}
