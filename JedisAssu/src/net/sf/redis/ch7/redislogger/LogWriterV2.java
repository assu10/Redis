package net.sf.redis.ch7.redislogger;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * ����Ʈ ���������� ����Ͽ� ���� ������ �α� ���ڿ��� ���.
 * 
 * @author assu
 * @date 2016.04.02
 */
public class LogWriterV2 { 
	private static final String KEY_WAS_LOG = "was:log:list";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS ");
	
	JedisHelper helper;
	
	/**
	 * ���𽺿� �α׸� ����ϱ� ���� Logger�� ������
	 * @param helper ���� ���� ��ü
	 */
	public LogWriterV2(JedisHelper helper) {
		this.helper = helper;
	}
	
	/**
	 * ���𽺿� �α׸� ����ϴ� �޼���.
	 * @param log ������ �α׹��ڿ�
	 * @return ����� ����� ������ ����� ����Ʈ�� ��� ����
	 */
	public Long log(String log) {
		Jedis jedis = this.helper.getConnection();
		Long rtn = jedis.lpush(KEY_WAS_LOG, sdf.format(new Date()) + log + "\n");
		
		this.helper.returnResource(jedis);
		
		return rtn;
	}
}
