package com.example.purchasenoti.utilities;

import android.content.Context;
import android.util.TypedValue;

public class UiUtils {

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
