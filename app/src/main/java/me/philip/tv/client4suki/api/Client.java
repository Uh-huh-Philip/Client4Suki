package me.philip.tv.client4suki.api;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by phili on 6/13/2017.
 */

public class Client {
    private Endpoint mEndpoint;
    private Retrofit mRetrofit;
    private PersistentCookieJar mPersistentCookieJar;

    public Endpoint getEndpoint(){
        if(mEndpoint!= null){
            return mEndpoint;
        }
        return null;
    }

    public void initial(Context context, String host){
        mPersistentCookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(mPersistentCookieJar)
                .build();

        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(host)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mEndpoint = mRetrofit.create(Endpoint.class);
    }
}
