package com.developgmail.mitroshin.photogallery.activity;

import android.support.v4.app.Fragment;

import com.developgmail.mitroshin.photogallery.fragment.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
