package net.sf.redis.ch7.recentview;

import java.util.List;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 최근 조회 상품 목록 (list 데이터형)
 * 
 *  - 저장되는 상품의 최대 갯수는 30개
 *  - 화면 우측에 최근 상품 4개 노출
 *  - 더 보기 클릭하면 저장된 모든 상품목록 출력
 *  
 *  -> 중복된 상품이 저장됨
 *  
 * @author assu
 * @date 2016.05.06
 */
public class RecentViewList {
	private Jedis jedis;
	/** 사용자가 조회한 상품 목록 (recent:view:12345) */
	private static final String KEY_VIEW_LIST = "recent:view:";
	private static final int LIST_MAX_SIZE = 30;
	private String userNo;
	
	public RecentViewList(JedisHelper helper, String userNo) {
		this.jedis = helper.getConnection();
		this.userNo = userNo;
	}
	
	/**
	 * 최근 조회 상품 목록에 상품 추가 및 최대저장갯수만 빼고 나머지는 삭제
	 * @param productNo 상품 번호
	 * @return 저장된 상품 목록 개수
	 */
	public Long add(String productNo) {
		Long result = this.jedis.lpush(KEY_VIEW_LIST + this.userNo, productNo);	// 리스트 데이터 왼쪽에 저장
		
		// ltrim(키,시작인덱스,종료인덱스) : 주어진 키의 시작인덱스부터 종료인덱스 범위의 요소만을 남기고 삭제
		//                                단, 종료인덱스가 시작인덱스보다 작으면 모든 요소 삭제
		//                                리턴값은 항상 OK
		this.jedis.ltrim(KEY_VIEW_LIST + this.userNo, 0, LIST_MAX_SIZE-1);
		System.out.println("최근 조회 상품 목록에 상품 추가 및 최대저장갯수만 빼고 나머지는 삭제 add - " + result);
		return result;
	}
	
	/**
	 * 주어진 사용자의 저장된 최근 조회 상품목록 조회
	 * @return 조회된 상품목록
	 */
	public List<String> getRecentViewList() {
		System.out.println("주어진 사용자의 저장된 최근 조회 상품목록 조회 getRecentViewList - " + this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, -1));
		// lrange(키,시작인덱스,종료인덱스) : 시작인덱스부터 종료인덱스 범위 요소 조회, 지정된 키가 존재하지 않으면 nil
		return this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, -1);
	}
	
	/**
	 * 주어진 개수에 해당하는 최근 조회 상품목록 조회
	 * @param cnt 조회할 상품 개수
	 * @return 조회된 상품목록
	 */
	public List<String> getRecentViewList(int cnt) {
		System.out.println("주어진 개수에 해당하는 최근 조회 상품목록 조회 getRecentViewList(int) - " + this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, cnt-1));
		return this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, cnt-1);
	}
	
	/**
	 * 저장가능한 상품 목록의 최대 개수 조회
	 * @return 상품목록의 최대 개수
	 */
	public int getListMaxSize() {
		return LIST_MAX_SIZE;
	}
}
