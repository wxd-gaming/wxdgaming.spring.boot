package code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;
import wxdgaming.spring.boot.core.ReflectContext;
import wxdgaming.spring.boot.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * 资源读取
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-22 14:08
 */
public class NativeClassActionTest {

    @Test
    public void f1() throws Exception {

        TreeSet<String> classNames = new TreeSet<>();
        ReflectContext.Builder.of(Thread.currentThread().getContextClassLoader(), "wxdgaming").build()
                .classStream()
                .forEach(c -> {
                    System.out.println("ReflectContext：" + c);
                    classNames.add(c.getName());
                });

        /*读取所有的资源文件*/
        List<String> strings = FileUtil.jarResources();
        for (String string : strings) {
            string = string.replace("\\", "/");
            if (string.endsWith(".class")) continue;
            System.out.println("resource：" + string);
            classNames.add(string);
        }

        String jsonString = JSON.toJSONString(new ArrayList<>(classNames), SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.PrettyFormat);

        String first = "src/main/resources/resources.json";
        File file = new File(first);
        file.getParentFile().mkdirs();
        Files.write(Paths.get(first),
                jsonString.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

    }

}
