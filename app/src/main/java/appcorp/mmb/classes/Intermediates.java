package appcorp.mmb.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Intermediates {

    private static Intermediates instance = new Intermediates();

    private Intermediates() {

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

    public static String encodeToURL(String inString) {
        String outString = "";
        try {
            outString = URLEncoder.encode(inString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return outString;
    }

    /*public static String convertRuToEn(String inString) {
        String outString = "";
        inString = inString.toLowerCase();
        inString = inString.replace("а", "_a_");
        inString = inString.replace("б", "_b_");
        inString = inString.replace("в", "_v_");
        inString = inString.replace("г", "_g_");
        inString = inString.replace("д", "_d_");
        inString = inString.replace("е", "_e_");
        inString = inString.replace("ё", "_yo_");
        inString = inString.replace("ж", "_zh_");
        inString = inString.replace("з", "_z_");
        inString = inString.replace("и", "_i_");
        inString = inString.replace("й", "_ii_");
        inString = inString.replace("к", "_k_");
        inString = inString.replace("л", "_l_");
        inString = inString.replace("м", "_m_");
        inString = inString.replace("н", "_n_");
        inString = inString.replace("о", "_o_");
        inString = inString.replace("п", "_p_");
        inString = inString.replace("р", "_r_");
        inString = inString.replace("с", "_s_");
        inString = inString.replace("т", "_t_");
        inString = inString.replace("у", "_u_");
        inString = inString.replace("ф", "_f_");
        inString = inString.replace("х", "_kh_");
        inString = inString.replace("ц", "_tc_");
        inString = inString.replace("ч", "_ch_");
        inString = inString.replace("ш", "_sh_");
        inString = inString.replace("щ", "_shch_");
        inString = inString.replace("ь", "_bbb_");
        inString = inString.replace("ы", "_y_");
        inString = inString.replace("ъ", "_bbbb_");
        inString = inString.replace("э", "_ye_");
        inString = inString.replace("ю", "_yu_");
        inString = inString.replace("я", "_ya_");
        outString = inString;
        return outString;
    }

    public static String convertEnToRu(String inString) {
        String outString = "";
        inString = inString.toLowerCase();
        inString = inString.replace("_a_", "а");
        inString = inString.replace("_b_", "б");
        inString = inString.replace("_v_", "в");
        inString = inString.replace("_g_", "г");
        inString = inString.replace("_d_", "д");
        inString = inString.replace("_e_", "е");
        inString = inString.replace("_yo_", "ё");
        inString = inString.replace("_zh_", "ж");
        inString = inString.replace("_z_", "з");
        inString = inString.replace("_i_", "и");
        inString = inString.replace("_ii_", "й");
        inString = inString.replace("_k_", "к");
        inString = inString.replace("_l_", "л");
        inString = inString.replace("_m_", "м");
        inString = inString.replace("_n_", "н");
        inString = inString.replace("_o_", "о");
        inString = inString.replace("_p_", "п");
        inString = inString.replace("_r_", "р");
        inString = inString.replace("_s_", "с");
        inString = inString.replace("_t_", "т");
        inString = inString.replace("_u_", "у");
        inString = inString.replace("_f_", "ф");
        inString = inString.replace("_kh_", "х");
        inString = inString.replace("_tc_", "ц");
        inString = inString.replace("_ch_", "ч");
        inString = inString.replace("_sh_", "ш");
        inString = inString.replace("_shch_", "щ");
        inString = inString.replace("_bbb_", "ь");
        inString = inString.replace("_y_", "ы");
        inString = inString.replace("_bbbb_", "ъ");
        inString = inString.replace("_ye_", "э");
        inString = inString.replace("_yu_", "ю");
        inString = inString.replace("_ya_", "я");
        outString = inString;
        return outString;
    }*/
}