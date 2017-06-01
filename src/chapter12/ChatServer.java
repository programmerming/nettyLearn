package chapter12;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class ChatServer {

	private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
	private final EventLoopGroup group = new NioEventLoopGroup();
	private Channel channel;
	
	public ChannelFuture start(InetSocketAddress address){
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(group)
			.channel(NioServerSocketChannel.class)
			.childHandler(createInitializer(channelGroup));
		ChannelFuture f = bootstrap.bind(address);
		f.syncUninterruptibly();
		channel = f.channel();
		return f;
	}

	public ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup2) {
		return new ChatServerInitializer(channelGroup2);
	}
	
	public void destroy(){
		if(channel != null){
			channel.close();
		}
		channelGroup.close();
		group.shutdownGracefully();
	}
	
	public static void main(String[] args) {
		int port = 8080;
		final ChatServer endpoint = new ChatServer();
		ChannelFuture f = endpoint.start(new InetSocketAddress(port));
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				endpoint.destroy();
			}
		});
		f.channel().closeFuture().syncUninterruptibly();
	}
}
