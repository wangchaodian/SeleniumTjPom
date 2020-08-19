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

	// ����Excel�ļ�·���������ȡ���ļ�
	public static void setExcelFile(String Path) throws Exception {
		try {
			FileInputStream ExcelFile = new FileInputStream(Path);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
		} catch (Exception e) {
			Log.error("�޷���ȡ�ļ� --- " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	// ��ȡExcel�ļ���Ԫ������
	// ����sheetname�����������Ϳ���ȥ��ȡTest Steps��Test Cases����������ĵ�Ԫ������
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

	// �õ�һ������������
	public static int getRowCount(String SheetName) {
		int number = 0;
		try {
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			number = ExcelWSheet.getLastRowNum() + 1;
		} catch (Exception e) {
			Log.error("�޷���������� --- " + e.getMessage());
			DriverScript.bResult = false;

		}
		// System.out.println(number);
		return number;
	}

	// �õ������������к�,�����ֱ���Test Case ID, Test Case��һ�������sheet���ƣ�����ֵ�ǲ��������Ĳ�������
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
			Log.error("�޷�����к� --- " + e.getMessage());
			DriverScript.bResult = false;
		}
		// System.out.println(i);
		return i;
	}

	// ����һ�����������ж��ٸ�����
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
			Log.error("�޷���ò����������� --- " + e.getMessage());
			DriverScript.bResult = false;
			return 0;
		}

	}

	// ����һ������Ԫ��д���ݵķ�������Ҫ������д���pass����fail
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
