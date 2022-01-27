package com.zg.task;

import com.sinosig.saab.util.DateUtil;
import com.zg.database.util.SvnUtil;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CreateSVNDirTask {

    private String svnRootURL = "http://it_doc.sinosig.com/SPIS/非车新一代/01项目范围管理/UAT脚本发布提交";
    private String localurl = "D:\\test\\UAT脚本发布提交";
    private SVNClientManager ourClientManager;
    private String name = "zengguang-phq";
    private String password = "";

    private boolean createDir(SVNURL repositoryURL) throws SVNException {
        SVNURL sunURL = repositoryURL;
        if (SvnUtil.isURLExist(sunURL, name, password)) {

        } else {
            SvnUtil.makeDirectory(ourClientManager, sunURL, "创建文件夹" + DateUtil.format(new Date(), "yyyy-MM-dd hh:mm:ss"));
        }
        return true;
    }

    private boolean createSVNDir() {

//初始化支持svn:
//协议的库。 必须先执行此操作。
        SVNRepositoryFactoryImpl.setup();
//相关变量赋值
        SVNURL repositoryURL = null;
        try {
            repositoryURL = SVNURL.parseURIEncoded(svnRootURL);

        } catch (Exception e) {

        }

        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);

//实例化客户端管理类

        ourClientManager = SVNClientManager.newInstance(

                (DefaultSVNOptions) options, name, password);

//要把版本库的内容check out到的目录

        File wcDir = new File(localurl);

        long workingVersion = -1;

        try {
            Date currentDate=new Date();

            // workingVersion = SvnUtil.checkout(ourClientManager, repositoryURL, SVNRevision.HEAD, wcDir, SVNDepth.INFINITY);
            SVNURL sunURL = repositoryURL.appendPath(DateUtil.format(currentDate,"yyyyMM"), true);
            if (createDir(sunURL)) {
                sunURL = sunURL.appendPath(DateUtil.format(currentDate,"yyyyMMdd"), true);
                if (createDir(sunURL)){
                   SVNURL newSvnUrl = sunURL.appendPath("提交",true);
                    if (createDir(newSvnUrl)){
                        System.out.println(newSvnUrl.getPath()+"创建完成！");
                    }
                     newSvnUrl = sunURL.appendPath("补提",true);
                    if (createDir(newSvnUrl)){
                        System.out.println(newSvnUrl.getPath()+"创建完成！");
                    }

                    newSvnUrl = sunURL.appendPath("执行结果",true);
                    if (createDir(newSvnUrl)){
                        System.out.println(newSvnUrl.getPath()+"创建完成！");
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return true;
    }

    public boolean domain() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1); // 控制时
        calendar.set(Calendar.MINUTE, 30);       // 控制分
        calendar.set(Calendar.SECOND, 0);       // 控制秒

        Date time = calendar.getTime();         // 得出执行任务的时间,此处为今天的17：30：00

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println("-------脚本执行定时任务启动--------");
                createSVNDir();
                System.out.println("-------脚本执行定时任务完成--------");
            }
        }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行

        return true;
    }


    public static void main(String args[]){
        System.out.println("启动定时svn目录自动创建程序");
        CreateSVNDirTask createSVNDirTask=new CreateSVNDirTask();
        createSVNDirTask.domain();
    }

}
