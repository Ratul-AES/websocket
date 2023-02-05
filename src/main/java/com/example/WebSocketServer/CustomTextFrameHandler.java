package com.example.WebSocketServer;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.util.*;

@ChannelHandler.Sharable
@Slf4j
public class CustomTextFrameHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(CustomTextFrameHandler.class);
    private Queue<byte[]> q = new LinkedList<byte[]>();


    public static Map<Integer, byte[]> imageData = new HashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object frame) throws Exception {

        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            System.out.println("WebSocket Client received message: " + textFrame.text());
            System.out.println("Queue size: "+q.size());

            if(!q.isEmpty()) {
                byte[] a = q.remove();
                BinaryWebSocketFrame sendFrame = new BinaryWebSocketFrame();
                sendFrame.content().writeBytes(a);
                System.out.println("Queue array size: " + a.length);
                System.out.println("Send msg: " + sendFrame.toString());

                ChannelFuture f = ctx.channel().writeAndFlush(sendFrame);
            }else{
                BinaryWebSocketFrame sendFrame = new BinaryWebSocketFrame();
                sendFrame.content().writeByte(0);
                ChannelFuture f = ctx.channel().writeAndFlush(sendFrame);
            }

        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;

            ByteBuffer buf = binaryFrame.content().nioBuffer();
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            System.out.println("Queue size: "+q.size());
            q.add(bytes);

            ctx.fireChannelActive().writeAndFlush(buf);
        }
    }
}

