import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.*;
import org.HdrHistogram.Histogram;

import java.util.concurrent.TimeUnit;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final long MESSAGE_COUNT = 1000000L;
    private static final long CONFIDENTIAL_THRESHOLD = 100000L;
    private static long sentMessages = 0L;
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
            long elapsedTime = System.nanoTime() - m.readLong();
            receivedMessages++;
            if (receivedMessages % 50000 == 0)
                System.out.println(receivedMessages);
            if(receivedMessages > CONFIDENTIAL_THRESHOLD)
                HISTOGRAM.recordValue(elapsedTime);
            if(receivedMessages > 950000 && receivedMessages%1000 == 0)
                System.out.println(receivedMessages);

            if ( MESSAGE_COUNT - receivedMessages < 100) {
                System.out.println(MESSAGE_COUNT + " " + receivedMessages);
                System.out.println("average elapsedTime : " + HISTOGRAM.getMean() + " " + HISTOGRAM.getStdDeviation());
//                ctx.channel().close();
//                ctx.close();
            }
        } finally {
            ReferenceCountUtil.release(res);
        }
    }
}
