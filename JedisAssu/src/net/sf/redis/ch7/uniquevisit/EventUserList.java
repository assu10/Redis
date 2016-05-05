package net.sf.redis.ch7.uniquevisit;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

public class EventUserList {
	private Jedis jedis;
	
	public EventUserList(JedisHelper helper) {
		this.jedis = helper.getConnection();
	}
	
	/**
	 * �̺�Ʈ ����� ����� ��ȸ�ϱ� ���� ��� ��ũ��Ʈ�� �ʱ�ȭ �Ѵ�.
	 * @return ���� ������ ��ϵ� ��ũ��Ʈ�� ���� sha1Ű�� 
	 */
	public String initLuaScript() {
		StringBuilder builder = new StringBuilder();
		builder.append("local function hasbit(x, p) ");
		builder.append("	return x % (p + p) >= p ");
		builder.append("end ");
		builder.append("local mask = {128,64,32,16,8,4,2,1} ");
		builder.append("local len = redis.call('strlen', 'uv:event') ");
		builder.append("local userlist = redis.call('get', 'uv:event') ");
		builder.append("local table = {} ");
		builder.append("local idx = 1 ");
		builder.append("for i = 1, len, 1 do ");
		builder.append("	if string.byte(userlist, i) ~= 0 then ");
		builder.append("		for j = 1, 8, 1 do ");
		builder.append("			if hasbit(string.byte(userlist, i), mask[j]) then ");
		builder.append("				table[idx] = tostring((i-1) * 8 + (j -1)) ");
		builder.append("				idx = idx + 1");
		builder.append("			end");
		builder.append("		end");
		builder.append("	end ");
		builder.append("end ");
		builder.append("return table ");
		
		System.out.println("��� ��ũ��Ʈ�� �ʱ�ȭ initLuaScript - " + this.jedis.scriptLoad(builder.toString()));
		
		return this.jedis.scriptLoad(builder.toString());
	}
	
	/**
	 * ���� ������ ��ϵ� ��� ��ũ��Ʈ ����
	 * @return ��� ��ũ��Ʈ ���� ���
	 */
	public Object getEventUserList(String sha1) {
		System.out.println("��� ��ũ��Ʈ ���� getEventUserList1 - " + sha1);
		System.out.println("��� ��ũ��Ʈ ���� getEventUserList2 - " + this.jedis.evalsha(sha1));
		return this.jedis.evalsha(sha1);
	}
}
