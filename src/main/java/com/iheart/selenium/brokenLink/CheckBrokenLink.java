package com.iheart.selenium.brokenLink;


import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;  
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import java.net.MalformedURLException;  
import java.net.URL;  

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

public class CheckBrokenLink {

	 public static WebDriver driver;
	   public static final String screenshot_folder="screenshots";
	   public static StringBuffer errors = new StringBuffer(); 
	   
	   public static String browser = "";
	   
	   public static String url;
	   
	   public CheckBrokenLink()
	   {
		   
	   }
		
	   public CheckBrokenLink(WebDriver _driver)
	   {
		   driver = _driver;
	   }

	  
	   public static void setDriver(WebDriver _driver)
	   {
		   driver = _driver;
	   }
	   
	   public static WebDriver getDriver()
	   {
		   return driver;
	   }
	   
	  
		public String getCurrentDateString()
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
			Date date = new Date();
			return dateFormat.format(date);
		}
		
		public String getCurrentDateInMilli()
		{
			Date date = new Date();
			return date.getTime() + "";
		}
	   
		
		public void goThroughLinks(String category) throws Exception
		{  
			String href ="";
	    	String linkText ="";//if it is an image, put image src here
	    	
			//for mobile site, click on sandwich to get all the links there
			if (isMobileSite(getURL()))
				driver.findElement(By.cssSelector("[title='Menu']")).click();
			
			int statusCode = -1;
			List<BadLink> badLinks = new ArrayList<BadLink>();
			List<WebElement> links = driver.findElements(By.tagName("a"));  
			System.out.println("Total links: " + links.size());
			
					
	        for (WebElement link: links)
	        {   href ="";
	            linkText ="";
	        	//write all the links to a excel FILE
	        	//Aren't null link and empty link suspicious?
	        	try{
	        		href  =	link.getAttribute("href").trim();
	        		//for mobilie site, replace "www." link with "m."
	        		if (isMobileSite(getURL()) && href.contains("www." + getDomainName(url)))
	        			href = convertToMobileURL(href);
	        			
	        			
	        		linkText = link.getText();
	        		
	        	}catch(Exception e)
	        	{
	        		System.out.println("Null href!");
	        		
	        	}
	        	
				if (href != null && !href.equals("") && !isAdLink(href) && !isMailLink(href) &&
				     !isSocial(href) && !isVoid(href)  && !isMobileNative(href) && !isJavaScriptAlert(href))
				{	
					
					//System.out.println("See link: " +  href );
					try{
						if (href.startsWith("https"))
							//statusCode= getSSLResponseCode(href); 
							statusCode= getResponseCodeViaHttpClient(href); 
						else
							statusCode= getResponseCode(href); 
					}catch(Exception e)
					{
						//Only take care image src for bad links
						e.printStackTrace();
						System.out.println("eXCEPTON IS THROWN FOR HREF/STATUS:" + href + "--------" + link.getText() );
						
						badLinks.add(new BadLink( href, -2)); //status code is not available
					}
					
				//	if (statusCode != 200 && statusCode != 302 && statusCode != 301)
					if (statusCode == 404 || statusCode ==500 )
					{	
						
						System.out.println("HREF/STATUS:" + href  + "-------" +  statusCode );
					   //  badLinks.add(new BadLink(linkText, href , statusCode));
						badLinks.add(new BadLink(href , statusCode));
					}
				}		
				
	        
	        }  //for()
	  
	        //output bad link to a file 
	        
	        System.out.println("Bad links/statusCode:");
	        
	        List<BadLink> cleanOne = new ArrayList<BadLink>();
	        for (BadLink link: badLinks)
	        {	System.out.println(link.getUrl() + "------" + link.getStatusCode());
	            //Filter out forwarded links
	    	    if (!isForwardLink(link.getUrl()))
	    	        cleanOne.add(link);
	        }
	        
	       
	        
	       // if (badLinks.size() > 0)
	        
	        if (cleanOne.size() > 0)
	            ExcelUtility.writeToExcel(cleanOne, category);
		}
		
		
		private boolean isForwardLink(String _href)
		{  System.out.println("isForwardLink?:" + _href);
			
			driver.get(_href);
			WaitUtility.sleep(1000);
			//System.out.println("isForwardLink?SEE PAGE:" + driver.getPageSource());
			if (driver.getPageSource().contains("Continue to Page"))
				return true;
			else return false;
		}
		
		private boolean isSocial(String url)
		{
			return (url.contains("facebook.com") || url.contains("twitter.com") || url.contains("instagram.com") || url.contains("plus.google.com"));
		}
		
		private boolean isVoid(String url)
		{
			return url.contains("javascript:void(null)");
		}
		
		private boolean isMobileNative(String url)
		{   //if (url.contains("tel:") || url.contains("sms:"))
			 //  System.out.println("Caught mobile phone link:" + url);
			return url.contains("tel:") || url.contains("sms:") ;
		}
		
		public int getResponseCode(String urlString) throws MalformedURLException, IOException {         
		    URL u = new URL(urlString);  
		   
		    HttpURLConnection huc = (HttpURLConnection) u.openConnection();  
		    huc.setRequestMethod("GET");  
		    huc.connect();  
		  //  System.out.println("response:" + huc.getResponseCode());
		    return huc.getResponseCode();  
		}
		
	
		private int getResponseCodeViaHttpClient(String href) throws MalformedURLException, IOException 
		{         
			HttpClient client = HttpClientBuilder.create().build();
			 final String USER_AGENT = "Mozilla/5.0";
		
			HttpGet request = new HttpGet(url);
			 
		//	request.setHeader("User-Agent", USER_AGENT);
			request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.setHeader("Accept-Language", "en-US,en;q=0.5");
		 
			HttpResponse response = client.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
		   // if (responseCode == 404)
			 //   System.out.println("bad url:" + href + "/" +  responseCode );
			
			return responseCode;
		}
		
		
		public void handleError(String msg, String methodName) 
		{
			errors.append(msg);
			try{
				Page.takeScreenshot(driver, methodName);
				
			}catch(Exception e)
			{
				System.out.println("Exception is thrown taking screenshot.");
			}
		}
		
		
		
		public static StringBuffer getErrors()
		{
			return errors;
		}
		
		public static void setBrowser(String _browser)
		{
			browser = _browser;
		}
		
		
		public static void setURL(String _url)
		{
			url = _url;
		}
		
		public static String getURL()
		{
			return url;
		}
		
		
		public boolean isMobileSite(String url)
		{   
			return url.startsWith("http://m.") || url.startsWith("https://m.");
		}
		
		private boolean isAdLink(String url)
		{
			return url.contains("googleads.g.doubleclick.net");
		}
		
		private boolean isMailLink(String url)
		{
			return url.startsWith("mailto");
		}
		
		private boolean isJavaScriptAlert(String url)
		{  // System.out.println("isJavaScriptAlert:" + url);
			boolean isAlert = url.contains("javascript:");
			if (isAlert) 
				System.out.println("Shall return :" + isAlert) ;
			return isAlert;
		}
		
		
		public static void takeScreenshot(WebDriver driver, String testMethod) throws Exception 
		   {      
			        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		   			Date date = new Date();
		   			//System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
			       String screenshotName = testMethod + dateFormat.format(date) + ".png";
			       System.out.println("See screenshotName:" + screenshotName);
		           File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		        //The below method will save the screen shot in d drive with name "screenshot.png"
		           FileUtils.copyFile(scrFile, new File(screenshotName));
		           System.out.println("Screenshot is taken.");
		   }
		
		//Convert http://www.z100.com  to http://m.z100.com
		public String convertToMobileURL(String href)
		{   String mhref = href;
				
		     String part1 = href.split(":")[0]; //http or https:
		     String part2 = href.split(":")[1];
		     part2 = part2.replace("www.", "m.");
		     mhref = part1 + ":" + part2; 
			
			
			System.out.println("convert to mobile site:" + mhref);
			return mhref;
		}
		
		
		
		
		//M.Z100.COM  --> z100.com
		public String getDomainName(String url)
		{   
		     String part1 = url.split(":")[0]; //http or https:
		     String part2 = url.split(":")[1];
		     part2 = part2.substring(4);
		    
			System.out.println("domain:" +  part2);
			return part2;
		}
		
		
	}
