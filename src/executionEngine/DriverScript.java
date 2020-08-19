package executionEngine;

import config.ActionsKeywords;
import config.Constants;

import org.apache.log4j.xml.DOMConfigurator;

import utility.ExcelUtils;
import utility.Log;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

public class DriverScript {

	// 声明一个public static的类对象，所以我们可以在main方法范围之外去使用。
	public static ActionsKeywords actionsKeywords;
	public static String sActionKeyword;
	// 下面是返回类型是方法，这里用到反射类
	public static Method method[];
	// 新建一个Properties对象
	public static Properties OR;
	public static String sPageObject;

	public static int iTestStep;
	public static int iTestLastStep;
	public static String sTestCaseID;
	public static String sRunMode;
	public static boolean bResult;
	public static String sData;
	public static String sy;

	// 这里我们初始化'ActionsKeywords'类的一个对象
	public DriverScript() throws NoSuchMethodException, SecurityException {
		actionsKeywords = new ActionsKeywords();
		method = actionsKeywords.getClass().getMethods();
	}

	public static void main(String[] args) throws Exception {
		// 这样一定要加，否则报log4j初始化的警告
		DOMConfigurator.configure("log4j.xml");
		ExcelUtils.setExcelFile(Constants.Path_TestData);
		// 创建一个文件输入流对象，参数来源外部OR.txt文件
		FileInputStream fs = new FileInputStream(Constants.Path_OR);
		// 创建一个Properties对象
		OR = new Properties(System.getProperties());
		// 加载全部对象仓库文件
		OR.load(fs);
		// 执行用例
		DriverScript startEngine = new DriverScript();
		startEngine.execute_TestCase();
	}

	private void execute_TestCase() throws Exception {
		// 获取测试用例数量
		int iTotalTestCases = ExcelUtils.getRowCount(Constants.Sheet_TestCases);
//		System.out.println(iTotalTestCases);
		// 外层for循环，有多少个测试用例就执行多少次循环
		for (int iTestcase = 1; iTestcase < iTotalTestCases; iTestcase++) {
			// 从Test Case表获取测试ID
			bResult = true;
			sTestCaseID = ExcelUtils.getCellData(iTestcase,Constants.Col_TestCaseID, Constants.Sheet_TestCases);
			// 获取当前测试用例的Run Mode的值
			sRunMode = ExcelUtils.getCellData(iTestcase, Constants.Col_RunMode,Constants.Sheet_TestCases);
//			System.out.println(sRunMode);
			// Run Mode的值控制用例是否被执行
			if (sRunMode.equals("Yes")) {
				// 只有当Run Mode的单元格数据是Yes，下面代码才会被执行
				iTestStep = ExcelUtils.getRowContains(sTestCaseID,Constants.Col_TestCaseID, Constants.Sheet_TestSteps);
				iTestLastStep = ExcelUtils.getTestStepsCount(Constants.Sheet_TestSteps, sTestCaseID, iTestStep);

				bResult = true;
				// 下面这个for循环的次数就等于测试用例的步骤数
				for (; iTestStep < iTestLastStep; iTestStep++) {
					//从TestStep表获得关键字一列
					sActionKeyword = ExcelUtils.getCellData(iTestStep,Constants.Col_ActionKeyword,Constants.Sheet_TestSteps);
					//从TestStep表获得PageObject一列
					sPageObject = ExcelUtils.getCellData(iTestStep, Constants.Col_PageObject,Constants.Sheet_TestSteps);
					//从TestStep表获得SetData一列
					sData = ExcelUtils.getCellData(iTestStep,Constants.Col_DataSet, Constants.Sheet_TestSteps);
					
					sy = ExcelUtils.getCellData(iTestStep,Constants.col_Y, Constants.Sheet_TestSteps);	
					execute_Actions();
					if (bResult == false) {

						ExcelUtils.setCellData(Constants.KEYWORD_FAIL,iTestcase, Constants.Col_Result,Constants.Sheet_TestCases);
						Log.endTestCase(sTestCaseID);
						break;
					}
				}
				if (bResult == true) {

					ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestcase,Constants.Col_Result, Constants.Sheet_TestCases);
					Log.endTestCase(sTestCaseID);
				}
			}else{
				System.err.println(sTestCaseID+" :不执行");
			}
		}
		
	}

	private static void execute_Actions() throws Exception {

		for (int i = 0; i < method.length; i++) {

			if (method[i].getName().equals(sActionKeyword)) {
				method[i].invoke(actionsKeywords, sPageObject, sData,sy);
				Thread.sleep(1000);
				if (bResult == true) {
					ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestStep,Constants.Col_TestStepResult,Constants.Sheet_TestSteps);
					break;
				} else {
					ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestStep,Constants.Col_TestStepResult,Constants.Sheet_TestSteps);
//					ActionsKeywords.closeBrowser("", "");
					break;
				}
			}
		}
	}
}
