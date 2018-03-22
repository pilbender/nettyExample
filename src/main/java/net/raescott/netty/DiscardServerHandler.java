package net.raescott.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Richard Scott Smith <pilbender@gmail.com>
 */
public class DiscardServerHandler extends ByteToMessageDecoder {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
		// Discard the received data silently.
		System.out.println("Message Contents: ");
		ByteBuf in = (ByteBuf) msg;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			while (in.isReadable()) {
				stringBuilder.append((char) in.readByte());
			}
		} finally {
			//ReferenceCountUtil.release(msg);
			ctx.close();
		}
		System.out.print(stringBuilder.toString());
		System.out.flush();
		System.out.println();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		/* TODO: This can be deleted if we don't end up using it.
		ChannelFuture channelFuture = ctx.writeAndFlush(""); // TODO: Verify this "" is okay.
		channelFuture.addListener(ChannelFutureListener.CLOSE);
		*/
	}

	// TODO: I don't see that this method is firing.
	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
		while (in.isReadable()) {
			if (in.readChar() == '$') {
				System.out.println("$ character found.");
				out.add(in.readByte());
			} else {
				System.out.println("$ character *not* found.");
				return;
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
