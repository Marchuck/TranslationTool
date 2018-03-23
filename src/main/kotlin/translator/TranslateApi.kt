package translator

import com.google.gson.JsonArray
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslateApi {

    @GET("/translate_a/single")
    fun translate(@Query("client") client: String = "gtx",
                  @Query("sl") sourceLanguage: String,
                  @Query("tl") targetLanguage: String,
                  @Query("dt") noIdeaWhatIsIt: String = "t",
                  @Query("q") query: String): Single<JsonArray>
}
