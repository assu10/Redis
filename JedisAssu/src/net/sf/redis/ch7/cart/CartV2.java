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
 * 장바구니 상품 등록,삭제,비우기,목록조회, 3일지난 상품 삭제
 * 
 * getProductList를 보면 저장된 상품목록만큼 레디스 조회 요청 발생
 *   --> 불필요한 네트워크 왕복 시간을 제거하기 위해 레디스 파이프라인을 사용하여 개선
 *  
 * @author assu
 * @date 2016.04.20
 */
public class CartV2 {
	private Jedis jedis;
	/** 해당 고객의 상품정보(상품번호리스트), {"products":["151","152","153","154"]}, 장바구니 생성 시 KEY_CART_LIST 가 들어간다 */
	private JSONObject cartInfo;
	/** 사용자번호 */
	private String userNo;
	/** 장바구니 상품번호 리스트 (16251:cart:product), {"products":["151","152","153","154","155"]} */
	private static final String KEY_CART_LIST = ":cart:product";
	/** 장바구니 상품번호, 이름, 수량 */
	private static final String KEY_CART_PRODUCT = ":cart:productid:";
	/** 상품번호 리스트 (products), ["151","152","153","154"]*/
	private static final String JSON_PRODUCT_LIST = "products";
	/** 장바구니 상품의 만료일은 3일 */
	private static final int EXPIRE = 60*60*24*3; 

	
	/**
	 * 장바구니를 처리하기 위한 CartV2 클래스 생성자
	 * @param helper 제디스 헬퍼 객체
	 * @param userNo 사용자번호
	 */
	public CartV2(JedisHelper helper, String userNo) {
		this.jedis = helper.getConnection();
		this.userNo = userNo;
		// {"products":[]}
		this.cartInfo = getCartInfo();	// 사용자의 Cart 객체가 생성될 때 저장된 상품번호 목록 조회
	}
	
	/**
	 * 레디스에 저장된 장바구니 정보를 조회하여 JSON 객체로 변환
	 * @return 장바구니 정보가 저장된 JSONObject
	 */
	private JSONObject getCartInfo() {
		// {"products":["151","152","153","154","155"]}
		String productInfo = this.jedis.get(userNo + KEY_CART_LIST);	// 상품번호 리스트
		// 저장된 상품번호 목록이 없으면 빈 장바구니 객체 생성
		if (productInfo == null || "".equals(productInfo)) {
			return makeEmptyCart();
		}
		
		try {
			JSONParser parser = new JSONParser();
			System.out.println("레디스에 저장된 장바구니 정보를 조회하여 JSON 객체로 변환 getCartInfo() - " + parser.parse(productInfo));
			System.out.println("레디스에 저장된 장바구니 정보를 조회하여 JSON 객체로 변환 productInfo - " + this.jedis.get(userNo + KEY_CART_LIST));
			return (JSONObject) parser.parse(productInfo);
		} catch (Exception e) {
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
		cart.put(JSON_PRODUCT_LIST, new JSONArray());
		// {"products":[]}
		System.out.println("장바구니가 존재하지 않는 사용자를 위한 빈 장바구니 정보 생성 makeEmptyCart() - " + cart);
		return cart;
	}
	
	/**
	 * 장바구니에 저장된 상품 삭제 (Cart랑 다름)
	 * @return 삭제된 상품갯수
	 */
	public int flushCart() {
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
		
		Pipeline p = jedis.pipelined();
		// 장바구니 상품정보 삭제
		for(int i=0; i<products.size(); i++) {
			System.out.println("장바구니에 저장된 상품 삭제 (Cart랑 다름) flushCart [" + i + "] - " + this.userNo + KEY_CART_PRODUCT + products.get(i));
			System.out.println("장바구니에 저장된 상품 삭제 JSON_PRODUCT_LIST flushCart [" + i + "] - " + this.cartInfo.get(JSON_PRODUCT_LIST));
			p.del(this.userNo + KEY_CART_PRODUCT + products.get(i));
		}
		
		// 장바구니 상품번호 리스트 초기화
		p.set(this.userNo + KEY_CART_LIST, "");
		p.sync();
		return products.size();
	}
	
	/**
	 * 장바구니에 상품 추가
	 * @param productNo 장바구니에 추가할 상품번호
	 * @param productName 상품명
	 * @param quantity 갯수
	 * @return 장바구니 등록 결과
	 */
	@SuppressWarnings("unchecked")
	public String addProduct(String productNo, String productName, int quantity) {
		// 장바구니 카트리스트 업데이트
		System.out.println("장바구니에 상품 추가 시 카트리스트 조회 - " + this.cartInfo.get(JSON_PRODUCT_LIST));		// ["151","152","153","154"]
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);	
		products.add(productNo);
		this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());

		// 장바구니 상품정보 추가
		JSONObject product = new JSONObject();
		product.put("productNo", productNo);
		product.put("productName", productName);
		product.put("quantity", quantity);
		
		String productKey = this.userNo + KEY_CART_PRODUCT + productNo;
		return this.jedis.setex(productKey, EXPIRE, product.toJSONString());
	}
	
	/**
	 * 장바구니에 저장된 상품정보 삭제
	 * @param productNo 삭제할 상품번호 목록
	 * @return 삭제된 상품의 개수
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
		System.out.println("장바구니에 저장된 상품정보 삭제 , 삭제된 상품의 개수 - " + result);
		return result;
	}
	
	/**
	 * 장바구니에 저장된 상품목록을 JSONArray 형식으로 조회
	 * @return 조회된 상품 목록
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getProductList() {
		boolean isChanged = false;
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
		
		// 상품이 만료되었는지 확인하기 위함
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
		
		// 상품이 저장된 키가 만료되면 레디스에서 자동으로 사라지므로 조회된 값이 존재하지 않는다.
		// 그러므로 상품번호 목록 갱신
		if (isChanged) {
			this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());
		}
		// ["{\"quantity\":1,\"productNo\":\"151\",\"productName\":\"원두커피1\"}","{\"quantity\":2,\"productNo\":\"152\",\"productName\":\"원두커피2\"}","{\"quantity\":3,\"productNo\":\"153\",\"productName\":\"원두커피3\"}","{\"quantity\":4,\"productNo\":\"154\",\"productName\":\"원두커피4\"}","{\"quantity\":5,\"productNo\":\"155\",\"productName\":\"원두커피5\"}"]
		System.out.println("장바구니에 저장된 상품목록을 JSONArray 형식으로 조회 getProductList() -  " + result);
		return result;
	}
	
	/**
	 * keys 명령을 사용하여 장바구니에 저장된 상품 삭제
	 * @return 삭제된 상품갯수
	 * @deprecated keys 명령을 사용한 잘못된 구현
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
