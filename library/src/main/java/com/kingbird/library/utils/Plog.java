package com.kingbird.library.utils;

import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.kingbird.library.base.Base;
import com.kingbird.library.manager.ThreadManager;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static android.os.Build.VERSION_CODES.M;
import static com.kingbird.library.utils.Config.CONSTANT_FOUR;
import static com.kingbird.library.utils.Config.MY_LOG_URL;

/**
 * 说明：
 *
 * @author Pan Yingdao
 * @time : 2019/9/24/024
 */
public class Plog {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String NULL_TIPS = "Log with null object";
    private static final String TAG = "Plog";
    private static final String SUFFIX = ".java";
    private static final int V = 0x1;
    private static final int D = 0x2;
    private static final int I = 0x3;
    private static final int W = 0x4;
    private static final int E = 0x5;
    private static final int A = 0x6;
    private static final int J = 0x7;
    private static boolean DEBUG = true;
    private static String mGlobalTag;
    private static boolean mIsGlobalTagEmpty = true;

    /**
     * init Plog
     */
    public synchronized static void init(boolean debug) {
        KLog.e("Plog 初始化");
        DEBUG = debug;
        //7 day expired
        deleteExpiredLogs(5);
    }

    /**
     * init Plog
     */
    public synchronized static void init(boolean debug, @Nullable String tag) {
        DEBUG = debug;
        mGlobalTag = tag;
        mIsGlobalTagEmpty = TextUtils.isEmpty(mGlobalTag);
        //7 day expired
        deleteExpiredLogs(7);
    }

    public static void v(Object msg) {
        printLog(V, null, msg);
    }

    public static void v(String tag, Object... objects) {
        printLog(V, tag, objects);
    }

    public static void d(Object msg) {
        printLog(D, null, msg);
    }

    public static void d(String tag, Object... objects) {
        printLog(D, tag, objects);
    }

    public static void i(Object msg) {
        printLog(I, null, msg);
    }

    public static void i(String tag, Object... objects) {
        printLog(I, tag, objects);
    }

    public static void w(Object msg) {
        printLog(W, null, msg);
    }

    public static void w(String tag, Object... objects) {
        printLog(W, tag, objects);
    }

    public static void e(Object msg) {
        printLog(E, null, msg);
    }

    public static void e(String tag, Object... objects) {
        printLog(E, tag, objects);
    }

    public static void a(Object msg) {
        printLog(A, null, msg);
    }

    public static void a(String tag, Object... objects) {
        printLog(A, tag, objects);
    }

    public static void json(String jsonFormat) {
        printLog(J, null, jsonFormat);
    }

    public static void json(String tag, String jsonFormat) {
        printLog(J, tag, jsonFormat);
    }

    private static void printLog(int type, String tagStr, Object... objects) {
        if (!DEBUG) {
            return;
        }
        String[] contents = wrapperContent(tagStr, objects);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];
        switch (type) {
            case V:
            case D:
            case I:
            case W:
            case E:
            case A:
//                KLog.e("执行默认");
                printDefault(type, tag, headString, headString + msg);
                break;
            case J:
                printJson(tag, msg, headString);
                break;
            default:
        }
    }

    private static String[] wrapperContent(String tagStr, Object... objects) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement = stackTrace[5];
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1] + SUFFIX;
        }

        if (className.contains("$")) {
            className = className.split("\\$")[0] + SUFFIX;
        }

        String methodName = targetElement.getMethodName();
        int lineNumber = targetElement.getLineNumber();

        if (lineNumber < 0) {
            lineNumber = 0;
        }

        String tag = (tagStr == null ? className : tagStr);

        if (mIsGlobalTagEmpty && TextUtils.isEmpty(tag)) {
            tag = TAG;
        } else if (!mIsGlobalTagEmpty) {
            tag = mGlobalTag;
        }

        String msg = (objects == null) ? NULL_TIPS : getObjectsString(objects);
        String headString = "[ (" + className + ":" + lineNumber + ")#" + methodName + " ] ";

        return new String[]{tag, msg, headString};
    }

    private static String getObjectsString(Object... objects) {
        if (objects.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                if (object == null) {
                    stringBuilder.append("Param").append("[").append(i).append("]").append(" = ").append("null").append("\n");
                } else {
                    stringBuilder.append("Param").append("[").append(i).append("]").append(" = ").append(object.toString()).append("\n");
                }
            }
            return stringBuilder.toString();
        } else {
            Object object = objects[0];
            return object == null ? "null" : object.toString();
        }
    }

    private static void printDefault(int type, String tag, String headString, String msg) {
        int index = 0;
        int maxLength = 4000;
        int countOfSub = msg.length() / maxLength;
        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + maxLength);
                print(type, tag, headString, sub);
                index += maxLength;
            }
            print(type, tag, headString, msg.substring(index));
        } else {
            print(type, tag, headString, msg);
        }
    }

    private static void print(int type, String tag, String headString, String msg) {
        try {
            String paths = MY_LOG_URL + getLogFileName2(new Date());
            File file = new File(paths);
            if (!file.exists()) {
                if (SharedPreferencesUtils.readBoolean(Base.getActivity(), Const.PERMISSONS)) {
                    KLog.e("创建log: " + file.mkdirs());
                }
            }
            switch (type) {
                case V:
//                log2File("V", tag, msg);
                    Log.v(tag, msg);
                    break;
                case D:
                    log2File2("D", tag, paths, msg, headString, getLogFileName(new Date()));
                    Log.d(tag, msg);
                    break;
                case I:
//                log2File("I", tag, msg);
                    Log.i(tag, msg);
                    break;
                case W:
//                log2File("W", tag, msg);
                    Log.w(tag, msg);
                    break;
                case E:
                    if (Build.VERSION.SDK_INT >= M) {
                        if (SharedPreferencesUtils.readBoolean(Base.getActivity(), Const.PERMISSONS)) {
                            plogFile(tag, headString, msg, paths);
                        }
                    } else {
                        plogFile(tag, headString, msg, paths);
                    }
                    Log.e(tag, msg);
                    break;
                case A:
//                log2File2("A", tag, msg, getLogFileName(new Date()));
                    Log.wtf(tag, msg);
                    break;
                default:
            }
        } catch (Exception e) {
            KLog.e("异常原因：" + e.toString());
        }
    }


    private static void plogFile(String tag, String headString, String msg, String paths) {
        File file;
        String lastFileName = listFileSortByModifyTime(paths);
        file = new File(paths + "/" + lastFileName);
        if (file.exists()) {
            long fileSize = file.length() / 1024 / 1024;
            KLog.w("当前文件大小：" + fileSize);
            if (fileSize >= CONSTANT_FOUR) {
                lastFileName = getLogFileName3(new Date(), getFiles(paths, new ArrayList<File>()).size() + 1);
                KLog.w("新文件名：" + lastFileName);
            }
        }
        KLog.w("最终文件名字：" + lastFileName);
        log2File2("E", tag, paths, msg, headString, lastFileName);
    }

    private static void printJson(String tag, String msg, String headString) {
        String message;
        try {
            final int jsonIndent = 4;
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(jsonIndent);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(jsonIndent);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }
        Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.d(tag, "║ " + line);
        }
        Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
    }

    private static void printXml(String tag, String xml, String headString) {
        if (xml != null) {
            xml = formatXml(xml);
            xml = headString + "\n" + xml;
        } else {
            xml = headString + NULL_TIPS;
        }
        Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        String[] lines = xml.split(LINE_SEPARATOR);
        for (String line : lines) {
            if (!(TextUtils.isEmpty(line) || "\n".equals(line) || "\t".equals(line) || TextUtils.isEmpty(line.trim()))) {
                Log.d(tag, "║ " + line);
            }
        }
        Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
    }

    private static String formatXml(String inputXml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(inputXml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (Exception e) {
            e.printStackTrace();
            return inputXml;
        }
    }

    private static String getLogFileName(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date) + ".txt";
    }

    public static String getLogFileName2(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    public static String getLogFileName3(Date date, int number) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date) + "_" + number + ".txt";
    }

    private static synchronized void log2File2(String level, String tag, String path, String msg, String headString, String fileName) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String filePath = path + "/" + ((fileName == null) ? getLogFileName(now) : fileName);
//        KLog.e("文件完整路径：", filePath);
        File file;
        OutputStream outputStream = null;
        try {
            file = new File(filePath);
            outputStream = new FileOutputStream(file, true);

            StringWriter sw = new StringWriter();
//            String sb = level +
//                    " " +
//                    sdf.format(now) +
//                    " " +
//                    headString +
//                    " " +
//                    msg +
//                    "\n" +
//                    sw.toString() +
//                    "\n";
            String sb = level +
                    " " +
                    sdf.format(now) +
                    " " +
                    headString +
                    " " +
                    tag +
                    " " +
                    msg +
                    "\n" +
                    sw.toString() +
                    "\n";
            byte[] data = sb.getBytes();
            for (byte datum : data) {
                outputStream.write(datum);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Plog.e("添加日志异常", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Plog.e("关闭日志文件异常", e);
                }
            }
        }
    }

    /**
     * 检查是否有需要清理文件
     */
    private static void deleteExpiredLogs(final int day) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                String path = MY_LOG_URL;
                File dir = new File(path);
                File[] subFiles = dir.listFiles();
//                KLog.w("这是文件夹数量：" + subFiles.length);
                if (subFiles != null) {
                    int logFileCnt = 0;
                    int expiredLogFileCnt = 0;
                    //one day
                    final int dayMilliseconds = 24 * 60 * 60 * 1000;
                    //day * dayMilliseconds 表示7天的毫秒值
                    long expiredTimeMillis = System.currentTimeMillis() - (day * dayMilliseconds);
                    for (File file : subFiles) {
                        ++logFileCnt;
                        KLog.w("文件最后修改时间", getTime(file.lastModified()));
                        KLog.w("比较时间", getTime(expiredTimeMillis));
                        if (file.lastModified() < expiredTimeMillis) {
                            ++expiredLogFileCnt;
                            path = path + file.getName();
                            boolean deleteResult = deleteDirectory(path);
//                            KLog.w("删除结果：" + deleteResult);
                            if (deleteResult) {
                                KLog.w(TAG, "Delete expired log files successfully:" + file.getName());
                                KLog.w(TAG, "删除过期日志:文件总数=" + (subFiles.length) + ", 日志文件数=" + logFileCnt + ", " +
                                        "过期日志文件数=" + expiredLogFileCnt);
                            } else {
                                KLog.w(TAG, "Delete expired log files failure:" + file.getName());
                            }
                        } else {
                            KLog.w("当前log文件没有过期", file.getName());
                        }
                    }
                }
            }
        });
    }

    private static String getTime(long millisecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        // time为转换格式后的字符串
        return dateFormat.format(new Date(millisecond));
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String filePath) {
        boolean flag;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (File file : files) {
            if (file.isFile()) {
                //删除子文件
                flag = deleteFile(file.getAbsolutePath());
                KLog.w("删除文件");
                if (!flag) {
                    break;
                }
            } else {
                KLog.w("删除目录");
                //删除子目录
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    private static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 获取目录下所有文件(按时间排序)
     */
    private static String listFileSortByModifyTime(String path) {
        String fileName;
//        KLog.e("比较路径：" + path);
        List<File> list = getFiles(path, new ArrayList<File>());
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        int size = list.size();
        if (size > 0) {
            fileName = list.get(list.size() - 1).getName();
            KLog.w("集合最后一个文件名：" + fileName);
        } else {
            fileName = getLogFileName3(new Date(), 0);
        }
        return fileName;
    }

    /**
     * 获取目录下所有文件
     */
    private static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    //对文件进行时间排序
    private static void fileSortByTime(List<File> fileList) {
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File p1, File p2) {
                if (p1.lastModified() < p2.lastModified()) {
                    return -1;
                }
                return 1;
            }
        });
    }

}
