package appcorp.mmb.loaders;

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

import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.fragment_adapters.FavoritesFragmentAdapter;

public class FavoriteMakeupLoader extends AsyncTask<Void, Void, String> {

    private HttpURLConnection urlFeedConnection = null;
    private BufferedReader reader = null;
    private String resultJsonFeed = "";
    private FavoritesFragmentAdapter adapter;
    private int position;
    private List<Long> likesMakeup = new ArrayList<>();

    public FavoriteMakeupLoader(FavoritesFragmentAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
        new Get("http://195.88.209.17/app/in/favoritesMakeup.php?email=" + Storage.getString("E-mail", "")).execute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (position == 1) {
                URL feedURL = new URL("http://195.88.209.17/app/static/makeup" + position + ".html");
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
                    URL feedURL = new URL("http://195.88.209.17/app/static/makeup" + i + ".html");
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
                    resultJsonFeed = resultJsonFeed.replace("][", ",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJsonFeed;
    }

    @Override
    protected void onPostExecute(String resultJsonFeed) {
        super.onPostExecute(resultJsonFeed);

        List<MakeupDTO> makeupData = new ArrayList<>();
        try {
            JSONArray items = new JSONArray(resultJsonFeed);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                List<String> images = new ArrayList<>();
                List<String> hashTags = new ArrayList<>();

                for (int j = 0; j < 10; j++)
                    if (!item.getString("screen" + j).equals("empty.jpg"))
                        images.add(item.getString("screen" + j));

                String[] tempTags = item.getString("tags").replace(" ", "").split(",");
                for (int j = 0; j < tempTags.length; j++) {
                    hashTags.add(tempTags[j]);
                }

                if (likesMakeup.contains(item.getLong("id"))) {
                    MakeupDTO makeupDTO = new MakeupDTO(
                            item.getLong("id"),
                            item.getString("uploadDate"),
                            item.getString("authorName"),
                            item.getString("authorPhoto"),
                            images,
                            item.getString("colors"),
                            item.getString("eyeColor"),
                            item.getString("occasion"),
                            item.getString("difficult"),
                            hashTags,
                            item.getLong("likes"));
                    makeupData.add(makeupDTO);
                }
            }
            adapter.setMakeupData(makeupData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class Get extends AsyncTask<Void,Void,String> {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String url = "";
        String result = "";

        public Get(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL feedURL = new URL(url);
                connection = (HttpURLConnection) feedURL.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer profileBuffer = new StringBuffer();
                String profileLine;
                while ((profileLine = reader.readLine()) != null) {
                    profileBuffer.append(profileLine);
                }
                result = profileBuffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            String[] set = s.split(",");
            for (int i = 0; i < set.length; i++)
                if (!set[i].equals(""))
                    likesMakeup.add(new Long(set[i]));
        }
    }
}
