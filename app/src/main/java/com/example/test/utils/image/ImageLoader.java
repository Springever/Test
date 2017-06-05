package com.example.test.utils.image;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.example.test.R;

public class ImageLoader {

	private static final String CACHE_NAME = "Images";
	private static final String THUMBNAIL_CACHE_NAME = "Thumbnails";
	private static ImageLoader sInstance;

	public static ImageLoader getInstance() {
		if (sInstance == null) {
			sInstance = new ImageLoader();
		}
		
		return sInstance;
	}
	
	public static String wrapPackageUriString(String packageName) {
		Uri uri = Uri.parse(ImageWorker.PACKAGE + ":" + packageName);
		return uri.toString();
	}
	
	private ImageFetcher mFetcher;
	private ImageFetcher mThumbnailFetcher;
	
	private ImageLoader() { }
	
	public void initLoader(Context context) {
		ImageCache cache = new ImageCache(context, CACHE_NAME);
		
		mFetcher = new ImageFetcher(context);
		mFetcher.setImageCache(cache);
		mFetcher.setLoadingImage(R.drawable.default_app_icon);
		
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(THUMBNAIL_CACHE_NAME);
		cacheParams.memoryCacheEnabled = false;
		ImageCache thumbnailCache = new ImageCache(context, cacheParams);
		mThumbnailFetcher = new ImageFetcher(context);
		mThumbnailFetcher.setImageCache(thumbnailCache);
		mThumbnailFetcher.setLoadingImage(R.drawable.detail_default_icon);
	}

	public void loadImage(String data, ImageView imageView) {
		imageView.setScaleType(ScaleType.CENTER);
		imageView.setWillNotCacheDrawing(false);
		mFetcher.loadImage(data, imageView);
	}
}
