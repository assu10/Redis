-- 2016.05.05
-- 레디스에서 루아스크립스 실행 (redis 2.6부터 루아스크립트 엔진 추가)

local sum = ARGV[1] + ARGV[2]
local result = redis.call('set', KEYS[1], sum)
return result
