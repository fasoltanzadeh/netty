import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private long receivedMessages = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try{
            ctx.writeAndFlush(msg).await();
            receivedMessages++;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
////        System.out.println(receivedMessages);
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getStackTrace();
    }
}
