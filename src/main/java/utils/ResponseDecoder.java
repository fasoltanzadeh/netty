package utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import models.ResponseData;

import java.util.List;

public class ResponseDecoder extends ReplayingDecoder<ResponseData> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        ResponseData resData = new ResponseData();
        resData.setResData(in.readLong());
        out.add(resData);
    }
}
