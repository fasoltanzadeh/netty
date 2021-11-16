package utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import models.RequestData;

import java.util.List;

public class RequestDecoder extends ReplayingDecoder<RequestData> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        RequestData reqData = new RequestData();
        reqData.setReqData(in.readLong());
        out.add(reqData);
    }
}
