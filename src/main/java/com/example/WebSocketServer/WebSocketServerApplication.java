package com.example.WebSocketServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class WebSocketServerApplication {
	public static int port = 8089;
	public WebSocketServerApplication() {}

	public static void main(String[] args) {

		//SpringApplication.run(WebSocketServerApplication.class, args);
		CustomTextFrameHandler cs = new CustomTextFrameHandler();
		EventLoopGroup bossGroup = new NioEventLoopGroup();      	//Create a new instance using the default number of threads
		EventLoopGroup workerGroup = new NioEventLoopGroup();	//
		try {
			final ServerBootstrap sb = new ServerBootstrap();
			sb.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(final SocketChannel ch) throws Exception {
							ch.pipeline().addLast(
									new HttpRequestDecoder(),
									new HttpObjectAggregator(65536),
									new HttpResponseEncoder(),
									new WebSocketServerProtocolHandler("/websocket"),
									cs);
						}
					});

			final Channel ch = sb.bind(port).sync().channel();
			System.out.println("Web socket server started at port " + port);

			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
