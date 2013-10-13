package org.wasabi.test

import org.junit.Test as spec
import kotlin.test.assertEquals
import org.wasabi.http.StatusCodes

public class RedirectSpecs: TestServerContext() {

    spec fun redirect_should_set_status_code_and_location() {

        TestServer.reset()
        TestServer.appServer.get("/redirect", {
            response.redirect("http://www.google.com")
        })

        val response = get("http://localhost:3000/redirect", hashMapOf())

        assertEquals(StatusCodes.Found.statusCode, response.statusCode)
        assertEquals("http://www.google.com", response.headers.filter { it.getName() == "Location" }.first().getValue())


    }
}