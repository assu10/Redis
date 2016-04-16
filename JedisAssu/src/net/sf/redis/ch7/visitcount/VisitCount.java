package net.sf.redis.ch7.visitcount;

import java.util.ArrayList;
import java.util.List;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * �̺�Ʈ ���̵� ���ڷ� �޾Ƽ� �湮 Ƚ�� ������Ű�� ��ȸ
 * --> ��¥�� ��ȸ�� �ȵ�
 * 
 * �̺�Ʈ ������ �湮Ƚ�� ����
 * ��� ������ �湮Ƚ�� ��ȸ
 * ���� �̺�Ʈ ������ �湮Ƚ�� ��ȸ
 * 
 * @author assu
 * @date 2016.04.16
 */
public class VisitCount {
	private JedisHelper jedisHelper;
	private Jedis jedis;
	private static final String KEY_EVENT_CLICK_TOTAL = "event:click:total";
	private static final String KEY_EVENT_CLICK = "event:click:";
	
	/**
	 * �湮Ƚ�� ó���� ���� Ŭ���� ������.
	 * @param jedisHelper ���� ���� ��ü
	 */
	public VisitCount(JedisHelper jedisHelper) {
		this.jedisHelper = jedisHelper;
		this.jedis = this.jedisHelper.getConnection();
	}
	
	/**
	 * ��û�� �̺�Ʈ �������� �湮Ƚ���� ��ü �̺�Ʈ �������� �湮Ƚ�� ����
	 * @param eventId �̺�Ʈ���̵�
	 * @return ��û�� �̺�Ʈ �������� �� �湮Ƚ��
	 */
	public Long addVisit(String eventId) {
		this.jedis.incr(KEY_EVENT_CLICK_TOTAL);
		return this.jedis.incr(KEY_EVENT_CLICK + eventId);
	}
	
	/**
	 * ��ü �̺�Ʈ ������ �湮Ƚ�� ��ȸ
	 * @return ��ü �̺�Ʈ ������ �湮Ƚ��
	 */
	public String getVisitTotalCount() {
		return this.jedis.get(KEY_EVENT_CLICK_TOTAL);
	}
	
	/**
	 * ��û�� �̺�Ʈ ���̵�鿡 ���� �湮Ƚ�� ��ȸ
	 * @param strings
	 * @return
	 */
	public List<String> getVisitCount(String... eventList) {
		List<String> result = new ArrayList<String>();
		for (int i=0; i<eventList.length; i++) {
			result.add(this.jedis.get(KEY_EVENT_CLICK + eventList[i]));
		}
		return result;
	}
}
