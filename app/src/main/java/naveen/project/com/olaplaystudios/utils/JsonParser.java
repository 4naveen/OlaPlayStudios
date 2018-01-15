package naveen.project.com.olaplaystudios.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import naveen.project.com.olaplaystudios.Album;

/**
 * Created by Personal on 12/17/2017.
 */

public class JsonParser {
static String []download_song_url=new String[]{
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Afreen%20Afreen%20(DjRaag.Net)2cc6f8b.mp3",
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Aik%20-%20Alif-(Mr-Jatt.com)8ae5316.mp3",
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Tajdar%20E%20Haram-(Mr-Jatt.com)7f311d4.mp3",
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Aaj%20Rung%20-(Mr-Jatt.com)140b9e4.mp3",
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Ae%20Dil-(Mr-Jatt.com)bd4f44a.mp3",
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Man%20Aamadeh%20Am%20(RaagJatt.com)ae36c8d.mp3",
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Man%20Aamadeh%20Am%20(RaagJatt.com)ae36c8d.mp3","" +
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Bewaja%20%20Coke%20Studio%208%20-(Mr-Jatt.com)c4f2b1b.mp3",
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Dinae%20Dinae-(Mr-Jatt.com)53f3f1f.mp3",
        "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Tera%20Woh%20Pyar-(Mr-Jatt.com)16d7c13.mp3"};
    public static ArrayList<Album>Parse(String response){
        ArrayList<Album> albumArrayList=new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Album album = new Album();
                album.setSong_name(object.getString("song"));
                album.setArtists(object.getString("artists"));
                album.setSong_url(object.getString("url"));
                album.setDownload_song_url(download_song_url[i]);
                album.setCover_image(object.getString("cover_image"));
                albumArrayList.add(album);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return albumArrayList;
    }
}
