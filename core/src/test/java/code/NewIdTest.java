package code;

import org.junit.Test;
import wxdgaming.spring.boot.core.format.NewId;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NewIdTest {

    @Test
    public void newId() throws Exception {
        System.out.println(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));

        System.out.println(Long.MAX_VALUE);
        System.out.println(NewId.Offset32);
        System.out.println((4000 << 17 | 86400));

        final NewId newId = new NewId(80001, 120);
        {
            final long id = newId.newId();
            System.out.println(id);
            System.out.println("serverId=" + newId.hexId(id));
            System.out.println("type=" + newId.type(id));
            System.out.println("idValue=" + newId.idValue(id));
        }

        Set<Long> ids = new HashSet<>();
        while (true) {
            for (int i = 0; i < 16000; i++) {
                final long id = newId.newId();
                if (!ids.add(id)) {
                    System.out.println("重复id " + id + " " + new Date());
                    System.out.println("结束 " + ids.size());
                    return;
                }
                //                log.debug("{}", id);
            }
            Thread.sleep(900);
        }
    }
}