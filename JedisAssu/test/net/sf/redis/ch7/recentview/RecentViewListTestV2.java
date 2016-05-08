package net.sf.redis.ch7.recentview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * RecentViewListV2 테스트 클래스
 * 
 * 최근 조회 상품 조회
 * 1~50까지의 상품번호를 저장하고 조회
 * 
 * @author assu
 * @date 2016.05.08
 */
public class RecentViewListTestV2 {
	static JedisHelper helper;
	private RecentViewListV2 recentViewListV2;
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
		this.recentViewListV2 = new RecentViewListV2(helper, TEST_USER);
		assertNotNull(this.recentViewListV2);
		this.LIST_MAX_SIZE = this.recentViewListV2.getListMaxSize();
	}
	
	/**
	 * 최근 조회 상품 목록에 상품 추가 및 최대저장갯수만 빼고 나머지는 삭제 테스트
	 */
	@Test
	public void testAdd() {
		for (int i=1; i<=50; i++) {
			this.recentViewListV2.add(String.valueOf(i));
		}
	}
	
	/**
	 * 저장된 상품최대개수 확인
	 */
	@Test
	public void checkMaxSize() {
		int storedSize = this.recentViewListV2.getRecentViewList().size();
		assertEquals(this.LIST_MAX_SIZE, storedSize);
	}
	
	/**
	 * 최근 조회상품 4개 조회
	 */
	@Test
	public void checkRecentSize() {
		int checkSize = 4;
		int redisSize = this.recentViewListV2.getRecentViewList(checkSize).size();
		assertEquals(redisSize, checkSize);
	}
	
	/**
	 * 상품번호 조회
	 */
	@Test
	public void checkProductNo() {
		this.recentViewListV2.add("48");
		assertEquals(this.recentViewListV2.getRecentViewList().size(), this.LIST_MAX_SIZE);
		Set<String> itemList = this.recentViewListV2.getRecentViewList(5);
		
		for (String item : itemList) {
			System.out.println(item);
		}
		
		String[] list = itemList.toArray(new String[0]);
		assertEquals("48", list[0]);
	}
}
