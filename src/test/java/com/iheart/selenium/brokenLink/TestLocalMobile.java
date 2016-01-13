package com.iheart.selenium.brokenLink;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;

import com.iheart.selenium.brokenLink.*;

@RunWith(Parameterized.class)



public class TestLocalMobile {
	
	@Parameters
	public static Collection<Object[]> data() {

		//What day is it today?
		int day = dayOfWeekToday(); //Start from Sunday. 4: Wed, 3: Tuesday...
		
		//Choose sites for checking.  Divide the whole sites by 5, and run the corresponding part
		
		
		//List<String>  siteList_deskTop= ExcelUtility.readSiteList("siteList_desktop.txt");
		List<String>  siteList_mobile= ExcelUtility.readSiteList("siteList_mobile.txt");
		//List<String>  siteList_premiere= ExcelUtility.readSiteList("siteList_premiere.txt");
		
		List<String> siteToCheckToday = new ArrayList<String>();
		//siteToCheckToday.addAll(getTodaysChunk(siteList_deskTop));
		siteToCheckToday.addAll(getTodaysChunk(siteList_mobile));
		//siteToCheckToday.addAll(getTodaysChunk(siteList_premiere));
		
		
		
		Collection<Object[]> params = new ArrayList<>(100);
		
		int i = 0; 
		for (String site:siteToCheckToday)
			params.add(new Object[] {site });
		//params.add(new Object[] {  "http://www.z100.com"});
	   
	    System.out.println("Site count:" + params.size());
	    return params;
	}

	
	private static List<String> getTodaysChunk(List<String> siteList)
	{  
		//What day is it today?
		int dayOfWeek = dayOfWeekToday(); //Start from Saturday:0: Sunday:1; Monday: 2. Tuesday:3; Wed: 4, ..
				
		if (dayOfWeek < 2) return null; //Do nothing during weekends
		
		int dailySize = siteList.size()/5 + 1;
		
		int startIndex = dailySize * (dayOfWeek -2);
		int endIndex = startIndex + dailySize;
	    //
		List<String> siteToCheck = new ArrayList<String>();
		for (int i = startIndex ; i < endIndex; i++)
		{
			siteToCheck.add(siteList.get(i));
		}
		
		System.out.println("siteToCheck count:" + siteToCheck.size());
		
		return siteToCheck;
		
	}
	
	protected static DesiredCapabilities dCaps;

	private final String url;
	
	WebDriver driver;
	
	CheckBrokenLink checkBroken ;
		
	
	
	public TestLocalMobile(String url) {
		this.url = url;
	
	}
	
	@Rule public TestName name = new TestName();
	

	///* run headless
	@Before
	public void setUp() throws Exception {

		/*
	   // System.setProperty("phantomjs.binary.path", "C:\\Users\\1111128\\workspace\\lib\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe");
		System.setProperty("phantomjs.binary.path", "/Users/1111128/git/lib/phantomjs-2.0.0-macosx/bin/phantomjs");
		
		dCaps = new DesiredCapabilities();
		dCaps.setBrowserName("firefox");
		dCaps.setVersion("39");
		dCaps.setJavascriptEnabled(true);
		dCaps.setCapability("takesScreenshot", true);
		
		driver = new PhantomJSDriver(dCaps);
		driver.get(url + "/");
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		*/
		 driver = Utils.launchBrowser(url, "firefox");
	      	
		 checkBroken = new CheckBrokenLink(driver);
		 CheckBrokenLink.setURL(url);
			
	}
	
	
	@Test
	 public void testBadLinks() throws Exception
	 {   
	 	System.out.println("test method:" +  name.getMethodName() );
	 	try{
	 		
	 		checkBroken.goThroughLinks("LocalMobile");
	 	}catch(Exception e)
	 	{
	 		e.printStackTrace();
	 		fail("Exception is thrown.");
	 	}  	
	 	System.out.println(name.getMethodName() + " is Done.");
	 }
   
  
	 @After
	    public void tearDown() throws Exception{
	    	driver.quit(); 
	    	
	    }
	
	    
	 
	 private static int dayOfWeekToday()
	 {
		 Calendar c = Calendar.getInstance();
		 c.setTime(new Date());
		 int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		 System.out.println("Today is:" + dayOfWeek);
		 
		 return dayOfWeek;
	 }
	   
	
	

}
