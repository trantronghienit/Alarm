package com.example.alarmpig.file;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.example.alarmpig.rest.ApiServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FileManager {
    private static final String TAG = "file";
    private DownloadZipFileTask downloadZipFileTask;
    private Context context;
    private String localFile;

    public FileManager(Context context,String localFile){
        this.context = context;
        this.localFile = localFile;
    }

    public void downloadFile(String link, final OnDownloadFileListener listener) {

        ApiServices downloadService = createService(ApiServices.class, "https://github.com/");
        Call<ResponseBody> call = downloadService.downloadFile(link);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if(listener != null){
                        listener.onPrepare();
                    }
                    downloadZipFileTask = new DownloadZipFileTask();
                    downloadZipFileTask.setListener(listener);
                    downloadZipFileTask.execute(response.body());

                } else {
                    if(listener != null){
                        listener.onError("");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(listener != null){
                    listener.onError(t.getMessage());
                }
            }
        });
    }

    public <T> T createService(Class<T> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().build())
                .build();
        return retrofit.create(serviceClass);
    }

    private class DownloadZipFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String> {
        private OnDownloadFileListener listener;

        public void setListener(OnDownloadFileListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            //Copy you logic to calculate progress and call
            return saveToDisk(urls[0], localFile);
        }

        @Override
        protected void onProgressUpdate(Pair<Integer, Long>... progress) {

            Log.d("API123", progress[0].second + " ");

            if (progress[0].first == 100)
                Toast.makeText(context, "File downloaded successfully", Toast.LENGTH_SHORT).show();


            if (progress[0].second > 0) {
                int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
                if(listener != null){
                    listener.onProgress(currentProgress);
                }

            }

            if (progress[0].first == -1) {
                if(listener != null){
                    listener.onError("Download failed");
                }
            }

        }

        public void doProgress(Pair<Integer, Long> progressDetails) {
            publishProgress(progressDetails);
        }

        @Override
        protected void onPostExecute(String result) {
            if(listener != null){
                listener.onCompleted(result);
            }
        }
    }

    private String saveToDisk(ResponseBody body, String filename) {
        try {

            File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                long fileSize = body.contentLength();
                Log.d(TAG, "File Size=" + fileSize);
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
                    downloadZipFileTask.doProgress(pairs);
                    Log.d(TAG, "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
                }

                outputStream.flush();

                Log.d(TAG, destinationFile.getParent());
                Pair<Integer, Long> pairs = new Pair<>(100, 100L);
                downloadZipFileTask.doProgress(pairs);
                return destinationFile.getCanonicalPath();
            } catch (IOException e) {
                Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
                downloadZipFileTask.doProgress(pairs);
                Log.d(TAG, "Failed to save the file!");
                return "";
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return "";
        }
    }

    public interface OnDownloadFileListener {
        void onPrepare();
        void onProgress(int Progress);
        void onCompleted(String fileName);
        void onError(String message);
    }
}
