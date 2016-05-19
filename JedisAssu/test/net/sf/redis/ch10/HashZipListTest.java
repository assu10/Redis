package net.sf.redis.ch10;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;
import static org.junit.Assert.*;
/**
 * HashZipList 테스트 클래스
 * 
 * 해시 데이터의 ziplist 인코딩 테스트
 * 
 * @author assu
 * @date 2016.05.019
 */
public class HashZipListTest {
	static JedisHelper helper;
	private HashZipList hashZipList;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();
	}
	
	@Before
	public void setUp() {
		this.hashZipList = new HashZipList(helper);
	}
	
	@Test
	public void testEndocingTestForEntrySize() {
		assertEquals("ziplist", this.hashZipList.getBeforeEncoding1());
		assertEquals("hashtable", this.hashZipList.getAfterEncoding1());
	}
	
	@Test
	public void testEncodingForDataSize() {
		assertEquals("ziplist", this.hashZipList.getBeforeEncoding2());
		assertEquals("hashtable", this.hashZipList.getAfterEncoding2());
	}
}
