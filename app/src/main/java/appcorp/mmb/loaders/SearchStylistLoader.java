package appcorp.mmb.loaders;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.dto.StylistDTO;
import appcorp.mmb.fragment_adapters.SearchStylistFeedFragmentAdapter;

public class SearchStylistLoader extends AsyncTask<Void, Void, String> {

    HttpURLConnection urlFeedConnection = null;
    BufferedReader reader = null;
    String resultJsonFeed = "";
    String city;
    String skill;
    SearchStylistFeedFragmentAdapter adapter;
    ProgressDialog progressDialog;
    int position;

    public SearchStylistLoader(SearchStylistFeedFragmentAdapter adapter, String city, String skill, int position, ProgressDialog progressDialog) {
        this.adapter = adapter;
        this.position = position;
        this.progressDialog = progressDialog;
        this.city = city;
        this.skill = skill;
    }

    public SearchStylistLoader(SearchStylistFeedFragmentAdapter adapter, String city, String skill, int position) {
        this.adapter = adapter;
        this.position = position;
        this.city = city;
        this.skill = skill;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            if (position == 1) {
                URL feedURL = new URL("http://195.88.209.17/app/out/stylists.php?position=" + position + "&city=" + city.replace(" ", "%20") + "&skill=" + skill.replace(" ", "%20"));
                urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                urlFeedConnection.setRequestMethod("GET");
                urlFeedConnection.connect();
                InputStream inputStream = urlFeedConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                resultJsonFeed += buffer.toString();
            } else {
                for (int i = 1; i <= position; i++) {
                    URL feedURL = new URL("http://195.88.209.17/app/out/stylists.php?position=" + position + "&city=" + Intermediates.encodeToURL(city) + "&skill=" + Intermediates.encodeToURL(skill));
                    urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                    urlFeedConnection.setRequestMethod("GET");
                    urlFeedConnection.connect();
                    InputStream inputStream = urlFeedConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null)
                        buffer.append(line);
                    resultJsonFeed += buffer.toString();
                    resultJsonFeed = resultJsonFeed.replace("][", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJsonFeed;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        List<StylistDTO> data = new ArrayList<>();
        try {
            JSONArray items = new JSONArray(resultJsonFeed);
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                StylistDTO stylistDTO = new StylistDTO(
                        item.getLong("id"),
                        item.getString("firstName"),
                        item.getString("lastName"),
                        item.getString("photo"),
                        item.getString("phoneNumber"),
                        item.getString("city"),
                        item.getString("address"),
                        item.getString("gplus"),
                        item.getString("facebook"),
                        item.getString("vkontakte"),
                        item.getString("instagram"),
                        item.getString("odnoklassniki"));
                data.add(stylistDTO);
                adapter.setData(data);
                progressDialog.hide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
