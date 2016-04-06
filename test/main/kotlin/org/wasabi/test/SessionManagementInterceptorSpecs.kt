 package org.wasabi.test

import org.junit.Test as spec
 import kotlin.test.assertEquals
 import org.wasabi.interceptors.enableSessionSupport
 import org.junit.Ignore
import org.wasabi.protocol.http.Session

 public class SessionManagementInterceptorSpecs: TestServerContext() {

    class CustomSession(val name: String) {

    }
    @Ignore("Still broken") @spec fun should_have_same_session_between_multiple_requests() {

        TestServer.appServer.enableSessionSupport()
        TestServer.appServer.get("/test_session", {

        })


        // TODO make sure session id stays consistent between requests.
        var response = get("http://localhost:${TestServer.definedPort}/test_session", hashMapOf())

        Thread.sleep(2000)

        val response2 = get("http://localhost:${TestServer.definedPort}/test_session", hashMapOf())

        // assertEquals("Joe", response.body)
    }

}
    