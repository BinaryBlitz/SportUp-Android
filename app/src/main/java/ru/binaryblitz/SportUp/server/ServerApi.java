package ru.binaryblitz.SportUp.server;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class ServerApi {
    private File cacheFile;
    private static final int TIME_OUT = 10;

    public ServerApi(File cacheFile) {
        this.cacheFile = cacheFile;
    }

    @Provides
    @Singleton
    Retrofit provideCall() {
        Cache cache = null;
        try {
            cache = new Cache(cacheFile, 10 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .removeHeader("Pragma")
                                .header("Cache-Control", "max-age=600")
                                .build();

                        okhttp3.Response response = chain.proceed(request);
                        response.cacheResponse();

                        return response;
                    }
                })
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .cache(cache)
                .build();


        return new Retrofit.Builder()
                .baseUrl(ServerConfig.INSTANCE.getApiURL())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public ApiEndpoints providesNetworkService(
            Retrofit retrofit) {
        return retrofit.create(ApiEndpoints.class);
    }

    @Provides
    @Singleton
    public EndpointsService providesService(
            ApiEndpoints networkService) {
        return new EndpointsService(networkService);
    }

}
