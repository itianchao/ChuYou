package com.jueda.ndian.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jueda.ndian.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class ImageLoaderUtil {
	public static ImageLoader imageLoader;
	public static DisplayImageOptions options;
	public static AbsListView.OnScrollListener pauseScrollListener;
	public static void Init(Context context){
		ImageLoaderUtil.imageLoader=ImageLoader.getInstance();
		//获取最大内存
		int maxMemory=(int)(Runtime.getRuntime().maxMemory());
//		ImageLoaderUtil.imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		ImageLoaderConfiguration cofing=new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(3) //线程池内加载的数量
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new WeakMemoryCache())
//				.memoryCacheSizePercentage(40)
				.memoryCacheSize(maxMemory / 4)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		imageLoader.getInstance().init(cofing);
		pauseScrollListener = new PauseOnScrollListener(imageLoader, true, true);
	}

	/***
	 * 普通加载用这个
	 * @param uri
	 * @param image
	 */
	public static RelativeLayout.LayoutParams ImageLoader(String uri, ImageView image){
		    // 第一个参数是uri,
		    //第二个参数是显示图片的imageView，
		    //第三个参数是刚刚构造的图片显示选项，
		    //第四个参数是加载的回调方法，displayImage有很多重载方法这中介其中一种；
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.background)
				.showImageOnFail(R.drawable.background)
				.showImageOnLoading(R.drawable.background)
				.cacheInMemory(true)
				.considerExifParams(true)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		    imageLoader.displayImage(uri,image, options, null);
		return null;
	}


	/**
	 * 根据情况设置默认图片
	 * @param uri
	 * @param image
	 * @param imageId
	 */
	public static void ImageLoader(String uri,ImageView image,int imageId){
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(imageId)
				.showImageOnFail(imageId)
				.showImageOnLoading(imageId)
				.cacheInMemory(true)
				.considerExifParams(true)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader.displayImage(uri,image, options, null);
	}
	/**
	 * 根据情况设置默认图片高清
	 * @param uri
	 * @param image
	 * @param imageId
	 */
	public static void ImageLoaderHome(String uri,ImageView image,int imageId){
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(imageId)
				.showImageOnFail(imageId)
				.showImageOnLoading(imageId)
				.cacheInMemory(true)
				.considerExifParams(true)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		imageLoader.displayImage(uri,image, options, null);

	}
}
