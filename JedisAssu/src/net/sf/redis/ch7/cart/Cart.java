package net.sf.redis.ch7.cart;

import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 장바구니 상품 등록,삭제,비우기,목록조회, 3일지난 상품 삭제
 * 
 * getProductList를 보면 저장된 상품목록만큼 레디스 조회 요청 발생
 *  
 * @author assu
 * @date 2016.04.17
 */
public class Cart {
	private Jedis jedis;
	 
	/** 해당 고객의 상품정보(상품번호리스트) */
	private JSONObject cartInfo;

	/** 사용자번호 */
	private String userNo;
	
	/** 상품번호 리스트 */
	private static final String KEY_CART_LIST = ":cart:product";
	
	/** 상품번호, 이름, 수량 */
	private static final String KEY_CART_PRODUCT = ":cart:productid:";
	
	private static final String JSON_PRODUCT_LIST = "products";
	private static final int EXPIRE = 60*60*24*3;	// 3일
	
	/**
	 * 장바구니 처리를 위한 Cart 클래스 생성자 (지정된 사용자에 대한 장바구니 객체 생성)
	 * @param helper 제디스 헬퍼 객체
	 * @param userNo 사용자 아이디
	 */
	public Cart(JedisHelper helper, String userNo) {
		this.jedis = helper.getConnection();
		this.userNo = userNo;
		this.cartInfo = getCartInfo();	// 사용자의 Cart 객체가 생성될 때 저장된 상품번호 목록 조회
	}
	
	/**
	 * 레디스에 저장된 장바구니 정보를 조회하여 JSON객체로 변환
	 * @return 장바구니 정보가 저장된 JSONObject
	 */
	private JSONObject getCartInfo() {
		String productInfo = this.jedis.get(this.userNo + KEY_CART_LIST);	// 16251:cart:product
		// 저장된 상품번호 목록이 없으면 빈 장바구니 객체 생성
		if (productInfo == null || "".equals(productInfo)) {
			return makeEmptyCart();
		}
		
		try {
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(productInfo);
		} catch (Exception e) {
			// 상품번호 목록에 오류가 있으면 빈 장바구니 객체 생성
			return makeEmptyCart();
		}
	}
	
	/**
	 * 장바구니가 존재하지 않는 사용자를 위한 빈 장바구니 정보 생성
	 * @return 빈 장바구니 정보
	 */
	@SuppressWarnings("unchecked")
	private JSONObject makeEmptyCart() {
		JSONObject cart = new JSONObject();
		cart.put(JSON_PRODUCT_LIST, new JSONArray());	// products
		return cart;
	}
	
	/**
	 * 장바구니에 저장된 모든 상품 삭제
	 * @return 삭제된 상품개수
	 */
	public int flushCart() {
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);	// products
		
		// 장바구니에 저장된 상품목록을 모두 삭제
		for(int i=0; i<products.size(); i++) {
			this.jedis.del(this.userNo + KEY_CART_PRODUCT + products.get(i));	// 16251:cart:productid:123
		}
		this.jedis.set(this.userNo + KEY_CART_LIST, "");	// 16251:cart:product
		return products.size();
	}
	
	/**
	 * 장바구니에 상품 추가
	 * @param productNo 추가할 상품번호
	 * @param productName 추가할 상품명
	 * @param quantity 상품의 개수
	 * @return 장바구니 등록 결과
	 */
	@SuppressWarnings("unchecked")
	public String addProduct(String productNo, String productName, int quantity) {
		// 추가되는 상품의 아이디를 상품 목록에 추가
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);	// products
		products.add(productNo);
		
		// 추가되는 상품을 레디스에 저장
		this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());	// 16251:cart:product
		
		JSONObject product = new JSONObject();
		product.put("productNo", productNo);
		product.put("productName", productName);
		product.put("quantity", quantity);
		
		// 개별상품의 정보를 레디스에 저장하고 3일의 만료기간 설정
		String productKey = this.userNo + KEY_CART_PRODUCT + productNo;	// 16251:cart:productid:123
		return this.jedis.setex(productKey, EXPIRE, product.toJSONString());
	}
	
	/**
	 * 장바구니에 저장된 상품정보 삭제
	 * @param productNo 삭제할 상품번호 목록
	 * @return 삭제된 상품의 개수
	 */
	public int deleteProduct(String[] productNo) {
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
		int result = 0;
		
		for (String item : productNo) {
			products.remove(item);
			// 저장된 상품번호 목록개수만큼 레디스에 삭제 명령 전송
			result += this.jedis.del(this.userNo + KEY_CART_PRODUCT + item);	// 16251:cart:productid:123
		}
		
		System.out.println(this.cartInfo.toJSONString());
		System.out.println(products.toJSONString());
		this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());	// {"products":["152","153","154","155"]}
//		this.jedis.set(this.userNo + KEY_CART_LIST, products.toJSONString());	    // ["152","153","154","155"]
		return result;
	}
	
	/**
	 * 장바구니에 저장된 상품 목록을 JSONArray형식으로 조회
	 * @return 조회된 상품목록
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
		
		// 상품이 저장된 키가 만료되면 레디스에서자동으로 사라지므로 조회된 값이 존재하지 않는다.
		// 그러므로 상품번호 목록 갱신
		if (isChanged) {
			this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());
		}
		return result;
	}
	
	/**
	 * keys 명령을 사용하여 장바구니에 저장된 상품 삭제 
	 * 
	 * 레디스 공식 문서에 keys 명령을 서비스중인 레디스에 사용하지 않도록 권함 -> 성능 저하
	 * @return 삭제된 상품개수
	 * @deprecated keys 명령을 사용한 잘못된 구현이다.
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
