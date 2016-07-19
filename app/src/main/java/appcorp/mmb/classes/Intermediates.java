package appcorp.mmb.classes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

import java.text.DateFormatSymbols;

import appcorp.mmb.loaders.Storage;

public class Intermediates {

    private static Intermediates instance = new Intermediates();

    private Intermediates() {

    }

    public static class URL {
        private static final String HOST = "http://195.88.209.17:8080/";
        public static final String GET_FEED = HOST + "mmbserver/feed";
        public static final String GET_CALENDAR_EVENT = HOST + "mmbserver/calendar";
        public static final String GET_GET_COMMENT = HOST + "mmbserver/comments";
        public static final String GET_FAVORITE = HOST + "mmbserver/favorites";
        public static final String GET_PROFILES = HOST + "mmbserver/profiles";
        public static final String GET_PROFILE = HOST + "mmbserver/profile";
        public static final String GET_PUBLICATION = HOST + "mmbserver/publication";
        public static final String GET_SANDBOX = HOST + "mmbserver/sandbox";
    }

    public static Intermediates getInstance() {
        return instance;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
            return true;
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
            return true;
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
            return true;
        return false;
    }

    public static String getData(Context context, String key) {
        Storage.Init(context);
        return Storage.Get(key);
    }

    public static String calculateAvailableTime(long temp, long current) {
        String date = "";
        if ((current - temp) < 3600000)
            date = " Только что";
        else if ((current - temp) > 3600000 && (current - temp) <= 7200000)
            date = " 1 час назад";
        else if ((current - temp) > 7200000 && (current - temp) <= 10800000)
            date = " 2 часа назад";
        else if ((current - temp) > 10800000 && (current - temp) <= 14400000)
            date = " 3 часа назад";
        else if ((current - temp) > 14400000 && (current - temp) <= 18000000)
            date = " 4 часа назад";
        else if ((current - temp) > 18000000 && (current - temp) <= 21600000)
            date = " 5 часов назад";
        else if ((current - temp) > 21600000 && (current - temp) <= 25200000)
            date = " 6 часов назад";
        else if ((current - temp) > 25200000 && (current - temp) <= 28800000)
            date = " 7 часов назад";
        else if ((current - temp) > 28800000 && (current - temp) <= 32400000)
            date = " 8 часов назад";
        else if ((current - temp) > 32400000 && (current - temp) <= 36000000)
            date = " 9 часов назад";
        else if ((current - temp) > 36000000 && (current - temp) <= 39600000)
            date = " 10 часов назад";
        else if ((current - temp) > 39600000 && (current - temp) <= 43200000)
            date = " 11 часов назад";
        else if ((current - temp) > 43200000 && (current - temp) <= 46800000)
            date = " 12 часов назад";
        else if ((current - temp) > 46800000 && (current - temp) <= 50400000)
            date = " 13 часов назад";
        else if ((current - temp) > 50400000 && (current - temp) <= 54000000)
            date = " 14 часов назад";
        else if ((current - temp) > 54000000 && (current - temp) <= 57600000)
            date = " 15 часов назад";
        else if ((current - temp) > 57600000 && (current - temp) <= 61200000)
            date = " 16 часов назад";
        else if ((current - temp) > 61200000 && (current - temp) <= 64800000)
            date = " 17 часов назад";
        else if ((current - temp) > 64800000 && (current - temp) <= 68400000)
            date = " 18 часов назад";
        else if ((current - temp) > 68400000 && (current - temp) <= 72000000)
            date = " 19 часов назад";
        else if ((current - temp) > 72000000 && (current - temp) <= 75600000)
            date = " 20 часов назад";
        else if ((current - temp) > 75600000 && (current - temp) <= 79200000)
            date = " 21 час назад";
        else if ((current - temp) > 79200000 && (current - temp) <= 82800000)
            date = " 22 часа назад";
        else if ((current - temp) > 82800000 && (current - temp) <= 86400000)
            date = " 23 часа назад";
        else if ((current - temp) > 86400000 && (current - temp) <= 172800000)
            date = " 1 день назад";
        else if ((current - temp) > 172800000 && (current - temp) <= 259200000)
            date = " 2 дня назад";
        return date;
    }

    public static String convertToString(Context context, int r) {
        TextView textView = new TextView(context);
        textView.setText(r);
        return textView.getText().toString();
    }
}