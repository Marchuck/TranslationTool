package translator

import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GoogleTranslateApi(private val translationMapper: TranslationMapper) {

    private val api: TranslateApi

    init {
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        val builder = Retrofit.Builder()
                .baseUrl("https://translate.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
        api = builder.create(TranslateApi::class.java)
    }

    fun translate(sourceLanguage: String,
                  targetLanguage: String,
                  query: String): Single<String> {
        return api.translate(
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                query = query)
                .map { translationMapper.toTranslationResult(it) }
    }
}