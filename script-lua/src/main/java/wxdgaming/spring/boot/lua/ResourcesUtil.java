package wxdgaming.spring.boot.lua;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ResourcesUtil {

    static List<String> resources = null;

    @SneakyThrows public static List<String> getResources(ClassLoader classLoader) {
        if (resources == null) {
            InputStream resourceAsStream = classLoader.getResourceAsStream("resources.json");
            if (resourceAsStream != null) {
                byte[] bytes = IOUtils.toByteArray(resourceAsStream);
                String string = new String(bytes, StandardCharsets.UTF_8);
                resources = JSON.parseObject(string, new TypeReference<ArrayList<String>>() {});
            }
        }

        if (resources == null) {
            resources = Collections.emptyList();
        }

        return resources;
    }

}
