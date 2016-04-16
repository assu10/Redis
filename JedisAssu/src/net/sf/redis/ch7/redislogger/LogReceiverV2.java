package net.sf.redis.ch7.redislogger;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redis.JedisHelper;
import redis.clients.jedis.Jedis;

/**
 * 레디스 서버에 저장된 로그(리스트 데이터 형태를 파일로 저장
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
	 * 레디스 서버에서 로그를 읽어서 파일로 저장.
	 * 리스트에 저장된 모든 요소를 파일로 저장, 요소가 없으면 메서드 종료
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
	 * 메서드가 호출된 시간에 해당하는 로그파일명 생성
	 * @return 현재 시간에 해당하는 로그파일명
	 */
	private String getCurrentFileName() {
		return LOG_FILE_NAME_PREFIX + sdf.format(new Date());
	}
}
