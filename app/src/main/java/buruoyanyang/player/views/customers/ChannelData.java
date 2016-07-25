package buruoyanyang.player.views.customers;

/**
 * buruoyanyang.player.views.customers
 * author xiaofeng
 * 16/7/25
 */
public class ChannelData {
    private String mCateId;
    private String mCateName;

    public ChannelData(String cateId, String cateName) {
        this.mCateId = cateId;
        this.mCateName = cateName;
    }

    public String getCateId() {
        return this.mCateId;
    }

    public String getCateName() {
        return this.mCateName;
    }

}
