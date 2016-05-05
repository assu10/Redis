-- 2016.05.05
-- 테이블 (lua에서는 배열을 테이블이라고 함)


local function tablesize()
	local table1= {}
	table1["assu"] = 100
	table1["juhyun"] = 1002
	table1["miok"] = 200

	print("table1의 크기", table.getn(table1))		-- table1의 크기	0
	print("table1의 크기", table.maxn(table1))		-- table1의 크기	0

	local table2 = {100,1002,200}

	print("table2의 크기", table.getn(table2))		-- table2의 크기	3
	print("table2의 크기", table.maxn(table2))		-- table2의 크기	3
end

tablesize()
