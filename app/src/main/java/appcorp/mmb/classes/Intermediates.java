package appcorp.mmb.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

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

    public static String convertToString(Context context, int r) {
        TextView textView = new TextView(context);
        textView.setText(r);
        return textView.getText().toString();
    }
}