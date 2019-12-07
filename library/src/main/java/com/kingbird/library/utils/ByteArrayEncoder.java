package com.kingbird.library.utils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @author Pan yingdao
 */
public class ByteArrayEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		byte[] bytes = (byte[])message;
		
		IoBuffer buffer = IoBuffer.allocate(bytes.length);
		buffer.setAutoExpand(true);
		
		buffer.put(bytes);
		buffer.flip();
		
		out.write(buffer);
		out.flush();
		
		buffer.free();
	}

}
