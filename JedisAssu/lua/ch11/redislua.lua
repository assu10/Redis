-- 2016.05.05
-- ���𽺿��� ��ƽ�ũ���� ���� (redis 2.6���� ��ƽ�ũ��Ʈ ���� �߰�)

local sum = ARGV[1] + ARGV[2]
local result = redis.call('set', KEYS[1], sum)
return result
