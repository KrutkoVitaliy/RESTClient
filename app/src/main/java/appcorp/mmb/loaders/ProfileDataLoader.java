package appcorp.mmb.loaders;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import appcorp.mmb.classes.Intermediates;

public class ProfileDataLoader extends AsyncTask<Void, Void, String>{

    HttpURLConnection profileConnection = null;
    BufferedReader profileReader = null;
    String resultJsonProfile = "";
    private long sid;
    private Context ctx = null;

    public ProfileDataLoader(long sid, Context ctx) {
        this.sid  = sid;
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL profileURL = new URL(Intermediates.URL.GET_PROFILE+"/"+sid);
            profileConnection = (HttpURLConnection) profileURL.openConnection();
            profileConnection.setRequestMethod("GET");
            profileConnection.connect();
            InputStream inputStream = profileConnection.getInputStream();
            profileReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer profileBuffer = new StringBuffer();
            String profileLine;
            while ((profileLine = profileReader.readLine()) != null) {
                profileBuffer.append(profileLine);
            }
            resultJsonProfile = profileBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJsonProfile;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            Storage.Init(ctx);
            JSONObject profileItem = new JSONObject(s);
            Storage.Add("avatar"+sid, profileItem.getString("avatar"));
            Storage.Add("name"+sid, profileItem.getString("name"));
            Storage.Add("lastname"+sid, profileItem.getString("last_name"));
            Storage.Add("id", profileItem.getString("id"));
            Storage.Add("aid", profileItem.getString("aid"));
            Storage.Add("registration_date", profileItem.getString("registration_date"));
            Storage.Add("last_visit"+sid, profileItem.getString("last_visit"));
            Storage.Add("nickname"+sid, profileItem.getString("nickname"));
            Storage.Add("skills"+sid, profileItem.getString("skills"));
            Storage.Add("likes"+sid, profileItem.getString("likes"));
            Storage.Add("followers"+sid, profileItem.getString("followers"));
            Storage.Add("city"+sid, profileItem.getString("city"));
            Storage.Add("address"+sid, profileItem.getString("address"));
            Storage.Add("user_type"+sid, profileItem.getString("user_type"));
            Storage.Add("phone_number"+sid, profileItem.getString("phone_number"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}