package com.developgmail.mitroshin.photogallery;

import java.util.List;

public class GsonPhotos {
    private PhotosBean photos;

    public PhotosBean getPhotos() {
        return photos;
    }

    public static class PhotosBean {
        private List<PhotoBean> photo;

        public List<PhotoBean> getPhoto() {
            return photo;
        }

        public static class PhotoBean {
            private String id;
            private String title;
            private String url_s;

            public String getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getUrl_s() {
                return url_s;
            }
        }
    }
}
