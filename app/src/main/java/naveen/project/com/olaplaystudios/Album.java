package naveen.project.com.olaplaystudios;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Personal on 12/17/2017.
 */

public class Album implements Parcelable {

public String song_name,artists,song_url,cover_image,download_song_url;

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getSong_url() {
        return song_url;
    }

    public void setSong_url(String song_url) {
        this.song_url = song_url;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getDownload_song_url() {
        return download_song_url;
    }

    public void setDownload_song_url(String download_song_url) {
        this.download_song_url = download_song_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.song_name);
        dest.writeString(this.artists);
        dest.writeString(this.song_url);
        dest.writeString(this.cover_image);
        dest.writeString(this.download_song_url);

    }

    public Album() {
    }

    protected Album(Parcel in) {
        this.song_name = in.readString();
        this.artists = in.readString();
        this.song_url = in.readString();
        this.cover_image = in.readString();
        this.download_song_url=in.readString();
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
