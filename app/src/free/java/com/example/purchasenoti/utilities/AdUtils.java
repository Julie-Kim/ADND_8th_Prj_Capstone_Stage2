package com.example.purchasenoti.utilities;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AdUtils {
    private static final String TAG = AdUtils.class.getSimpleName();

    private static final int AD_VIEW_HEIGHT_DP = 60;

    public static void initMobileAds(Context context, AdView adView, ViewGroup viewGroup) {
        MobileAds.initialize(context, initializationStatus -> {
            Log.d(TAG, "MobileAds is initialized.");

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewGroup.getLayoutParams();

            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin,
                    layoutParams.bottomMargin + UiUtils.dpToPx(context, AD_VIEW_HEIGHT_DP));

            viewGroup.setLayoutParams(layoutParams);
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
