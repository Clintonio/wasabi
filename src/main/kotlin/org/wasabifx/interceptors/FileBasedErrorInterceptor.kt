package org.wasabifx.interceptors

import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response
import java.io.File
import org.wasabifx.app.AppServer
import org.wasabifx.routing.InterceptOn

public class FileBasedErrorInterceptor(val folder: String, val fileExtensions: String = "html", val fallbackGenericFile: String = "error.html"): Interceptor() {
    override fun intercept(request: Request, response: Response): Boolean {
        val path = folder.trim('/')
        var fileToServe = "${path}/${response.statusCode}.${fileExtensions}"
        val file = File(fileToServe)
        if (!file.exists()) {
            fileToServe = "${path}/$fallbackGenericFile"
        }
        response.setFileResponseHeaders(fileToServe)

        return false
    }
}

public fun AppServer.serveErrorsFromFolder(folder: String, fileExtensions: String = "html", fallbackGenericFile: String = "error.html") {
    intercept(FileBasedErrorInterceptor(folder, fileExtensions, fallbackGenericFile), "*", InterceptOn.Error)
}
