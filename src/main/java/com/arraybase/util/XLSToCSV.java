package com.arraybase.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

public class XLSToCSV {

	public static void xlsx(File inputFile, File outputFile) {
		// For storing data into CSV files
		StringBuffer data = new StringBuffer();
		FileOutputStream fos = null;
		XSSFWorkbook wBook = null;
		try {
			fos = new FileOutputStream(outputFile);

			// Get the workbook object for XLSX file
			wBook = new XSSFWorkbook(new FileInputStream(inputFile));

			// Get first sheet from the workbook
			XSSFSheet sheet = wBook.getSheetAt(0);
			Row row;
			Cell cell;

			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				row = rowIterator.next();

				// For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {

					cell = cellIterator.next();

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						data.append(cell.getBooleanCellValue() + ",");

						break;
					case Cell.CELL_TYPE_NUMERIC:
						data.append(cell.getNumericCellValue() + ",");

						break;
					case Cell.CELL_TYPE_STRING:
						data.append(cell.getStringCellValue() + ",");
						break;

					case Cell.CELL_TYPE_BLANK:
						data.append("" + ",");
						break;
					default:
						data.append(cell + ",");

					}
				}
			}

			fos.write(data.toString().getBytes());
		} catch (Exception ioe) {
			ioe.printStackTrace();
		} finally {
			IOUTILs.closeResource(fos);
			IOUTILs.closeResource(wBook);
		}
	}

	public static void main(String[] args) {

		File inputFile = new File("test.xlsx");
		File outputFile = new File("output.csv");
		xlsx(inputFile, outputFile);
	}
}
