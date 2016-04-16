package net.sf.redis.ch7.redislogger;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * ���� ������ ����� �α�(����Ʈ ������ ���¸� ���Ϸ� ����
 * 
 * @author assu
 * @date 2016.04.02
 */
public class LogReceiverV2 {
	private static final JedisHelper helper = JedisHelper.getInstance();
	private static final String KEY_WAS_LOG = "was:log:list";
	private static final String LOG_FILE_NAME_PREFIX = "./waslog";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH'.log'");
	
	/**
	 * ���� �������� �α׸� �о ���Ϸ� ����.
	 * ����Ʈ�� ����� ��� ��Ҹ� ���Ϸ� ����, ��Ұ� ������ �޼��� ����
	 */
	public void start() {
		Jedis jedis = helper.getConnection();
		
		while(true) {
			String log =jedis.rpop(KEY_WAS_LOG);
			if (log == null) {
				break;
			}
			writeFile(log);
		}
	}
	
	private void writeFile(String log) {
		try {
			if (log == null) {
				return;
			}
			FileWriter writer = new FileWriter(getCurrentFileName(), true);
			
			writer.append(log);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �޼��尡 ȣ��� �ð��� �ش��ϴ� �α����ϸ� ����
	 * @return ���� �ð��� �ش��ϴ� �α����ϸ�
	 */
	private String getCurrentFileName() {
		return LOG_FILE_NAME_PREFIX + sdf.format(new Date());
	}
}
