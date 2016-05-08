package net.sf.redis.ch7.recentview;

import java.util.Set;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 최근 조회 상품 목록 (정렬된 셋 데이터형, 가중치로는 현재 시간 저장)
 * 
 *  - 저장되는 상품의 최대 갯수는 20개
 *  - 화면 우측에 최근 상품 4개 노출
 *  - 더 보기 클릭하면 저장된 모든 상품목록 출력
 *  
 *  -> 중복된 상품 제거
 *  
 * @author assu
 * @date 2016.05.06
 */
public class RecentViewListV2 {
	private Jedis jedis;
	/** 사용자가 조회한 상품 목록 (recent:view:zset:12345) */
	private static final String KEY_VIEW_SET = "recent:view:zset:";
	private static final int LIST_MAX_SIZE = 20;
	private String KEY;
	
	public RecentViewListV2(JedisHelper helper, String userNo) {
		this.jedis = helper.getConnection();
		this.KEY = KEY_VIEW_SET + userNo;
	}
	
	/**
	 * 최근 조회 상품목록에 상품 추가
	 * @param productNo 상품 번호
	 * @return 저장된 상품 목록의 개수
	 */
	public Long add(String productNo) {
		// zadd(키,가중치,요소) : 이미 존재하면 저장된 요소의 가중치 변경, 새로 추가되면 1, 기존에 존재하면 0 반환
		Long result = jedis.zadd(this.KEY, System.nanoTime(), productNo);
		
		// zrangeByRank(키,시작인덱스,종료인덱스) : 주어진 키에 저장된 요소 중에서 순위가 시작인덱스부터 종료인덱스 범위에 해당하는 요소 삭제
        //                                     : 삭제된 값을 수 반환
		// 상품 저장 후 최대 개수보다 많은 데이터 삭제하기 위해 zremrangebyrank 명령수행하여 가장 오래된 데이터 삭제
		jedis.zremrangeByRank(this.KEY, -(LIST_MAX_SIZE+1), -(LIST_MAX_SIZE+1));
		System.out.println("최근 조회 상품목록에 상품 추가 add - " + result);
		return result;
	}
	
	/**
	 * 주어진 사용자의 저장된 최근 조회 상품 목록 조회
	 * @return 조회된 상품 목록
	 */
	public Set<String> getRecentViewList() {
		// zrange(키,시작인덱스,종료인덱스) : 시작인덱스부터 종료인덱스까지 가중치 오름차순으로 조회 (zrevrange는 내림차순
		//                               : 조회된 값 목록 반환
		System.out.println("주어진 사용자의 저장된 최근 조회 상품 목록 조회 getRecentViewList - " + this.jedis.zrevrange(this.KEY, 0, -1));
		return this.jedis.zrevrange(this.KEY, 0, -1);
	}
	
	/**
	 * 주어진 개수에 해당하는 최근 조회 상품 목록 조회
	 * @param cnt 조회할 상품의 개수
	 * @return 조회된 상품 목록
	 */
	public Set<String> getRecentViewList(int cnt) {
		System.out.println("주어진 개수에 해당하는 최근 조회 상품 목록 조회 getRecentViewList - " + this.jedis.zrevrange(this.KEY, 0, cnt-1));
		return this.jedis.zrevrange(this.KEY, 0, cnt-1);
	}
	
	/**
	 * 저장가능한 상품 목록의 최대 개수 조회
	 * @return 상품 목록의 최대 개수
	 */
	public int getListMaxSize() {
		return LIST_MAX_SIZE;
	}
}
