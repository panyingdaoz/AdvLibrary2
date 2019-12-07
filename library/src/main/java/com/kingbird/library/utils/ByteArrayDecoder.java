package com.kingbird.library.utils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * @author Pan yingdao
 */
public class ByteArrayDecoder extends ProtocolDecoderAdapter {

	@Override
	public void decode(IoSession arg0, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		int limit = in.limit();
		byte[] bytes = new byte[limit];
		
		in.get(bytes);
		
		out.write(bytes);
	}

}
