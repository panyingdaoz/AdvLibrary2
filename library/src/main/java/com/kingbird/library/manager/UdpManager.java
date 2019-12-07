package com.kingbird.library.manager;

import com.kingbird.library.base.Base;
import com.kingbird.library.utils.ByteArrayCodecFactory;
import com.kingbird.library.utils.Plog;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

import java.net.InetSocketAddress;

import static com.kingbird.library.utils.Config.INITIAL_IP;
import static com.kingbird.library.utils.Config.INITIAL_PORT;

/**
 * UDP管理
 *
 * @author panyingdao
 * @date 2018-5-22.
 */
public class UdpManager {

    private static UdpManager instance;
    private static UdpManager instanceUDP;
    private static String mIP = INITIAL_IP;
    private static int mPort = INITIAL_PORT;
    //    private static boolean mConnected;//udp通讯判断
//    private static NioDatagramConnector connector;//连接器
//    private static ConnectFuture cf;//连接状态
    /**
     *  udp通讯判断
     */
    private static boolean mConnectedUDP;
    /**
     *  连接器
     */
    private static NioDatagramConnector connectorUDP;
    /**
     *  连接状态
     */
    private static ConnectFuture cfUDP;

    private UdpManager() {
    }

    /**
     *  单例模式
     */
    public static UdpManager getInstance() {
        if (instance == null) {
            synchronized (UdpManager.class) {
                if (instance == null) {
                    instance = new UdpManager();
                }
            }
        }
        return instance;
    }

//    public boolean isConnected() {
//        return mConnected;
//    }

//    public static void setConnected(boolean mConnected) {
//        UdpManager.mConnected = mConnected;
//    }

//    public static String getIp() {
//        return mIP;
//    }

    public static void setIp(String mIp) {
        UdpManager.mIP = mIp;
    }

//    public static int getPort() {
//        return mPort;
//    }

    public static void setPort(int mPort) {
        UdpManager.mPort = mPort;
    }

//    public NioDatagramConnector getConnector() {
//        return connector;
//    }

    public NioDatagramConnector getConnectorUdp() {
        return connectorUDP;
    }

    /**
     * 打开通讯
     */
    public boolean connectUdp(IoHandler ioHandler) {
        try {
            if (connectorUDP == null) {
                connectorUDP = new NioDatagramConnector();
                // //创建接收数据的过滤器
                DefaultIoFilterChainBuilder chain = connectorUDP.getFilterChain();
                // 创建接收数据的过滤器
                // 创建接收数据的过滤器
                chain.addLast("yang", new ProtocolCodecFilter(new ByteArrayCodecFactory()));
                //设置日志记录器
                //chain.addLast("Logger", new LoggingFilter());
                //设置连接超时检查时间
                connectorUDP.setConnectTimeoutCheckInterval(10000);
                //设置事件处理器
                connectorUDP.setHandler(ioHandler);
                //建立连接
                Plog.e("UDP建立连接", mIP + "---" + mPort);
                cfUDP = connectorUDP.connect(new InetSocketAddress(mIP, mPort));
                mConnectedUDP = true;
                // 等待是否连接成功，相当于是转异步执行为同步执行。（让线程阻塞）
                //让连接阻塞
                cfUDP.awaitUninterruptibly();
                //让关闭阻塞
                cfUDP.getSession().getCloseFuture().awaitUninterruptibly();

            }
        } catch (Exception e) {
            Plog.e("connect", e.toString());
            mConnectedUDP = false;
        }
        return mConnectedUDP;
    }

    /**
     * 发送消息
     */
    boolean sendUdp(byte[] data) {
        try {
            if (connectorUDP == null) {
                Plog.e("send connector is null");
            }
            if (cfUDP != null) {
                cfUDP.getSession().write(data);
                Plog.e("UDP发送的数据", Base.bytes2HexString(data));
            } else {
                Plog.e("send cf is null");
                return false;
            }
        } catch (Exception e) {
            Plog.e(e.toString());
            return false;
        }
        return true;
    }

    /**
     * 释放资源
     */
    public void closeUdp() {
        try {
            if (cfUDP != null) {
                cfUDP.getSession().close(true);
                cfUDP = null;
            }
            if (connectorUDP != null) {
                connectorUDP.dispose();
                connectorUDP = null;
            }
            if (instanceUDP != null) {
                instanceUDP = null;
            }
            mConnectedUDP = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打开通讯
//    public boolean connect(IoHandler ioHandler) {
//
//        try {
//            if (connector == null) {
//                connector = new NioDatagramConnector();
//                // //创建接收数据的过滤器
//                DefaultIoFilterChainBuilder chain = connector.getFilterChain();
//                // 创建接收数据的过滤器
//                // 创建接收数据的过滤器
//                chain.addLast("yang", new ProtocolCodecFilter(new ByteArrayCodecFactory()));
//                //设置日志记录器
//                //chain.addLast("Logger", new LoggingFilter());
//                //设置连接超时检查时间
//                connector.setConnectTimeoutCheckInterval(10000);
//                //设置事件处理器
//                connector.setHandler(ioHandler);
//                //建立连接
//                Plog.e("通讯建立连接", mIP + "---" + mPort);
//                cf = connector.connect(new InetSocketAddress(mIP, mPort));
//                mConnected = true;
//                // 等待是否连接成功，相当于是转异步执行为同步执行。（让线程阻塞）
//                cf.awaitUninterruptibly();//让连接阻塞
//                cf.getSession().getCloseFuture().awaitUninterruptibly();//让关闭阻塞
//
//            }
//        } catch (Exception e) {
//            Plog.e("connect", e.toString());
//            mConnected = false;
//        }
//        return mConnected;
//    }

//    public void send(byte[] data, IoFutureListener ioFutureListener) {
//        if (cf != null) {
//            cf.getSession().write(data).addListener(ioFutureListener);
//        } else {
//            Plog.e( "send cf is null");
//        }
//    }

    //状态提示
//    boolean send(byte[] data) {
//        try {
//            if (connector == null) {
//                Plog.e("send connector is null");
//            }
//            if (cf != null) {
//                cf.getSession().write(data);
//                Plog.e("UDP发送的数据", Base.bytes2HexString(data));
//            } else {
//                Plog.e( "send cf is null");
//                return false;
//            }
//        } catch (Exception e) {
//            Plog.e( e.toString());
//            return false;
//        }
//        return true;
//    }

    //退出登录  释放资源
//    public void close() {
//        if (cf != null) {
//            cf.getSession().close(true);
//            cf = null;
//        }
//        if (connector != null) {
//            connector.dispose();
//            connector = null;
//        }
//        if (instance != null) {
//            instance = null;
//        }
//        mConnected = false;
//        Plog.e("closeUdp");
//    }

}
