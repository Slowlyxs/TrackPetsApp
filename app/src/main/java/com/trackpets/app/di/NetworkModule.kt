package com.trackpets.app.di

import com.trackpets.app.data.datastore.TokenDataStore
import com.trackpets.app.data.remote.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("AuthOkHttpClient")
    fun provideAuthOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenDataStore: TokenDataStore): AuthInterceptor {
        return AuthInterceptor(tokenDataStore)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenDataStore: TokenDataStore,
        authApiServiceProvider: Provider<AuthApiService>
    ): TokenAuthenticator {
        return TokenAuthenticator(tokenDataStore, authApiServiceProvider)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(@Named("AuthOkHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("AuthRetrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService = retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideOwnerApiService(retrofit: Retrofit): OwnerApiService = retrofit.create(OwnerApiService::class.java)

    @Provides
    @Singleton
    fun providePetApiService(retrofit: Retrofit): PetApiService = retrofit.create(PetApiService::class.java)

    @Provides
    @Singleton
    fun provideDeviceApiService(retrofit: Retrofit): DeviceApiService = retrofit.create(DeviceApiService::class.java)

    @Provides
    @Singleton
    fun provideGeofenceApiService(retrofit: Retrofit): GeofenceApiService = retrofit.create(GeofenceApiService::class.java)

    @Provides
    @Singleton
    fun provideAlertApiService(retrofit: Retrofit): AlertApiService = retrofit.create(AlertApiService::class.java)
}
