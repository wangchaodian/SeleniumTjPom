package config;

public class Constants {

	// ���ﶨ��Ϊpublic static�����ͣ����������κ�����з��ʺ͵���
	public static final String URL = "http://192.168.1.222/tj/";
	public static final String Path_TestData = ".//dataEngine//dataEngine.xlsx";
	public static final String File_TestData = "dataEngine.xlsx";

	// dataEngine.xlsx��һЩ��Ԫ�������ֵ(Test Steps)
	public static final int Col_TestCaseID = 0;
	public static final int Col_TestScenarioID = 1;
	public static final int Col_PageObject = 3;
	public static final int Col_ActionKeyword = 4;
	public static final int Col_DataSet = 5;
	public static final int Col_TestStepResult = 7;
	public static final int col_ModuleName = 8;
	public static final int col_Y = 6;

	// �ڶ����ǲ��������������е���������һ���ǲ��Բ�������Ľ��������(Test Cases)
	public static final int Col_Result = 3;
	public static final int Col_RunMode = 2;
	
	// ���״̬���
	public static final String KEYWORD_FAIL = "FAIL";
	public static final String KEYWORD_PASS = "PASS";

	// DataEngmine.excel��sheet������
	public static final String Sheet_TestSteps = "Test Steps";
	// �ڶ���������������
	public static final String Sheet_TestCases = "Test Cases";

	// ���Ե�¼�õ����û�����
	public static final String UserName = "xxxxxx";
	public static final String Password = "xxxxxx";
	public static final String local1 = "�Ĵ�";
	public static final String local2 = "�ڽ�";
	public static final String local3 = "������";
	// OR(����ֿ�)�ļ�·��
	public static final String Path_OR = ".//object/OR.txt";

}
