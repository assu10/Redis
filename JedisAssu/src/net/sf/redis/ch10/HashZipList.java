package net.sf.redis.ch10;

import java.util.Random;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * ����� ������ ũ�� �׽�Ʈ
 * hash-max-ziplist-enties, hash-max-ziplist-value
 * 
 * �� ���� �ؽõ����͸� �����ϰ� ���� 512���� 64����Ʈ�� ������ ����
 * ù ��° �ؽ� �����Ϳ��� 512���� ������ �Է�, �� ��° �ؽ� �����Ϳ��� �� ���� 64����Ʈ�� ������ �Է�.
 * 
 * �׸��� getAfterEncoding1, getAfterEncoding2 �޼��忡�� ���� �ϳ����� �����͸� �߰��� �Է��Ͽ� ���ڵ� ������ ��ȸ
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
	 * 512���� �����Ͱ� ����� �ؽ� �������� ���ڵ� ���� ��ȸ
	 * @return ���ڵ� ���� ���ڿ�
	 */
	public String getBeforeEncoding1() {
		initHashForEntryTest();
		return this.jedis.objectEncoding(KEY_HASH_ENTRY_TEST);
	}
	
	/**
	 * 513���� �����Ͱ� ����� �ؽ� �������� ���ڵ� ���� ��ȸ
	 * @return
	 */
	public String getAfterEncoding1() {
		this.jedis.hset(KEY_HASH_ENTRY_TEST, "lastfield", "test value");
		return this.jedis.objectEncoding(KEY_HASH_ENTRY_TEST);
	}
	
	/**
	 * �Ѱ��� 64bytes�� �����Ͱ� ����� �ؽ� �������� ���ڵ� ���� ��ȸ
	 * @return ���ڵ� ���� ���ڿ�
	 */
	public String getBeforeEncoding2() {
		initHashForLenghTest();
		return this.jedis.objectEncoding(KEY_HASH_LENGTH_TEST);
	}
	
	/**
	 * �Ѱ��� 65bytes�� �����Ͱ� �߰��� �ؽ� �������� ���ڵ� ���� ��ȸ
	 * @return ���ڵ� ���� ���ڿ�
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
	 * ������ ������ ���� ���ڿ� ����
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
