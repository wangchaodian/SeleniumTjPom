package utility;

import config.Constants;
import executionEngine.DriverScript;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ExcelUtils {

	private static XSSFSheet ExcelWSheet;
	private static XSSFWorkbook ExcelWBook;
	private static XSSFCell Cell;
	private static XSSFRow Row;

	// 设置Excel文件路径，方便读取到文件
	public static void setExcelFile(String Path) throws Exception {
		try {
			FileInputStream ExcelFile = new FileInputStream(Path);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
		} catch (Exception e) {
			Log.error("无法读取文件 --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// 读取Excel文件单元格数据
	// 新增sheetname参数，这样就可以去读取Test Steps和Test Cases两个工作表的单元格数据
	public static String getCellData(int RowNum, int ColNum, String SheetName)
			throws Exception {
		ExcelWSheet = ExcelWBook.getSheet(SheetName);
		try {
			Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
			ExcelWSheet.getRow(RowNum).getCell(ColNum).setCellType(Cell.CELL_TYPE_STRING);
			String CellData = Cell.getStringCellValue();
			return CellData;
		} catch (Exception e) {
			return "";
		}

	}

	// 得到一共多少行数据
	public static int getRowCount(String SheetName) {
		int number = 0;
		try {
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			number = ExcelWSheet.getLastRowNum() + 1;
		} catch (Exception e) {
			Log.error("无法获得行数据 --- " + e.getMessage());
			DriverScript.bResult = false;

		}
		// System.out.println(number);
		return number;
	}

	// 得到测试用例的行号,参数分别是Test Case ID, Test Case第一个步骤和sheet名称，返回值是测试用例的步骤数。
	public static int getRowContains(String sTestCaseName, int colNum,
			String SheetName) throws Exception {
		int i = 0;
		try {
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			int rowCount = ExcelUtils.getRowCount(SheetName);
			for (; i < rowCount; i++) {
				if (ExcelUtils.getCellData(i, colNum, SheetName).equalsIgnoreCase(sTestCaseName)) {
					break;
				}
			}
		} catch (Exception e) {
			Log.error("无法获得行号 --- " + e.getMessage());
			DriverScript.bResult = false;
		}
		// System.out.println(i);
		return i;
	}

	// 计算一个测试用例有多少个步骤
	public static int getTestStepsCount(String SheetName, String sTestCaseID,
			int iTestCaseStart) throws Exception {

		try {
			for (int i = iTestCaseStart; i <= ExcelUtils.getRowCount(SheetName); i++) {
				if (!sTestCaseID.equals(ExcelUtils.getCellData(i,
						Constants.Col_TestCaseID, SheetName))) {
					int number = i;
					return number;
				}
			}
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			int number = ExcelWSheet.getLastRowNum() + 1;
			return number;

		} catch (Exception e) {
			Log.error("无法获得测试用例步骤 --- " + e.getMessage());
			DriverScript.bResult = false;
			return 0;
		}

	}

	// 构造一个往单元格写数据的方法，主要是用来写结果pass还是fail
	public static void setCellData(String Result, int RowNum, int ColNum,
			String SheetName) {

		try {

			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			Row = ExcelWSheet.getRow(RowNum);
			Cell = Row.getCell(ColNum);
			if (Cell == null) {
				Cell = Row.createCell(ColNum);
				Cell.setCellValue(Result);
			} else {
				Cell.setCellValue(Result);
			}
			// Constant variables Test Data path and Test Data file name
			FileOutputStream fileOut = new FileOutputStream(
					Constants.Path_TestData);
			ExcelWBook.write(fileOut);
			// fileOut.flush();
			fileOut.close();
			ExcelWBook = new XSSFWorkbook(new FileInputStream(
					Constants.Path_TestData));
		} catch (Exception e) {

			DriverScript.bResult = false;
		}
	}

}
