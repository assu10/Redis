package net.sf.redis.ch10;

import java.util.Random;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 저장될 데이터 크기 테스트
 * hash-max-ziplist-enties, hash-max-ziplist-value
 * 
 * 두 개의 해시데이터를 생성하고 각각 512개와 64바이트의 데이터 저장
 * 첫 번째 해시 데이터에는 512개의 데이터 입력, 두 번째 해시 데이터에는 한 개의 64바이트의 데이터 입력.
 * 
 * 그리고 getAfterEncoding1, getAfterEncoding2 메서드에서 각각 하나씩의 데이터를 추가로 입력하여 인코디 ㅇ정보 조회
 * 
 * @author assu
 * @date 2016.05.19
 */
public class HashZipList {
	private Jedis jedis;
	private static final int INT_HASH_ENTRY = 512;
	private static final int INT_HASH_DATA_LENGTH = 64;
	private static final String KEY_HASH_ENTRY_TEST = "ziplist:hash:entry:test";
	private static final String KEY_HASH_LENGTH_TEST = "ziplist:hash:length:test";
	String chars[] = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",");
	
	public HashZipList(JedisHelper helper) {
		this.jedis = helper.getConnection();
	}
	
	/**
	 * 512개의 데이터가 저장된 해시 데이터의 인코딩 정보 조회
	 * @return 인코딩 상태 문자열
	 */
	public String getBeforeEncoding1() {
		initHashForEntryTest();
		return this.jedis.objectEncoding(KEY_HASH_ENTRY_TEST);
	}
	
	/**
	 * 513개의 데이터가 저장된 해시 데이터의 인코딩 정보 조회
	 * @return
	 */
	public String getAfterEncoding1() {
		this.jedis.hset(KEY_HASH_ENTRY_TEST, "lastfield", "test value");
		return this.jedis.objectEncoding(KEY_HASH_ENTRY_TEST);
	}
	
	/**
	 * 한개의 64bytes의 데이터가 저장된 해시 데이터의 인코딩 정보 조회
	 * @return 인코딩 상태 문자열
	 */
	public String getBeforeEncoding2() {
		initHashForLenghTest();
		return this.jedis.objectEncoding(KEY_HASH_LENGTH_TEST);
	}
	
	/**
	 * 한개의 65bytes의 데이터가 추가된 해시 데이터의 인코딩 정보 조회
	 * @return 인코딩 상태 문자열
	 */
	public String getAfterEncoding2() {
		String testValue = this.makeRandomString(INT_HASH_DATA_LENGTH+1);
		this.jedis.hset(KEY_HASH_LENGTH_TEST, "field2", testValue);
		return this.jedis.objectEncoding(KEY_HASH_LENGTH_TEST);
	}
	
	private void initHashForEntryTest() {
		this.jedis.del(KEY_HASH_ENTRY_TEST);
		for (int i=0; i<INT_HASH_ENTRY; i++) {
			this.jedis.hset(KEY_HASH_ENTRY_TEST, "field" + (i+1), "test value" + i);
		}
	}
	
	private void initHashForLenghTest() {
		this.jedis.del(KEY_HASH_LENGTH_TEST);
		String testValue = this.makeRandomString(INT_HASH_DATA_LENGTH);
		this.jedis.hset(KEY_HASH_LENGTH_TEST, "field", testValue);
	}
	
	/**
	 * 지정한 길이의 랜덤 문자열 생성
	 * @param size
	 * @return
	 */
	private String makeRandomString(int size) {
		Random rand = new Random();
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<size; i++) {
			builder.append(this.chars[rand.nextInt(this.chars.length)]);
		}
		return builder.toString();
	}
}
