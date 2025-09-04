import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Cheng Yufei
 * @create 2025-09-04 23:07
 **/
public class TestFeature {

    @Test
    public void jdk9() {

        // 创建不可变集合，无需在引用第三方类库
        List<String> list = List.of("A", "B", "C");
        list.forEach(System.out::println);

        Map<String, String> map = Map.of("A", "a", "B", "b");
        map.forEach((k, v) -> System.out.println(k + " : " + v));


    }
}
