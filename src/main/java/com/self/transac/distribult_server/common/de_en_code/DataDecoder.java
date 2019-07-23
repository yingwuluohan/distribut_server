package com.self.transac.distribult_server.common.de_en_code;

import com.self.transac.distribult_server.common.SerializingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class DataDecoder extends ByteToMessageDecoder {

    private Class<?> typeClass;

    public DataDecoder(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    /**
     * 解码
     * @param ctx
     * @param byteBuf
     * @param decoded
     */
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> decoded) {
        int length = byteBuf.readableBytes();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Object obj = SerializingUtil.unserialize(bytes, typeClass);

        decoded.add(obj);
    }
}
