package com.example.WebSocketServer;

import com.example.WebSocketServer.image.CreateImage;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IStream;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@ChannelHandler.Sharable
@Slf4j
@Service
public class CustomTextFrameHandler extends SimpleChannelInboundHandler<Object> {
    @Autowired
    private CreateImage createImage;

    long nextFrameTime = 0;
    int videoStreamIndex = 0;
    final int videoStreamId = 0;
    final long frameRate = DEFAULT_TIME_UNIT.convert(200, MILLISECONDS);
    final int width = 320;
    final int height = 200;
    final IMediaWriter writer = ToolFactory.makeWriter("test.avi");
    final int a = writer.addVideoStream(videoStreamIndex, videoStreamId, width, height);

    private static final Logger logger = LoggerFactory.getLogger(CustomTextFrameHandler.class);
    private Queue<byte[]> q = new LinkedList<>();
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
            System.out.println("Queue size: " + q.size());
            if(q.size() > 100){
                q.poll();
            }
            q.add(bytes);
            createVideo1(bytes);
            ctx.fireChannelActive().writeAndFlush(buf);

        }
    }

    public void createVideo1(byte[] data){

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            BufferedImage frame = ImageIO.read(bis);

            //File file = new File(imageName);
            //BufferedImage frame = ImageIO.read(file);
            writer.encodeVideo(videoStreamIndex, frame, nextFrameTime, DEFAULT_TIME_UNIT);
            nextFrameTime += frameRate;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

