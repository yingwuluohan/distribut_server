package com.self.transac.distribult_server.server;

import com.self.transac.distribult_server.handle.DistributTrancHandler;
import com.self.transac.distribult_server.initializer.DistributTrancInitialiser;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

@Component
public class

DistributTrancServer implements InitializingBean{


    static {
        System.out.println( "*****************server start ********************");
        System.out.println( "                ********* ");
        System.out.println( "*****************server start ********************");
    }

    public DistributTrancServer(){

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startServer();
    }


    public void startServer(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();//接收连接。把连接给worker
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group( bossGroup ,workerGroup).
                    channel(NioServerSocketChannel.class).
                    childHandler( new DistributTrancInitialiser() );



            //监听端口
            ChannelFuture channelFuture = serverBootstrap.bind( 8888 ).sync();
            channelFuture.channel().closeFuture().sync();
        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }



}
