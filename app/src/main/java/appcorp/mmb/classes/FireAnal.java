package appcorp.mmb.classes;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FireAnal {

    private static FirebaseAnalytics firebaseAnalytics;
    private static FireAnal instance = new FireAnal();

    private FireAnal() {
    }

    public static FireAnal getInstance() {
        return instance;
    }

    public static void setContext(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static void sendString(String id, String name, String contentType) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}