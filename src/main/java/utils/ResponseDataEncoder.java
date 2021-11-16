package utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import models.ResponseData;

public class ResponseDataEncoder extends MessageToByteEncoder<ResponseData> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseData in, ByteBuf out) throws Exception {
        out.writeLong(in.getResData());
    }
}
