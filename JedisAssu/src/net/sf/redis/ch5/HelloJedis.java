package net.sf.redis.ch5;

import redis.clients.jedis.Jedis;

public class HelloJedis {
	public static void main(String[] args) {
		// ���� ������ ����
		Jedis jedis = new Jedis("127.0.0.1", 6379);
		
		String result = jedis.set("leejh", "Hello~");
		System.out.println("result : " + result);	// OK
		
		System.out.println("leejhŰ�� �� : " + jedis.get("was:log"));	// Hello~
	}
}
