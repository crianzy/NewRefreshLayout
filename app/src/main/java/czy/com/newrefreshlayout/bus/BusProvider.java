package czy.com.newrefreshlayout.bus;

import com.squareup.otto.Bus;

/**
 * Created by chenzhiyong on 16/4/27.
 */
public class BusProvider {
    private static final Bus BUS = new Bus();

    private BusProvider() {
    }

    public static Bus getInstance() {
        return BUS;
    }

    public static void register(Object o) {
        try {
            BUS.register(o);
        } catch (Exception e) {

        }
    }

    public static void unregister(Object o) {
        try {
            BUS.unregister(o);
        } catch (Exception e) {

        }
    }

    public static void post(Object event) {
        BUS.post(event);
    }
}