package com.example.financialapp;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LimitWarningHelper {

    private static final String PREF_NAME = "limit_warn_pref";

    public static boolean shouldShow(Context ctx, String category) {
        SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String lastShown = pref.getString(category, "");
        String today = new SimpleDateFormat("dd-MM-yyyy")
                .format(Calendar.getInstance().getTime());

        return !today.equals(lastShown);
    }

    public static void setShown(Context ctx, String category) {
        SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String today = new SimpleDateFormat("dd-MM-yyyy")
                .format(Calendar.getInstance().getTime());

        pref.edit().putString(category, today).apply();
    }
}
