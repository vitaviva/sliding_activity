
package vitaviva.com.slidingactivity.slidingactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IntentUtils {

    public static final String TAG = IntentUtils.class.toString();
    private static final int SF_SLIDING_PRIVIEW_MAX = 10;
    private static volatile int S_SLIDING_PREVIEW_SIZE = 0;

    public static synchronized void slidingPreviewPop() {
        if (S_SLIDING_PREVIEW_SIZE > 0) {
            S_SLIDING_PREVIEW_SIZE--;
        }
    }

    public static synchronized boolean slidingPreviewPush() {
        if (S_SLIDING_PREVIEW_SIZE < SF_SLIDING_PRIVIEW_MAX) {
            S_SLIDING_PREVIEW_SIZE++;
            return true;
        }
        return false;
    }

    public static String KEY_PREVIEW_IMAGE = "preview_image";

    /**
     * start screen capture with no delay
     * 
     * @param context
     * @param intent
     */
    public static void startPreviewActivity(Context context, Intent intent, int requestCode) {
        startPreviewActivity(context, intent, 0, requestCode);
    }

    /**
     * start screen capture after "delay" milliseconds, so the previous
     * activity's state recover to normal state, such as button click, list item
     * click,wait them to normal state so we can make a good screen capture
     * 
     * @param context
     * @param intent
     * @param delay time in milliseconds
     */
    public static void startPreviewActivity(final Context context, final Intent intent, long delay,
            final int requestCode) {
        final Handler mainThread = new Handler(Looper.getMainLooper());
        final Runnable postAction = new Runnable() {
            @Override
            public void run() {
                if (requestCode == 0) {
                    context.startActivity(intent);
                } else {
                    ((Activity) context).startActivityForResult(intent, requestCode);
                }
            }
        };

        /** process screen capture on background thread */
        Runnable action = new Runnable() {
            @Override
            public void run() {
                if (slidingPreviewPush()) {
                    /**
                     * activity's root layout id, you can change the
                     * android.R.id.content to your root layout id
                     */
                    final ViewGroup contentView =  (ViewGroup)((Activity) context).findViewById(android.R.id.content);

                    ByteArrayOutputStream baos = null;
                    WeakReference<Bitmap> bitmap = null;

                    try {
//                        bitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(contentView.getWidth(),
//                                contentView.getHeight(), Bitmap.Config.ARGB_8888));
//                        contentView.draw(new Canvas(bitmap.get()));

                        bitmap = new WeakReference<Bitmap>(getViewBitmap(contentView));

                        if (bitmap != null && bitmap.get() != null) {
                            baos = new ByteArrayOutputStream();
                            bitmap.get().compress(Bitmap.CompressFormat.JPEG, 70, baos);
                            intent.putExtra(KEY_PREVIEW_IMAGE, baos.toByteArray());
                        }

                    } finally {
                        try {
                            /** no need to close, actually do nothing */
                            if (null != baos)
                                baos.close();
                            if (null != bitmap && null != bitmap.get() && !bitmap.get().isRecycled()) {
                                bitmap.get().recycle();
                                bitmap = null;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mainThread.post(postAction);
            }
        };

        if (delay > 0) {
            ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
            worker.schedule(action, delay, TimeUnit.MILLISECONDS);
        } else {
            action.run();
        }
    }

    /**
     * 暂时没发现取得上次cache的bitmap的现象
     */
    public static Bitmap getViewBitmap(View view) {
        view.clearFocus();
        view.setPressed(false);

        boolean willNotCache = view.willNotCacheDrawing();
        view.setWillNotCacheDrawing(false);
        int color = view.getDrawingCacheBackgroundColor();
        view.setDrawingCacheBackgroundColor(0xffffffff);
        if (color != 0) {
            view.destroyDrawingCache();
        }

        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        try {
            view.buildDrawingCache();
        } catch (NullPointerException e) {
            return null;
        }
        Bitmap bitmap = null;

        try {
            Bitmap cacheBitmap = view.getDrawingCache();
            if (cacheBitmap == null) {
                return null;
            }
            bitmap = Bitmap.createBitmap(cacheBitmap);
            cacheBitmap.recycle();
        }catch(OutOfMemoryError ex) {
            ex.printStackTrace();
        }finally {
            // Restore the view
            view.destroyDrawingCache();
            view.setWillNotCacheDrawing(willNotCache);
            view.setDrawingCacheBackgroundColor(color);
        }

        return bitmap;

    }
}
