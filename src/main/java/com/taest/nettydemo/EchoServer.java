package com.taest.nettydemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 1.设置端口值（抛出一个 NumberFormatException 如果该端口参数的格式不正确）
 *
 * 2.呼叫服务器的 start（）方法
 *
 * 3.创建 EventLoopGroup
 *
 * 4.创建 ServerBootstrap
 *
 * 5.指定使用 NIO 的传输 Channel
 *
 * 6.设置 socket 地址使用所选的端口
 *
 * 7.添加 EchoServerHandler 到 Channel 的 ChannelPipeline
 *
 * 8.绑定的服务器;sync 等待服务器关闭
 *
 * 9.关闭 channel 和 块，直到它被关闭
 *
 * 10.关机的 EventLoopGroup，释放所有资源。
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage:" + EchoServer.class.getSimpleName() + "<port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler=  new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture sync = bootstrap.bind().sync();
            System.out.println(EchoServer.class.getName() + " started");
            sync.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
