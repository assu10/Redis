package net.sf.redis.ch7.recentview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * RecentViewList 테스트 클래스
 * 
 * 최근 조회 상품 조회
 * 1~50까지의 상품번호를 저장하고 조회
 * 
 * @author assu
 * @date 2016.05.06
 */
public class RecentViewListTest {
	static JedisHelper helper;
	private RecentViewList recentViewList;
	private static final String TEST_USER = "12345";
	private int LIST_MAX_SIZE;
	
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
		this.recentViewList = new RecentViewList(helper, TEST_USER);
		assertNotNull(this.recentViewList);
		this.LIST_MAX_SIZE = this.recentViewList.getListMaxSize();
	}
	
	/**
	 * 최근 조회 상품 목록에 상품 추가 및 최대저장갯수만 빼고 나머지는 삭제 테스트
	 */
	@Test
	public void testAdd() {
		for (int i=1; i<=50; i++) {
			this.recentViewList.add(String.valueOf(i));
		}
	}
	
	/**
	 * 저장된 상품최대개수 확인
	 */
	@Test
	public void checkMaxSize() {
		int storedSize = this.recentViewList.getRecentViewList().size();
		assertEquals(this.LIST_MAX_SIZE, storedSize);
	}
	
	/**
	 * 최근 조회상품 4개 조회
	 */
	@Test
	public void checkRecentSize() {
		int checkSize = 4;
		int redisSize = this.recentViewList.getRecentViewList(checkSize).size();
		assertEquals(redisSize, checkSize);
	}
	
	/**
	 * 상품번호 조회
	 */
	@Test
	public void checkProductNo() {
		this.recentViewList.add("57");	// 55번 상품 저장
		assertEquals(this.recentViewList.getRecentViewList().size(), this.LIST_MAX_SIZE);
		List<String> itemList = this.recentViewList.getRecentViewList(5);
		assertEquals("57", itemList.get(0));
		for(String item : itemList) {
			System.out.println(item);
		}
	}
}
