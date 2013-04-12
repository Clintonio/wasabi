package org.wasabi.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.HttpVersion
import io.netty.util.CharsetUtil
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInboundMessageHandlerAdapter


public class Response() {

    public var etag: String = ""
    public var location: String = ""
    public var allow: String = ""
    public var buffer: String = ""

    private var statusCode: Int = 200
    private var statusDescription: String = ""

    fun send(message: String) {
        buffer = message
    }

    public fun setStatusCode(statusCode: Int, statusDescription: String) {
        this.statusCode = statusCode
        this.statusDescription = statusDescription
    }
    public fun writeResponse(ctx: ChannelHandlerContext) {
        var response = DefaultFullHttpResponse(HttpVersion("HTTP", 1, 1, true), HttpResponseStatus(statusCode,statusDescription),  Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8))
        addHeaders(response)
        ctx.nextOutboundMessageBuffer()?.add(response)
        ctx.flush()?.addListener(ChannelFutureListener.CLOSE)
    }

    private fun addHeaders(response: DefaultHttpResponse) {
        if (allow != "") {
            response.headers()?.add("Allow", allow)
        }
    }
}
