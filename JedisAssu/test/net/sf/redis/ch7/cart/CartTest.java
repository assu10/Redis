package net.sf.redis.ch7.cart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONArray;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.redis.JedisHelper;

/**
 * Cart �׽�Ʈ Ŭ����
 * 
 * ����ڹ�ȣ�� 16251�� ������� ��ٱ��� ���� �� ��ǰ ���, ����, ��ȸ �׽�Ʈ �� ���������� ��ٱ��� ����
 * 
 * @author assu
 * @date 2016.04.17
 */
public class CartTest {
	private static final String TESTUSER = "16251";
	static JedisHelper helper;
	private CartV2 cart;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		helper = JedisHelper.getInstance();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		helper.destroyPool();
	}
	
	/**
	 * ����ڹ�ȣ�� 16251�� ������� ��ٱ��� ��ü�� �����ϰ� �������� �׽�Ʈ
	 */
	@Before
	public void setUp() {
		this.cart = new CartV2(helper, TESTUSER);
		assertNotNull(this.cart);
	}
	
	/**
	 * ��ٱ��Ͽ� ��ǰ��ȣ 151, 156 �߰�
	 */
	@Test
	public void testAddProduct() {
		assertEquals("OK", this.cart.addProduct("151", "����Ŀ��1", 1));
		assertEquals("OK", this.cart.addProduct("152", "����Ŀ��2", 2));
		assertEquals("OK", this.cart.addProduct("153", "����Ŀ��3", 3));
		assertEquals("OK", this.cart.addProduct("154", "����Ŀ��4", 4));
		assertEquals("OK", this.cart.addProduct("155", "����Ŀ��5", 5));
	}
	
	/**
	 * ��ٱ��Ͽ� ��ϵ� ��ǰ��� ��ȸ
	 */
	@Test
	public void testGetProductList() {
		JSONArray products = this.cart.getProductList();
		assertNotNull(products);
		assertEquals(2, products.size());
	}
	
	/**
	 * ��ٱ��Ͽ��� 151���� ��ǰ ���� 
	 */
	@Test
	public void testDeleteProduct() {
		String[] products= {"152"};
		int result = this.cart.deleteProduct(products);
		assertEquals(1, result);
	}
	
	/**
	 * ��ٱ��� ����
	 */
	@Test
	public void testFlushCart() {
		assertTrue(this.cart.flushCart() > -1);
		
		assertTrue(this.cart.flushCartDeprecated() > -1);
	}
}
