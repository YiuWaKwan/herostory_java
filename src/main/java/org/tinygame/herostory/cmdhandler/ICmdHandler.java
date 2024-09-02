package org.tinygame.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * 指令处理器接口
 *
 * @param <TCmd>
 */
public interface ICmdHandler<TCmd extends GeneratedMessage> {
    /**
     * 处理指令
     *
     * @param ctx 客户端信道处理器上下文
     * @param cmd 指令
     */
    void handle(ChannelHandlerContext ctx, TCmd cmd);
}
