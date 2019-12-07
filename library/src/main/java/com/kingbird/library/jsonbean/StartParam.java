package com.kingbird.library.jsonbean;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * 设备启动参数
 *
 * @author Administrator
 * @date 2018/12/6/006.
 */
public class StartParam implements Serializable {

    /**
     * success : true
     * qrCodeUrl : /QRCode/qr00000001285.jpg
     * logoUrl : null
     * landscapeUrl : null
     * portraitUrl : null
     * deviceAppType : 1
     * logoShow : false
     * qrCodeShow : false
     * tissueQRCodeUrl : /QRCode/tissue00000001285.png
     * hasRedPacket : false
     */


    private boolean success;
    private String qrCodeUrl;
    private Object logoUrl;
    private Object landscapeUrl;
    private Object portraitUrl;
    private int deviceAppType;
    private boolean logoShow;
    private boolean qrCodeShow;
    private String tissueQrcodeUrl;
    private boolean hasRedPacket;
    /**
     * animals : {"dog":[{"name":"Rufus","breed":"labrador","count":1,"twoFeet":false},{"name":"Marty","breed":"whippet","count":1,"twoFeet":false}],"cat":{"name":"Matilda"}}
     */

    private AnimalsBean animals;

    public static StartParam objectFromData(String str) {

        return new Gson().fromJson(str, StartParam.class);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Object getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(Object logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Object getLandscapeUrl() {
        return landscapeUrl;
    }

    public void setLandscapeUrl(Object landscapeUrl) {
        this.landscapeUrl = landscapeUrl;
    }

    public Object getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(Object portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public int getDeviceAppType() {
        return deviceAppType;
    }

    public void setDeviceAppType(int deviceAppType) {
        this.deviceAppType = deviceAppType;
    }

    public boolean isLogoShow() {
        return logoShow;
    }

    public void setLogoShow(boolean logoShow) {
        this.logoShow = logoShow;
    }

    public boolean isQrCodeShow() {
        return qrCodeShow;
    }

    public void setQrCodeShow(boolean qrCodeShow) {
        this.qrCodeShow = qrCodeShow;
    }

    public String getTissueQRCodeUrl() {
        return tissueQrcodeUrl;
    }

    public void setTissueQRCodeUrl(String tissueQRCodeUrl) {
        this.tissueQrcodeUrl = tissueQRCodeUrl;
    }

    public boolean isHasRedPacket() {
        return hasRedPacket;
    }

    public void setHasRedPacket(boolean hasRedPacket) {
        this.hasRedPacket = hasRedPacket;
    }

    public AnimalsBean getAnimals() {
        return animals;
    }

    public void setAnimals(AnimalsBean animals) {
        this.animals = animals;
    }

    public static class AnimalsBean {
        /**
         * dog : [{"name":"Rufus","breed":"labrador","count":1,"twoFeet":false},{"name":"Marty","breed":"whippet","count":1,"twoFeet":false}]
         * cat : {"name":"Matilda"}
         */

        private CatBean cat;
        private List<DogBean> dog;

        public CatBean getCat() {
            return cat;
        }

        public void setCat(CatBean cat) {
            this.cat = cat;
        }

        public List<DogBean> getDog() {
            return dog;
        }

        public void setDog(List<DogBean> dog) {
            this.dog = dog;
        }

        public static class CatBean {
            /**
             * name : Matilda
             */

            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class DogBean {
            /**
             * name : Rufus
             * breed : labrador
             * count : 1
             * twoFeet : false
             */

            private String name;
            private String breed;
            private int count;
            private boolean twoFeet;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBreed() {
                return breed;
            }

            public void setBreed(String breed) {
                this.breed = breed;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public boolean isTwoFeet() {
                return twoFeet;
            }

            public void setTwoFeet(boolean twoFeet) {
                this.twoFeet = twoFeet;
            }
        }
    }
}
