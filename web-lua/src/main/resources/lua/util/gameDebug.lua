--- 游戏辅助调试
--- @Generated by EmmyLua(https://github.com/EmmyLua)
--- @Created by wxd-gaming(無心道, 15388152619)
--- @DateTime: 2024/8/29 15:53
gameDebug = {}
LUA_Error = load("return _G.error")()
LUA_Print = load("return _G.print")()

function print(...)
    local var = gameDebug.toStrings(" ", ...)
    LUA_Print(var)
    io.flush()
end

--- 把 table 数据转化成json字符串
--- @param tab table数据类型
function gameDebug.toTableJson(tab, appendYinhao, appendType)
    local json = ""
    for k, v in pairs(tab) do
        if not (json == nil or json == "") then
            json = json .. ", "
        end
        json = json .. gameDebug.toString(k, appendYinhao, appendType) .. ":" .. gameDebug.toString(v, appendYinhao, appendType)
    end
    local var = "{" .. json .. "}"
    return var
end

--- 把数组转换成字符串
--- @param arr 数组数据类型
function gameDebug.toArrayJson(arr, appendYinhao, appendType)
    local json = ""
    local success, result = pcall(function()
        for i, v in ipairs(arr) do
            if not (json == nil or json == "") then
                json = json .. ", "
            end
            json = json .. gameDebug.toString(v, appendYinhao, appendType)
        end
    end)
    if not success then
        --print("gameDebug.toArrayJson error: " .. result)
        if (json == nil or json == "") then
            if type(arr) == "userdata" then
                -- 这里可能是luaj的数组
                local len = arr.length
                if type(len) == "number" then
                    for i = 1, len, 1 do
                        if not (json == nil or json == "") then
                            json = json .. ", "
                        end
                        json = json .. gameDebug.toString(arr[i], appendYinhao, appendType)
                    end
                else
                    json = tostring(arr)
                end
            end
        end
    end
    local var = "[" .. json .. "]"
    return var
end

--- 把对象转化成字符串
--- @param ... 参数
function gameDebug.toStrings(split, ...)
    local printString = ""
    local tmp = { ... }
    local _, _ = pcall(function()
        for i, v in pairs(tmp) do
            if not (printString == nil or printString == "") then
                printString = printString .. split
            end
            printString = printString .. gameDebug.toString(v, false, false)
        end
    end)
    return printString
end

--- 把对象转化成字符串
--- @param obj 参数
--- @param appendYinhao 是否添加引号
--- @param appendType 是否添加类型
function gameDebug.toString(obj, appendYinhao, appendType)
    if obj == nil or obj == "nil" then
        if appendType then
            return "【nil】 nil";
        end
        return "nil";
    end
    local typeString = type(obj)
    if typeString == "number" or typeString == "boolean" then
        if appendType then
            return "【" .. typeString .. "】 " .. tostring(obj)
        end
        return tostring(obj)
    elseif typeString == 'string' then
        local str = tostring(obj);
        if appendYinhao then
            str = "\"" .. tostring(obj) .. "\""
        end
        if appendType then
            str = "【string】 " .. str
        end
        return str
    elseif typeString == 'table' then
        local str = gameDebug.toTableJson(obj, appendYinhao, appendType)
        if appendType then
            str = "【" .. typeString .. "】 " .. str
        end
        return str
    else
        local str = gameDebug.toArrayJson(obj, appendYinhao, appendType)
        if appendType then
            str = "【" .. typeString .. "】 " .. str
        end
        return str
    end
end

--- 打印参数信息
function gameDebug.print(...)
    gameDebug.print0(false, true, false, ...)
end

--- 打印参数信息， 输出变量类型
function gameDebug.printType(...)
    gameDebug.print0(false, true, true, ...)
end

--- 打印参数信息，并且打印调用堆栈
function gameDebug.printTraceback(...)
    gameDebug.print0(true, true, false, ...)
end

--- 打印参数信息，并且打印调用堆栈 输出变量类型
function gameDebug.printTracebackType(...)
    gameDebug.print0(true, true, true, ...)
end

--- 打印参数信息，并且打印调用堆栈
--- @param traceback 是否打印堆栈
--- @param appendYinhao 是否添加引号
--- @param appendType 是否添加类型
--- @param ... 参数
function gameDebug.print0(traceback, appendYinhao, appendType, ...)
    local printString = ""
    local tmp = { ... }
    local success, result = pcall(function()
        for i, v in pairs(tmp) do
            if not (printString == nil or printString == "") then
                printString = printString .. ",\n"
            end
            printString = printString .. "  " .. gameDebug.toString(v, appendYinhao, appendType)
        end
    end)
    printString = "===================参数======================\n" .. "[\n" .. printString .. "\n]"
    if traceback then
        printString = printString .. "\n===================堆栈=======================\n" .. debug.traceback(result)
    end
    printString = printString .. "\n===================结束=======================\n"
    print(printString)
end

--- 辅助调试
--- @param fun 函数
--- @param ... 如果调用 函数 异常后打印你需要显示的参数
function gameDebug.debug(fun, ...)
    gameDebug.debug0(fun, true, false, ...)
end
--- 辅助调试 输出变量类型
--- @param fun 函数
--- @param ... 如果调用 函数 异常后打印你需要显示的参数
function gameDebug.debugType(fun, ...)
    gameDebug.debug0(fun, true, true, ...)
end

function gameDebug.debug0(fun, appendYinhao, appendType, ...)
    local f_success, f_error = xpcall(fun, debug.traceback, ...)
    if not f_success then
        local printString = ""
        local tmp = { ... }
        local s, e = pcall(function()
            for i, v in pairs(tmp) do
                if not (printString == nil or printString == "") then
                    printString = printString .. ",\n"
                end
                printString = printString .. "  " .. gameDebug.toString(v, appendYinhao, appendType)
            end
        end)
        print("===================参数======================\n"
                .. "[\n" .. printString .. "\n]" ..
                "\n===================堆栈=======================\n"
                .. f_error ..
                "\n===================结束=======================\n"
        )
    end
end

--- 断言 当值 false 异常
function gameDebug.assertEquals(o1, o2, ...)
    if o1 ~= o2 then
        gameDebug.error(...)
    end
end

--- 断言 当值 false 异常
function gameDebug.assertTrue(bool, ...)
    if not bool then
        gameDebug.error(...)
    end
end

--- 断言对象为nil
function gameDebug.assertNil(obj, ...)
    if obj == nil then
        gameDebug.error(...)
    end
end

--- 带堆栈抛出异常
function gameDebug.error(...)
    local var = gameDebug.toStrings(" ", ...)
    LUA_Error(debug.traceback(var), 1)
end

--- 测试函数
function gamedebugt2(key, vs, list)
    gameDebug.print("key = ", key, "vs = ", vs, "list = ", list)
    gameDebug.printType("key = ", key, "vs = ", vs, "list = ", list)
    gameDebug.printTraceback("key = ", key, "vs = ", vs, "list = ", list)
    gameDebug.printTracebackType("key = ", key, "vs = ", vs, "list = ", list)
    --gameDebug.debug(
    --        function()
    --            for _, item in pairs(list) do
    --                local fid = item.fidd
    --                local sid = item.sid
    --                gameDebug.print("fid", fid, "sid", sid)
    --            end
    --        end,
    --        key,
    --        map,
    --        list
    --)

    local keyString = tostring(key)
    local keyNumber = tonumber(keyString)
    print(type(key), key, keyString, keyNumber)

    return list
end

function debugt3(...)
    --gameDebug.assertEquals(1, 1, "id异常")
    --gameDebug.assertEquals(1, 2, "id异常")
    --gameDebug.assertTrue(1 == 1, "对象 nil")
    --gameDebug.assertTrue(1 == 2, "对象 nil")
    --gameDebug.assertNil(nil, "对象 nil")
    --gameDebug.assertNil("11", "对象 nil")
    gameDebug.print(...)
    test3(...)
end

function test3()
    local tab = {}
    tab[1] = "dd"
    --print(1 .. tab)
end

function testlong(l)
    local var = tonumber(l)
    print(var)
    io.flush()
end

--function error(...)
--    gameDebug.error(...)
--end

function dispatch(function_name, ...)
    --gameDebug.print(function_name, ...)
    --查找函数 通过load字符串的形式 动态编译 返回函数
    local loadFunc = load("return " .. function_name)()
    --调用函数
    local success, result = xpcall(loadFunc, debug.traceback, ...)
    if not success then
        --local trace = debug.traceback(result)
        local var = "[Error] dispatch func name [" .. function_name .. "] 参数：" .. gameDebug.toStrings(" ", ...)
        var = var .. "\n" .. result
        LUA_Error(var)
    end
    return result
end

local mem = {  }
function cache_memory()
    for i = 1, 10 do
        local var = {}
        for j = 1, 2000 do
            var[j] = tostring(i) .. tostring(j) .. "gddddd"
        end
        mem[#mem + 1] = var
    end
end

function cleancache()
    mem = {}
end

function cleanup(th)

    print("当前的暂停时间: " .. collectgarbage("setpause", 100))
    print("当前的步进比例: " .. collectgarbage("setstepmul", 100))

    -- 打印当前的暂停时间和步进比例
    print("当前的暂停时间: " .. collectgarbage("setpause"))
    print("当前的步进比例: " .. collectgarbage("setstepmul"))

    local m1 = memory2()
    mem = {}
    collectgarbage("collect")
    collectgarbage("collect")
    collectgarbage("collect")
    collectgarbage("collect")
    local m2 = memory2()
    print("name=", th, "回收前", m1, "回收后", m2, "lua version", _VERSION)
end

function showmemory(th)
    print("thread=", th, "内存占用", memory2(), "lua version", _VERSION)
end

function memory2()
    --collectgarbage("collect")
    local var = collectgarbage("count")
    return tostring(math.ceil(var / 1024)) .. " mb"
end