package garyhomewood.co.uk.planespotter.di;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import garyhomewood.co.uk.planespotter.BuildConfig;
import garyhomewood.co.uk.planespotter.PlaneSpotterApp;
import garyhomewood.co.uk.planespotter.api.PlanesService;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

/**
 *
 */
public class Injector {

    private static final String BASE_URL = "https://www.planespotters.net/Aviation_Photos/";
    private static final String PRAGMA = "Pragma";
    private static final String CACHE_CONTROL = "Cache-Control";

    private static HttpLoggingInterceptor addHttpLogging() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Timber.d(message);
                    }
                });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HEADERS : NONE);
        return httpLoggingInterceptor;
    }

    private static Interceptor addRequestCacheHeader() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request;

                if (PlaneSpotterApp.hasNetwork()) {
                    request = chain.request();
                } else {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();

                    request = chain.request().newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }

                return chain.proceed(request);
            }
        };
    }

    private static Interceptor addResponseCacheHeader() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(2, TimeUnit.MINUTES)
                        .onlyIfCached()
                        .maxStale(300, TimeUnit.SECONDS)
                        .build();

                return response.newBuilder()
                        .removeHeader(PRAGMA)
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    public static PlanesService providePlanesService() {
        Cache cache = new Cache(new File(PlaneSpotterApp.getInstance().getCacheDir(), "http-cache"), 10 * 1024 * 1024);

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(addHttpLogging())
                .addInterceptor(addRequestCacheHeader())
                .addNetworkInterceptor(addResponseCacheHeader())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        return retrofit.create(PlanesService.class);
    }
}
