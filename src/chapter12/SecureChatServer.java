package chapter12;

import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class SecureChatServer extends ChatServer{

	private final SslContext context;
	
	public SecureChatServer(SslContext context) {
		this.context = context;
	}

	@Override
	public ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup2) {
		return new SecureChatServerInitializer(channelGroup2, context);
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		int port = 8080;
		SelfSignedCertificate cert = null;
		try {
			cert = new SelfSignedCertificate();
		} catch (CertificateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SslContext context = null;
		try {
			context = SslContext.newServerContext(cert.certificate(), cert.privateKey());
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final SecureChatServer endpoint = new SecureChatServer(context);
		ChannelFuture f  =endpoint.start(new InetSocketAddress(port));
		Runtime.getRuntime().addShutdownHook(new Thread(){

			@Override
			public void run() {
				endpoint.destroy();
			}
			
		});
		f.channel().close().syncUninterruptibly();
	}
}
