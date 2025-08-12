package wxdgaming.game.server.script.http.gm.dynamiccode;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.chatset.Base64Util;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.core.loader.ClassBytesLoader;
import wxdgaming.spring.boot.core.zip.GzipUtil;

import java.util.Map;

@RestController
@RequestMapping(path = "/888")
public class DynamicCodeScript extends HoldRunApplication {

    static final String SIGN = "ABC";

    @RequestMapping(path = "/dynamic")
    public RunResult dynamic(HttpServletRequest request,
                             @RequestParam("sign") String sign,
                             @RequestParam("code") String codeBase64) throws Exception {
        if (!SIGN.equals(sign)) return RunResult.fail("签名错误");
        System.out.println(codeBase64);
        String zipJson = Base64Util.decode(codeBase64);
        String json = GzipUtil.unGzip2String(zipJson);
        Map<String, byte[]> parseMap = FastJsonUtil.parseMap(json, String.class, byte[].class);
        /*map 内容 写入某个文件夹，比如 target/dynamic/ */
        // new URLClassLoader(new URL[]{new URL("file:///target/dynamic/")});
        try (ClassBytesLoader classBytesLoader = new ClassBytesLoader(parseMap, DynamicCodeScript.class.getClassLoader())) {
            classBytesLoader.loadAll();
            Map<String, Class<?>> loadClassMap = classBytesLoader.getLoadClassMap();
            for (Class<?> cls : loadClassMap.values()) {
                if (IGmDynamic.class.isAssignableFrom(cls)) {
                    IGmDynamic gmDynamic = (IGmDynamic) cls.getDeclaredConstructor().newInstance();
                    Object result = gmDynamic.execute(runApplication);
                    System.out.println(result);
                    return RunResult.ok().data(result);
                }
            }
        }
        return RunResult.fail("没有找到对应的动态脚本");
    }

}
