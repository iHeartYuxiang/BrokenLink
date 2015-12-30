package com.iheart.selenium.brokenLink;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class ExcelUtility {
	
	    private static final String FILE_PATH = "C:\\Users\\1111128\\workspace\\LocalSanity\\EXCEL";
	    private static final String siteList_desktop = "C:\\Users\\1111128\\workspace\\LocalSanity\\desktop.txt";
	    private static final String siteList_mobile = "C:\\Users\\1111128\\workspace\\LocalSanity\\mobile.txt";
	    private static final String siteList_premier = "C:\\Users\\1111128\\workspace\\LocalSanity\\premier.txt";
	    //We are making use of a single instance to prevent multiple write access to same file.
	    private static final ExcelUtility INSTANCE = new ExcelUtility();

	    public static ExcelUtility getInstance() {
	        return INSTANCE;
	    }

	    private ExcelUtility() {
	    }
	    
	    public static List<String> readSiteList(String file) 
	    {   List<String> result = new ArrayList<String>();
	        try{
		    	for (String line : Files.readAllLines(Paths.get(file))) {
		    	   // System.out.println("See line:" + line);
		    	    result.add(line);
		    	}
	        }catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	    	return result;
	    }

	    public static void writeToExcel( List<BadLink> brokenLinkList)
	    {  
	    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
   			Date date = new Date();
   			//System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
	       String fileName = "BadLinks_" +  dateFormat.format(date) + ".xls";
	    	
	    	try {
	    	    
				FileOutputStream fileOut = new FileOutputStream(fileName);
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet worksheet = workbook.createSheet("POI Worksheet");
				
				HSSFRow row = worksheet.createRow((short) 0);
				
				HSSFCell cellA1 = row.createCell((short) 0);
				
				
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setFillForegroundColor(HSSFColor.BLUE.index);
			//	cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				cellA1.setCellStyle(cellStyle);
				
				
				int rowCount = brokenLinkList.size();
				if (rowCount < 1)
					cellA1.setCellValue("Check:" + CheckBrokenLink.getURL() + ": WOW WOW WOW! All good! Nothing is broken!");
				else
				{	cellA1.setCellValue("Check:" + CheckBrokenLink.getURL());
				
				
				    // MAKE SOME TITLE
					row = worksheet.createRow((short) (1));
					
					cellA1 = row.createCell((short) 0);
					cellA1.setCellValue("Bad Link URL");
		
					HSSFCell cellB1 = row.createCell((short) 1);
					cellB1.setCellValue("Link Text or Image Source");
					
					HSSFCell cellC1 = row.createCell((short) 2);
					cellC1.setCellValue("Status Code");
					
					cellA1.setCellStyle(cellStyle);
					cellB1.setCellStyle(cellStyle);
					cellC1.setCellStyle(cellStyle);
				
				}
	
				
				
				for (int i = 0 ; i < rowCount; i++)
				{	
					// index from 0,0... cell A1 is cell(0,0)
					
					row = worksheet.createRow((short) (i+2));
		
					cellA1 = row.createCell((short) 0);
					cellA1.setCellValue(brokenLinkList.get(i).getUrl());
		
					HSSFCell cellB1 = row.createCell((short) 1);
					cellB1.setCellValue(brokenLinkList.get(i).getLinkText());
					
					HSSFCell cellC1 = row.createCell((short) 2);
					cellC1.setCellValue(brokenLinkList.get(i).getStatusCode());
					
				}	
	
	
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }


	    public static void writeToExcel_bad(List<BadLink> brokenLinkList){

	        // Using XSSF for xlsx format, for xls use HSSF
	        Workbook workbook = new XSSFWorkbook();

	        Sheet linkSheet = workbook.createSheet("BrokenLinks");

	        int rowIndex = 0;
	        for(BadLink link : brokenLinkList)
	        {
	            Row row = linkSheet.createRow(rowIndex++);
	            int cellIndex = 0;
	            //first place in row is url
	            row.createCell(cellIndex++).setCellValue(link.getUrl());

	            //second place in row is status code
	           row.createCell(cellIndex++).setCellValue(link.getStatusCode());
	        }//FOR()

	        //write this workbook in excel file.
	        try {
	            FileOutputStream fos = new FileOutputStream(FILE_PATH);
	            workbook.write(fos);
	            fos.close();

	            System.out.println(FILE_PATH + " is successfully written");
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }


	    }
	
	
}
