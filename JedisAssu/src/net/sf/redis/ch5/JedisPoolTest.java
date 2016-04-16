package net.sf.redis.ch5;

import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * JedisPool�̶�� ���� Ǯ ��ü�� ����Ͽ� Ŀ�ؼ� ����
 * ���� Ǯ�� ����Ͽ� �ؽ� �����Ϳ� ���� ���� �� ��ȸ
 * ���𽺴� ����ġ�� ObjectPool ���̺귯���� ����Ͽ� ���� Ǯ�� ������.
 * @author juhyun10
 * @date 2016.03.07
 */
public class JedisPoolTest {
	public static void main(String[] args) {
		Config config = new Config();
		config.maxActive = 20;	// ObjectPool�� �ִ밹�� ����, ������ 8, ���̳ʽ��� ���� ����.
		// ObjectPool�� ��ϵ� ������ ������ �ִ� ������ �������� ��, ���ο� ��û�� ���� ó����� ����.
		// WHEN_EXHAUSTED_BLOCK : ���뿬���� ���涧���� ���
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		
		// ���� �������� ���ʷ� �Ͽ� 10.10~ 6379 ��Ʈ���� �����ϴ� ���� ������ ���� ����Ǯ�� ����.
		// ��������� WAS���� �Ҷ��� ���� Ǯ�� �������(static final ���) �� ����ؼ� �����.
		JedisPool pool = new JedisPool(config, "127.0.0.1", 6379);
		
		// ������ ���� Ǯ���� ù ��° Ŀ�ؼ��� ������.
		Jedis firstClient = pool.getResource();
		
		// ù��° Ŀ�ؼ��� �̿��Ͽ� info:�繫������ Ű�� ������ ���� �Է�
		firstClient.hset("info:�繫������", "�̸�", "������");
		firstClient.hset("info:�繫������", "����", "840509");
		
		// ������ ���� Ǯ���� �� ��° Ŀ�ؼ��� ������.
		Jedis secondClient = pool.getResource();
		
		// info:�繫������ Ű�� ����� ������ ��ȸ
		Map<String, String> result = secondClient.hgetAll("info:�繫������");
		System.out.println("�̸� : " + result.get("�̸�"));
		System.out.println("���� : " + result.get("����"));
		
		// ù��° Ŀ�ؼ��� Ǯ�� ������
		pool.returnResource(firstClient);
		
		pool.returnResource(secondClient);
		
		// ������ ���� ���� Ǯ ����
		pool.destroy();
	}
}
