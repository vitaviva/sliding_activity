
package vitaviva.com.slidingactivity.slidingactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import vitaviva.com.slidingactivity.R;


public abstract class SlidingActivity extends AppCompatActivity {
    private static final float MIN_SCALE = 0.85f;

    private View mPreview;
    private FrameLayout mContentView;

    private float mInitOffset;
    private boolean hideTitle = false;
    private int titleResId = -1;
    private SlidingLayout slideLayout;

    public static class SlideOut {
        public SlidingActivity activity;
        public SlideOut(SlidingActivity activity) {
            this.activity = activity;
        }
    }

    protected void setSlideEnabled(boolean enabled){
        slideLayout.setSlideable(enabled);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.slide_layout);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInitOffset = (1 - MIN_SCALE) * metrics.widthPixels / 2.f;

        mPreview = findViewById(R.id.iv_preview);
        mContentView = (FrameLayout) findViewById(R.id.content_view);

        // if (!hideTitle) {
        // int resId = -1 == titleResId ? R.layout.title_layout : titleResId;
        // inflater.inflate(resId, contentView);
        // }
        hideTitle = true;
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, Gravity.BOTTOM);
        final int marginTop = hideTitle ? 0 : (int) (metrics.density * 48.f + .5f);
        layoutParams.setMargins(0, marginTop, 0, 0);
        mContentView.addView(inflater.inflate(layoutResID, null), layoutParams);

        slideLayout = (SlidingLayout) findViewById(R.id.slide_layout);
        slideLayout.setShadowResource(R.drawable.sliding_back_shadow);
        slideLayout.setSliderFadeColor(0x00000000);
        slideLayout.setPanelSlideListener(new SlidingLayout.SimpleSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                final int sdkInt = Build.VERSION.SDK_INT;

                if (slideOffset <= 0) {
                    mPreview.setScaleX(MIN_SCALE);
                    mPreview.setScaleY(MIN_SCALE);
                } else if (slideOffset < 1) {
                    // Scale the page down (between MIN_SCALE and 1)
                    float scaleFactor = MIN_SCALE + Math.abs(slideOffset) * (1 - MIN_SCALE);

                    mPreview.setAlpha(slideOffset);
                    mPreview.setTranslationX(mInitOffset * (1 - slideOffset));
                    mPreview.setScaleX(scaleFactor);
                    mPreview.setScaleY(scaleFactor);
                } else {
                    mPreview.setScaleX(1);
                    mPreview.setScaleY(1);
                    mPreview.setAlpha(1);
                    mPreview.setTranslationX(0);

                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });

        byte[] byteArray = getIntent().getByteArrayExtra(IntentUtils.KEY_PREVIEW_IMAGE);
        if (null != byteArray && byteArray.length > 0) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inPurgeable = true;
            opts.inSampleSize = 2;
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);
            if (null != bmp) {
                ((ImageView) mPreview).setImageBitmap(bmp);
                mPreview.setScaleX(MIN_SCALE);
                mPreview.setScaleY(MIN_SCALE);
            } else {
                /** preview image captured fail, disable the slide back */
                slideLayout.setSlideable(false);
            }
        } else {
            /** preview image captured fail, disable the slide back */
            slideLayout.setSlideable(false);
        }

    }

    protected void setContentView(int layoutResID, int titleResId) {
        this.titleResId = titleResId;
        setContentView(layoutResID);
    }

    protected void setContentView(int layoutResID, boolean hideTitle) {
        this.hideTitle = hideTitle;
        setContentView(layoutResID);
    }

    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null, 0);

    }

    public void openActivityForResult(Class<?> pClass, int requestCode) {
        openActivity(pClass, null, requestCode);
    }

    public void openActivity(Class<?> pClass, Bundle pBundle, int requestCode) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT < 16) {
                startActivity(intent);
            } else {
                IntentUtils.startPreviewActivity(this, intent, 0);
            }
        } else {
            if (Build.VERSION.SDK_INT < 16) {
                startActivityForResult(intent, requestCode);
            } else {
                IntentUtils.startPreviewActivity(this, intent, requestCode);
            }
        }
    }


    /**
     * 带动画效果的关闭
     */
    @Override
    public void finish() {
        super.finish();
        if (slideLayout != null && slideLayout.isSlideable()) {
            IntentUtils.slidingPreviewPop();
        }
    }

    /**
     * 系统默认关闭
     */
    public void defaultFinish() {
        super.finish();
    }

    /**
     * 系统默认关闭
     */
    public void defaultFinishNotActivityHelper() {
        super.finish();
    }

    /**
     * 返回
     */
    public void doBack(View view) {
        onBackPressed();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
