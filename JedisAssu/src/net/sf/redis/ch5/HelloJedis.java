package net.sf.redis.ch5;

import redis.clients.jedis.Jedis;

public class HelloJedis {
	public static void main(String[] args) {
		// 제디스 서버와 연걸
		Jedis jedis = new Jedis("127.0.0.1", 6379);
		
		String result = jedis.set("leejh", "Hello~");
		System.out.println("result : " + result);	// OK
		
		System.out.println("leejh키의 값 : " + jedis.get("was:log"));	// Hello~
	}
}
