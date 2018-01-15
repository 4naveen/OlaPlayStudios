package naveen.project.com.olaplaystudios;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Personal on 12/19/2017.
 */

public class DownloadFileFromURL extends AsyncTask<String, String, String> {
    Context context;
    public static final int progress_bar_type = 0;
    ProgressDialog pDialog;
    private static String file_path,fileName,file_uri;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       // showDialog(progress_bar_type);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public DownloadFileFromURL(Context context) {
        this.context = context;

    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            // Output stream
            file_uri="/sdcard/"+System.currentTimeMillis()+".mp3";
            if (new File(file_uri).exists()){
                new File(file_uri).delete();
            }
            OutputStream output = new FileOutputStream(file_uri);
            System.out.println("internal file url"+file_path);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String file_url) {
        pDialog.dismiss();
        sendNotification("Download Complete");

    }
    private void sendNotification(String messageBody) {
        Uri fileURI = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                new File(file_uri));
        Intent intent= openFile(fileURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(messageBody)
                .setContentText("Click to Open file")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.olaplay_logo);
            notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.olaplay_logo);
        }
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
    private Intent openFile(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //System.out.println("url--"+uri.getAbsolutePath());
        try {

            // Uri uri = Uri.fromFile(url);

            if (uri.toString().contains(".mp3") || uri.toString().contains(".mpeg")) {
                // Word document
                //intent.setDataAndType(uri, "application/msword");
                intent.setDataAndType(uri, "*/*");

                System.out.println("doc");
            }
            else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
        return intent;
    }

/*    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }*/
}

