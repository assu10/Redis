package net.sf.redis.ch7.redislogger;

/**
 * 키 메이커 인터페이스를 사용하여 로그 작성용 키 생성하는 클래스
 * 
 * 키를 생성하는 로직의 중복 제거하기 위함
 * 
 * @author assu
 * @date 2016.05.08
 */
public class KeyMakerForLogger implements KeyMaker {

	private static final String KEY_WAS_LOG = "was:log:list";
	private final String serverId;
	
	public KeyMakerForLogger(String serverId) {
		this.serverId = serverId;
	}
	
	@Override
	public String getKey() {
		return KeyMakerForLogger.KEY_WAS_LOG;
	}

	/**
	 * 서버 아이디 조회
	 * @return 서버 아이디
	 */
	public String getServerId() {
		return this.serverId;
	}
}
