package net.sf.redis.ch7.cart;

import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * ��ٱ��� ��ǰ ���,����,����,�����ȸ, 3������ ��ǰ ����
 * 
 * getProductList�� ���� ����� ��ǰ��ϸ�ŭ ���� ��ȸ ��û �߻�
 *  
 * @author assu
 * @date 2016.04.17
 */
public class Cart {
	private Jedis jedis;
	 
	/** �ش� ���� ��ǰ����(��ǰ��ȣ����Ʈ) */
	private JSONObject cartInfo;

	/** ����ڹ�ȣ */
	private String userNo;
	
	/** ��ǰ��ȣ ����Ʈ */
	private static final String KEY_CART_LIST = ":cart:product";
	
	/** ��ǰ��ȣ, �̸�, ���� */
	private static final String KEY_CART_PRODUCT = ":cart:productid:";
	
	private static final String JSON_PRODUCT_LIST = "products";
	private static final int EXPIRE = 60*60*24*3;	// 3��
	
	/**
	 * ��ٱ��� ó���� ���� Cart Ŭ���� ������ (������ ����ڿ� ���� ��ٱ��� ��ü ����)
	 * @param helper ���� ���� ��ü
	 * @param userNo ����� ���̵�
	 */
	public Cart(JedisHelper helper, String userNo) {
		this.jedis = helper.getConnection();
		this.userNo = userNo;
		this.cartInfo = getCartInfo();	// ������� Cart ��ü�� ������ �� ����� ��ǰ��ȣ ��� ��ȸ
	}
	
	/**
	 * ���𽺿� ����� ��ٱ��� ������ ��ȸ�Ͽ� JSON��ü�� ��ȯ
	 * @return ��ٱ��� ������ ����� JSONObject
	 */
	private JSONObject getCartInfo() {
		String productInfo = this.jedis.get(this.userNo + KEY_CART_LIST);	// 16251:cart:product
		// ����� ��ǰ��ȣ ����� ������ �� ��ٱ��� ��ü ����
		if (productInfo == null || "".equals(productInfo)) {
			return makeEmptyCart();
		}
		
		try {
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(productInfo);
		} catch (Exception e) {
			// ��ǰ��ȣ ��Ͽ� ������ ������ �� ��ٱ��� ��ü ����
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
		cart.put(JSON_PRODUCT_LIST, new JSONArray());	// products
		return cart;
	}
	
	/**
	 * ��ٱ��Ͽ� ����� ��� ��ǰ ����
	 * @return ������ ��ǰ����
	 */
	public int flushCart() {
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);	// products
		
		// ��ٱ��Ͽ� ����� ��ǰ����� ��� ����
		for(int i=0; i<products.size(); i++) {
			this.jedis.del(this.userNo + KEY_CART_PRODUCT + products.get(i));	// 16251:cart:productid:123
		}
		this.jedis.set(this.userNo + KEY_CART_LIST, "");	// 16251:cart:product
		return products.size();
	}
	
	/**
	 * ��ٱ��Ͽ� ��ǰ �߰�
	 * @param productNo �߰��� ��ǰ��ȣ
	 * @param productName �߰��� ��ǰ��
	 * @param quantity ��ǰ�� ����
	 * @return ��ٱ��� ��� ���
	 */
	@SuppressWarnings("unchecked")
	public String addProduct(String productNo, String productName, int quantity) {
		// �߰��Ǵ� ��ǰ�� ���̵� ��ǰ ��Ͽ� �߰�
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);	// products
		products.add(productNo);
		
		// �߰��Ǵ� ��ǰ�� ���𽺿� ����
		this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());	// 16251:cart:product
		
		JSONObject product = new JSONObject();
		product.put("productNo", productNo);
		product.put("productName", productName);
		product.put("quantity", quantity);
		
		// ������ǰ�� ������ ���𽺿� �����ϰ� 3���� ����Ⱓ ����
		String productKey = this.userNo + KEY_CART_PRODUCT + productNo;	// 16251:cart:productid:123
		return this.jedis.setex(productKey, EXPIRE, product.toJSONString());
	}
	
	/**
	 * ��ٱ��Ͽ� ����� ��ǰ���� ����
	 * @param productNo ������ ��ǰ��ȣ ���
	 * @return ������ ��ǰ�� ����
	 */
	public int deleteProduct(String[] productNo) {
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
		int result = 0;
		
		for (String item : productNo) {
			products.remove(item);
			// ����� ��ǰ��ȣ ��ϰ�����ŭ ���𽺿� ���� ��� ����
			result += this.jedis.del(this.userNo + KEY_CART_PRODUCT + item);	// 16251:cart:productid:123
		}
		
		System.out.println(this.cartInfo.toJSONString());
		System.out.println(products.toJSONString());
		this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());	// {"products":["152","153","154","155"]}
//		this.jedis.set(this.userNo + KEY_CART_LIST, products.toJSONString());	    // ["152","153","154","155"]
		return result;
	}
	
	/**
	 * ��ٱ��Ͽ� ����� ��ǰ ����� JSONArray�������� ��ȸ
	 * @return ��ȸ�� ��ǰ���
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getProductList() {
		boolean isChanged = false;
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
		JSONArray result = new JSONArray();
		String value = null;
		
		for (int i=0; i<products.size(); i++) {
			value = this.jedis.get(this.userNo + KEY_CART_PRODUCT + products.get(i));
			if (value == null) {
				isChanged = true;
			} else {
				result.add(value);
			}
		}
		
		// ��ǰ�� ����� Ű�� ����Ǹ� ���𽺿����ڵ����� ������Ƿ� ��ȸ�� ���� �������� �ʴ´�.
		// �׷��Ƿ� ��ǰ��ȣ ��� ����
		if (isChanged) {
			this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());
		}
		return result;
	}
	
	/**
	 * keys ����� ����Ͽ� ��ٱ��Ͽ� ����� ��ǰ ���� 
	 * 
	 * ���� ���� ������ keys ����� �������� ���𽺿� ������� �ʵ��� ���� -> ���� ����
	 * @return ������ ��ǰ����
	 * @deprecated keys ����� ����� �߸��� �����̴�.
	 */
	public int flushCartDeprecated() {
		Set<String> keys = this.jedis.keys(this.userNo + KEY_CART_PRODUCT + "*");

		for (String key : keys) {
			this.jedis.del(key);
		}

		this.jedis.set(this.userNo + KEY_CART_LIST, "");

		return keys.size();
	}
}
