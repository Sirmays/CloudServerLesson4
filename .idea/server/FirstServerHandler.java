package com.lezenford.netty.advanced.server;

import com.lezenford.netty.advanced.common.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;
import java.util.Random;


public class FirstServerHandler extends SimpleChannelInboundHandler<Message> {
    private  int counter = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New active channel");
        TextMessage answer = new TextMessage();
        answer.setText("Successfully connection");
        ctx.writeAndFlush(answer);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println("incoming text message: " + message.getText());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof DateMessage) {
            DateMessage message = (DateMessage) msg;
            System.out.println("incoming date message: " + message.getDate());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof FileRequestMessage) {
            FileRequestMessage frm = (FileRequestMessage) msg;
            final File file = new File((frm.getPath()));
            try (final RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
                while (accessFile.getFilePointer() != accessFile.length()) {
                    final byte[] fileContent;
                    final long available = accessFile.getFilePointer() - accessFile.length();
                    if (available > 64 * 1024) {
                        fileContent = new byte[64 * 1024];
                    } else {
                        fileContent = new byte[(int) available];
                    }

                    final FileContentMessage message = new FileContentMessage();
                    message.setStartPosition(accessFile.getFilePointer());
                    accessFile.read(fileContent);
                    message.setContent(fileContent);
                    message.setLast(accessFile.getFilePointer() == accessFile.length());
                    ctx.writeAndFlush(message);
                    System.out.println("Message sent "+ ++counter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//            try (final FileInputStream inputStream = new FileInputStream(file)) {
//                long startPosition = 0;
//                while (inputStream.available() > 0) {
//                    final byte[] fileContent = inputStream.readNBytes(64 * 11024);
//                    startPosition += fileContent.length;
//                    final FileContentMessage message = new FileContentMessage();
//                    message.setLast(inputStream.available()>0);
//                    message.setContent(fileContent);
//                    message.setStartPosition(startPosition);
//                    ctx.writeAndFlush(message);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException();
//            }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("client disconnect");
    }
}
