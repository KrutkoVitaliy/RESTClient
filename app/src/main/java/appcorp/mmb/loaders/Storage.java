package appcorp.mmb.loaders;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {

    public static final String STORAGE_NAME = "MMB_STORAGE";

    private static SharedPreferences _Settings = null;

    private static SharedPreferences.Editor _Editor = null;

    private static Context _Context = null;

    public static void Init(Context cntxt) {
        _Context = cntxt;
    }

    private static void Init() {
        _Settings = _Context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        _Editor = _Settings.edit();
    }

    public static void Add(String _Key, String _Value) {
        if (_Settings == null) {
            Init();
        }
        _Editor.putString(_Key, _Value);
        _Editor.commit();
    }

    public static void Clear(String _Key) {
        if (_Settings == null) {
            Init();
        }
        _Editor.remove(_Key);
        _Editor.commit();
    }

    public static String Get(String _Key) {
        if (_Settings == null)
            Init();
        return _Settings.getString(_Key, "Null");
    }
}