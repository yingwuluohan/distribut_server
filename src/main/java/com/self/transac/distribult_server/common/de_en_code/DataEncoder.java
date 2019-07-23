package com.self.transac.distribult_server.common.de_en_code;

import com.self.transac.distribult_server.common.SerializingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class DataEncoder extends MessageToByteEncoder {

    private Class<?> typeClass;

    public DataEncoder(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) {
        if (typeClass.isInstance(o)) {
            byte[] data = SerializingUtil.serialize(o);
            byteBuf.writeBytes(data);
        }
    }
}
