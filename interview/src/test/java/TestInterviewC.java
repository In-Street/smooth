import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 *
 * @author Cheng Yufei
 * @create 2026-10-29 15:50
 **/
public class TestInterviewC {

    @Test
    public void t1() {
        LocalDateTime from = LocalDateTime.from(Instant.ofEpochMilli(1793260245091L).atZone(ZoneId.systemDefault()));
        System.out.println(from);
    }
}
