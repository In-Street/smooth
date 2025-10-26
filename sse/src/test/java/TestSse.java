import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-26 16:22
 **/
public class TestSse {

    @Test
    public void t1() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("AA", "a");

        map.remove("BB");
    }
}
