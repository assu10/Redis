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
 * RecentViewList �׽�Ʈ Ŭ����
 * 
 * �ֱ� ��ȸ ��ǰ ��ȸ
 * 1~50������ ��ǰ��ȣ�� �����ϰ� ��ȸ
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
	 * �ֱ� ��ȸ ��ǰ ��Ͽ� ��ǰ �߰� �� �ִ����尹���� ���� �������� ���� �׽�Ʈ
	 */
	@Test
	public void testAdd() {
		for (int i=1; i<=50; i++) {
			this.recentViewList.add(String.valueOf(i));
		}
	}
	
	/**
	 * ����� ��ǰ�ִ밳�� Ȯ��
	 */
	@Test
	public void checkMaxSize() {
		int storedSize = this.recentViewList.getRecentViewList().size();
		assertEquals(this.LIST_MAX_SIZE, storedSize);
	}
	
	/**
	 * �ֱ� ��ȸ��ǰ 4�� ��ȸ
	 */
	@Test
	public void checkRecentSize() {
		int checkSize = 4;
		int redisSize = this.recentViewList.getRecentViewList(checkSize).size();
		assertEquals(redisSize, checkSize);
	}
	
	/**
	 * ��ǰ��ȣ ��ȸ
	 */
	@Test
	public void checkProductNo() {
		this.recentViewList.add("57");	// 55�� ��ǰ ����
		assertEquals(this.recentViewList.getRecentViewList().size(), this.LIST_MAX_SIZE);
		List<String> itemList = this.recentViewList.getRecentViewList(5);
		assertEquals("57", itemList.get(0));
		for(String item : itemList) {
			System.out.println(item);
		}
	}
}
