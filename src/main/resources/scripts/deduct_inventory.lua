local stock = tonumber(redis.call('GET', KEYS[1]))

if stock == nil then
    return -1
end

local requested = tonumber(ARGV[1])

if stock >= requested then
    redis.call('DECRBY', KEYS[1], requested)
    return 1
else
    return 0
end