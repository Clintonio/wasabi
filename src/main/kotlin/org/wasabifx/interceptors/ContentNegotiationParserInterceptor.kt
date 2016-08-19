package org.wasabifx.interceptors

import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response
import org.wasabifx.protocol.http.NegotiateOn
import org.wasabifx.app.AppServer
import org.wasabifx.routing.InterceptOn
import java.util.PriorityQueue
import java.util.HashMap
import java.util.LinkedList


public class ContentNegotiationParserInterceptor(val mappings: HashMap<String, String> = HashMap()): Interceptor() {
    val ACCEPT_HEADER = 0
    val QUERY_PARAM = 1
    val EXTENSION = 2
    val orderQueue = LinkedList<Int>()
    var queryParameterName = ""

    override fun intercept(request: Request, response: Response): Boolean {
        var contentType = ""

        response.requestedContentTypes.clear()
        while (contentType == "" && orderQueue.size > 0) {
            var connegType = orderQueue.removeFirst()
            when (connegType) {
                ACCEPT_HEADER -> {
                    for (mediaTypes in request.accept) {
                        response.requestedContentTypes.add(mediaTypes.key)
                    }
                }
                QUERY_PARAM -> {
                    request.queryParams.get(queryParameterName)?.let {
                        mappings.get(it)?.let {
                            response.requestedContentTypes.add(it)
                        }
                    }
                }
                EXTENSION -> {
                    request.document.dropWhile { it != '.' }.let {
                        mappings.get(it)?.let {
                            response.requestedContentTypes.add(it.toString())
                        }
                    }
                }
                else -> {
                    throw IllegalArgumentException("unknown conneg")
                }
            }

        }
        return true
    }

    fun onAcceptHeader(): ContentNegotiationParserInterceptor {
        orderQueue.add(ACCEPT_HEADER)
        return this
    }

    fun onQueryParameter(queryParameterName: String = "format"): ContentNegotiationParserInterceptor {
        this.queryParameterName = queryParameterName
        orderQueue.add(QUERY_PARAM)
        return this
    }

    fun onExtension(): ContentNegotiationParserInterceptor {
        orderQueue.add(EXTENSION)
        return this
    }

}

fun AppServer.parseContentNegotiationHeaders(path: String = "*", mappings: HashMap<String, String> = hashMapOf(Pair("json","application/json"), Pair("xml", "application/xml")), body: ContentNegotiationParserInterceptor.()->Unit)  {
    val conneg = ContentNegotiationParserInterceptor(mappings)
    conneg.body()
    intercept(conneg, path, InterceptOn.PostExecution)
}
