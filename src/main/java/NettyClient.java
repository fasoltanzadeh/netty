import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import utils.RequestDataEncoder;
import utils.ResponseDecoder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient implements Runnable {
    private ExecutorService executor = null;
    private boolean isRunning;
    private final ClientHandler clientHandler = new ClientHandler();

    public static void main(String[] args) throws Exception {
        NettyClient handler = new NettyClient();
        handler.startClient();
        for (int i = 0; i < 2000000; i++) {
            handler.writeMessage();
            long s = System.nanoTime();
            while(System.nanoTime() - s < 150000){
                Thread.yield();
            }
        }
        handler.writeEndMessage();

    }

    public void writeMessage() {
        try {
            clientHandler.sendMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writeEndMessage() {
        clientHandler.sendEndMessage();
    }

    public void startClient() {
        if (!isRunning) {
            executor = Executors.newFixedThreadPool(1);
            executor.execute(this);
            isRunning = true;
        }
    }

    @Override
    public void run() {
        int SIZE_BUF = 1048576;

        String host = "localhost";
        int port = 12346;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

            ChannelFuture f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}