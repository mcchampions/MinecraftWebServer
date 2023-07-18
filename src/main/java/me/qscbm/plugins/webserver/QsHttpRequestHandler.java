package me.qscbm.plugins.webserver;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QsHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        String uri = req.uri().split("\\?")[0];
        if (HttpUtil.is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));
        }
        if (uri.lastIndexOf("/") == uri.length() - 1) {
            uri = uri + WebServerPlugin.getConfiguration().getString("Settings.defaultIndexFile");
        }
        if (uri.contains("%")) {
            int start = uri.indexOf("%") + 1;
            int end = uri.lastIndexOf("%") + 3;
            String temp = uri.substring(start,end).replaceAll("%","");
            String chinese = Util.decode(temp);
            StringBuilder sb = new StringBuilder(uri.substring(0,start-1));
            sb.append(chinese);
            sb.append(uri.substring(end));
            uri = sb.toString();
        }
        String[] args = uri.split("/");
        String[] temp = args[args.length-1].split("\\.");
        String suffix = "." + temp[temp.length-1];
        String contentType = Util.returnContentType(suffix);
        Map<String,String> resMap = new HashMap<>();
        byte[] data;
        resMap.put("method",req.method().name());
        resMap.put("uri",uri);
        String path = WebServerPlugin.getConfiguration().getString("Settings.path");
        File file = new File(path + uri);
        if (!file.exists()) {
            data  = Util.readFile(new File(WebServerPlugin.getConfiguration().getString("Settings.path") +WebServerPlugin.getConfiguration().getString("Settings.404filePath")));
        } else {
            data = Util.readFile(file);
        }
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(data));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}