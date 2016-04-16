package net.sf.redis.ch5;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * ���� ����� Ŭ���̾�Ʈ���� ����� ������������ ���� �ؽ�Ʈ ���� ����
 * @author Administrator
 *
 */
public class PipelineData {
	private BufferedWriter writer;
	
	private String fileNamePrefix = "./redis_data";
	
	private String fileNamePostfix = ".txt";
	
	private final int TOTAL_NUMBER_OF_COMMAND = 100000;	// �ۼ��� ������ �Ǽ� ���� (�ʸ���)
	
	public static void main(String[] args) throws IOException {
		PipelineData data = new PipelineData();
		data.makeDataFileAsCommans();
		data.makeDataFileAsProtocol();
	}
	
	/**
	 * ���� ��ɾ� �������� �ؽ�Ʈ ���� ����
	 * set key100000 key100000
	 * @throws IOException 
	 */
	private void makeDataFileAsCommans() throws IOException {
		String fileName = fileNamePrefix + "_command" + fileNamePostfix;	// ��ɾ� ���� ������ ������ �̸� ����
		writer = new BufferedWriter(new FileWriter(fileName));
		
		String key, value;
		for (int i = 0; i < TOTAL_NUMBER_OF_COMMAND; i++) {
			key = value = String.valueOf("key" + (100000+i));
			writer.write("set " + key + " " + value + "\r\n");	// ��ɾ� ������ ������ ����, Ű�� ���� 	9����Ʈ�� ����
		}
		
		writer.flush();
		writer.close();
	}
	
	/**
	 * ���� �������� ������ �ؽ�Ʈ ���� ����
	 * *3
	 * $3
	 * set
	 * $9
	 * key100000
	 * $9
	 * key100000
	 * @throws IOException 
	 */
	private void makeDataFileAsProtocol() throws IOException {
		String fileName = fileNamePrefix + "_protocal" + fileNamePostfix;	// �������� ���� ������ ������ �̸��� ����
		writer = new BufferedWriter(new FileWriter(fileName));
		
		String key, value;
		
		for (int i = 0; i < TOTAL_NUMBER_OF_COMMAND; i++) {
			key = value = String.valueOf("key" + (100000+i));
			
			writer.write("*3\r\n");		// 5
			writer.write("$3\r\n");
			writer.write("set\r\n");
			writer.write("$" + key.length() + "\r\n");
			writer.write(key + "\r\n");
			writer.write("$" + value.length() + "\r\n");
			writer.write(value + "\r\n");
		}
		
		writer.flush();
		writer.close();
	}
}
