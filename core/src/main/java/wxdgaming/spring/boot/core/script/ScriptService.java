package wxdgaming.spring.boot.core.script;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.ReflectContext;
import wxdgaming.spring.boot.core.SpringReflectContent;
import wxdgaming.spring.boot.core.ann.ReLoad;
import wxdgaming.spring.boot.core.ann.AppStart;
import wxdgaming.spring.boot.core.collection.Table;
import wxdgaming.spring.boot.core.util.AssertUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * script
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-11-18 15:56
 **/
@Slf4j
@Getter
@Service
public class ScriptService {

    private HashMap<String, List<IScript>> scriptMap = new HashMap<>();
    private Table<String, Serializable, IScriptByKey<Serializable>> keyScriptMap = new Table<>();


    @AppStart
    @ReLoad
    @Order(99999999)
    public void initScript(SpringReflectContent springReflectContent) {
        Stream<IScript> beansOfType = springReflectContent.withSuper(IScript.class);

        HashMap<String, List<IScript>> tmpScriptMap = new HashMap<>();
        Table<String, Serializable, IScriptByKey<Serializable>> tmpKeyScriptMap = new Table<>();

        beansOfType.forEach(script -> {
            ReflectContext.getInterfaces(script.getClass())
                    .filter(IScript.class::isAssignableFrom)
                    .forEach(cls -> {
                        String name = cls.getName();
                        List<IScript> iScripts = tmpScriptMap.computeIfAbsent(name, l -> new ArrayList<>());
                        if (IScriptSingleton.class.isAssignableFrom(cls)) {
                            AssertUtil.assertTrueFmt(iScripts.isEmpty(), "脚本：%s, 应该是单例", script.getClass().getName());
                        }
                        iScripts.add(script);
                        if (IScriptByKey.class.isAssignableFrom(cls)) {
                            IScriptByKey<Serializable> scriptByKey = (IScriptByKey) script;
                            IScriptByKey<Serializable> old = tmpKeyScriptMap.put(name, scriptByKey.scriptKey(), scriptByKey);
                            if (IScriptByKeySingleton.class.isAssignableFrom(cls)) {
                                AssertUtil.assertTrueFmt(old == null, "脚本：%s, 应该是单例", script.getClass().getName());
                            }
                        }
                    });
            log.debug("script {} init success", script.getClass().getName());
        });
        scriptMap = tmpScriptMap;
        keyScriptMap = tmpKeyScriptMap;
    }

}
