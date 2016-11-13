package appcorp.mmb.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Intermediates {

    private static Intermediates instance = new Intermediates();

    private Intermediates() {
    }

    public static Intermediates getInstance() {
        return instance;
    }

    @SuppressWarnings("deprecation")
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            FireAnal.sendString("Wi-Fi network connection", "System", "Application");
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            FireAnal.sendString("Mobile network connection", "System", "Application");
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }

    public static String convertToString(Context context, int r) {
        TextView textView = new TextView(context);
        textView.setText(r);
        return textView.getText().toString();
    }

    public static String encodeToURL(String inString) {
        String outString = "";
        try {
            outString = URLEncoder.encode(inString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return outString;
    }
}