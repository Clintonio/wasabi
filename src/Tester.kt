import org.wasabi
import javax.security.auth.login.Configuration
import org.wasabi.http.HttpServer
import org.wasabi.app.AppConfiguration
import org.wasabi.app.AppServer

fun main(args: Array<String>) {

    val server = AppServer()

   // server.get("/", { req, res -> res.send("object")})

    server.routes.get("/good",{ req, res -> res.send("Well this means that routes now work!")})
    server.routes.get("/",{ req, res -> res.send("Hello, how are you")})
    server.start()




}




