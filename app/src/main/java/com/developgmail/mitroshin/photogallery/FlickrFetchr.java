package com.developgmail.mitroshin.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    public static final String TAG = "FlickrFetchr";
    public static final String API_KEY = "e6cfa93d66603917c9d5be0b8f54d129";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems() {
        List<GalleryItem> items = new ArrayList<>();
        try {
            String url = getRecentQuery();
            String jsonString = getUrlString(url);
            items = fillListFromJson(items, jsonString);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items: ", e);
        }
        return items;
    }

    private String getRecentQuery() {
        return Uri.parse("https://api.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("method", "flickr.photos.getRecent")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_s")
                .build().toString();
    }

    private List<GalleryItem> fillListFromJson(List<GalleryItem> listOfItems, String jsonString) {
        Gson gson = new Gson();
        GsonPhotos gsonPhotos = gson.fromJson(jsonString, GsonPhotos.class);
        for (GsonPhotos.PhotosBean.PhotoBean photo : gsonPhotos.getPhotos().getPhoto()) {
            GalleryItem item = new GalleryItem();
            item.setId(photo.getId());
            item.setCaption(photo.getTitle());
            item.setUrl(photo.getUrl_s());
            listOfItems.add(item);
        }
        return listOfItems;
    }
}
