package com.self.transac.distribult_server.initializer;

import com.self.transac.distribult_server.common.de_en_code.DataDecoder;
import com.self.transac.distribult_server.common.de_en_code.DataEncoder;
import com.self.transac.distribult_server.handle.DistributTrancHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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
//        pipeline.addLast( "HttpServerCodec" , new HttpServerCodec());
        pipeline.addLast( "decoder" , new DataDecoder( Object.class ));
        pipeline.addLast( "encoder" , new DataEncoder( Object.class ));
        pipeline.addLast( "DistributTrancHandler" , new DistributTrancHandler());

        //new LengthFieldBasedFrameDecoder(1024*8*20, 0, 4,0,4) 最大1024*8*20位为接收数据包，
        // 从0，长4Byte是数据宽度，然后从0，长4Byte剔除后的byte数据包，传送 到后面的handler链处理




    }
}
