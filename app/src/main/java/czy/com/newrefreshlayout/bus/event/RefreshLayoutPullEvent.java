package czy.com.newrefreshlayout.bus.event;

/**
 * Created by chenzhiyong on 2017/2/10.
 */

public class RefreshLayoutPullEvent {
    private float percent;

    public RefreshLayoutPullEvent(float percent) {
        this.percent = percent;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
