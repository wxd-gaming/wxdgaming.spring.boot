function t1()
    print("位移：" .. (1 << 2))
end

function t2_2(c1, c2)
    return 1 + 1 + c1 + c2;
end

function t2_2(c)
    return 1 + 1 + c;
end

function t2_2()

    return 1 + 1;
end

function login()
    print("我是2")
    local testfun0 = testfun0("我是lua")
    print(testfun0)
    jlog:info("测试Log: " .. testfun0)
    print(globalArgs:get("ss"))
end