package com.developgmail.mitroshin.photogallery;

import android.net.Uri;
import android.util.Log;

import com.developgmail.mitroshin.photogallery.model.GalleryItem;
import com.developgmail.mitroshin.photogallery.model.GsonPhotos;
import com.developgmail.mitroshin.photogallery.model.GsonPhotos.PhotosBean.PhotoBean;
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
        List<GalleryItem> galleryItemGroup = new ArrayList<>();
        try {
            String jsonGetRecentAnswer = getUrlString(getQueryToApiMethodGetRecent());
            galleryItemGroup = fillGalleryItemGroupFromJsonPhotosGroup(galleryItemGroup, jsonGetRecentAnswer);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items: ", e);
        }
        return galleryItemGroup;
    }

    private String getQueryToApiMethodGetRecent() {
        return Uri.parse("https://api.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("method", "flickr.photos.getRecent")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_s")
                .build().toString();
    }

    private List<GalleryItem> fillGalleryItemGroupFromJsonPhotosGroup(List<GalleryItem> galleryItemGroup, String jsonPhotosGroupString) {
        GsonPhotos gsonPhotoGroup = getGsonPhotoGroupFromJson(jsonPhotosGroupString);
        for (PhotoBean gsonPhoto : gsonPhotoGroup.getPhotos().getPhoto()) {
            GalleryItem galleryItem = getGalleryItemFromGsonPhoto(gsonPhoto);
            galleryItemGroup.add(galleryItem);
        }
        return galleryItemGroup;
    }

    private GsonPhotos getGsonPhotoGroupFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, GsonPhotos.class);
    }

    private GalleryItem getGalleryItemFromGsonPhoto(PhotoBean gsonPhoto) {
        GalleryItem item = new GalleryItem();
        item.setId(gsonPhoto.getId());
        item.setCaption(gsonPhoto.getTitle());
        item.setUrl(gsonPhoto.getUrl_s());

        return item;
    }
}
