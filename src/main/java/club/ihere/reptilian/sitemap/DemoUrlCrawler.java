package club.ihere.reptilian.sitemap;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 页面爬取URL
 */
public class DemoUrlCrawler extends BreadthCrawler {

    private static Set<String> sitmap = new HashSet<>();

    /**
     * 构造一个基于伯克利DB的爬虫
     * 伯克利DB文件夹为crawlPath，crawlPath中维护了历史URL等信息
     * 不同任务不要使用相同的crawlPath
     * 两个使用相同crawlPath的爬虫并行爬取会产生错误
     *
     * @param crawlPath 伯克利DB使用的文件夹
     */
    public DemoUrlCrawler(String crawlPath, String baseUrl, String... urlRegex) {
        super(crawlPath, true);
        //只有在autoParse和autoDetectImg都为true的情况下
        //爬虫才会自动解析图片链接
        // getConf().setAutoDetectImg(true);
        //如果使用默认的Requester，需要像下面这样设置一下网页大小上限
        //否则可能会获得一个不完整的页面
        //下面这行将页面大小上限设置为10M
        getConf().setMaxReceiveSize(1024 * 1024 * 10);
        //添加种子URL
        addSeed(baseUrl);
        //限定爬取范围
        for (String regex : urlRegex) {
            addRegex(regex);
        }
        //设置为断点爬取，否则每次开启爬虫都会重新爬取
//        demoImageCrawler.setResumable(true);
        setThreads(30);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        //根据http头中的Content-Type信息来判断当前资源是网页还是图片
        String contentType = page.contentType();
        //根据Content-Type判断
        if (contentType != null && contentType.startsWith("text/html")) {
            Links links = page.links();
            for (String link : links) {
                if (link.contains("qinqinxiaobao") && link.endsWith(".html")) {
                    next.add(link);
                    sitmap.add(link);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String baseUrl = "https://www.qinqinxiaobao.com";
        String[] urlRegexs = {"(https:\\/\\/www.qinqinxiaobao.com\\/)([^# ]*).html$"};
        String crawlPath = "DemoUrlCrawler";
        String filePath = "D:\\seo\\";
        String fileName = "sitemap.txt";
        sitmap.clear();
        File dir = new File(filePath);
        File file = new File(filePath + fileName);
        if (!file.exists()) {
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            file.createNewFile();
        } else {
            sitmap.addAll(readFile(file));
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        }
        DemoUrlCrawler demoUrlCrawler = new DemoUrlCrawler(crawlPath, baseUrl, urlRegexs);
        demoUrlCrawler.setRequester(new OkHttpRequester());
        //设置为断点爬取，否则每次开启爬虫都会重新爬取
        demoUrlCrawler.setResumable(true);
        demoUrlCrawler.start(20);
        System.out.println(sitmap.size());
        String line = System.getProperty("line.separator");
        StringBuffer str = new StringBuffer();
        FileWriter fw = new FileWriter(file, true);
        Iterator iter = sitmap.iterator();
        while (iter.hasNext()) {
            String next = (String) iter.next();
            str.append(next).append(line);
        }
        fw.write(str.toString());
        fw.flush();
        fw.close();
    }

    private static Set<String> readFile(File fin) throws IOException {
        Set<String> linkSet = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(fin));
        String line = null;
        while ((line = br.readLine()) != null) {
            linkSet.add(line);
        }
        br.close();
        return linkSet;
    }
}
