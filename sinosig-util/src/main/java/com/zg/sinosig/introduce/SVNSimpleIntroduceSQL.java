package com.zg.sinosig.introduce;

import com.sinosig.saab.util.DateUtil;
import com.sinosig.saab.util.FileUtils;
import com.zg.database.util.SvnUtil;
import com.zg.handler.CommitInterfaceHandler;
import com.zg.handler.ProxyUtils;
import com.zg.util.io.POIUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

public class SVNSimpleIntroduceSQL implements IntroduceSQL {


    private String executeBatchNo;
    private String svnRootURL = "http://it_doc.sinosig.com/SPIS/非车新一代/01项目范围管理/UAT脚本发布提交";
    private String localurl = "D:\\test\\UAT脚本发布提交";
    private SVNClientManager ourClientManager;
    private String name = "zengguang-phq";
    private String password = "tx3Zak7!";
    private String executeRoot = "D:\\test\\SQLExecuteTask\\";


    // private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyClass(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog");

    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog"));

    public SVNSimpleIntroduceSQL(String executeRoot, String executeBatchNo) {
        this.executeRoot = executeRoot;
        this.executeBatchNo = executeBatchNo;

    }


    private boolean initSVNLoad() {

        SVNRepositoryFactoryImpl.setup();
        SVNURL repositoryURL = null;
        try {
            Date currentDate = new Date();
            String relativePath = DateUtil.format(currentDate, "yyyyMM") + "/" + DateUtil.format(currentDate, "yyyyMMdd");
            ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
            ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, name, password);
            repositoryURL = SVNURL.parseURIEncoded(svnRootURL);
            SVNURL sunURL = repositoryURL.appendPath(relativePath + "/" + "提交", true);
            if (SvnUtil.isURLExist(sunURL, name, password)) {
                SvnUtil.update(ourClientManager, new File(localurl), SVNRevision.HEAD, SVNDepth.INFINITY);
                SvnUtil.update(ourClientManager, new File(localurl, relativePath + "/" + "提交"), SVNRevision.HEAD, SVNDepth.INFINITY);
            } else {
                System.out.println("目录未创建！");
                return false;
            }
            //copy  需要执行的excel到工作文件夹
            FileUtils.deleteQuietly(new File(executeRoot, "in"));  //清空工作目录
            FileUtils.copyDirectory(new File(localurl, relativePath + "/" + "提交"), new File(executeRoot, "in"));


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }



    private boolean checkSinoSigSQLLogEntity(SinoSigSQLLogEntity baseEntity) {

        if ("new-non-auto".endsWith(baseEntity.systemflag)) {
            if (!"保单库".equals(baseEntity.databasename) && !"批单修改库".equals(baseEntity.databasename) && !"投保单库".equals(baseEntity.databasename)) {
                baseEntity.setErrormassage("数据库名无效!");
                baseEntity.executestate = "-1";
            }

            if (!"DDL".equals(baseEntity.sqltype) && !"DML".equals(baseEntity.sqltype)) {
                baseEntity.setErrormassage("脚本类型填入错误!");
                baseEntity.executestate = "-1";
            }
            if (!"建表".equals(baseEntity.sqlpurpose) && !"拉长字段".equals(baseEntity.sqlpurpose) && !"增加字段".equals(baseEntity.sqlpurpose) && !"数据操作".equals(baseEntity.sqlpurpose)) {
                baseEntity.setErrormassage("脚本用途填入错误!");
                baseEntity.executestate = "-1";
            }
            if ("DML".equals(baseEntity.sqltype) && !"数据操作".equals(baseEntity.sqlpurpose)) {
                baseEntity.setErrormassage("脚本用途填入错误!");
                baseEntity.executestate = "-1";
            }
            if ("DDL".equals(baseEntity.sqltype) && !"建表拉长字段增加字段".contains(baseEntity.sqlpurpose)) {
                baseEntity.setErrormassage("脚本用途填入错误!");
                baseEntity.executestate = "-1";
            }
            if ("建表".equals(baseEntity.sqlpurpose) && !baseEntity.basesql.contains("create")) {
                baseEntity.setErrormassage("脚本用途填入错误!");
                baseEntity.executestate = "-1";
            }
            if ("建表".equals(baseEntity.sqlpurpose) && baseEntity.basesql.contains("create")) {
                String[] array = baseEntity.needPowerTableArray;
                if (array == null || array.length == 0) {
                    baseEntity.setErrormassage("建表请赋权！");
                    baseEntity.executestate = "-1";
                } else {

                    for (String tableName : array) {
                        if (!baseEntity.basesql.contains(tableName)) {
                            baseEntity.setErrormassage("建表缺少赋权!");
                            baseEntity.executestate = "-1";
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<SinoSigSQLLogEntity> generateSQLFromExcel(File file) throws IOException, SQLException, IllegalAccessException {
        List<SinoSigSQLLogEntity> logEntities = new ArrayList<>();

        if (file.exists()) {
            if (file.isDirectory()) {

                File[] files = file.listFiles();
                for (File f : files) {
                    logEntities.addAll(generateSQLFromExcel(f));
                }
            } else if (file.getName().endsWith(".xls")) {

                InputStream inputStream = new FileInputStream(file);
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
                String[] sheetNames = {"新一代脚本", "平台配置脚本", "老核心脚本"};
                for (String sheetName : sheetNames) {

                    List<Map> mapList = POIUtils.readExcel(hssfWorkbook, sheetName);
                    Random random = new Random();
                    String batchNo = "JB" + new Date().getTime() + random.nextInt();
                    String systemFlag = "";
                    switch (sheetName) {
                        case "新一代脚本": {
                            systemFlag = "new-non-auto";
                            break;
                        }
                        case "平台配置脚本": {
                            systemFlag = "platform";
                            break;
                        }
                        case "老核心脚本": {
                            systemFlag = "old-non-auto";
                            break;
                        }
                    }
                    for (int i = 1; i < mapList.size(); i++) {
                        Map<String, String> map = mapList.get(i);
                        if (map.get("序号") != null && !"".equals(map.get("序号").trim())) {

                            SinoSigSQLLogEntity baseEntity = new SinoSigSQLLogEntity();
                            baseEntity.batchno = batchNo;
                            baseEntity.serialno = map.get("序号").trim();
                            baseEntity.executestate = "0";
                            baseEntity.executebatchno = executeBatchNo;
                            baseEntity.systemflag = systemFlag;
                            if (map.get("数据库") != null) {
                                baseEntity.databasename = map.get("数据库").trim();
                            }
                            if (map.get("提交人") != null) {
                                baseEntity.applyusername = map.get("提交人").trim();
                            }
                            if (map.get("发布计划名称") != null) {
                                baseEntity.planname = map.get("发布计划名称").trim();
                            }

                            if (map.get("需求ID") != null) {
                                baseEntity.demandid = map.get("需求ID").trim();
                            }

                            if (map.get("需求名称") != null) {
                                baseEntity.demandname = map.get("需求名称").trim();
                            }

                            if (map.get("脚本类型") != null) {
                                baseEntity.sqltype = map.get("脚本类型").trim();
                            }

                            if (map.get("需赋权表名") != null) {
                                baseEntity.needpowertables = map.get("需赋权表名").trim();
                                if (baseEntity.needpowertables != null && !"".equals(baseEntity.needpowertables)) {
                                    baseEntity.needPowerTableArray = baseEntity.needpowertables.split(",");
                                }
                            }
                            if (map.get("脚本") != null) {
                                baseEntity.basesql = map.get("脚本").trim();

                            }
                            if (map.get("备注") != null) {
                                baseEntity.sqldescribe = map.get("备注").trim();
                            }
                            if (map.get("脚本用途") != null) {
                                baseEntity.sqlpurpose = map.get("脚本用途").trim();
                            }

                            if (!checkSinoSigSQLLogEntity(baseEntity)) {
                                continue;
                            }

                            logEntities.add(baseEntity);
                            sinoSigSQLLogService.insertSinoSingSQLLog(baseEntity);
                        }

                    }
                }
            }

        }
        return logEntities;
    }

    @Override
    public List initLoadSinoSigSQL() {
        List<SinoSigSQLLogEntity> list = new ArrayList();
        try {
            initSVNLoad();
            File dirFile = new File(executeRoot + "in\\");
            list = generateSQLFromExcel(dirFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

}
