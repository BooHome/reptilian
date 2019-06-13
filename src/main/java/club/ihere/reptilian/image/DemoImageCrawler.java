package club.ihere.reptilian.image;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import cn.edu.hfut.dmic.webcollector.util.ExceptionUtils;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;

import java.io.File;

/**
 * WebCollector抓取图片的例子
 *
 * @author hu
 */
public class DemoImageCrawler extends BreadthCrawler {
    File baseDir = new File("images");
    int i=1;
    /**
     * 构造一个基于伯克利DB的爬虫
     * 伯克利DB文件夹为crawlPath，crawlPath中维护了历史URL等信息
     * 不同任务不要使用相同的crawlPath
     * 两个使用相同crawlPath的爬虫并行爬取会产生错误
     *
     * @param crawlPath 伯克利DB使用的文件夹
     */
    public DemoImageCrawler(String crawlPath) {
        super(crawlPath, true);

        //只有在autoParse和autoDetectImg都为true的情况下
        //爬虫才会自动解析图片链接
        getConf().setAutoDetectImg(true);

        //如果使用默认的Requester，需要像下面这样设置一下网页大小上限
        //否则可能会获得一个不完整的页面
        //下面这行将页面大小上限设置为10M
        //getConf().setMaxReceiveSize(1024 * 1024 * 10);

        //添加种子URL
        addSeed("https://www.qqtn.com/tx/");
        //限定爬取范围
        addRegex("https://www.qqtn.com/.*");
        addRegex("https://pic.qqtn.com/.*");
        addRegex("-.*#.*");
        addRegex("-.*\\?.*");
        //设置为断点爬取，否则每次开启爬虫都会重新爬取
//        demoImageCrawler.setResumable(true);
        setThreads(30);

    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        //根据http头中的Content-Type信息来判断当前资源是网页还是图片
        String contentType = page.contentType();
        //根据Content-Type判断是否为图片
        if (contentType != null && contentType.startsWith("image")) {
            //从Content-Type中获取图片扩展名
            String extensionName = contentType.split("/")[1];
            try {
                byte[] image = page.content();
                //根据图片MD5生成文件名
                String fileName = String.format("%s.%s", i++, extensionName);
                File imageFile = new File(baseDir, fileName);
                FileUtils.write(imageFile, image);
                System.out.println("保存图片 " + page.url() + " 到 " + imageFile.getAbsolutePath());
            } catch (Exception e) {
                ExceptionUtils.fail(e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        DemoImageCrawler demoImageCrawler = new DemoImageCrawler("crawl");
        demoImageCrawler.setRequester(new OkHttpRequester());
        //设置为断点爬取，否则每次开启爬虫都会重新爬取
        demoImageCrawler.setResumable(true);
        demoImageCrawler.start(3);
    }
}
