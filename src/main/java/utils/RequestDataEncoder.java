package utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import models.RequestData;

public class RequestDataEncoder extends MessageToByteEncoder<RequestData> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestData reqData, ByteBuf out) throws Exception {
        out.writeLong(reqData.getReqData());
    }
}
