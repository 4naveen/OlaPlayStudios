package naveen.project.com.olaplaystudios;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import naveen.project.com.olaplaystudios.utils.JsonParser;

/**
 * Created by user on 7/7/17.
 */

public class SongLoader extends AsyncTaskLoader {
    private String mUrl;
    private Context context;

    public SongLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Album> loadInBackground() {
        String response;
        URL url = null;
        HttpURLConnection connection = null;
        ArrayList<Album> albumArrayList = new ArrayList<>();

        try {
            url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
            }
            response = buffer.toString();
            System.out.println("response" + response);
            albumArrayList = JsonParser.Parse(response);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return albumArrayList;
    }


}
