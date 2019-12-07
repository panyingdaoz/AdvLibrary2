package com.kingbird.library.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kingbird.library.utils.Config.CONSTANT_LENGTH;

/**
 * 文件工具类
 *
 * @author Pan
 */
public class FileUtils {
    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName 要操作的文件名
     * @param content  要执行的命令
     */
    public static void method(String fileName, String content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, false);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  读取系统指定文件
     */
    public static String read(String fileName) {
        String s = "";
        try {
            FileReader reader = new FileReader(fileName);
            BufferedReader br = new BufferedReader(reader);
            s = br.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 获取文件md5值
     */
    public static String getFileMd5(File file) {
        BigInteger bigInt = null;
        MessageDigest digest;
        FileInputStream in;
        try {
            if (!file.isFile()) {
                return null;
            }

            byte[] buffer = new byte[1024];
            int len;
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, CONSTANT_LENGTH)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
//            BigInteger bigInt = new BigInteger(1, digest.digest());
            bigInt = new BigInteger(1, digest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        assert bigInt != null;
        Plog.e("读取本地文件的MD5值："+bigInt.toString(16).toUpperCase());
        return bigInt.toString(16).toUpperCase();
    }

}