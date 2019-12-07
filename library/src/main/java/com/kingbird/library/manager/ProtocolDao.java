package com.kingbird.library.manager;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.kingbird.library.litepal.Parameter;
import com.kingbird.library.utils.Const;
import com.kingbird.library.utils.Plog;
import com.kingbird.library.utils.SharedPreferencesUtils;

import org.litepal.LitePal;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;

import static com.kingbird.library.base.Base.bytes2HexString;
import static com.kingbird.library.base.Base.getActivity;
import static com.kingbird.library.base.Base.hexToStringGbk;
import static com.kingbird.library.base.Base.intToButeArray;
import static com.kingbird.library.base.Base.strToByteArray1;
import static com.kingbird.library.base.Base.toBytes;
import static com.kingbird.library.utils.Config.APK_CHECK;
import static com.kingbird.library.utils.Config.CONSTANT_EIGHT;
import static com.kingbird.library.utils.Config.CONSTANT_FIVE;
import static com.kingbird.library.utils.Config.CONSTANT_FOUR;
import static com.kingbird.library.utils.Config.CONSTANT_THIRTEEN;
import static com.kingbird.library.utils.Config.CONSTANT_THREE;
import static com.kingbird.library.utils.Config.CONSTANT_TWO;

/**
 * 协议管理
 *
 * @author panyingdao
 * @date 2017-8-16.
 */

public class ProtocolDao {
    /**
     * 帧头
     */
    public static final short HEADER_NORMAL = (short) 0xA881;
    private static final short CONFIGURE_HEADER_NORMAL = (byte) 0x7B;
    /**
     * 包尾
     */
    private static final short END_NORMAL = (byte) 0x7C;
    /**
     * 数据长度
     */
    private static final int DEVICE_ID_LENGTH = 11;
    private static final int RCODE_LENGTH = 4;
    public static final int HEARTBEAT_DATA_LENGTH = 22;
    /**
     * 功能码
     */
    private static final int FUNCTION_CODE = 17;
    /**
     * 终端->服务器
     */
    private static final byte FUNCTION_CODE_HEARTBEAT_REQ = 0x02;

    /**
     * 打开端口包尾
     */
    private static final short OPEN_END_NORMAL_ONE = (short) 0x8C3A;
    private static final short OPEN_END_NORMAL_TWO = (short) 0xDDFA;
    private static final short OPEN_END_NORMAL_THREER = (short) 0x2DFA;
    private static final short OPEN_END_NORMAL_FOUR = (short) 0x7C3A;
    private static final short OPEN_END_NORMAL_FIVE = (short) 0xCDFB;
    private static final short OPEN_END_NORMAL_SIX = (short) 0x9C3B;
    /**
     * 关闭端口包尾
     */
    private static final short CLOSE_END_NORMAL_ONE = (short) 0xCDCA;
    private static final short CLOSE_END_NORMAL_TWO = (short) 0x9C0A;
    private static final short CLOSE_END_NORMAL_THREER = (short) 0x6C0A;
    private static final short CLOSE_END_NORMAL_FOUR = (short) 0x3DCA;
    private static final short CLOSE_END_NORMAL_FIVE = (short) 0x8C0B;

    private static class HolderClass {
        private final static ProtocolDao INSTANCE = new ProtocolDao();
    }

    public static ProtocolDao getInstance() {
        return HolderClass.INSTANCE;
    }

    /**
     * 校验
     */
    private static byte calcCheckSum(byte[] data, int len) {
        byte sum = 0;
        for (int i = 0; i < len; i++) {
            sum += data[i];
        }
        return sum;
    }

    /**
     * 16进制补零
     *
     * @param a   参数长度
     * @param len 可以存储长度
     * @return 返回
     */
    private static String intToHexString(int a, int len) {
        len <<= 1;
        String hexString = Integer.toHexString(a);
        int b = len - hexString.length();
        if (b > 0) {
            for (int i = 0; i < b; i++) {
                hexString = "0" + hexString;
            }
        }
        return hexString;
    }

    /**
     * 验证码 16进制字符转换为字节字符
     */
    private static byte[] hexString2Bytes(String src) {
        if (null == src || 0 == src.length()) {
            return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / CONSTANT_TWO); i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * 字节合并
     */
    private static byte uniteBytes(byte src0, byte src1) {
        byte byteValue = Byte.decode("0x" + new String(new byte[]{src0}));
        byteValue = (byte) (byteValue << 4);
        byte byteValue1 = Byte.decode("0x" + new String(new byte[]{src1}));

        return (byte) (byteValue ^ byteValue1);
    }

    /**
     * 获取0x84包头
     */
    private byte[] getLanCommHeader() {
        byte[] header = new byte[2];
        header[0] = CONFIGURE_HEADER_NORMAL;
        header[1] = (byte) 0x84;
        return header;
    }

    /**
     * 获取0x91包头
     */
    private static byte[] getLanCommHeader1() {
        byte[] header = new byte[2];
        header[0] = CONFIGURE_HEADER_NORMAL;
        header[1] = (byte) 0x91;
        return header;
    }

    /**
     * 获取包尾
     */
    private static byte[] getLanCommEnd() {
        byte[] header = new byte[1];
        header[0] = END_NORMAL;
        return header;
    }

    /**
     * 参数全写应答
     */
    static byte[] allWrittenAnswer(StringBuffer sendStr) {
        byte[] header = getLanCommHeader1();
        byte[] end = getLanCommEnd();
        int length = sendStr.toString().length() / 2;

        byte[] data = new byte[5 + length];
        System.arraycopy(header, 0, data, 0, 2);
        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(length, 2));
        System.arraycopy(bytes, 0, data, 2, 2);
        Plog.e("数据", sendStr.toString());
        Plog.e("数据长度", length);
        System.arraycopy(strToByteArray1(sendStr.toString()), 0, data, 4, length);
        System.arraycopy(end, 0, data, 4 + length, 1);
        return data;
    }

    /**
     * 终端回应读数据
     */
    static byte[] readDataAnswer(byte[] command, byte[] pnumber, String parameter) {

        byte[] parameterBytes = new byte[0];

        try {
            parameterBytes = parameter.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] data = new byte[7 + parameterBytes.length];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        System.arraycopy(command, 0, data, 1, 1);

        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(2 + parameterBytes.length, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        System.arraycopy(pnumber, 0, data, 4, 1);

        //参数长度
        byte[] bytes1 = hexString2Bytes(intToHexString(parameterBytes.length, 1));
        System.arraycopy(bytes1, 0, data, 5, 1);

        //参数、数据
        System.arraycopy(parameterBytes, 0, data, 6, parameterBytes.length);

        //包尾
        data[6 + parameterBytes.length] = END_NORMAL;

        return data;
    }

    /**
     * 参数全读应答
     */
    static byte[] readAllDataAnswer(byte[] command) {
        byte[] data = null;
        List<Parameter> parameter1 = LitePal.findAll(Parameter.class);
        for (Parameter parameters : parameter1) {
            int ipLength = parameters.getIp().getBytes().length;
            int startUrlLength = parameters.getStartPlayUrl().getBytes().length;
            String versionName = APK_CHECK;
            int versionNameLength = versionName.getBytes().length;

            byte[] portBytes = new byte[2];
            portBytes[0] = (byte) ((parameters.getPort() & 0xff00) >> 8);
            portBytes[1] = (byte) (parameters.getPort() & 0xff);
            byte[] heartBeatBytes = new byte[2];
            heartBeatBytes[0] = (byte) ((parameters.getHeartBeat() & 0xff00) >> 8);
            heartBeatBytes[1] = (byte) (parameters.getHeartBeat() & 0xff);

            data = new byte[70 + ipLength + startUrlLength + versionNameLength];
            //帧头
            data[0] = CONFIGURE_HEADER_NORMAL;

            //命令字
            System.arraycopy(command, 0, data, 1, 1);

            //数据长度
            byte[] bytes = hexString2Bytes(intToHexString(65 + ipLength + startUrlLength + versionNameLength, 2));
            System.arraycopy(bytes, 0, data, 2, 2);

            //参数编号
            data[4] = (byte) 1;
            data[5] = (byte) 11;
            System.arraycopy(parameters.getDeviceId().getBytes(), 0, data, 6, 11);

            data[17] = (byte) 2;
            data[18] = (byte) 4;
            System.arraycopy(parameters.getrCode().getBytes(), 0, data, 19, 4);

            data[23] = (byte) 3;
            data[24] = (byte) ipLength;
            System.arraycopy(parameters.getIp().getBytes(), 0, data, 25, ipLength);

            data[25 + ipLength] = (byte) 4;
            data[26 + ipLength] = (byte) 2;
            System.arraycopy(portBytes, 0, data, 27 + ipLength, 2);

            data[29 + ipLength] = (byte) 5;
            data[30 + ipLength] = (byte) 1;
            data[31 + ipLength] = (byte) parameters.getProtocolType();

            data[32 + ipLength] = (byte) 6;
            data[33 + ipLength] = (byte) 1;
            data[34 + ipLength] = (byte) parameters.getStartPlayType();

            data[35 + ipLength] = (byte) 7;
            data[36 + ipLength] = (byte) startUrlLength;
            System.arraycopy(parameters.getStartPlayUrl().getBytes(), 0, data, 37 + ipLength, startUrlLength);

            data[37 + ipLength + startUrlLength] = (byte) 13;
            data[38 + ipLength + startUrlLength] = (byte) 1;
            data[39 + ipLength + startUrlLength] = (byte) parameters.getIsResult();

            data[40 + ipLength + startUrlLength] = (byte) 14;
            data[41 + ipLength + startUrlLength] = (byte) 6;
            data[42 + ipLength + startUrlLength] = (byte) parameters.getStartHour();
            data[43 + ipLength + startUrlLength] = (byte) parameters.getStartMinute();
            data[44 + ipLength + startUrlLength] = (byte) 0;
            data[45 + ipLength + startUrlLength] = (byte) parameters.getEndHour();
            data[46 + ipLength + startUrlLength] = (byte) parameters.getEndMinute();
            data[47 + ipLength + startUrlLength] = (byte) 0;

            data[48 + ipLength + startUrlLength] = (byte) 15;
            data[49 + ipLength + startUrlLength] = (byte) 1;
            data[50 + ipLength + startUrlLength] = (byte) parameters.getScreenSize();

            data[51 + ipLength + startUrlLength] = (byte) 16;
            data[52 + ipLength + startUrlLength] = (byte) 1;
            data[53 + ipLength + startUrlLength] = (byte) parameters.getIsUploading();

            data[54 + ipLength + startUrlLength] = (byte) 17;
            data[55 + ipLength + startUrlLength] = (byte) 2;
            System.arraycopy(heartBeatBytes, 0, data, 56 + ipLength + startUrlLength, 2);

            data[58 + ipLength + startUrlLength] = (byte) 18;
            data[59 + ipLength + startUrlLength] = (byte) versionNameLength;
            System.arraycopy(versionName.getBytes(), 0, data, 60 + ipLength + startUrlLength, versionNameLength);

            data[60 + ipLength + startUrlLength + versionNameLength] = (byte) 19;
            data[61 + ipLength + startUrlLength + versionNameLength] = (byte) 1;
            data[62 + ipLength + startUrlLength + versionNameLength] = (byte) parameters.getDecodingWay();

            data[63 + ipLength + startUrlLength + versionNameLength] = (byte) 20;
            data[64 + ipLength + startUrlLength + versionNameLength] = (byte) 1;
            data[65 + ipLength + startUrlLength + versionNameLength] = (byte) parameters.getNetworkType();

            data[66 + ipLength + startUrlLength + versionNameLength] = (byte) 21;
            data[67 + ipLength + startUrlLength + versionNameLength] = (byte) 1;
            data[68 + ipLength + startUrlLength + versionNameLength] = (byte) parameters.getApplicationType();

            //包尾
            data[69 + ipLength + startUrlLength + versionNameLength] = END_NORMAL;
        }
        return data;
    }
//
//    static byte[] readDataAnswer0B(byte[] command, byte[] number, int oderNumber) {
//        byte[] parameterBytes = new byte[2];
//        parameterBytes[0] = (byte) ((oderNumber & 0xff00) >> 8);
//        parameterBytes[1] = (byte) (oderNumber & 0xff);
//
//        byte[] data = new byte[10];
//
//        //帧头
//        data[0] = CONFIGURE_HEADER_NORMAL;
//
//        //命令字
//        System.arraycopy(command, 0, data, 1, 1);
//
//        //数据长度
//        byte[] bytes = hexString2Bytes(intToHexString(5, 2));
//        System.arraycopy(bytes, 0, data, 2, 2);
//
//        //参数编号
//        System.arraycopy(number, 0, data, 4, 1);
//
//        //参数长度
//        byte[] bytes1 = hexString2Bytes(intToHexString(3, 1));
//        System.arraycopy(bytes1, 0, data, 5, 1);
//
//        //参数、数据
//        System.arraycopy(parameterBytes, 0, data, 6, 2);
//
//        //参数、数据
//        data[8] = (byte) 0;
//
//        //包尾
//        data[9] = END_NORMAL;
//
//        return data;
//    }

    /**
     * 读取应答
     */
    static byte[] readTypeAnswer(byte[] command, byte[] number, int parameter) {

        byte[] parameterBytes = new byte[2];
        //高字节
        parameterBytes[0] = (byte) ((parameter & 0xff00) >> 8);
        //低字节
        parameterBytes[1] = (byte) (parameter & 0xff);

        byte[] data = new byte[9];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        System.arraycopy(command, 0, data, 1, 1);

        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(4, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        System.arraycopy(number, 0, data, 4, 1);

        //参数长度
        byte[] bytes1 = hexString2Bytes(intToHexString(2, 1));
        System.arraycopy(bytes1, 0, data, 5, 1);

        //类型
        System.arraycopy(parameterBytes, 0, data, 6, 2);

        //包尾
        data[8] = END_NORMAL;

        return data;
    }

    /**
     * 读取清单ID应答
     */
    static byte[] readShowIdAnswer(byte[] command, byte[] number, int showId) {

        byte[] data = new byte[11];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        System.arraycopy(command, 0, data, 1, 1);

        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(4, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        System.arraycopy(number, 0, data, 4, 1);

        //参数长度
        byte[] bytes1 = hexString2Bytes(intToHexString(2, 1));
        System.arraycopy(bytes1, 0, data, 5, 1);

        //节目ID
        System.arraycopy(intToButeArray(showId), 0, data, 6, 4);

        //包尾
        data[10] = END_NORMAL;

        return data;
    }

    /**
     * 读取数据应答
     */
    static byte[] readAnswer3(byte[] command, byte[] number, int showId, int count) {

        byte[] data = new byte[15];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        System.arraycopy(command, 0, data, 1, 1);

        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(10, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        System.arraycopy(number, 0, data, 4, 1);

        //参数长度
        byte[] bytes1 = hexString2Bytes(intToHexString(8, 1));
        System.arraycopy(bytes1, 0, data, 5, 1);

        //节目ID
        System.arraycopy(intToButeArray(showId), 0, data, 6, 4);

        //参数、数据
        System.arraycopy(intToButeArray(count), 0, data, 10, 4);

        //包尾
        data[14] = END_NORMAL;

        return data;
    }

    /**
     * 读取开关机时间应答
     */
    static byte[] readAnswer0E(byte[] command, byte[] pNumber, int startHour, int startMinute, int endHour, int endMinute) {

        byte[] data = new byte[13];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        System.arraycopy(command, 0, data, 1, 1);

        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(8, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        System.arraycopy(pNumber, 0, data, 4, 1);

        //参数长度
        byte[] bytes1 = hexString2Bytes(intToHexString(6, 1));
        System.arraycopy(bytes1, 0, data, 5, 1);

        //参数、数据
        data[6] = (byte) startHour;
        data[7] = (byte) startMinute;
        data[8] = (byte) 0;
        data[9] = (byte) endHour;
        data[10] = (byte) endMinute;
        data[11] = (byte) 0;

        //包尾
        data[12] = END_NORMAL;

        return data;
    }

    /**
     * 配置结果终端回应(写数据)
     */
    static byte[] writeDataAnswer(byte[] command, byte[] number, boolean isResult) {

        byte[] data = new byte[7];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        System.arraycopy(command, 0, data, 1, 1);

        //数据长度 ,第一个2表示数据长度，第二个表示字节数
        byte[] bytes = hexString2Bytes(intToHexString(2, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        System.arraycopy(number, 0, data, 4, 1);

        //结果
        data[5] = (byte) (isResult ? 1 : 0);

        //包尾
        data[6] = END_NORMAL;

        return data;
    }

    /**
     * 控制应答
     */
    static byte[] controlAnswer(byte[] command, byte[] number, int playType, int showID, int isResult) {

        byte[] data = new byte[12];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        System.arraycopy(command, 0, data, 1, 1);

        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(7, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        System.arraycopy(number, 0, data, 4, 1);

        data[5] = (byte) playType;

        System.arraycopy(intToButeArray(showID), 0, data, 6, 4);

        //结果
        data[10] = (byte) (isResult);

        //包尾
        data[11] = END_NORMAL;

        return data;
    }

    /**
     * 写参数应答
     */
    static byte[] writeShowAnswer(byte[] command, byte[] number, int showNumber, int showID, int isResult) {

        byte[] showNumberBytes = new byte[2];
        showNumberBytes[0] = (byte) ((showNumber & 0xff00) >> 8);
        showNumberBytes[1] = (byte) (showNumber & 0xff);

        byte[] data = new byte[13];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        System.arraycopy(command, 0, data, 1, 1);

        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(8, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        System.arraycopy(number, 0, data, 4, 1);

        //节目序号
        System.arraycopy(showNumberBytes, 0, data, 5, 2);

        //节目ID
        System.arraycopy(intToButeArray(showID), 0, data, 7, 4);

        //结果
        data[11] = (byte) isResult;

        //包尾
        data[12] = END_NORMAL;

        return data;
    }

    /**
     * ftp数据回应
     */
    static byte[] ftpDataAnswer(byte command, byte number, int showId, boolean isResult) {

        byte[] data = new byte[11];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        data[1] = command;

        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(6, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        data[4] = number;

        //节目ID
        System.arraycopy(intToButeArray(showId), 0, data, 5, 4);

        // 结果
        data[9] = (byte) (isResult ? 2 : 1);

        //包尾
        data[10] = END_NORMAL;

        return data;
    }

    /**
     * 配置结果终端回应(FTP异常)
     */
    static byte[] ftpErrorAnswer(byte command, byte number, int showId, byte error) {

        byte[] data = new byte[11];

        //帧头
        data[0] = CONFIGURE_HEADER_NORMAL;

        //命令字
        data[1] = command;

        //数据长度  第一个2表示数据长度，第二个表示字节数
        byte[] bytes = hexString2Bytes(intToHexString(6, 2));
        System.arraycopy(bytes, 0, data, 2, 2);

        //参数编号
        data[4] = number;

        //节目ID
        System.arraycopy(intToButeArray(showId), 0, data, 5, 4);

        //结果
        data[9] = error;

        //包尾
        data[10] = END_NORMAL;

        return data;
    }

    /**
     * 获取文件数据
     */
    byte[] getFileData(StringBuffer sendStr) {

        byte[] header = getLanCommHeader();
        byte[] end = getLanCommEnd();

        byte[] sendStrBytes = new byte[0];
        try {
            sendStrBytes = sendStr.toString().getBytes("GBK");
            Plog.e("数据String", sendStr.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] data = new byte[5 + sendStrBytes.length];
        System.arraycopy(header, 0, data, 0, 2);
        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(sendStrBytes.length, 2));
        System.arraycopy(bytes, 0, data, 2, 2);
        System.arraycopy(sendStrBytes, 0, data, 4, sendStrBytes.length);
        System.arraycopy(end, 0, data, 4 + sendStrBytes.length, 1);

        return data;
    }

    /**
     * 发送本地文件名
     */
    public static byte[] getListFile(int count, String filename) {

        byte[] filenameBytes = new byte[0];
        try {
            filenameBytes = filename.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[2 + filenameBytes.length];
        //序号
        data[0] = (byte) count;
        //文件名长度
        data[1] = hexString2Bytes(intToHexString(filenameBytes.length, 1))[0];
        //文件名
        System.arraycopy(filenameBytes, 0, data, 2, filenameBytes.length);
        Plog.e("文件名(发送包中的)", hexToStringGbk(bytes2HexString(data)));
        return data;
    }
//
//    static byte[] getParameter(byte[] command, byte[] pNumber, int condition, int startYear, int startMonths, int startDay,
//                               int startHour, int startMinute, int startSecond, int endYear, int endMonths,
//                               int endDay, int endHour, int endMinute, int endSecond, int duration, int type,
//                               String fileName, int accountName, String showName, int count, int showID, int nextMediaId, int member, int vip) {
//        byte[] fileNameBytes = new byte[0];
//        byte[] showNameBytes = new byte[0];
//        try {
//            fileNameBytes = fileName.getBytes("GBK");
//            showNameBytes = showName.getBytes("GBK");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        byte[] countBytes = new byte[2];
//        countBytes[0] = (byte) ((count & 0xff00) >> 8);
//        countBytes[1] = (byte) (count & 0xff);
//        byte[] durationBytes = new byte[2];
//        durationBytes[0] = (byte) ((duration & 0xff00) >> 8);
//        durationBytes[1] = (byte) (duration & 0xff);
//        byte[] startYearBytes = new byte[2];
//        startYearBytes[0] = (byte) ((startYear & 0xff00) >> 8);
//        startYearBytes[1] = (byte) (startYear & 0xff);
//        byte[] endYearBytes = new byte[2];
//        endYearBytes[0] = (byte) ((endYear & 0xff00) >> 8);
//        endYearBytes[1] = (byte) (endYear & 0xff);
//
//        byte[] data = new byte[39 + fileNameBytes.length + 4 + showNameBytes.length];
//
//        //帧头
//        data[0] = CONFIGURE_HEADER_NORMAL;
//
//        //命令字
//        System.arraycopy(command, 0, data, 1, 1);
//
//        //数据长度
//        byte[] bytes = hexString2Bytes(intToHexString(35 + fileNameBytes.length + 4 + showNameBytes.length, 2));
//        System.arraycopy(bytes, 0, data, 2, 2);
//
//        //参数编号
//        System.arraycopy(pNumber, 0, data, 4, 1);
//
//        //参数长度
//        byte[] bytes1 = hexString2Bytes(intToHexString(33 + fileNameBytes.length + 4 + showNameBytes.length, 1));
//        System.arraycopy(bytes1, 0, data, 5, 1);
//        //序号改为节目ID
//        System.arraycopy(intToButeArray(showID), 0, data, 6, 4);
//        //限定条件
//        data[10] = (byte) condition;
//        //开始时间
//        System.arraycopy(startYearBytes, 0, data, 11, 2);
//        data[13] = (byte) startMonths;
//        data[14] = (byte) startDay;
//        data[15] = (byte) startHour;
//        data[16] = (byte) startMinute;
//        data[17] = (byte) startSecond;
//        //结束时间
//        System.arraycopy(endYearBytes, 0, data, 18, 2);
//        data[20] = (byte) endMonths;
//        data[21] = (byte) endDay;
//        data[22] = (byte) endHour;
//        data[23] = (byte) endMinute;
//        data[24] = (byte) endSecond;
//        //限定播放次数
//        System.arraycopy(durationBytes, 0, data, 25, 2);
//        //播放类型
//        data[27] = (byte) type;
//        //文件名长度
//        byte[] bytes2 = hexString2Bytes(intToHexString(fileNameBytes.length, 1));
//        System.arraycopy(bytes2, 0, data, 28, 1);
//        //文件名、播放路径
//        System.arraycopy(fileNameBytes, 0, data, 29, fileNameBytes.length);
//        //商家账号
//        System.arraycopy(intToButeArray(accountName), 0, data, 29 + fileNameBytes.length, 4);
//        //数据长度
//        byte[] bytes4 = hexString2Bytes(intToHexString(showNameBytes.length, 1));
//        System.arraycopy(bytes4, 0, data, 29 + fileNameBytes.length + 4, 1);
//        //节目名
//        System.arraycopy(showNameBytes, 0, data, 30 + fileNameBytes.length + 4, showNameBytes.length);
//        //已播放次数
//        System.arraycopy(countBytes, 0, data, 30 + fileNameBytes.length + 4 + showNameBytes.length, 2);
//        //清单ID
//        System.arraycopy(intToButeArray(nextMediaId), 0, data, 32 + fileNameBytes.length + 4 + showNameBytes.length, 4);
//        //会员类型
//        data[36 + fileNameBytes.length + 4 + showNameBytes.length] = (byte) member;
//        //VIP
//        data[37 + fileNameBytes.length + 4 + showNameBytes.length] = (byte) vip;
//        //包尾
//        data[38 + fileNameBytes.length + 4 + showNameBytes.length] = END_NORMAL;
//
//        return data;
//    }

    /**
     * 发送互联网数据
     */
    static byte[] getInternetData(@NonNull String deviceId, String rCode) {

        byte[] data = new byte[20];
        //帧头
        data[0] = (byte) ((HEADER_NORMAL >> 8) & 0xFF);
        data[1] = (byte) (HEADER_NORMAL & 0xFF);

        //设备ID
        System.arraycopy(deviceId.getBytes(), 0, data, 2, 11);

        //区号，默认：0000
        System.arraycopy(rCode.getBytes(), 0, data, 13, 4);

        //功能码
        data[FUNCTION_CODE] = 0x01;

        //区号启用标志
        System.arraycopy("00".getBytes(), 0, data, 19, 1);

        //校验
        data[data.length - 1] = calcCheckSum(data, data.length - 1);

        return data;
    }

    /**
     * 发送心跳包
     */
    static byte[] sendHeartBeatData(@NonNull String id, String rCode, String longitudeStr, String latitudeStr, @NonNull String mac) {

        String cpuId = SharedPreferencesUtils.readString(getActivity(), Const.CPU_ID);
        int cpuIdLength = cpuId.length();
        Plog.e("cupID和长度", cpuId + "++" + cpuIdLength);

        byte[] data = new byte[58 + cpuIdLength];

        //帧头
        data[0] = (byte) ((HEADER_NORMAL >> 8) & 0xFF);
        data[1] = (byte) (HEADER_NORMAL & 0xFF);

        //设备ID
        System.arraycopy(id.getBytes(), 0, data, 2, 11);

        //区号
        System.arraycopy(rCode.getBytes(), 0, data, 13, 4);

        //功能码
        data[FUNCTION_CODE] = 0x02;

        //数据长度
        byte[] bytes = hexString2Bytes(intToHexString(38 + cpuIdLength, 2));
        assert bytes != null;
        System.arraycopy(bytes, 0, data, 18, 2);

        //经度
        System.arraycopy(longitudeStr.getBytes(), 0, data, 20, 11);
        //纬度
        System.arraycopy(latitudeStr.getBytes(), 0, data, 31, 10);

        //时间
        calender(data, 41, 43, 44, 45, 46, 47);

        //mac
        System.arraycopy(toBytes(mac), 0, data, 48, 6);

        //设备类型
        data[54] = (byte) 3;

        //屏幕类型
        int deviceType = SharedPreferencesUtils.readInt(getActivity(), Const.DEVICEID_TYPE);
        data[55] = (byte) 1;
        data[56] = (byte) deviceType;

//        System.arraycopy(toBytes(cpuId), 0, data, 56, cpuIdLength);38363732323330323537303339313
        System.arraycopy(cpuId.getBytes(), 0, data, 57, cpuIdLength);

        //校验
        data[57 + cpuIdLength] = calcCheckSum(data, data.length - 1);

        return data;
    }

    public static byte[] sendProtectHeartBeatData(String id, String rCode, short functionCode, boolean enableRcode) {
        byte[] data = new byte[20];
        //帧头
        data[0] = (byte) ((HEADER_NORMAL >> 8) & 0xFF);
        data[1] = (byte) (HEADER_NORMAL & 0xFF);

        //设备ID
        System.arraycopy(id.getBytes(), 0, data, 2, 11);

        //区号
        System.arraycopy(rCode.getBytes(), 0, data, 13, 4);

        //功能码
//        data[FUNCTION_CODE] = (byte) (dunctionCode ? 0xAA : 0x02);
        data[FUNCTION_CODE] = (byte) functionCode;

        //启用标志
        data[18] = (byte) (enableRcode ? 0x01 : 0x00);

        //校验
        data[19] = calcCheckSum(data, data.length - 1);

        return data;
    }

    /**
     * 开启充电
     */
    public static byte[] openChargeData(int chargeNumber) {

        byte[] data = new byte[8];

        data[0] = 0x01;
        data[1] = 0x05;
        data[2] = 0x00;

        if (chargeNumber == 1) {
            data[3] = (byte) 0x00;
        } else if (chargeNumber == CONSTANT_TWO) {
            data[3] = (byte) 0x01;
        } else if (chargeNumber == CONSTANT_THREE) {
            data[3] = (byte) 0x02;
        } else if (chargeNumber == CONSTANT_FOUR) {
            data[3] = (byte) 0x03;
        } else if (chargeNumber == CONSTANT_FIVE) {
            data[3] = (byte) 0x04;
        } else {
            data[3] = (byte) 0x05;
        }

        data[4] = (byte) 0xFF;
        data[5] = 0x00;

        if (chargeNumber == 1) {
            data[6] = (byte) ((OPEN_END_NORMAL_ONE >> 8) & 0xFF);
            data[7] = (byte) (OPEN_END_NORMAL_ONE & 0xFF);
        } else if (chargeNumber == CONSTANT_TWO) {
            data[6] = (byte) ((OPEN_END_NORMAL_TWO >> 8) & 0xFF);
            data[7] = (byte) (OPEN_END_NORMAL_TWO & 0xFF);
        } else if (chargeNumber == CONSTANT_THREE) {
            data[6] = (byte) ((OPEN_END_NORMAL_THREER >> 8) & 0xFF);
            data[7] = (byte) (OPEN_END_NORMAL_THREER & 0xFF);
        } else if (chargeNumber == CONSTANT_FOUR) {
            data[6] = (byte) ((OPEN_END_NORMAL_FOUR >> 8) & 0xFF);
            data[7] = (byte) (OPEN_END_NORMAL_FOUR & 0xFF);
        } else if (chargeNumber == CONSTANT_FIVE) {
            data[6] = (byte) ((OPEN_END_NORMAL_FIVE >> 8) & 0xFF);
            data[7] = (byte) (OPEN_END_NORMAL_FIVE & 0xFF);
        } else {
            data[6] = (byte) ((OPEN_END_NORMAL_SIX >> 8) & 0xFF);
            data[7] = (byte) (OPEN_END_NORMAL_SIX & 0xFF);
        }

        return data;
    }

    /**
     * 结束充电
     */
    public static byte[] closeChargeData(int chargeNumber) {

        byte[] data = new byte[8];

        data[0] = 0x01;
        data[1] = 0x05;
        data[2] = 0x00;

        if (chargeNumber == 1) {
            data[3] = (byte) 0x00;
        } else if (chargeNumber == CONSTANT_TWO) {
            data[3] = (byte) 0x01;
        } else if (chargeNumber == CONSTANT_THREE) {
            data[3] = (byte) 0x02;
        } else if (chargeNumber == CONSTANT_FOUR) {
            data[3] = (byte) 0x03;
        } else if (chargeNumber == CONSTANT_FIVE) {
            data[3] = (byte) 0x04;
        } else {
            data[3] = (byte) 0x05;
        }

        data[4] = (byte) 0x00;
        data[5] = 0x00;

        if (chargeNumber == 1) {
            data[6] = (byte) ((CLOSE_END_NORMAL_ONE >> 8) & 0xFF);
            data[7] = (byte) (CLOSE_END_NORMAL_ONE & 0xFF);
        } else if (chargeNumber == CONSTANT_TWO) {
            data[6] = (byte) ((CLOSE_END_NORMAL_TWO >> 8) & 0xFF);
            data[7] = (byte) (CLOSE_END_NORMAL_TWO & 0xFF);
        } else if (chargeNumber == CONSTANT_THREE) {
            data[6] = (byte) ((CLOSE_END_NORMAL_THREER >> 8) & 0xFF);
            data[7] = (byte) (CLOSE_END_NORMAL_THREER & 0xFF);
        } else if (chargeNumber == CONSTANT_FOUR) {
            data[6] = (byte) ((CLOSE_END_NORMAL_FOUR >> 8) & 0xFF);
            data[7] = (byte) (CLOSE_END_NORMAL_FOUR & 0xFF);
        } else if (chargeNumber == CONSTANT_FIVE) {
            data[6] = (byte) ((CLOSE_END_NORMAL_FIVE >> 8) & 0xFF);
            data[7] = (byte) (CLOSE_END_NORMAL_FIVE & 0xFF);
        }

        return data;
    }

    /**
     * 重启终端回应
     */
    static byte[] restartTerminalAnswer(String deviceId, String rCode) {

        byte[] data = new byte[20];

        //帧头
        data[0] = (byte) ((HEADER_NORMAL >> 8) & 0xFF);
        data[1] = (byte) (HEADER_NORMAL & 0xFF);

        //设备ID
        System.arraycopy(deviceId.getBytes(), 0, data, 2, 11);

        //区号
        System.arraycopy(rCode.getBytes(), 0, data, 13, 4);

        //功能码
        data[17] = (byte) 0xAA;

        //区号启用标志
        data[18] = (byte) 0;

        //校验
        data[19] = calcCheckSum(data, data.length - 1);

        return data;
    }

    /**
     * 终端log上传回应
     */
    public static byte[] appLogAnswer(String deviceId, boolean isResult) {

        byte[] data = new byte[17];

        //帧头
        data[0] = (byte) ((HEADER_NORMAL >> 8) & 0xFF);
        data[1] = (byte) (HEADER_NORMAL & 0xFF);

        //设备ID
        System.arraycopy(deviceId.getBytes(), 0, data, 2, 11);

        //功能码
        data[13] = (byte) 0x0B;

        //数据长度
        data[14] = (byte) 1;

        //LOG 上传结果
        data[15] = (byte) (isResult ? 1 : 0);

        //校验
        data[16] = calcCheckSum(data, data.length - 1);

        return data;
    }

    /**
     * 终端更新回应
     */
    public static byte[] appUpdateAswer(String deviceId, boolean isResult) {

        byte[] data = new byte[17];

        //帧头
        data[0] = (byte) ((HEADER_NORMAL >> 8) & 0xFF);
        data[1] = (byte) (HEADER_NORMAL & 0xFF);

        //设备ID
        System.arraycopy(deviceId.getBytes(), 0, data, 2, 11);

        //功能码
        data[13] = (byte) 0x0C;

        //数据长度
        data[14] = (byte) 1;

        //LOG 上传结果
        data[15] = (byte) (isResult ? 1 : 0);

        //校验
        data[16] = calcCheckSum(data, data.length - 1);

        return data;
    }

    /**
     * 时间获取
     */
    private static void calender(byte[] data, int a, int b, int c, int d, int e, int f) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        byte[] yearBytes = hexString2Bytes(intToHexString(year, 2));
        System.arraycopy(yearBytes, 0, data, a, 2);
        data[b] = (byte) month;
        data[c] = (byte) date;
        data[d] = (byte) hour;
        data[e] = (byte) minute;
        data[f] = (byte) second;
    }

    /**
     * 心跳数据解析
     */
    static boolean parseHeartBeatData(byte[] data, String id, String rCode) {
        short s = 0xFF;
        if (data[0] != (byte) ((HEADER_NORMAL >> CONSTANT_EIGHT) & s)
                || data[1] != (byte) (HEADER_NORMAL & 0xFF)) {
            return false;
        }
        if (!checkEquals(data, CONSTANT_TWO, DEVICE_ID_LENGTH, id)) {
            return false;
        }
        if (!checkEquals(data, CONSTANT_THIRTEEN, RCODE_LENGTH, rCode)) {
            return false;
        }
        if (data[FUNCTION_CODE] != FUNCTION_CODE_HEARTBEAT_REQ) {
            return false;
        }
        return true;
    }

    /**
     * 通讯数据校验
     */
    private static boolean checkEquals(byte[] data, int start, int len, String expectData) {
        if (expectData.length() != len) {
            return false;
        }
        byte[] dest = new byte[len];
        System.arraycopy(expectData.getBytes(), 0, dest, 0, len);

        for (int i = 0; i < len; i++) {
            if (dest[i] != data[start + i]) {
                return false;
            }
        }
        return true;
    }
}
