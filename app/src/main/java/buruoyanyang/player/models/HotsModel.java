package buruoyanyang.player.models;

import java.util.List;

/**
 * buruoyanyang.player.models
 * author xiaofeng
 * 16/7/14
 */
public class HotsModel {
    /**
     * id : 10
     * seq : 1
     * sImage : http://115.29.190.54:1987/image/flash/xiaoyingjiejie12.jpg
     * status : 1
     * title : 全网独播 中文字幕 点击播放
     * type : 2
     * url : http://115.29.190.54:1987/image/flash/banner/yingjj4567.png
     * videoId : 380146
     */

    private List<FlashEntity> flash;
    /**
     * id : 382
     * cateId : 26
     * districtId : 0
     * kindId : 0
     * name : 独播
     */

    private List<HomeEntity> home;

    public List<FlashEntity> getFlash() {
        return flash;
    }

    public void setFlash(List<FlashEntity> flash) {
        this.flash = flash;
    }

    public List<HomeEntity> getHome() {
        return home;
    }

    public void setHome(List<HomeEntity> home) {
        this.home = home;
    }

    public static class FlashEntity {
        private int id;
        private String seq;
        private String sImage;
        private int status;
        private String title;
        private int type;
        private String url;
        private int videoId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public String getSImage() {
            return sImage;
        }

        public void setSImage(String sImage) {
            this.sImage = sImage;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getVideoId() {
            return videoId;
        }

        public void setVideoId(int videoId) {
            this.videoId = videoId;
        }
    }

    public static class HomeEntity {
        private int id;
        private int cateId;
        private int districtId;
        private int kindId;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCateId() {
            return cateId;
        }

        public void setCateId(int cateId) {
            this.cateId = cateId;
        }

        public int getDistrictId() {
            return districtId;
        }

        public void setDistrictId(int districtId) {
            this.districtId = districtId;
        }

        public int getKindId() {
            return kindId;
        }

        public void setKindId(int kindId) {
            this.kindId = kindId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
