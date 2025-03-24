package xyz.doikki.videoplayer.exo;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.OptIn;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.database.ExoDatabaseProvider;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.NoOpCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.datasource.okhttp.OkHttpDataSource;
import androidx.media3.datasource.rtmp.RtmpDataSourceFactory;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
//import androidx.media3.exoplayer.source.dash.DashMediaSource;
//import androidx.media3.exoplayer.source.hls.HlsMediaSource;
//import androidx.media3.exoplayer.source.progressive.ProgressiveMediaSource;
//import androidx.media3.exoplayer.source.rtsp.RtspMediaSource;
//import androidx.media3.exoplayer.upstream.DataSource;
//import androidx.media3.exoplayer.upstream.DefaultDataSourceFactory;
//import androidx.media3.exoplayer.upstream.cache.Cache;
//import androidx.media3.exoplayer.upstream.cache.CacheDataSource;
//import androidx.media3.exoplayer.upstream.cache.LeastRecentlyUsedCacheEvictor;
//import androidx.media3.exoplayer.upstream.cache.SimpleCache;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

@UnstableApi public final class ExoMediaSourceHelper {

    private static ExoMediaSourceHelper sInstance;

    private final String mUserAgent;
    private final Context mAppContext;
    private OkHttpDataSource.Factory mHttpDataSourceFactory;
    private OkHttpClient mOkClient = null;
    private SimpleCache mCache;

    @OptIn(markerClass = UnstableApi.class) private ExoMediaSourceHelper(Context context) {
        mAppContext = context.getApplicationContext();
        mUserAgent = Util.getUserAgent(mAppContext, mAppContext.getApplicationInfo().name);
    }

    public static ExoMediaSourceHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ExoMediaSourceHelper.class) {
                if (sInstance == null) {
                    sInstance = new ExoMediaSourceHelper(context);
                }
            }
        }
        return sInstance;
    }

    public void setOkClient(OkHttpClient client) {
        mOkClient = client;
    }

    public MediaSource getMediaSource(String uri) {
        return getMediaSource(uri, null, false);
    }

    public MediaSource getMediaSource(String uri, Map<String, String> headers) {
        return getMediaSource(uri, headers, false);
    }

    public MediaSource getMediaSource(String uri, boolean isCache) {
        return getMediaSource(uri, null, isCache);
    }

    @OptIn(markerClass = UnstableApi.class) public MediaSource getMediaSource(String uri, Map<String, String> headers, boolean isCache) {
        Uri contentUri = Uri.parse(uri);
        if ("rtmp".equals(contentUri.getScheme())) {
            return new ProgressiveMediaSource.Factory(new RtmpDataSourceFactory(null))
                    .createMediaSource(MediaItem.fromUri(contentUri));
        } else if ("rtsp".equals(contentUri.getScheme())) {
            return new RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(contentUri));
        }
        int contentType = inferContentType(uri);
        DataSource.Factory factory;
        if (isCache) {
            factory = getCacheDataSourceFactory();
        } else {
            factory = getDataSourceFactory();
        }
        if (mHttpDataSourceFactory != null) {
            setHeaders(headers);
        }
        switch (contentType) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(contentUri));
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(contentUri));
            default:
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(contentUri));
        }
    }

    @OptIn(markerClass = UnstableApi.class) private int inferContentType(String fileName) {
        fileName = fileName.toLowerCase();
        if (fileName.contains(".mpd") || fileName.contains("type=mpd")) {
            return C.TYPE_DASH;
        } else if (fileName.contains("m3u8")) {
            return C.TYPE_HLS;
        } else {
            return C.TYPE_OTHER;
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private DataSource.Factory getCacheDataSourceFactory() {
        if (mCache == null) {
            mCache = newSimpleCache(); // 假设有一个方法来创建 SimpleCache 实例
        }
        return new CacheDataSource.Factory()
                .setCache(mCache)
                .setUpstreamDataSourceFactory(getDataSourceFactory())
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    @OptIn(markerClass = UnstableApi.class) private SimpleCache newSimpleCache() {
        // 这里假设你已经有了一个 File 对象来存储缓存数据
        Context context = null;
        File cacheDir = new File(context.getCacheDir(), "media_cache");
        long maxFileSize = 1024 * 1024 * 10; // 10 MB
        long maxCacheSize = 1024 * 1024 * 50; // 50 MB
        return new SimpleCache(cacheDir, new NoOpCacheEvictor(), new ExoDatabaseProvider(context));
    }



//    @OptIn(markerClass = UnstableApi.class) private Cache newCache() {
//        return new SimpleCache(
//                new File(mAppContext.getExternalCacheDir(), "exo-video-cache"), //缓存目录
//                new LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024), //缓存大小，默认512M，使用LRU算法实现
//                new ExoDatabaseProvider(mAppContext));
//    }

    /**
     * Returns a new DataSource factory.
     *
     * @return A new DataSource factory.
     */
    @OptIn(markerClass = UnstableApi.class) private DataSource.Factory getDataSourceFactory() {
        return new DefaultDataSourceFactory(mAppContext, getHttpDataSourceFactory());
    }

    /**
     * Returns a new HttpDataSource factory.
     *
     * @return A new HttpDataSource factory.
     */
    @OptIn(markerClass = UnstableApi.class) private DataSource.Factory getHttpDataSourceFactory() {
        if (mHttpDataSourceFactory == null) {
            mHttpDataSourceFactory = new OkHttpDataSource.Factory((Call.Factory) mOkClient)
                    .setUserAgent(mUserAgent);
        }
        return mHttpDataSourceFactory;
    }

    private void setHeaders(Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            //如果发现用户通过header传递了UA，则强行将HttpDataSourceFactory里面的userAgent字段替换成用户的
            if (headers.containsKey("User-Agent")) {
                String value = headers.remove("User-Agent");
                if (!TextUtils.isEmpty(value)) {
                    try {
                        Field userAgentField = mHttpDataSourceFactory.getClass().getDeclaredField("userAgent");
                        userAgentField.setAccessible(true);
                        userAgentField.set(mHttpDataSourceFactory, value.trim());
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
            Iterator<String> iter = headers.keySet().iterator();
            while (iter.hasNext()) {
                String k = iter.next();
                String v = headers.get(k);
                if (v != null)
                    headers.put(k, v.trim());
            }
            mHttpDataSourceFactory.setDefaultRequestProperties(headers);
        }
    }

    public void setCache(SimpleCache cache) {
        this.mCache = cache;
    }
}
