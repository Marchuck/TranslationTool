package translator

import io.reactivex.Observable
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory


class TranslationEngine(private val api: GoogleTranslateApi) {


    fun translateAndroidResourceFile(pathname: String, sourceLanguage: String, targetLanguage: String): Observable<List<String>> {
        val items = getStringCollection(pathname, sourceLanguage)
        println("\n\n\n\ntranslating in progress...\n\n\n")

        return Observable.fromIterable(items)
                .flatMap {
                    executeTranslateRequest(it, sourceLanguage, targetLanguage)
                            .map { formatLikeAndroidDoes(it) }
                }
                .toList()
                .toObservable()
    }

    private fun getStringCollection(pathname: String, sourceLanguage: String): List<Node> {
        val fXmlFile = File(pathname)
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(fXmlFile)

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.documentElement.normalize()

        val stringResourcesList: NodeList = doc.getElementsByTagName("string")

        println("\n\nYour strings in \"$sourceLanguage\"\n")

        var items = emptyList<Node>()
        for (i in 0 until stringResourcesList.length) {
            val node = stringResourcesList.item(i)

            println(formatLikeAndroidDoes(node))

            items += stringResourcesList.item(i)
        }
        return items
    }

    private fun formatLikeAndroidDoes(node: Node): String {
        return formatLikeAndroidDoes(extractResourceName(node), extractResourceValue(node))
    }

    private fun formatLikeAndroidDoes(translation: AndroidTranslation): String {
        return formatLikeAndroidDoes(translation.resourceName, translation.translationValue)
    }

    private fun formatLikeAndroidDoes(name: String, value: String): String {
        return "<string name=\"$name\">$value</string>"
    }

    private fun extractResourceName(element: Node) = element.attributes.item(0).toString().split("\"")[1]

    private fun extractResourceValue(element: Node) = element.textContent

    private fun executeTranslateRequest(node: Node,
                                        sourceLanguage: String,
                                        targetLanguage: String): Observable<AndroidTranslation> {
        if (node.nodeType == Node.ELEMENT_NODE) {
            val elem = node as Element
            val stringResourceName = extractResourceName(elem)
            val stringResourceValue = extractResourceValue(elem)

            return api.translate(sourceLanguage, targetLanguage, stringResourceValue)
                    .toObservable()
                    .filter { it -> !it.isEmpty() }
                    .map {
                        AndroidTranslation(stringResourceName, it)
                    }
                    .onErrorResumeNext(Observable.empty())

        }
        return Observable.empty()
    }
}