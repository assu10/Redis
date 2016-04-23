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
 * Cart 테스트 클래스
 * 
 * 사용자번호가 16251인 사용자의 장바구니 생성 후 상품 등록, 삭제, 조회 테스트 후 마지막으로 장바구니 비우기
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
	 * 사용자번호가 16251인 사용자의 장바구니 객체를 생성하고 정상인지 테스트
	 */
	@Before
	public void setUp() {
		this.cart = new CartV2(helper, TESTUSER);
		assertNotNull(this.cart);
	}
	
	/**
	 * 장바구니에 상품번호 151, 156 추가
	 */
	@Test
	public void testAddProduct() {
		assertEquals("OK", this.cart.addProduct("151", "원두커피1", 1));
		assertEquals("OK", this.cart.addProduct("152", "원두커피2", 2));
		assertEquals("OK", this.cart.addProduct("153", "원두커피3", 3));
		assertEquals("OK", this.cart.addProduct("154", "원두커피4", 4));
		assertEquals("OK", this.cart.addProduct("155", "원두커피5", 5));
	}
	
	/**
	 * 장바구니에 등록된 사품목록 조회
	 */
	@Test
	public void testGetProductList() {
		JSONArray products = this.cart.getProductList();
		assertNotNull(products);
		assertEquals(2, products.size());
	}
	
	/**
	 * 장바구니에서 151번인 상품 삭제 
	 */
	@Test
	public void testDeleteProduct() {
		String[] products= {"152"};
		int result = this.cart.deleteProduct(products);
		assertEquals(1, result);
	}
	
	/**
	 * 장바구니 비우기
	 */
	@Test
	public void testFlushCart() {
		assertTrue(this.cart.flushCart() > -1);
		
		assertTrue(this.cart.flushCartDeprecated() > -1);
	}
}
