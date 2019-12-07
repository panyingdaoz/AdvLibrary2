package com.kingbird.library.manager;

import com.kingbird.library.litepal.Parameter;
import com.kingbird.library.utils.Const;
import com.kingbird.library.utils.Plog;
import com.kingbird.library.utils.SharedPreferencesUtils;

import org.litepal.LitePal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static com.kingbird.library.base.Base.bytes2HexString;
import static com.kingbird.library.base.Base.getActivity;

/**
 * Socket类
 *
 * @author panyingdao
 * @date 2018-1-5.
 */
public class SocketManager {
    private static final String TAG = "SocketManager";
    private static SocketManager instance;
    private static Socket socket;
    //    private static Socket socket2;
    private static DataInputStream din;
    //    private static DataInputStream din2;
    private static DataOutputStream dout;
    //    private static DataOutputStream dout2;
    public boolean mConnected;

    private SocketManager() {
    }

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() {
        return socket;
    }

    /*
    public Socket getSocket2() {
            return socket2;
       }
    */

    //    public void connet2(){
//        InputStreamReader isr;
//        BufferedReader br;
//        OutputStreamWriter osw;
//        BufferedWriter rw;
//        try {
//            Socket socket = new Socket("localhost", 4444);
//            osw = new OutputStreamWriter(socket.getOutputStream());
//            rw = new BufferedWriter(osw);
//            Plog.e("数据",rw);
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//    }
//
    public boolean connect() {
        try {
            if (socket == null) {
                List<Parameter> parameter = LitePal.findAll(Parameter.class);
                for (Parameter parameters : parameter) {
                    String ip = parameters.getIp();
                    int port = parameters.getPort();
                    socket = new Socket(ip, port);
//                    socket2 = new Socket(ip, 9031);
                    dout = new DataOutputStream(socket.getOutputStream());
//                    dout2 = new DataOutputStream(socket2.getOutputStream());
                    din = new DataInputStream(socket.getInputStream());
//                    din2 = new DataInputStream(socket2.getInputStream());

                    Plog.e(TAG, socket.toString());
//                    Plog.e(TAG, socket2.toString());
                }
            }
            mConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
            Plog.e("connect", e.toString());
            //2018-10-26新增
//            reconnect();
            //2018-11-2 由于有部分机子有“failed to connect to localhost/127.0.0.1”这个错误所以取消
            mConnected = false;
        }
        return mConnected;
    }

    public synchronized int send(byte[] data) {
        if (!mConnected) {
            return 0;
        }
        try {
            if (dout != null) {
                dout.write(data);
                Plog.e("发送的数据", bytes2HexString(data));
            }
        } catch (IOException e) {
            Plog.e(e.toString());
            dealWithHeartBeat();
            e.printStackTrace();
            return 0;
        }
        return data.length;
    }

//    public synchronized int sendProtect(byte[] data) {
//        if (!mConnected) {
//            return 0;
//        }
//        try {
////            if (dout2 != null) {
////                dout2.write(data);
////                Plog.e("发送次数上报数据", bytes2HexString(data));
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
////            dealWithProtectHeartBeat();
//            dealWithHeartBeat();
//            Plog.e(e.toString());
//            return 0;
//        }
//        return data.length;
//    }

    public byte[] receive() {
        if (!mConnected || din == null) {
            return null;
        }
        try {
            if (din.available() > 0) {
                byte[] data = new byte[din.available()];
                int ret = din.available();
                if (din.read(data) != ret) {
                    return null;
                }
                return data;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
/*
    public byte[] receiveProtect() {
        if (!mConnected || din2 == null) {
            return null;
        }
        try {
            if (din2.available() > 0) {
                byte[] data = new byte[din2.available()];
                int ret = din2.available();
                if (din2.read(data) != ret) {
                    return null;
                }
                return data;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    */

    public void dealWithHeartBeat() {
        close();
        boolean connect = connect();
        if (connect) {
            ThreadManager.getInstance().doExecute(new Runnable() {
                @Override
                public void run() {
                    SharedPreferencesUtils.writeString(getActivity(), Const.NET_TYPE, "tcp");
                    ProtocolManager.getInstance().sendHeartBeat();
                }
            });
        }
    }

//    private void dealWithProtectHeartBeat() {
//        close();
//        boolean connect = connect();
//        if (connect) {
//            ThreadManager.getInstance().doExecute(new Runnable() {
//                @Override
//                public void run() {
//                    SharedPreferencesUtils.writeString(Const.NET_TYPE, "tcp");
//                    ProtocolManager.getInstance().sendHeartBeat();
//                }
//            });
//        }
//    }

    public void close() {
        try {
//            if (din2 != null) {
//                din2.close();
//                Plog.e(TAG, "din2 已关闭");
//            }
            if (din != null) {
                din.close();
                Plog.e(TAG, "din 已关闭");
            }
//            if (dout2 != null) {
//                dout2.close();
//                Plog.e(TAG, "dout2 已关闭");
//            }
            if (dout != null) {
                dout.close();
                Plog.e(TAG, "dout 已关闭");
            }
//            if (socket2 != null) {
//                socket2.close();
//                Plog.e(TAG, "socket2 已关闭");
//            }
            if (socket != null) {
                socket.close();
                Plog.e(TAG, "socket 已关闭");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Plog.e(TAG, e.toString());
        } finally {
//            socket2 = null;
            socket = null;
//            dout2 = null;
            dout = null;
//            din2 = null;
            din = null;
        }
        mConnected = false;
    }
}
