package config;

public class Constants {

	// 这里定义为public static的类型，方便其他任何类进行访问和调用
	public static final String URL = "http://192.168.1.222/tj/";
	public static final String Path_TestData = ".//dataEngine//dataEngine.xlsx";
	public static final String File_TestData = "dataEngine.xlsx";

	// dataEngine.xlsx中一些单元格的索引值(Test Steps)
	public static final int Col_TestCaseID = 0;
	public static final int Col_TestScenarioID = 1;
	public static final int Col_PageObject = 3;
	public static final int Col_ActionKeyword = 4;
	public static final int Col_DataSet = 5;
	public static final int Col_TestStepResult = 7;
	public static final int col_ModuleName = 8;
	public static final int col_Y = 6;

	// 第二个是测试用例结果标记列的索引，第一个是测试步骤里面的结果列索引(Test Cases)
	public static final int Col_Result = 3;
	public static final int Col_RunMode = 2;
	
	// 结果状态标记
	public static final String KEYWORD_FAIL = "FAIL";
	public static final String KEYWORD_PASS = "PASS";

	// DataEngmine.excel中sheet的名称
	public static final String Sheet_TestSteps = "Test Steps";
	// 第二个工作薄的名称
	public static final String Sheet_TestCases = "Test Cases";

	// 测试登录用到的用户数据
	public static final String UserName = "xxxxxx";
	public static final String Password = "xxxxxx";
	public static final String local1 = "四川";
	public static final String local2 = "内江";
	public static final String local3 = "资中县";
	// OR(对象仓库)文件路径
	public static final String Path_OR = ".//object/OR.txt";

}
