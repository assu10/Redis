package net.sf.redis.ch7.cart;

import net.sf.redis.JedisHelper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
	/** 해당 고객의 상품정보(상품번호리스트) */
	private JSONObject cartInfo;
	/** 사용자번호 */
	private String userNo;
	/** 상품번호 리스트 (16251:cart:product) */
	private static final String KEY_CART_LIST = ":cart:product";
	/** 상품번호, 이름, 수량 */
	private static final String KEY_CART_PRODUCT = ":cart:productid:";
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
		this.cartInfo = getCartInfo();	// 사용자의 Cart 객체가 생성될 때 저장된 상품번호 목록 조회
	}
	
	/**
	 * 레디스에 저장된 장바구니 정보를 조회하여 JSON 객체로 변환
	 * @return 장바구니 정보가 저장된 JSONObject
	 */
	private JSONObject getCartInfo() {
		String productInfo = this.jedis.get(userNo + KEY_CART_LIST);	// 상품번호 리스트
		
		// 저장된 상품번호 목록이 없으면 빈 장바구니 객체 생성
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
	 * 장바구니가 존재하지 않는 사용자를 위한 빈 장바구니 정보 생성
	 * @return 빈 장바구니 정보
	 */
	@SuppressWarnings("unchecked")
	private JSONObject makeEmptyCart() {
		JSONObject cart = new JSONObject();
		cart.put(JSON_PRODUCT_LIST, new JSONArray());
		return cart;
	}
	
	/**
	 * 장바구니에 저장된 상품 삭제 (Cart랑 다름)
	 * @return 삭제된 상품갯수
	 */
	public int flushCart() {
		JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
		
		Pipeline p = jedis.pipelined();
		★
		return 1;
	}
	
	/**
	 * 장바구니에 상품 추가
	 * @param productNo 장바구니에 추가할 상품번호
	 * @param productName 상품명
	 * @param quantity 갯수
	 * @return 장바구니 등록 결과
	 */
	public String addProduct(String productNo, String productName, int quantity) {

	}
	
	/**
	 * 장바구니에 저장된 상품정보 삭제
	 * @param productNo 삭제할 상품번호 목록
	 * @return 삭제된 상품의 개수
	 */
	public int deleteProduct(String[] productNo) {

	}
	
	/**
	 * 장바구니에 저장된 상품목록을 JSONArray 형식으로 조회
	 * @return 조회된 상품 목록
	 */
	public JSONArray getProductList() {

	}
	
	/**
	 * keys 명령을 사용하여 장바구니에 저장된 상품 삭제
	 * @return 삭제된 상품갯수
	 * @deprecated keys 명령을 사용한 잘못된 구현
	 */
	public int flushCartDeprecated() {

	}
}
