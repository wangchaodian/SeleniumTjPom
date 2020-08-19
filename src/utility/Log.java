package utility;

import org.apache.log4j.Logger;

public class Log {

	// ��ʼ��log4g log
	private static Logger Log = Logger.getLogger(Log.class.getName());

	// ���в�������֮ǰ����־���
	public static void startTestCase(String sTestCaseName) {

		Log.info("*******************************************");
		Log.info("$$$$$$    " + sTestCaseName + "     $$$$$$$$$$$$");
		Log.info("*******************************************");

	}

	// ����ִ�н�������־���
	public static void endTestCase(String sTestCaseName) {
		Log.info("XXXXXXX            " + "-E---N---D-" + "          XXXXXXXXX");
		Log.info("X");
		Log.info("X");

	}

	// �����ǲ�ͬ��־����ķ�����������Ҫ��ʱ����ã�һ��info��error�õ����
	public static void info(String message) {
		Log.info(message);
	}

	public static void warn(String message) {
		Log.warn(message);
	}

	public static void error(String message) {
		Log.error(message);
	}

	public static void fatal(String message) {
		Log.fatal(message);
	}

	public static void debug(String message) {
		Log.debug(message);
	}

}
