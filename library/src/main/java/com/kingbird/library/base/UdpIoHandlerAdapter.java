package com.kingbirdle.advertistingjar.base;

import android.util.Log;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * UDP IO流程序处理适配器
 * @author panyingdao
 * @date 2018-5-22.
 */
public class UdpIoHandlerAdapter implements IoHandler {

	private static final String TAG = "UdpIoHandlerAdapter";

	public UdpIoHandlerAdapter(UdpIoHandlerListener udpIoHandlerListener) {
		this.udpIoHandlerListener = udpIoHandlerListener;
	}


	public interface UdpIoHandlerListener {
		void exceptionCaught(IoSession ioSession, Throwable throwable);
		void messageReceived(IoSession ioSession, byte[] data);
		void sessionClosed(IoSession ioSession);
		void sessionCreated(IoSession ioSession);
		void sessionIdle(IoSession ioSession, IdleStatus idleStatus);
		void sessionOpened(IoSession ioSession);
		void messageSent(IoSession ioSession, Object object);

	}

	private UdpIoHandlerListener udpIoHandlerListener;

	public void setUdpIoHandlerListener(UdpIoHandlerListener udpIoHandlerListener) {
		this.udpIoHandlerListener = udpIoHandlerListener;
	}

	@Override
	public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {
		udpIoHandlerListener.exceptionCaught(ioSession, throwable);
	}

	@Override
	public void messageReceived(IoSession ioSession, Object arg1) throws Exception {
		byte[] rec = (byte[]) arg1;
		udpIoHandlerListener.messageReceived(ioSession, rec);
	}

	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		udpIoHandlerListener.messageSent(arg0, arg1);
	}

	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		udpIoHandlerListener.sessionClosed(arg0);
		udpIoHandlerListener = null;
	}

	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
		udpIoHandlerListener.sessionCreated(arg0);
	}

	@Override
	public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {
		if (idleStatus == IdleStatus.BOTH_IDLE) {
			Log.e(TAG, "sessionIdle");
		}
		udpIoHandlerListener.sessionIdle(ioSession, idleStatus);
	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
		udpIoHandlerListener.sessionOpened(arg0);
	}

}
