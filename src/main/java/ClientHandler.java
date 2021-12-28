import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.*;
import org.HdrHistogram.Histogram;

import java.util.concurrent.TimeUnit;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final long MESSAGE_COUNT = 1500000L;
    private static final long CONFIDENTIAL_THRESHOLD = 500000L;
    private long receivedMessages = 0L;
    private static final Histogram HISTOGRAM = new Histogram(TimeUnit.SECONDS.toNanos(10), 3);
    //    private final Timer timer = new HashedWheelTimer();
    private static volatile ChannelHandlerContext context;

    public ClientHandler() {
        HISTOGRAM.reset();
    }

    public void sendMessage() throws InterruptedException {
        while (context == null);

        ByteBuf time = context.alloc().buffer(8);
        time.writeLong(System.nanoTime());
        ChannelFuture f = context.writeAndFlush(time);
//        f.await(150, TimeUnit.MICROSECONDS);
//        f.awaitUninterruptibly();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("started sending messages");
        context = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object res) {
        try
        {
            ByteBuf m = (ByteBuf) res;
            long sentTime = m.readLong();
            long elapseTime = System.nanoTime() - sentTime;
            if(sentTime < 0){
                System.out.println("average elapsedTime : " + HISTOGRAM.getMean() + " std elapsedTime : " + HISTOGRAM.getStdDeviation() + " received : " + receivedMessages);
                ctx.channel().close();
                ctx.close();
            }else if(receivedMessages > CONFIDENTIAL_THRESHOLD) {
                HISTOGRAM.recordValue(elapseTime);
            }
            receivedMessages++;

        } finally {
            ReferenceCountUtil.release(res);
        }
    }

    public void sendEndMessage() {
        ByteBuf lastMessage = context.alloc().buffer(8);
        lastMessage.writeLong(-1);
        ChannelFuture f = context.writeAndFlush(lastMessage);
    }
}
