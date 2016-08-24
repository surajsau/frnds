package com.halfplatepoha.frnds;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by surajkumarsau on 24/08/16.
 */
public class ServiceGenerator {

    private static Interceptor apiKeyInterceptor = new Interceptor() {
         @Override
         public Response intercept(Interceptor.Chain chain) throws IOException {
             Request request = chain.request();
             HttpUrl url = request.url().newBuilder().addQueryParameter(IConstants.API_KEY_PARAM, IConstants.API_KEY_VALUE).build();
             request = request.newBuilder().url(url).build();
             return chain.proceed(request);
         }
    };

     private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

     private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                                                            .addInterceptor(apiKeyInterceptor);


    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(IConstants.BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    public static<T> T createService (Class<T> serviceClass) {
        //--adding loggingInterceptor
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(loggingInterceptor);

        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
