-- 2016.05.05
-- lua의 제어문 (조건문은 if, 반복문은 while, repeat, for)

local function condition(size, flag)
	local result, filler = 'This is string'
	local filler= nil

	if flag == 0 then
		filler = '  zero'
	elseif flag == 1 then
		filler = '  one'
	else
		filler = '  none'
	end

	for idx = 1, size, 1 do
		result = result .. filler
	end

	return result
end

print(condition(3))		-- This is string  none  none  none
print(condition(1,0))	-- This is string  zero
print(condition(2,1))	-- This is string  one  one

