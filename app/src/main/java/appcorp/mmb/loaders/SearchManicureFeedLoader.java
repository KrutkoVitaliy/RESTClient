package appcorp.mmb.loaders;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;

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

import appcorp.mmb.dto.ManicureDTO;
import appcorp.mmb.fragment_adapters.ManicureFeedFragmentAdapter;
import appcorp.mmb.fragment_adapters.SearchHairstyleFeedFragmentAdapter;
import appcorp.mmb.fragment_adapters.SearchManicureFeedFragmentAdapter;

public class SearchManicureFeedLoader extends AsyncTask<Void, Void, String> {
    HttpURLConnection urlFeedConnection = null;
    BufferedReader reader = null;
    String resultJsonFeed = "", output = "";
    int position;
    private String request, shape, design;
    private ArrayList<String> colors = new ArrayList<>();
    String colorsStr = "";
    private SearchManicureFeedFragmentAdapter adapter;
    private Toolbar toolbar;
    ProgressDialog progressDialog;

    public SearchManicureFeedLoader(Toolbar toolbar, SearchManicureFeedFragmentAdapter adapter, String request, ArrayList<String> colors, String shape, String design, int position, ProgressDialog progressDialog) {
        this.toolbar = toolbar;
        this.progressDialog = progressDialog;
        this.adapter = adapter;
        this.request = request;

        if (shape.equals("0"))
            this.shape = "";
        else if (shape.equals("1"))
            this.shape = "square";
        else if (shape.equals("2"))
            this.shape = "oval";
        else if (shape.equals("3"))
            this.shape = "stiletto";

        if (design.equals("0"))
            this.design = "";
        else if (design.equals("1"))
            this.design = "french_classic";
        else if (design.equals("2"))
            this.design = "french_chevron";
        else if (design.equals("3"))
            this.design = "french_millennium";
        else if (design.equals("4"))
            this.design = "french_fun";
        else if (design.equals("5"))
            this.design = "french_crystal";
        else if (design.equals("6"))
            this.design = "french_colorful";
        else if (design.equals("7"))
            this.design = "french_designer";
        else if (design.equals("8"))
            this.design = "french_spa";
        else if (design.equals("9"))
            this.design = "french_moon";
        else if (design.equals("10"))
            this.design = "art";
        else if (design.equals("11"))
            this.design = "designer";
        else if (design.equals("12"))
            this.design = "volume";
        else if (design.equals("13"))
            this.design = "aqua";
        else if (design.equals("14"))
            this.design = "american";
        else if (design.equals("15"))
            this.design = "photo";

        this.colors = colors;
        for (int i = 0; i < colors.size(); i++) {
            this.colorsStr += colors.get(i) + ",";
        }
        if (colorsStr.length() > 0)
            this.colorsStr = colorsStr.substring(0, colorsStr.length() - 1);
        this.position = position;
    }

    public SearchManicureFeedLoader(Toolbar toolbar, SearchManicureFeedFragmentAdapter adapter, String request, ArrayList<String> colors, String shape, String design, int position) {
        this.toolbar = toolbar;
        this.adapter = adapter;
        this.request = request;

        if (shape.equals("0"))
            this.shape = "";
        else if (shape.equals("1"))
            this.shape = "square";
        else if (shape.equals("2"))
            this.shape = "oval";
        else if (shape.equals("3"))
            this.shape = "stiletto";

        if (design.equals("0"))
            this.design = "";
        else if (design.equals("1"))
            this.design = "french_classic";
        else if (design.equals("2"))
            this.design = "french_chevron";
        else if (design.equals("3"))
            this.design = "french_millennium";
        else if (design.equals("4"))
            this.design = "french_fun";
        else if (design.equals("5"))
            this.design = "french_crystal";
        else if (design.equals("6"))
            this.design = "french_colorful";
        else if (design.equals("7"))
            this.design = "french_designer";
        else if (design.equals("8"))
            this.design = "french_spa";
        else if (design.equals("9"))
            this.design = "french_moon";
        else if (design.equals("10"))
            this.design = "art";
        else if (design.equals("11"))
            this.design = "designer";
        else if (design.equals("12"))
            this.design = "volume";
        else if (design.equals("13"))
            this.design = "aqua";
        else if (design.equals("14"))
            this.design = "american";
        else if (design.equals("15"))
            this.design = "photo";

        this.colors = colors;
        for (int i = 0; i < colors.size(); i++) {
            this.colorsStr += colors.get(i) + ",";
        }
        if (colorsStr.length() > 0)
            this.colorsStr = colorsStr.substring(0, colorsStr.length() - 1);
        this.position = position;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (position == 1) {
                URL feedURL = new URL("http://195.88.209.17/search/manicure.php?request=" + request.replace(" ", "%20") + "&colors=" + colorsStr + "&shape=" + shape + "&design=" + design + "&position=" + position);
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
                    URL feedURL = new URL("http://195.88.209.17/search/manicure.php?request=" + request + "&colors=" + colorsStr + "&shape=" + shape + "&design=" + design + "&position=" + position);
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

                if (!this.request.isEmpty())
                    toolbar.setTitle("#" + this.request + " - " + item.getString("count"));
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

                if (published.equals("t")) {
                    ManicureDTO manicureDTO = new ManicureDTO(id, availableDate, authorName, authorPhoto, shape, design, images, colors, hashTags, likes);
                    data.add(manicureDTO);
                }
                adapter.setData(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String upperCaseFirst(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}