package net.sf.redis.ch7.cart;

import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.sf.redis.JedisHelper;
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
	/** �ش� ���� ��ǰ����(��ǰ��ȣ����Ʈ), {"products":["151","152","153","154"]}, ��ٱ��� ���� �� KEY_CART_LIST �� ���� */
	private JSONObject cartInfo;
	/** ����ڹ�ȣ */
	private String userNo;
	/** ��ٱ��� ��ǰ��ȣ ����Ʈ (16251:cart:product), {"products":["151","152","153","154","155"]} */
	private static final String KEY_CART_LIST = ":cart:product";
	/** ��ٱ��� ��ǰ��ȣ, �̸�, ���� */
	private static final String KEY_CART_PRODUCT = ":cart:productid:";
	/** ��ǰ��ȣ ����Ʈ (products), ["151","152","153","154"]*/
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
		// {"products":[]}
		this.cartInfo = getCartInfo();	// ������� Cart ��ü�� ������ �� ����� ��ǰ��ȣ ��� ��ȸ
	}
	
	/**
	 * ���𽺿� ����� ��ٱ��� ������ ��ȸ�Ͽ� JSON ��ü�� ��ȯ
	 * @return ��ٱ��� ������ ����� JSONObject
	 */
	private JSONObject getCartInfo() {
		// {"products":["151","152","153","154","155"]}
		String productInfo = this.jedis.get(userNo + KEY_CART_LIST);	// ��ǰ��ȣ ����Ʈ
		// ����� ��ǰ��ȣ ����� ������ �� ��ٱ��� ��ü ����
		if (productInfo == null || "".equals(productInfo)) {
			return makeEmptyCart();
		}
		
		try {
			JSONParser parser = new JSONParser();
			System.out.println("���𽺿� ����� ��ٱ��� ������ ��ȸ�Ͽ� JSON ��ü�� ��ȯ getCartInfo() - " + parser.parse(productInfo));
			System.out.println("���𽺿� ����� ��ٱ��� ������ ��ȸ�Ͽ� JSON ��ü�� ��ȯ productInfo - " + this.jedis.get(userNo + KEY_CART_LIST));
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
		// {"products":[]}
		System.out.println("��ٱ��ϰ� �������� �ʴ� ����ڸ� ���� �� ��ٱ��� ���� ���� makeEmptyCart() - " + cart);
		return cart;
	}
	
	/**
	 * ��ٱ��Ͽ� ����� ��ǰ ���� (Cart�� �ٸ�)
	 * @return ������ ��ǰ����
	 */
	public int flushCart() {
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
		
		Pipeline p = jedis.pipelined();
		// ��ٱ��� ��ǰ���� ����
		for(int i=0; i<products.size(); i++) {
			System.out.println("��ٱ��Ͽ� ����� ��ǰ ���� (Cart�� �ٸ�) flushCart [" + i + "] - " + this.userNo + KEY_CART_PRODUCT + products.get(i));
			System.out.println("��ٱ��Ͽ� ����� ��ǰ ���� JSON_PRODUCT_LIST flushCart [" + i + "] - " + this.cartInfo.get(JSON_PRODUCT_LIST));
			p.del(this.userNo + KEY_CART_PRODUCT + products.get(i));
		}
		
		// ��ٱ��� ��ǰ��ȣ ����Ʈ �ʱ�ȭ
		p.set(this.userNo + KEY_CART_LIST, "");
		p.sync();
		return products.size();
	}
	
	/**
	 * ��ٱ��Ͽ� ��ǰ �߰�
	 * @param productNo ��ٱ��Ͽ� �߰��� ��ǰ��ȣ
	 * @param productName ��ǰ��
	 * @param quantity ����
	 * @return ��ٱ��� ��� ���
	 */
	@SuppressWarnings("unchecked")
	public String addProduct(String productNo, String productName, int quantity) {
		// ��ٱ��� īƮ����Ʈ ������Ʈ
		System.out.println("��ٱ��Ͽ� ��ǰ �߰� �� īƮ����Ʈ ��ȸ - " + this.cartInfo.get(JSON_PRODUCT_LIST));		// ["151","152","153","154"]
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);	
		products.add(productNo);
		this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());

		// ��ٱ��� ��ǰ���� �߰�
		JSONObject product = new JSONObject();
		product.put("productNo", productNo);
		product.put("productName", productName);
		product.put("quantity", quantity);
		
		String productKey = this.userNo + KEY_CART_PRODUCT + productNo;
		return this.jedis.setex(productKey, EXPIRE, product.toJSONString());
	}
	
	/**
	 * ��ٱ��Ͽ� ����� ��ǰ���� ����
	 * @param productNo ������ ��ǰ��ȣ ���
	 * @return ������ ��ǰ�� ����
	 */
	public int deleteProduct(String[] productNo) {
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
//		int result = products.size();
		int result = 0;
		
		Pipeline p = jedis.pipelined();
		for (String item : productNo) {
			products.remove(item);
			p.del(this.userNo + KEY_CART_PRODUCT + item);
			result++;
		}
		p.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());
		p.sync();
		System.out.println("��ٱ��Ͽ� ����� ��ǰ���� ���� , ������ ��ǰ�� ���� - " + result);
		return result;
	}
	
	/**
	 * ��ٱ��Ͽ� ����� ��ǰ����� JSONArray �������� ��ȸ
	 * @return ��ȸ�� ��ǰ ���
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getProductList() {
		boolean isChanged = false;
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
		
		// ��ǰ�� ����Ǿ����� Ȯ���ϱ� ����
		Pipeline p = jedis.pipelined();
		for(int i=0; i<products.size(); i++) {
			p.get(this.userNo + KEY_CART_PRODUCT + products.get(i));
		}
		List<Object> redistResult = p.syncAndReturnAll();
		
		JSONArray result = new JSONArray();
		for (Object item : redistResult) {
			if (item == null) {
				isChanged = true;
			} else {
				result.add(item);
			}
		}
		
		// ��ǰ�� ����� Ű�� ����Ǹ� ���𽺿��� �ڵ����� ������Ƿ� ��ȸ�� ���� �������� �ʴ´�.
		// �׷��Ƿ� ��ǰ��ȣ ��� ����
		if (isChanged) {
			this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());
		}
		// ["{\"quantity\":1,\"productNo\":\"151\",\"productName\":\"����Ŀ��1\"}","{\"quantity\":2,\"productNo\":\"152\",\"productName\":\"����Ŀ��2\"}","{\"quantity\":3,\"productNo\":\"153\",\"productName\":\"����Ŀ��3\"}","{\"quantity\":4,\"productNo\":\"154\",\"productName\":\"����Ŀ��4\"}","{\"quantity\":5,\"productNo\":\"155\",\"productName\":\"����Ŀ��5\"}"]
		System.out.println("��ٱ��Ͽ� ����� ��ǰ����� JSONArray �������� ��ȸ getProductList() -  " + result);
		return result;
	}
	
	/**
	 * keys ����� ����Ͽ� ��ٱ��Ͽ� ����� ��ǰ ����
	 * @return ������ ��ǰ����
	 * @deprecated keys ����� ����� �߸��� ����
	 */
	public int flushCartDeprecated() {
		Set<String> keys = this.jedis.keys(this.userNo + KEY_CART_PRODUCT + "*");

		Pipeline p = jedis.pipelined();
		for (String key : keys) {
			p.del(key);
		}

		p.set(this.userNo + KEY_CART_LIST, "");
		p.sync();

		return keys.size();
	}
}
