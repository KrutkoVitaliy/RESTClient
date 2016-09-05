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
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.fragment_adapters.FavoritesFragmentAdapter;

public class FavoriteHairstyleLoader extends AsyncTask<Void, Void, String> {

    private HttpURLConnection urlFeedConnection = null;
    private BufferedReader reader = null;
    private String resultJsonFeed = "";
    private FavoritesFragmentAdapter adapter;
    private int position;
    String ids;

    HttpURLConnection connectionGet = null;
    String resultGet = "";

    public FavoriteHairstyleLoader(FavoritesFragmentAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            URL feedURL = new URL("http://195.88.209.17/app/in/favoritesHairstyle.php?email=" + Storage.getString("E-mail", ""));
            connectionGet = (HttpURLConnection) feedURL.openConnection();
            connectionGet.setRequestMethod("GET");
            connectionGet.connect();
            InputStream inputStream = connectionGet.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer profileBuffer = new StringBuffer();
            String profileLine;
            while ((profileLine = reader.readLine()) != null) {
                profileBuffer.append(profileLine);
            }
            resultGet = profileBuffer.toString();
            ids = resultGet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (position == 1) {
                URL feedURL = new URL("http://195.88.209.17/favorites/hairstyle.php?position=" + position + "&ids=" + ids);
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
                    URL feedURL = new URL("http://195.88.209.17/favorites/hairstyle.php?position=" + position + "&ids=" + ids);
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
    protected void onPostExecute(String resultJsonFeed) {
        super.onPostExecute(resultJsonFeed);

        List<HairstyleDTO> hairstyleData = new ArrayList<>();

        try {
            JSONArray items = new JSONArray(resultJsonFeed);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                List<String> images = new ArrayList<>();
                List<String> hashTags = new ArrayList<>();

                for (int j = 0; j < 10; j++)
                    if (!item.getString("screen" + j).equals("empty.jpg"))
                        images.add(item.getString("screen" + j));

                String[] tempTags = item.getString("tags").split(",");
                for (int j = 0; j < tempTags.length; j++) {
                    hashTags.add(tempTags[j]);
                }

                HairstyleDTO hairstyleDTO = new HairstyleDTO(
                        item.getLong("id"),
                        item.getString("uploadDate"),
                        item.getString("authorName"),
                        item.getString("authorPhoto"),
                        item.getString("hairstyleType"),
                        images,
                        hashTags,
                        item.getLong("likes"),
                        item.getString("length"),
                        item.getString("type"),
                        item.getString("for"));
                hairstyleData.add(hairstyleDTO);
                adapter.setHairstyleData(hairstyleData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}