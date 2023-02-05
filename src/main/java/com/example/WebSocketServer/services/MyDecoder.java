package com.example.WebSocketServer.services;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

class MyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out) {
        byte[] bytes = input.array();
        for (int i = 0; i < bytes.length; i++) {
            System.out.println(bytes[i]+"-");
        }
    }
}