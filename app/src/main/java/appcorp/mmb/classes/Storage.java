package appcorp.mmb.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {

    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context = null;

    public static void init(Context ctx) {
        context = ctx;
    }

    private static void init() {
        String STORAGE_NAME = "MMB_STORAGE";
        sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void addString(String key, String value) {
        if (sharedPreferences == null)
            init();
        editor.putString(key, value);
        editor.commit();
    }

    public static void addInt(String key, int value) {
        if (sharedPreferences == null)
            init();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getString(String key, String defaultValue) {
        if (sharedPreferences == null)
            init();
        return sharedPreferences.getString(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        if (sharedPreferences == null)
            init();
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void clearString(String key) {
        if (sharedPreferences == null)
            init();
        editor.remove(key);
        editor.commit();
    }
}