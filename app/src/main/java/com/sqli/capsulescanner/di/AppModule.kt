package com.sqli.capsulescanner.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.sqli.capsulescanner.BuildConfig
import com.sqli.capsulescanner.data.MainDataSource
import com.sqli.capsulescanner.data.MainDataSourceImpl
import com.sqli.capsulescanner.data.api.ApiService
import com.sqli.capsulescanner.data.api.AppConstants.API_BASE_URL
import com.sqli.capsulescanner.data.api.AppConstants.GOOGLE_VISION_API_BASE_URL
import com.sqli.capsulescanner.data.api.AppConstants.OPEN_AI_API_BASE_URL
import com.sqli.capsulescanner.data.api.googleVision.GoogleVisionApiService
import com.sqli.capsulescanner.data.api.openAI.OpenAIApiService
import com.sqli.capsulescanner.repository.MainRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OpenAIRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleVisionRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DefaultRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OpenAIOkHttp

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DefaultOkHttp

    @Singleton
    @Provides
    @DefaultOkHttp
    fun provideOkHttpClient(
    ): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }


    @Singleton
    @Provides
    @OpenAIOkHttp
    fun provideOpenAiOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val openAiApiKey = BuildConfig.OPENAI_API_KEY
            val originalRequest: Request = chain.request()

            val modifiedRequest: Request = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $openAiApiKey")
                .build()

            chain.proceed(modifiedRequest)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @DefaultRetrofit
    fun providesRetrofit(@DefaultOkHttp okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @GoogleVisionRetrofit
    fun providesGoogleVisionRetrofit(@DefaultOkHttp okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_VISION_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providesApiService(@DefaultRetrofit retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    @OpenAIRetrofit
    fun providesOpenAIRetrofit(@OpenAIOkHttp okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return Retrofit.Builder()
            .baseUrl(OPEN_AI_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    fun providesOpenAIApiService(@OpenAIRetrofit retrofit: Retrofit): OpenAIApiService {
        return retrofit.create(OpenAIApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesGoogleVisionApiService(@GoogleVisionRetrofit retrofit: Retrofit): GoogleVisionApiService {
        return retrofit.create(GoogleVisionApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesMainDataSource(
        apiService: ApiService,
        openAIApiService: OpenAIApiService,
        googleVisionApiService: GoogleVisionApiService
    ): MainDataSource {
        return MainDataSourceImpl(
            apiService,
            openAIApiService,
            googleVisionApiService
        )
    }

    @Singleton
    @Provides
    fun providesMainRepository(
        mainDataSource: MainDataSource
    ): MainRepository {
        return MainRepository(
            mainDataSource
        )
    }

}