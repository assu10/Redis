-- 2016.05.05
-- 테이블 (lua에서는 배열을 테이블이라고 함)

local function main()
	local table1 = {}
	table1["assu"] = 100
	table1["juhyun"] = 1002
	table1["miok"] = 200

	-- 위처럼 배열의 첨자로 문자열을 사용하면 선언된 배열의 크기를알 수 없다.
	print(table1.assu)		-- 100
end


main()
