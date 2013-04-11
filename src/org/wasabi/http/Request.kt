package org.wasabi.http

import org.apache.http.client.methods.HttpRequestBase
import io.netty.handler.codec.http.HttpRequest
import java.util.Dictionary
import org.wasabi.routing.QueryParams
import java.util.ArrayList

public class Request(private val httpRequest: HttpRequest) {

    public val uri: String  = httpRequest.getUri()!!.split('?')[0]
    public val method: HttpMethod =  mapNettyHttpMethodToOwn(httpRequest.getMethod()!!)
    public val host: String = getHeader("Host").takeWhile { it != ':' }
    public val port : Int = (getHeader("Host").dropWhile { it != ':' }).drop(1).toInt() ?: 80
    public val keepAlive: Boolean  = getHeader("Connection").compareToIgnoreCase("keep-alive") == 0
    public val cacheControl: String = getHeader("Cache-Control")
    public val userAgent: String = getHeader("User-Agent")
    public val accept: Array<String> = getHeader("Accept").split(",")
    public val acceptEncoding: Array<String> = getHeader("Accept-Encoding").split(",")
    public val acceptLanguage: Array<String> = getHeader("Accept-Language").split(",")
    public val acceptCharset: Array<String> = getHeader("Accept-Charset").split(",")
    public val queryParams: QueryParams = QueryParams()

    private fun getHeader(header: String): String {

        return httpRequest.headers()?.get(header).toString()
    }

    public fun parseQueryParams() {

        val urlParams = httpRequest.getUri()!!.split('?')
        if (urlParams.size == 2) {
            val queryNameValuePair = urlParams[1].split("&")
            for (entry in queryNameValuePair) {
                val nameValuePair = entry.split('=')
                if (nameValuePair.size == 2) {
                    queryParams[nameValuePair[0]] = nameValuePair[1]
                } else {
                    queryParams[nameValuePair[0]] = ""
                }
           }
        }
    }

// Cookie=jetbrains.charisma.main.security.PRINCIPAL=OWM3N2U5ZTllM2Y1ZWI2ZjUwMjM2MjRiNzdmOTE1MTkwMWZkNmU5ZTA5MDNkZjdjYzgzMGNkN2RiMjU1NzUyZTpoaGFyaXJp
// RouteParams
// PostFields


}

