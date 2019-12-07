package com.kingbird.library.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.kingbird.library.base.Base;
import com.kingbird.library.manager.ExecutorServiceManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Glide 工具
 *
 * @author panyingdao
 * @date 2018/5/04.
 */
public class GlideUtil {

    public static void load(Activity activity, String url,
                            RequestOptions options, ImageView imageView) {
        Glide.with(activity).asBitmap().load(url).apply(options).into(imageView);
    }

    public static void qrLoad(Activity activity, String url,
                              RequestOptions options, ImageView imageView) {
        Glide.with(activity).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Plog.e("加载失败", target);
                String controlName = "ZXing", controlName2 = "Tissue",controlName3 = "AppLogo";
                int targetLength = target.toString().length();
                String targetName = target.toString().substring(target.toString().lastIndexOf("/") + 1, targetLength - 1);
                Plog.e("加载失败的控件名称：", targetName);
                if (controlName.equals(targetName)) {
                    Base.intentActivity("103");
                    Base.intentActivity("14");
                } else if (controlName2.equals(targetName)) {
                    ExecutorServiceManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            Base.intentActivity("15");
                        }
                    }, 1, TimeUnit.SECONDS);
                }else if (controlName3.equals(targetName)){
                    ExecutorServiceManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            Base.intentActivity("25");
                        }
                    }, 3, TimeUnit.SECONDS);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                int targetLength = target.toString().length();
                try {
                    String targetName = target.toString().substring(target.toString().lastIndexOf("/") + 1, targetLength - 1);
                    Plog.e("加载成功控件名称：", targetName);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Plog.e("异常原因", e.toString());
                }
                return false;
            }
        }).apply(options).into(imageView);
    }

    public static void qrLoad2(Activity activity, Bitmap bitmap,
                               RequestOptions options, ImageView imageView) {
        Glide.with(activity).asBitmap().load(bitmap).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Plog.e("加载失败", target);
                String controlName = "ZXing", controlName2 = "Tissue";
                int targetLength = target.toString().length();
                String targetName = target.toString().substring(target.toString().lastIndexOf("/") + 1, targetLength - 1);
                Plog.e("加载失败的控件名称：", targetName);
                if (controlName.equals(targetName)) {
                    Base.intentActivity("103");
                    Base.intentActivity("14");
                } else if (controlName2.equals(targetName)) {
                    ExecutorServiceManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            Base.intentActivity("15");
                        }
                    }, 1, TimeUnit.SECONDS);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                int targetLength = target.toString().length();
                try {
                    String targetName = target.toString().substring(target.toString().lastIndexOf("/") + 1, targetLength - 1);
                    Plog.e("加载成功控件名称：", targetName);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Plog.e("异常原因", e.toString());
                }
                return false;
            }
        }).apply(options).into(imageView);
    }

    public static void load(Context context, Drawable drawable,
                            RequestOptions options, ImageView imageView) {
        Glide.with(context).asBitmap().load(drawable).apply(options).into(imageView);
    }

    @SuppressLint("CheckResult")
    public static void loadPlay(Context context, File file,
                                RequestOptions options, int drawable, ImageView imageView) {
//        Glide.with(context).load(file).transition(new DrawableTransitionOptions().crossFade(500)).apply(options).into(imageView);
//        Glide.with(context).load(file).transition(new GenericTransitionOptions<Drawable>()).apply(options).into(imageView);
//        Glide.with(context).load(file).transition(new DrawableTransitionOptions().crossFade(500)).apply(options).into(imageView);
//        Glide.with(context).asBitmap().transition(new BitmapTransitionOptions().crossFade(1000)).load(file).apply(options).into(imageView);
        //默认动画时间是300毫秒
//        Glide.with(context).load(file).transition((DrawableTransitionOptions.withCrossFade(500))).apply(options).into(imageView);
        DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(1300).setCrossFadeEnabled(true).build();
        options.placeholder(drawable);
        Glide.with(context)
                .load(file)
                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .apply(options)
                .into(imageView);
    }

    public static void loadPlay3(Activity activity, File file,
                                 RequestOptions options, final ImageView imageView) {

        Glide.with(activity).load(file).transition(new DrawableTransitionOptions().crossFade(500)).apply(options).into(imageView);
    }

    public static void loadPlay2(Context context, File file, ImageView imageView) {
        Glide.with(context).load(file).transition(new DrawableTransitionOptions().crossFade(1300)).into(imageView);
    }

    public static void loadGif(Context context, File file,
                               RequestOptions options, ImageView imageView) {
        Glide.with(context).asGif().load(file).apply(options).into(imageView);
    }

    public static void load(Context context, Uri uri,
                            RequestOptions options, ImageView imageView) {
        Glide.with(context).asBitmap().load(uri).apply(options).into(imageView);
    }

    public static void load(Activity activity, Integer resourceId,
                            RequestOptions options, ImageView imageView) {
        try {
            Glide.with(activity).asBitmap().load(resourceId).apply(options).into(imageView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void glieClear(Activity activity, ImageView imageView) {
        Glide.with(activity).clear(imageView);
    }

    public static void glieClear2(Context context, ImageViewTarget imageView) {
        Glide.with(context).clear(imageView);
    }

}
