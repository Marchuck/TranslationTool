import translator.GoogleTranslateApi
import translator.TranslationEngine
import translator.TranslationMapper

fun main(args: Array<String>) {

    val path = "path_to_your_xml_file"

    execute(path, "en", "pl")
}

fun execute(path: String, sourceLanguage: String, targetLanguage: String) {
    TranslationEngine(GoogleTranslateApi(TranslationMapper))
            .translateAndroidResourceFile(path, sourceLanguage, targetLanguage)
            .doAfterTerminate {
                println("\n\nDONE!\n\n")
            }
            .subscribe({
                println("${it.size} elements has been parsed\ncontent of values-$targetLanguage/strings.xml :\n")
                for (string in it) {
                    println(string)
                }
            }, { throwable -> println("ERROR: $throwable") })
}
