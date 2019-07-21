package com.self.transac.distribult_server.initializer;

import com.self.transac.distribult_server.handle.DistributTrancHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class DistributTrancInitialiser extends ChannelInitializer<SocketChannel> {


    public final Logger logger= LoggerFactory.getLogger(getClass());
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        logger.info( "initChannel" + socketChannel.localAddress().getHostName() );
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast( "" , new HttpServerCodec());
        pipeline.addLast( "DistributTrancHandler" , new DistributTrancHandler());




    }
}
