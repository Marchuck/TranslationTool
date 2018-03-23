package translator

import com.google.gson.JsonArray
import com.google.gson.JsonElement

object TranslationMapper {

    fun toTranslationResult(response: JsonArray): String {
        val firstArrayCandidate = response[0] as JsonElement
        if (firstArrayCandidate.isJsonArray) {
            val firstArray = firstArrayCandidate as JsonArray
            if (firstArray.size() == 1) {
                val secondArrayCandidate = firstArray[0] as JsonElement
                return extractTranslation(secondArrayCandidate)
            } else if (firstArray.size() > 1) {
                var manyWords = ""
                for (elem in firstArray) {
                    val secondArrayCandidate = elem as JsonElement
                    val translation = extractTranslation(secondArrayCandidate);
                    manyWords += translation
                    manyWords += " "
                }
                return manyWords.trim()
            }
        }
        return ""
    }

    private fun extractTranslation(candidate: JsonElement): String {
        return if (candidate.isJsonArray) {
            val secondArray = candidate as JsonArray
            secondArray[0].asString
        } else {
            ""
        }
    }
}