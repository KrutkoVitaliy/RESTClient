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
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.fragment_adapters.FavoritesManicureFeedFragmentAdapter;

public class FavoriteManicureLoader extends AsyncTask<Void, Void, String> {

    private HttpURLConnection urlFeedConnection = null;
    private BufferedReader reader = null;
    private String resultJsonFeed = "";
    private FavoritesManicureFeedFragmentAdapter adapter;
    private int position;
    private List<Long> likesManicure = new ArrayList<>();

    public FavoriteManicureLoader(FavoritesManicureFeedFragmentAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
        new Get("http://195.88.209.17/app/in/favoritesManicure.php?email=" + Storage.getString("E-mail", "")).execute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (position == 1) {
                URL feedURL = new URL("http://195.88.209.17/app/static/manicure" + position + ".html");
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
                    URL feedURL = new URL("http://195.88.209.17/app/static/manicure" + i + ".html");
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

        long id, sid, likes, uploadDate, currentDate = System.currentTimeMillis();
        List<ManicureDTO> data = new ArrayList<>();
        String availableDate, colors, shape, design, tags, authorPhoto, authorName, published;

        try {
            JSONArray items = new JSONArray(resultJsonFeed);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                List<String> images = new ArrayList<>();
                List<String> hashTags = new ArrayList<>();

                for (int j = 0; j < 10; j++)
                    if (!item.getString("screen" + j).equals("empty.jpg"))
                        images.add(item.getString("screen" + j));

                id = item.getLong("id");
                authorPhoto = item.getString("authorPhoto");
                authorName = item.getString("authorName");
                availableDate = item.getString("uploadDate");
                tags = item.getString("tags");
                shape = item.getString("shape");
                design = item.getString("design");
                colors = item.getString("colors");
                likes = item.getLong("likes");
                published = item.getString("published");

                String[] tempTags = tags.split(",");
                for (int j = 0; j < tempTags.length; j++) {
                    hashTags.add(tempTags[j]);
                }

                if (likesManicure.contains(id)) {
                    ManicureDTO manicureDTO = new ManicureDTO(id, availableDate, authorName, authorPhoto, shape, design, images, colors, hashTags, likes);
                    data.add(manicureDTO);
                }
            }
            adapter.setManicureData(data);
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
                    likesManicure.add(new Long(set[i]));
        }
    }
}