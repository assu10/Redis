-- 2016.05.05
-- ���̺� (lua������ �迭�� ���̺��̶�� ��)


local function tablesize()
	local table1= {}
	table1["assu"] = 100
	table1["juhyun"] = 1002
	table1["miok"] = 200

	print("table1�� ũ��", table.getn(table1))		-- table1�� ũ��	0
	print("table1�� ũ��", table.maxn(table1))		-- table1�� ũ��	0

	local table2 = {100,1002,200}

	print("table2�� ũ��", table.getn(table2))		-- table2�� ũ��	3
	print("table2�� ũ��", table.maxn(table2))		-- table2�� ũ��	3
end

tablesize()
