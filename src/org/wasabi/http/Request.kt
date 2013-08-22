package org.wasabi.http

import org.apache.http.client.methods.HttpRequestBase
import io.netty.handler.codec.http.HttpRequest
import java.util.Dictionary
import java.util.ArrayList
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.multipart.InterfaceHttpData
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import io.netty.handler.codec.http.multipart.Attribute
import io.netty.handler.codec.http.CookieDecoder
import java.util.HashMap

public class Request(private val httpRequest: HttpRequest) {

    public val uri: String  = httpRequest.getUri()!!.split('?')[0]
    public val document: String = uri.drop(uri.lastIndexOf("/") + 1)
    public val path: String = uri.take(uri.lastIndexOf("/"))
    public val method: HttpMethod =  httpRequest.getMethod()!!
    public val host: String = getHeader("Host").takeWhile { it != ':' }
    public val isSecure: Boolean = false // TODO: getHeader("Protocol").compareToIgnoreCase("HTTPS") == 0
    public val port : Int = (getHeader("Host").dropWhile { it != ':' }).drop(1).toInt() ?: 80
    public val keepAlive: Boolean  = getHeader("Connection").compareToIgnoreCase("keep-alive") == 0
    public val cacheControl: String = getHeader("Cache-Control")
    public val userAgent: String = getHeader("User-Agent")
    public val accept: Array<String> = getHeader("Accept").split(";")
    public val acceptEncoding: Array<String> = getHeader("Accept-Encoding").split(";")
    public val acceptLanguage: Array<String> = getHeader("Accept-Language").split(";")
    public val acceptCharset: Array<String> = getHeader("Accept-Charset").split(";")
    public val queryParams : HashMap<String, String> = HashMap<String, String>()
    public var routeParams: HashMap<String, String> = HashMap<String, String>()
    public var bodyParams: HashMap<String, String> = HashMap<String, String>()
    public var cookies: HashMap<String, Cookie> = HashMap<String, Cookie>()
    public var contentType: String = getHeader("Content-Type")
    public var chunked: Boolean = getHeader("Transfer-Encoding").compareToIgnoreCase("chunked") == 0
    public var authorization: String = getHeader("Authorization")

    public var session: Session? = null

    public fun init() {
        parseQueryParams()
        parseCookies()
    }

    private fun getHeader(header: String): String {
        var value = httpRequest.headers()?.get(header)
        if (value != null) {
            return value.toString()
        } else {
            return ""
        }
    }

    private fun parseQueryParams() {
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

    private fun parseCookies() {
        val cookieHeader = getHeader("Cookie")
        val cookieSet = CookieDecoder.decode(cookieHeader)
        for (cookie in cookieSet?.iterator()) {
            cookies[cookie.getName().toString()] = Cookie(cookie.getName().toString(), cookie.getValue().toString(), cookie.getPath().toString(), cookie.getDomain().toString(), cookie.isSecure())
        }

    }

    public fun parseBodyParams(httpDataList: MutableList<InterfaceHttpData>) {
        for(entry in httpDataList) {
            addBodyParam(entry)
        }
    }

    public fun addBodyParam(httpData: InterfaceHttpData) {
        // TODO: Add support for other types of attributes (namely file)
        if (httpData.getHttpDataType() == HttpDataType.Attribute) {
            val attribute = httpData as Attribute
            bodyParams[attribute.getName().toString()] = attribute.getValue().toString()
        }
    }


}

