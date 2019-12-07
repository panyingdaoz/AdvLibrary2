package com.kingbird.library.utils;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 数据的过滤器
 * @author panyingdao
 * @date 2018-5-22.
 */

public class ByteArrayCodecFactory implements ProtocolCodecFactory {
    //解码器
    private ByteArrayDecoder decoder;
    //编码器
    private ByteArrayEncoder encoder;

    public ByteArrayCodecFactory() {
        decoder = new ByteArrayDecoder();
        encoder = new ByteArrayEncoder();
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
        return decoder;
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
        return encoder;
    }
}
