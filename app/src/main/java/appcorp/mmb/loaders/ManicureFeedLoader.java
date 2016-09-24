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

import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.fragment_adapters.ManicureFeedFragmentAdapter;

public class ManicureFeedLoader extends AsyncTask<Void, Void, String> {

    HttpURLConnection urlFeedConnection = null;
    BufferedReader reader = null;
    String resultJsonFeed = "";
    ManicureFeedFragmentAdapter adapter;
    int position;
    ProgressDialog progressDialog;

    public ManicureFeedLoader(ManicureFeedFragmentAdapter adapter, int position, ProgressDialog progressDialog) {
        this.adapter = adapter;
        this.position = position;
        this.progressDialog = progressDialog;
    }

    public ManicureFeedLoader(ManicureFeedFragmentAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (position == 1) {
                URL feedURL = new URL("http://195.88.209.17/app/static/manicure" + position + ".html");
                //URL feedURL = new URL(Intermediates.URL.GET_FEED);
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
                    //URL feedURL = new URL(Intermediates.URL.GET_FEED);
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
        String availableDate, colors, shape, design, tags = "", authorPhoto, authorName, published;

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
                if (Storage.getString("Localization", "").equals("English"))
                    tags = item.getString("tags");
                else if (Storage.getString("Localization", "").equals("Russian"))
                    tags = item.getString("tagsRu");
                shape = item.getString("shape");
                design = item.getString("design");
                colors = item.getString("colors");
                likes = item.getLong("likes");
                published = item.getString("published");

                String[] tempTags = tags.split(",");
                for (int j = 0; j < tempTags.length; j++) {
                    hashTags.add(tempTags[j]);
                }

                    /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                    availableDate = simpleDateFormat.format(new Date(tempDate));
                    if ((currentDate - tempDate) <= 259200000)
                        availableDate = Intermediates.calculateAvailableTime(tempDate, currentDate);*/

                if (published.equals("t")) {
                    ManicureDTO manicureDTO = new ManicureDTO(id, availableDate, authorName, authorPhoto, shape, design, images, colors, hashTags, likes);
                    data.add(manicureDTO);
                }
            }
            if (adapter != null)
                adapter.setData(data);
            if (progressDialog != null)
                progressDialog.hide();

            FireAnal.sendString("1", "Open", "ManicureFeedLoaded");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String upperCaseFirst(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}