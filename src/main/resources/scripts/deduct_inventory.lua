-- KEYS[1] = ticket_stock:{categoryId}
-- KEYS[2] = event:{concertId}
-- ARGV[1] = requested quantity

-- 1. Check if the event exists in Redis
local eventExists = redis.call('EXISTS', KEYS[2])
if eventExists == 0 then
    return -2 -- Custom code: Event not found
end

-- 2. Check if the ticket category exists
local stock = tonumber(redis.call('GET', KEYS[1]))
if stock == nil then
    return -1 -- Custom code: Category not found
end

local requested = tonumber(ARGV[1])

-- 3. Deduct stock if enough tickets are available
if stock >= requested then
    redis.call('DECRBY', KEYS[1], requested)
    return 1 -- Success
else
    return 0 -- Custom code: Not enough stock
end