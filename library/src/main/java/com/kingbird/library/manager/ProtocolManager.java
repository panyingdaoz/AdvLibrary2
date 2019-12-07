package com.kingbird.library.manager;

import com.kingbird.library.base.Base;
import com.kingbird.library.litepal.Parameter;
import com.kingbird.library.utils.Const;
import com.kingbird.library.utils.NetUtil;
import com.kingbird.library.utils.SharedPreferencesUtils;
import com.kingbird.library.view.UdpView;

import org.litepal.LitePal;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kingbird.library.base.Base.bytes2HexString;
import static com.kingbird.library.base.Base.getActivity;
import static com.kingbird.library.utils.Config.APK_CHECK;
import static com.kingbird.library.utils.Plog.e;
import static com.kingbird.library.utils.SharedPreferencesUtils.readString;

/**
 * 协议管理类
 *
 * @author panyingdao
 * @date 2017-8-16.
 */
public class ProtocolManager {
    private String deviceId;
    private String rCode;

    private ProtocolManager() {
    }

    private static class HolderClass {
        private final static ProtocolManager INSTANCE = new ProtocolManager();
    }

    public static ProtocolManager getInstance() {
        return HolderClass.INSTANCE;
    }

    /**
     * 参数解析
     */
    public byte[] parseParameter(byte[] receive, int index, int length) {
        byte[] data = new byte[length];
        try {
            System.arraycopy(receive, index, data, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 参数读取应答
     */
    public void readAnswer(String deviceId, String rCode, byte[] command, byte[] number, String parameter, String server) {
        byte[] parameterBytes = new byte[0];
        try {
            parameterBytes = parameter.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[27 + parameterBytes.length];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.readDataAnswer(command, number, parameter);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        dataAnswer(server, data, dataLan);
    }

    /**
     * 读取所以参数应答
     */
    public void readAllDataAnswer(String deviceId, String rCode, byte[] command) {
        List<Parameter> parameter = LitePal.findAll(Parameter.class);
        for (Parameter parameters : parameter) {
            int ipLength = parameters.getIp().getBytes().length;
            int startUrlLength = parameters.getStartPlayUrl().getBytes().length;

            byte[] data = new byte[90 + ipLength + startUrlLength + APK_CHECK.getBytes().length];
            byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
            System.arraycopy(internet, 0, data, 0, internet.length);

            byte[] dataLan = ProtocolDao.readAllDataAnswer(command);
            System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

            netDataAnser(data);
        }
    }

    /**
     * 读参数应答
     */
    public void readAnswer2(String deviceId, String rCode, byte[] command, byte[] number, int parameter, String server) {
        byte[] data = new byte[29];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.readTypeAnswer(command, number, parameter);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        dataAnswer(server, data, dataLan);
    }

    /**
     * 读取节目ID应答
     */
    public void readShowIdAnswer(String deviceId, String rCode, byte[] command, byte[] number, int showId, String server) {
        byte[] data = new byte[31];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.readShowIdAnswer(command, number, showId);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        dataAnswer(server, data, dataLan);
    }

//    public void readAnswer0B(String deviceId, String rCode, byte[] command, byte[] number, int parameter, String server) {
//        byte[] data = new byte[30];
//        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
//        System.arraycopy(internet, 0, data, 0, internet.length);
//
//        byte[] dataLan = ProtocolDao.readDataAnswer0B(command, number, parameter);
//        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);
//
//        dataAnswer(server, data, dataLan);
//    }

    /**
     * 读取开关机时间
     */
    public void readSwitchesTime(String deviceId, String rCode, byte[] command, byte[] number, int startHour, int startMinute, int endHour, int endMinute, String server) {
        byte[] data = new byte[33];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.readAnswer0E(command, number, startHour, startMinute, endHour, endMinute);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        dataAnswer(server, data, dataLan);
    }

    /**
     * 读参数应答
     */
    public void readAnswer3(String deviceId, String rCode, byte[] command, byte[] number, int showId, int count, String server) {
        byte[] data = new byte[33];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.readAnswer3(command, number, showId, count);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        dataAnswer(server, data, dataLan);
    }

//    public void readShowList(String deviceId, String rCode, byte[] command, byte[] number, int condition, int startYear, int startMonths, int startDay,
//                             int startHour, int startMinute, int startSecond, int endYear, int endMonths,
//                             int endDay, int endHour, int endMinute, int endSecond, int duration, int type,
//                             String fileName, int accountName, String showName, int count, int showId, int nextMediaId, int member, int vip, String server) {
//
//        byte[] fileNmaeBytes = new byte[0];
//        byte[] showNmaeBytes = new byte[0];
//        try {
//            fileNmaeBytes = fileName.getBytes("GBK");
//            showNmaeBytes = showName.getBytes("GBK");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        byte[] data = new byte[63 + fileNmaeBytes.length + showNmaeBytes.length];
//        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
//        System.arraycopy(internet, 0, data, 0, internet.length);
//
//        byte[] dataLan = ProtocolDao.getParameter(command, number, condition, startYear, startMonths, startDay, startHour, startMinute, startSecond
//                , endYear, endMonths, endDay, endHour, endMinute, endSecond, duration, type, fileName, accountName, showName, count, showId, nextMediaId, member, vip);
//        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);
//
//        dataAnswer(server, data, dataLan);
//    }

    /**
     * 写参数应答
     */
    public void writeAnswer(String deviceId, String rCode, byte[] command, byte[] number, boolean isResult, String server) {
        byte[] data = new byte[27];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.writeDataAnswer(command, number, isResult);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        dataAnswer(server, data, dataLan);
    }

    /**
     * 互联网全写应答
     */
    public void internetAllWriteAnswer(String deviceId, String rCode, StringBuffer sendStr) {
        byte[] data = new byte[25 + sendStr.toString().length() / 2];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.allWrittenAnswer(sendStr);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        netDataAnser(data);
    }

    /**
     * 互联网节目应答
     */
    public void internetShowAnswer(String deviceId, String rCode, byte[] command, byte[] number, int showNumber, int showId, int isResult, String server) {
        byte[] data = new byte[33];
        byte[] intent = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(intent, 0, data, 0, intent.length);

        byte[] dataLan = ProtocolDao.writeShowAnswer(command, number, showNumber, showId, isResult);
        System.arraycopy(dataLan, 0, data, intent.length, dataLan.length);

        dataAnswer(server, data, dataLan);
    }

    /**
     * 控制回应
     */
    public void controlAnswer(String deviceId, String rCode, byte[] command, byte[] number, int playType, String showId, int isResult, String server) {
        try {
            byte[] data = new byte[32];
            byte[] intent = ProtocolDao.getInternetData(deviceId, rCode);
            System.arraycopy(intent, 0, data, 0, intent.length);

            byte[] dataLan = ProtocolDao.controlAnswer(command, number, playType, Integer.parseInt(showId), isResult);
            System.arraycopy(dataLan, 0, data, intent.length, dataLan.length);

            dataAnswer(server, data, dataLan);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件下载应答
     */
    public void fileDownloadAnswer(String deviceId, String rCode, byte command, byte number, String showId, boolean isResult) {
        byte[] data = new byte[31];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.ftpDataAnswer(command, number, Integer.parseInt(showId), isResult);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        netDataAnser(data);
    }

    /**
     * ftp异常应答
     */
    public void internetFtpErrorAnswer(String deviceId, String rCode, byte command, byte number, int showId, byte error) {
        byte[] data = new byte[31];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.ftpErrorAnswer(command, number, showId, error);

        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        SocketManager.getInstance().send(data);
    }

    /**
     * 文件读取
     */
    public void internetFileRead(String deviceId, String rCode, StringBuffer sendStr, String server) {
        byte[] sendStrBytes = new byte[0];
        try {
            sendStrBytes = sendStr.toString().getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] data = new byte[25 + sendStrBytes.length];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.getInstance().getFileData(sendStr);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        dataAnswer(server, data, dataLan);
    }

    /**
     * 数据应答
     */
    private void dataAnswer(String server, byte[] data, byte[] dataLan) {
        String communicationServices = "client";
        if (communicationServices.equals(server)) {
//            TcpServer.getInstance().send(dataLan);
        } else {
            netDataAnser(data);
        }
    }

    /**
     * 通讯类型应答
     */
    public void netDataAnser(byte[] data) {
        String networkingProtocol = "tcp";
        String protocolTpye = readString(getActivity(), Const.NET_TYPE);
        e("协议类型", protocolTpye);
        if (networkingProtocol.equals(protocolTpye)) {
            int sendLength = SocketManager.getInstance().send(data);
            if (sendLength == 0 && NetUtil.isNetConnected(getActivity())) {
                SocketManager.getInstance().dealWithHeartBeat();
            }
        } else {
            e("UDP回应");
            UdpManager.getInstance().sendUdp(data);
        }
    }

    /**
     * 心跳
     */
    public void sendHeartBeat() {
        String longitudeStr, latitudeStr;
        byte[] communicationData;
        String mac = readString(getActivity(), Const.MAC);

        longitudeStr = readString(getActivity(), Const.LONGITUDE);
        latitudeStr = readString(getActivity(), Const.LATITUDE);

        try {
            List<Parameter> parameter = LitePal.findAll(Parameter.class);
            for (Parameter parameters : parameter) {
                String id = parameters.getDeviceId();
                String rCode = parameters.getrCode();
                if (id != null) {
                    if (longitudeStr.length() == 0 || latitudeStr.length() == 0) {
                        longitudeStr = "+113.822604";
                        latitudeStr = "+22.698299";
                    }
                    if (rCode == null) {
                        rCode = "1111";
                    }
                    SharedPreferencesUtils.writeString(getActivity(), Const.DEVICE_ID, id);
                    SharedPreferencesUtils.writeString(getActivity(), Const.RCODE, rCode);
                    e("最终Mac地址", mac);
                    communicationData = ProtocolDao.sendHeartBeatData(id, rCode, longitudeStr, latitudeStr, mac);
                    netDataAnser(communicationData);
                } else {
                    e("ID为空，请检查数据");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e("心跳异常原因", e.toString());
        }
    }

    /**
     * 读取log应答
     */
    public void logPost(String deviceId, String rCode, boolean isResult) {
        byte[] data = new byte[31];
        byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
        System.arraycopy(internet, 0, data, 0, internet.length);

        byte[] dataLan = ProtocolDao.appLogAnswer(deviceId, isResult);
        System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

        dataAnswer("", data, dataLan);
    }

    /**
     * 重启应答
     */
    public void restartTerminalAnswer(String deviceId, String rCode) {
        byte[] communicationData = ProtocolDao.restartTerminalAnswer(deviceId, rCode);
        e("app重启回应数据", bytes2HexString(communicationData));
        netDataAnser(communicationData);
    }

    /**
     * 播放记录上报
     */
    public void sendTimesReported(String deviceId, String rCode, byte[] command, byte[] number, int showId, int count) {
        try {
            final byte[] data = new byte[35];
            byte[] internet = ProtocolDao.getInternetData(deviceId, rCode);
            System.arraycopy(internet, 0, data, 0, internet.length);

            byte[] dataLan = ProtocolDao.readAnswer3(command, number, showId, count);
            System.arraycopy(dataLan, 0, data, internet.length, dataLan.length);

            UdpManager.getInstance().closeUdp();

            List<Parameter> parameter = LitePal.findAll(Parameter.class);
            for (Parameter parameters : parameter) {
                e("UDP连接");
                UdpView.getInstance().connectUdp(parameters.getIp());

                ExecutorServiceManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSend = UdpManager.getInstance().sendUdp(data);
                        e("次数上报结果", isSend);
                    }
                }, 50, TimeUnit.MILLISECONDS);
            }
        } catch (IndexOutOfBoundsException e) {
            e("下标异常");
        }
    }

    /**
     * 通讯检查心跳
     */
    public boolean checkHeartBeat(byte[] data) {
        e("checkHeartBeat", bytes2HexString(data));
        List<Parameter> parameter = LitePal.findAll(Parameter.class);
        for (Parameter parameters : parameter) {
            deviceId = parameters.getDeviceId();
            rCode = parameters.getrCode();
            e("检查心跳", ProtocolDao.parseHeartBeatData(data, deviceId, rCode));
        }
        return ProtocolDao.parseHeartBeatData(data, deviceId, rCode);
    }
}
