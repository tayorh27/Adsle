package com.ad.adsle.Util;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import com.ad.adsle.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

public class GifViewUtils {
    static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    static final List<String> SUPPORTED_RESOURCE_TYPE_NAMES = Arrays.asList("raw", "drawable", "mipmap");

    public GifViewUtils() {
    }

    public static InitResult initImageView(ImageView view, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null && !view.isInEditMode()) {
            final int sourceResId = getResourceId(view, attrs, true);
            final int backgroundResId = getResourceId(view, attrs, false);
            final boolean freezesAnimation = isFreezingAnimation(view, attrs, defStyleAttr, defStyleRes);
            return new InitResult(sourceResId, backgroundResId, freezesAnimation);
        }
        return new InitResult(0, 0, false);
    }

    public static int getResourceId(ImageView view, AttributeSet attrs, final boolean isSrc) {
        final int resId = attrs.getAttributeResourceValue(ANDROID_NS, isSrc ? "src" : "background", 0);
        if (resId > 0) {
            final String resourceTypeName = view.getResources().getResourceTypeName(resId);
            if (SUPPORTED_RESOURCE_TYPE_NAMES.contains(resourceTypeName) && !setResource(view, isSrc, resId)) {
                return resId;
            }
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    public static boolean setResource(ImageView view, boolean isSrc, int resId) {
        Resources res = view.getResources();
        if (res != null) {
            try {
                GifDrawable d = new GifDrawable(res, resId);
                if (isSrc) {
                    view.setImageDrawable(d);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(d);
                } else {
                    view.setBackgroundDrawable(d);
                }
                return true;
            } catch (IOException | Resources.NotFoundException ignored) {
                //ignored
            }
        }
        return false;
    }

    public static boolean isFreezingAnimation(View view, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray gifViewAttributes = view.getContext().obtainStyledAttributes(attrs, R.styleable.GifView, defStyleAttr, defStyleRes);
        boolean freezesAnimation = gifViewAttributes.getBoolean(R.styleable.GifView_freezesAnimation, false);
        gifViewAttributes.recycle();
        return freezesAnimation;
    }

    public static boolean setGifImageUri(ImageView imageView, Uri uri) {
        if (uri != null) {
            try {
                imageView.setImageDrawable(new GifDrawable(imageView.getContext().getContentResolver(), uri));
                return true;
            } catch (IOException ignored) {
                //ignored
            }
        }
        return false;
    }

    public static float getDensityScale(@NonNull Resources res, @DrawableRes @RawRes int id) {
        final TypedValue value = new TypedValue();
        res.getValue(id, value, true);
        final int resourceDensity = value.density;
        final int density;
        if (resourceDensity == TypedValue.DENSITY_DEFAULT) {
            density = DisplayMetrics.DENSITY_DEFAULT;
        } else if (resourceDensity != TypedValue.DENSITY_NONE) {
            density = resourceDensity;
        } else {
            density = 0;
        }
        final int targetDensity = res.getDisplayMetrics().densityDpi;

        if (density > 0 && targetDensity > 0) {
            return (float) targetDensity / density;
        }
        return 1f;
    }

    public static class InitResult {
        public int mSourceResId;
        public int mBackgroundResId;
        public boolean mFreezesAnimation;

        public InitResult(int sourceResId, int backgroundResId, boolean freezesAnimation) {

            mSourceResId = sourceResId;
            mBackgroundResId = backgroundResId;
            mFreezesAnimation = freezesAnimation;
        }
    }
}
