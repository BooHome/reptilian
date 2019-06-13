package club.ihere.reptilian.sitemap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakeSiteMap {
	/** 
     * 发起http get请求获取网页源代码 
     * @param requestUrl     String    请求地址
     * @return                 String    该地址返回的html字符串
     */  
    private String httpRequest(String requestUrl) {  
        
        StringBuffer buffer = null;  
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConn = null;
  
        try {  
            // 建立get请求
            URL url = new URL(requestUrl);  
            httpUrlConn = (HttpURLConnection) url.openConnection();  
            httpUrlConn.setDoInput(true);  
            httpUrlConn.setRequestMethod("GET");  
  
            // 获取输入流  
            inputStream = httpUrlConn.getInputStream();  
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            bufferedReader = new BufferedReader(inputStreamReader);  
  
            // 从输入流读取结果
            buffer = new StringBuffer();  
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  finally {
            // 释放资源
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(httpUrlConn != null){
                httpUrlConn.disconnect();  
            }
        }
        if(buffer != null){
        	return buffer.toString();  
        }else{
        	return null;
        }
        
      
    }  
    public Map<String,String> targetMap = new HashMap<String,String>();
  
    /** 
     * 过滤掉html字符串中无用的信息
     * @param html    String    html字符串
     * @return         String    有用的数据
     */ 
    private Map<String,String> htmlFiter(String html,String url) {  
        
        // 取出有用的范围
        //Pattern pattern = Pattern.compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|((www.)|[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");
        //Pattern pattern = Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Pattern pattern = Pattern.compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
        Map<String,String> map = new TreeMap<String,String>();
        Matcher matcher = pattern.matcher(html);  
        while(matcher.find()){
        	 for (int i = 0; i < matcher.groupCount(); i++) {
        		 if(i==2){
        			 String targetUrl = matcher.group(i);
        			 //System.out.println("targetUrl="+targetUrl);
        			 if(targetUrl == null || targetUrl.trim().equals("")){
        				 continue;
        			 }
        			 if(targetUrl.indexOf("javascript") >=0){
        				 continue;
        			 }
        			 if(targetUrl.indexOf("http://") ==0 || targetUrl.indexOf("https://")==0){
        				 if(targetUrl.indexOf("www.qinqinxiaobao.com") >=0){
        					 if(!targetMap.containsKey(targetUrl)){
        						 map.put(targetUrl, targetUrl);
        					 }
        				 }
        				 continue;
        			 }
        			 if(targetUrl.indexOf("/") !=0){
        				 if(!targetMap.containsKey("https://www.qinqinxiaobao.com/"+targetUrl)){
        					 map.put("https://www.qinqinxiaobao.com/"+targetUrl, targetUrl);
        			 	}
        				 continue;
        			 }
        			 if(targetUrl.indexOf("/") ==0){
        				 if(!targetMap.containsKey("https://www.qinqinxiaobao.com"+targetUrl)){
        					 map.put("https://www.qinqinxiaobao.com"+targetUrl, targetUrl);
        				 }
        				 continue;
        			 }
        			 if(targetUrl.equals("/")){
        				 if(!targetMap.containsKey(url)){
        					 map.put(url, url);
        				 }
        				 continue;
        			 }

        			 if(!targetMap.containsKey(targetUrl)){
        				 map.put(targetUrl, targetUrl);
    				 }
        			
        		 }
             }
        }
        
        return map;
    }
  
    
    public void getTodayTemperatureInfo(Map<String,String> map) {  
    	targetMap.putAll(map);
    	for(Entry<String,String> entry : map.entrySet()){
    		String url = entry.getKey();
    		//System.err.println("url:"+url);
	        String html = httpRequest(url);
	        if(html == null || html.trim().equals("")){
	        	continue;
	        }
	        Map<String,String> urls = htmlFiter(html,url); 
	        for(Entry<String,String> temp : urls.entrySet()){
	        	System.out.println(temp.getKey());
	        }
	        getTodayTemperatureInfo(urls);
        }
    	
        
    }  
  
    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {  
    	MakeSiteMap make = new MakeSiteMap();
    	Map<String,String> map = new TreeMap<String,String>();
    	String url = "https://www.qinqinxiaobao.com/";
    	//String url = "https://www.qinqinxiaobao.com/news/detail/421";
    	map.put(url, url);
    	make.getTodayTemperatureInfo(map);
       
    }  
}
