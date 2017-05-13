//package com.wuwo.im.config;
//
//import android.os.Handler;
//import android.os.Looper;
//
//import com.squareup.otto.Bus;
//
///**
// * 单例模式。对OttoBus进行了扩展，从而使得可以在程序中的任意线程中使用
// *
// * @author
// */
//public final class OneMapOttoBus extends Bus {
//    private final Handler mHandler = new Handler(Looper.getMainLooper());
//
//    @Override
//    public void post(final Object event) {
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            super.post(event);
//        } else {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    OneMapOttoBus.super.post(event);
//                }
//            });
//        }
//    }
//
//    private static final Bus BUS = new OneMapOttoBus();
//
//    /**
//     * 返回OttoBus单例
//     *
//     * @return OttoBus单例
//     */
//    public static Bus getInstance() {
//        return BUS;
//    }
//
//    /**
//     * 单例模式，使用私有构造
//     */
//    private OneMapOttoBus() {
//        // leave empty
//    }
//}
