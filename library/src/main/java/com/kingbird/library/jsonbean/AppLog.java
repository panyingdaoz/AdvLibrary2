package com.kingbird.library.jsonbean;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 86185
 */
public class AppLog {

    /**
     * key : “kingbird2019”
     * data : {"filename":"\u201c2019-10-29.txt\u201d","content":"\u201c日志内容测试\u201d"}
     */

    private String key;
    private DataBean data;

    public static AppLog objectFromData(String str) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), AppLog.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * filename : “2019-10-29.txt”
         * content : “日志内容测试”
         */

        private String filename;
        private String content;

        public static DataBean objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), DataBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
