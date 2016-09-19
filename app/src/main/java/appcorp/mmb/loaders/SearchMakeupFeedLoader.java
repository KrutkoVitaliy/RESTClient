package appcorp.mmb.loaders;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

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

import appcorp.mmb.R;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.fragment_adapters.MakeupFeedFragmentAdapter;
import appcorp.mmb.fragment_adapters.SearchMakeupFeedFragmentAdapter;

public class SearchMakeupFeedLoader extends AsyncTask<Void, Void, String> {

    HttpURLConnection urlFeedConnection = null;
    BufferedReader reader = null;
    String resultJsonFeed = "", output = "";
    int position;
    private String request, eyeColor, difficult, occasion;
    private ArrayList<String> colors = new ArrayList<>();
    String colorsStr = "";
    private SearchMakeupFeedFragmentAdapter adapter;
    private Toolbar toolbar;
    ProgressDialog progressDialog;

    public SearchMakeupFeedLoader(Toolbar toolbar, SearchMakeupFeedFragmentAdapter adapter, String request, ArrayList<String> colors, String eyeColor, String difficult, String occasion, int position, ProgressDialog progressDialog) {
        this.toolbar = toolbar;
        this.progressDialog = progressDialog;
        this.adapter = adapter;
        this.request = request;
        this.eyeColor = eyeColor;
        this.difficult = difficult;
        if (occasion.equals("0"))
            this.occasion = "";
        else if (occasion.equals("1"))
            this.occasion = "everyday";
        else if (occasion.equals("2"))
            this.occasion = "celebrity";
        else if (occasion.equals("3"))
            this.occasion = "dramatic";
        else if (occasion.equals("4"))
            this.occasion = "holiday";
        this.colors = colors;
        for (int i = 0; i < colors.size(); i++) {
            this.colorsStr += colors.get(i) + ",";
        }
        if (colorsStr.length() > 0)
            this.colorsStr = colorsStr.substring(0, colorsStr.length() - 1);
        this.position = position;
    }

    public SearchMakeupFeedLoader(Toolbar toolbar, SearchMakeupFeedFragmentAdapter adapter, String request, ArrayList<String> colors, String eyeColor, String difficult, String occasion, int position) {
        this.toolbar = toolbar;
        this.adapter = adapter;
        this.request = request;
        this.eyeColor = eyeColor;
        this.difficult = difficult;
        if (occasion.equals("0"))
            this.occasion = "";
        else if (occasion.equals("1"))
            this.occasion = "everyday";
        else if (occasion.equals("2"))
            this.occasion = "celebrity";
        else if (occasion.equals("3"))
            this.occasion = "dramatic";
        else if (occasion.equals("4"))
            this.occasion = "holiday";
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
                URL feedURL = new URL("http://195.88.209.17/search/makeup.php?request=" + request + "&colors=" + colorsStr + "&eye_color=" + eyeColor + "&difficult=" + difficult + "&occasion=" + occasion + "&position=" + position);
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
                    URL feedURL = new URL("http://195.88.209.17/search/makeup.php?request=" + request + "&colors=" + colorsStr + "&eye_color=" + eyeColor + "&difficult=" + difficult + "&occasion=" + occasion + "&position=" + position);
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

        if(resultJsonFeed.equals("[]")) {
            List<MakeupDTO> data = new ArrayList<>();
            MakeupDTO makeupDTO = new MakeupDTO(
                    -1,
                    "nothing",
                    "nothing",
                    "nothing",
                    new ArrayList<String>(),
                    "nothing",
                    "nothing",
                    "nothing",
                    "nothing",
                    new ArrayList<String>(),
                    -1);
            data.add(makeupDTO);
            adapter.setData(data);
            if (progressDialog != null)
                progressDialog.hide();
        } else {
            List<MakeupDTO> data = new ArrayList<>();
            try {
                JSONArray items = new JSONArray(resultJsonFeed);

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    List<String> images = new ArrayList<>();
                    List<String> hashTags = new ArrayList<>();

                    for (int j = 0; j < 10; j++)
                        if (!item.getString("screen" + j).equals("empty.jpg"))
                            images.add(item.getString("screen" + j));

                    String[] tempTags;
                    if (Storage.getString("Localization", "").equals("English")) {
                        tempTags = item.getString("tags").split(",");
                        for (int j = 0; j < tempTags.length; j++) {
                            hashTags.add(tempTags[j]);
                        }
                    } else if (Storage.getString("Localization", "").equals("Russian")) {
                        tempTags = item.getString("tagsRu").split(",");
                        for (int j = 0; j < tempTags.length; j++) {
                            hashTags.add(tempTags[j]);
                        }
                    }

                    if (item.getString("published").equals("t") && !images.isEmpty()) {
                        if (!this.request.equals(""))
                            toolbar.setTitle("#" + this.request + " - " + item.getString("count"));
                        else {
                            if (toolbar.getTitle() == null)
                                toolbar.setTitle(R.string.menu_item_makeup);
                            if(toolbar.getTitle().equals("easy"))
                                toolbar.setTitle(R.string.difficult_easy);
                            if(toolbar.getTitle().equals("medium"))
                                toolbar.setTitle(R.string.difficult_medium);
                            if(toolbar.getTitle().equals("hard"))
                                toolbar.setTitle(R.string.difficult_hard);

                            if(toolbar.getTitle().equals("black"))
                                toolbar.setTitle(R.string.black_eyes);
                            if(toolbar.getTitle().equals("blue"))
                                toolbar.setTitle(R.string.blue_eyes);
                            if(toolbar.getTitle().equals("brown"))
                                toolbar.setTitle(R.string.brown_eyes);
                            if(toolbar.getTitle().equals("gray"))
                                toolbar.setTitle(R.string.gray_eyes);
                            if(toolbar.getTitle().equals("green"))
                                toolbar.setTitle(R.string.green_eyes);
                            if(toolbar.getTitle().equals("hazel"))
                                toolbar.setTitle(R.string.hazel_eyes);
                            if (!toolbar.getTitle().toString().contains(" - "))
                                toolbar.setTitle(toolbar.getTitle() + " - " + item.getString("count"));
                        }
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
                        data.add(makeupDTO);
                    }
                    adapter.setData(data);
                    if (progressDialog != null)
                        progressDialog.hide();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String upperCaseFirst(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}