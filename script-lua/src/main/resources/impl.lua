LUA_Error = load("return _G.error")()
LUA_Print = load("return _G.print")()

function print(...)
    local var = gameDebug.toStrings(" ", ...)
    LUA_Print(var)
    io.flush()
end

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

function memory0()
    return collectgarbage("count")
end

gameDebug = {}

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
    elseif typeString == 'cdata' then
        local str = tostring(obj)
        -- 获取字符串的长度
        local len = string.len(str)
        -- 检查字符串最后一个字符是否是目标字符
        if string.sub(str, len - 2, len) == 'ULL' then
            -- 删除最后一个字符
            str = string.sub(str, 1, len - 3)
        elseif string.sub(str, len - 1, len) == 'LL' then
            -- 删除最后一个字符
            str = string.sub(str, 1, len - 2)
        end
        if appendType then
            str = "【long】 " .. str
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