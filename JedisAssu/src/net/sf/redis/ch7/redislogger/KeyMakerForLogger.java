package net.sf.redis.ch7.redislogger;

/**
 * Ű ����Ŀ �������̽��� ����Ͽ� �α� �ۼ��� Ű �����ϴ� Ŭ����
 * 
 * Ű�� �����ϴ� ������ �ߺ� �����ϱ� ����
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
	 * ���� ���̵� ��ȸ
	 * @return ���� ���̵�
	 */
	public String getServerId() {
		return this.serverId;
	}
}
