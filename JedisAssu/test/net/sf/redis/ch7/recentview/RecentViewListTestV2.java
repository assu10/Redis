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
 * RecentViewListV2 �׽�Ʈ Ŭ����
 * 
 * �ֱ� ��ȸ ��ǰ ��ȸ
 * 1~50������ ��ǰ��ȣ�� �����ϰ� ��ȸ
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
	 * �ֱ� ��ȸ ��ǰ ��Ͽ� ��ǰ �߰� �� �ִ����尹���� ���� �������� ���� �׽�Ʈ
	 */
	@Test
	public void testAdd() {
		for (int i=1; i<=50; i++) {
			this.recentViewListV2.add(String.valueOf(i));
		}
	}
	
	/**
	 * ����� ��ǰ�ִ밳�� Ȯ��
	 */
	@Test
	public void checkMaxSize() {
		int storedSize = this.recentViewListV2.getRecentViewList().size();
		assertEquals(this.LIST_MAX_SIZE, storedSize);
	}
	
	/**
	 * �ֱ� ��ȸ��ǰ 4�� ��ȸ
	 */
	@Test
	public void checkRecentSize() {
		int checkSize = 4;
		int redisSize = this.recentViewListV2.getRecentViewList(checkSize).size();
		assertEquals(redisSize, checkSize);
	}
	
	/**
	 * ��ǰ��ȣ ��ȸ
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
