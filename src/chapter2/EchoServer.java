package chapter2;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
	
	private  final int port;
	
	public EchoServer(int port){
		this.port = port;
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
		if(args.length == 1){
			port = Integer.parseInt(args[0]);
		}
		new EchoServer(port).start();
	}

	private void start() throws Exception{
		final EchoServerHandler serverHandler = new EchoServerHandler();
		EventLoopGroup group = new NioEventLoopGroup(); //1 accept and handle new connection
		try{
			ServerBootstrap b = new ServerBootstrap(); //2
			b.group(group)
				.channel(NioServerSocketChannel.class) //3 specify channel type
				.localAddress(new InetSocketAddress(port)) //4 set the local address with the selected port
				.childHandler(new ChannelInitializer<SocketChannel>() {
					/**
					 * when a new connection is accepted, a new child Channel will be created,and the ChannelInitializar 
					 * will ad an instance of your EchoServerHandler to the Channel's ChannelPipeline.
					 */
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(serverHandler);
					}
					
				});
			ChannelFuture f = b.bind().sync(); //bind the server and wait until the bind complete
			f.channel().closeFuture().sync(); //wait until the server's Channel closes
		}finally{
			group.shutdownGracefully().sync();
		}
	}
}
