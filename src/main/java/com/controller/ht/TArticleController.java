package com.controller.ht;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.core.Controller;
import com.utils.ZJ_FileUtils;
import com.utils.ZJ_StringUtils;
import com.vo.ZQuestion;

public class TArticleController extends Controller {
    String baseUrl = "http://4s.quyixian.com/SQB/?s=1&action=list&id=1002&TypeYear=0&TypeCity=0&TypeSource=0&TypeSort=addtime&TypeSortVal=desc&method=&_=1535609706296";
    String fileBasePath;

    //下载文件
    public void downloadFile(String remoteFilePath, String localFilePath) {
        URL urlfile = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File f = new File(localFilePath);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            f.delete();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            urlfile = new URL(remoteFilePath);
            httpUrl = (HttpURLConnection) urlfile.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(f));
            int len = 2048;
            byte[] b = new byte[len];
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            bos.flush();
            bis.close();
            httpUrl.disconnect();
        } catch (Exception e) {
            System.out.println("图片不存在：" + remoteFilePath);
        } finally {
            try {

                if (null != bis) {
                    bis.close();
                }
                if (null != bos) {
                    bos.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<ZQuestion> getArticleList(int offset) {
        System.out.println("");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------");

        List<ZQuestion> arts = new ArrayList<ZQuestion>();
        Document doc;

        try {
            String weburl = baseUrl + "&page=" + offset;
            System.out.println("请求的列表页的URL为：" + weburl);
            System.out.println("");

            doc = Jsoup.connect(weburl).get();
            String baseUrl = doc.baseUri();
            Elements listOuterEle = doc.getElementsByAttributeValue("class", "xin2zuo");
            Elements trEles = listOuterEle.get(0).getElementsByTag("tr");
            for (int i = trEles.size() - 1; i >= 0; i--) {
                Element ele = trEles.get(i);
                Element trEle = ele.getAllElements().get(0);
                Elements tdEles = trEle.getElementsByTag("td");
                if (tdEles.size() == 3) {
                    ZQuestion articleVo = new ZQuestion();
                    Element titleA = trEle.getElementsByTag("td").get(1);
                    String title = titleA.text();
                    String url = titleA.getElementsByTag("a").attr("href");
                    url = baseUrl + url;
                    String addDate = trEle.getElementsByTag("td").get(2).html();
                    addDate = addDate.replace("[", "").replace("]", "");
                    articleVo.setTitle(title);

                }
            }

        } catch (IOException e) {
            System.out.println("网址无法访问");
        }
        return arts;
    }

    private List<ZQuestion> getArticleList(int beginOffset, int endOffset) {
        List<ZQuestion> arts = new ArrayList<ZQuestion>();
        for (int i = beginOffset; i >= endOffset; i -= 20) {
            List<ZQuestion> tempArts = getArticleList(i);
            arts.addAll(tempArts);
        }
        return arts;
    }

    private String getSql(List<ZQuestion> artList) {
        List<String> sqlList = new ArrayList<String>();
        for (ZQuestion articleVo : artList) {

            String sql = "";


            sqlList.add(sql);
        }
        return ZJ_StringUtils.listToStr(sqlList, ";\n");

    }


    public void listRange2() {
        int beginPage = getParaToInt("beginPage");
        int endPage = getParaToInt("endPage");
        int beginOffset = (beginPage - 1) * 20;
        int endOffset = (endPage - 1) * 20;
        if (endOffset > beginOffset) {
            int tempOffset = beginOffset;
            beginOffset = endOffset;
            endOffset = tempOffset;
        }

        fileBasePath = ZJ_FileUtils.getBaseFilePath(getRequest());
        List<ZQuestion> arts = getArticleList(beginOffset, endOffset);
        String sqls = getSql(arts);
        sqls += ";\ncommit;";
        sqls = "Set define off;\n" + sqls;
        renderText(sqls);

    }

}
