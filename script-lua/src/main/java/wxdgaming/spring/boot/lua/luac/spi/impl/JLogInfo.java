package wxdgaming.spring.boot.lua.luac.spi.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.lua.luac.spi.LuaSpi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * java log info
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-26 16:00
 **/
@Slf4j
@Component
public class JLogInfo extends LuaSpi {

    @Override public String name() {
        return "lInfo";
    }

    @Override public Object doAction(Lua L, List<Object> args) {
        log.info("{}", args.stream().map(String::valueOf).collect(Collectors.joining(" ")));
        return null;
    }

}
