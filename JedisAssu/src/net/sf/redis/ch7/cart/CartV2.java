package net.sf.redis.ch7.cart;

import net.sf.redis.JedisHelper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * ��ٱ��� ��ǰ ���,����,����,�����ȸ, 3������ ��ǰ ����
 * 
 * getProductList�� ���� ����� ��ǰ��ϸ�ŭ ���� ��ȸ ��û �߻�
 *   --> ���ʿ��� ��Ʈ��ũ �պ� �ð��� �����ϱ� ���� ���� ������������ ����Ͽ� ����
 *  
 * @author assu
 * @date 2016.04.20
 */
public class CartV2 {
	private Jedis jedis;
	/** �ش� ���� ��ǰ����(��ǰ��ȣ����Ʈ) */
	private JSONObject cartInfo;
	/** ����ڹ�ȣ */
	private String userNo;
	/** ��ǰ��ȣ ����Ʈ (16251:cart:product) */
	private static final String KEY_CART_LIST = ":cart:product";
	/** ��ǰ��ȣ, �̸�, ���� */
	private static final String KEY_CART_PRODUCT = ":cart:productid:";
	private static final String JSON_PRODUCT_LIST = "products";
	/** ��ٱ��� ��ǰ�� �������� 3�� */
	private static final int EXPIRE = 60*60*24*3; 

	
	/**
	 * ��ٱ��ϸ� ó���ϱ� ���� CartV2 Ŭ���� ������
	 * @param helper ���� ���� ��ü
	 * @param userNo ����ڹ�ȣ
	 */
	public CartV2(JedisHelper helper, String userNo) {
		this.jedis = helper.getConnection();
		this.userNo = userNo;
		this.cartInfo = getCartInfo();	// ������� Cart ��ü�� ������ �� ����� ��ǰ��ȣ ��� ��ȸ
	}
	
	/**
	 * ���𽺿� ����� ��ٱ��� ������ ��ȸ�Ͽ� JSON ��ü�� ��ȯ
	 * @return ��ٱ��� ������ ����� JSONObject
	 */
	private JSONObject getCartInfo() {
		String productInfo = this.jedis.get(userNo + KEY_CART_LIST);	// ��ǰ��ȣ ����Ʈ
		
		// ����� ��ǰ��ȣ ����� ������ �� ��ٱ��� ��ü ����
		if (productInfo == null || "".equals(productInfo)) {
			return makeEmptyCart();
		}
		
		try {
			JSONParser parser = new JSONParser();
			System.out.println("getCartInfo - " + parser.parse(productInfo));
			return (JSONObject) parser.parse(productInfo);
		} catch (Exception e) {
			return makeEmptyCart();
		}
	}
	
	/**
	 * ��ٱ��ϰ� �������� �ʴ� ����ڸ� ���� �� ��ٱ��� ���� ����
	 * @return �� ��ٱ��� ����
	 */
	@SuppressWarnings("unchecked")
	private JSONObject makeEmptyCart() {
		JSONObject cart = new JSONObject();
		cart.put(JSON_PRODUCT_LIST, new JSONArray());
		return cart;
	}
	
	/**
	 * ��ٱ��Ͽ� ����� ��ǰ ���� (Cart�� �ٸ�)
	 * @return ������ ��ǰ����
	 */
	public int flushCart() {
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
		
		Pipeline p = jedis.pipelined();
		��
		return 1;
	}
	
	/**
	 * ��ٱ��Ͽ� ��ǰ �߰�
	 * @param productNo ��ٱ��Ͽ� �߰��� ��ǰ��ȣ
	 * @param productName ��ǰ��
	 * @param quantity ����
	 * @return ��ٱ��� ��� ���
	 */
	public String addProduct(String productNo, String productName, int quantity) {

	}
	
	/**
	 * ��ٱ��Ͽ� ����� ��ǰ���� ����
	 * @param productNo ������ ��ǰ��ȣ ���
	 * @return ������ ��ǰ�� ����
	 */
	public int deleteProduct(String[] productNo) {

	}
	
	/**
	 * ��ٱ��Ͽ� ����� ��ǰ����� JSONArray �������� ��ȸ
	 * @return ��ȸ�� ��ǰ ���
	 */
	public JSONArray getProductList() {

	}
	
	/**
	 * keys ����� ����Ͽ� ��ٱ��Ͽ� ����� ��ǰ ����
	 * @return ������ ��ǰ����
	 * @deprecated keys ����� ����� �߸��� ����
	 */
	public int flushCartDeprecated() {

	}
}
