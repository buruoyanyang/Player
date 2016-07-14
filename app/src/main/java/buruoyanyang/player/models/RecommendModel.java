package buruoyanyang.player.models;

import java.util.List;

/**
 * buruoyanyang.player.models
 * author xiaofeng
 * 16/7/14
 */
public class RecommendModel {
    /**
     * id : 378141
     * cateId : 26
     * brief :
     * cover : http://tu.dapu.la/Uploads/pic/video/2013-07-21/51ebd069488d5.jpg
     * name : 偷自行车的人
     * rating : 0
     * updateInfo :
     * hotId : 382
     */

    private List<RecommendsEntity> recommends;

    public List<RecommendsEntity> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<RecommendsEntity> recommends) {
        this.recommends = recommends;
    }

    public static class RecommendsEntity {
        private int id;
        private int cateId;
        private String brief;
        private String cover;
        private String name;
        private int rating;
        private String updateInfo;
        private int hotId;

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

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getUpdateInfo() {
            return updateInfo;
        }

        public void setUpdateInfo(String updateInfo) {
            this.updateInfo = updateInfo;
        }

        public int getHotId() {
            return hotId;
        }

        public void setHotId(int hotId) {
            this.hotId = hotId;
        }
    }
}
