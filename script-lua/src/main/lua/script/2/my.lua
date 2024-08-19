require "bit32"

function t1()
    print("位移：" .. bit32.lshift(1, 2))
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

end