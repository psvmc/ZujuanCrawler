package com.controller.ht;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.utils.ZJ_DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.core.Controller;
import com.utils.ZJ_FileUtils;
import com.utils.ZJ_StringUtils;
import com.vo.ZQuestion;

public class ZQuestionController extends Controller {
    String baseUrl = "";


    String br = "\n";
    String imageShowUrl = "http://wordupload.xhkjedu.com/paper/";
    String uploadFolderName = "uploadimgs";

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

    private List<ZQuestion> getQuestionList(int page) {
        System.out.println("");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------");

        List<ZQuestion> arts = new ArrayList<ZQuestion>();
        Document doc;

        try {
            String weburl = baseUrl + "&page=" + page;
            System.out.println("请求的列表页的URL为：" + weburl);
            System.out.println("");
            doc = Jsoup.connect(weburl).get();
            //保存所有的图片
            Elements imgList = doc.getElementsByTag("img");
            String fileBasePath = ZJ_FileUtils.getBaseFilePath(getRequest());
            for (Element imageEle : imgList) {
                String imageUrl = imageEle.attr("src");
                String fileName = ZJ_StringUtils.getFileName(imageUrl);
                String dateStr = ZJ_DateUtils.getNowDate().replace("-", "");
                String filePath = fileBasePath + uploadFolderName + File.separator + dateStr + File.separator + fileName;
                downloadFile(imageUrl, filePath);
                String src = imageShowUrl + uploadFolderName + "/" + dateStr + "/" + fileName;
                imageEle.attr("src", src);
                imageEle.removeAttr("data-md5");
                System.out.println(" └图片的URL为:" + imageUrl);
                System.out.println(" └图片保存路径为:" + filePath);
            }
            Elements quesList = doc.getElementsByTag("li");
            for (int i = 0; i < quesList.size(); i++) {
                ZQuestion question = new ZQuestion();
                StringBuilder questionTitle = new StringBuilder();
                Element ques = quesList.get(i);
                Element dl = ques.getElementsByTag("dt").get(0);
                Elements pList = dl.getElementsByTag("p");
                if (dl.html().split("[ABCD][.]").length == 5) {
                    for (int j = 0; j < pList.size(); j++) {
                        String titItem = pList.get(j).html();
                        if (titItem.contains("A.") || titItem.contains("B.") || titItem.contains("C.") || titItem.contains("D.")) {
                            question.setQuestionType("单选题");
                            String[] ops = titItem.split("[ABCD][.]");

                            for (int k = 0; k < ops.length; k++) {
                                if (k != 0) {
                                    question.getOptions().add(ops[k]);
                                }
                            }
                        } else {
                            questionTitle.append(titItem).append(br);
                        }
                    }
                } else {
                    for (int j = 0; j < pList.size(); j++) {
                        String titItem = pList.get(j).html();
                        questionTitle.append(titItem).append(br);
                    }
                }

                for (int j = 0; j < question.getOptions().size(); j++) {
                    char[] opStr = "ABCDEFGHIJKLMNOPQRST".toCharArray();
                    String opsStr = question.getOptions().get(j);
                    question.getOptions().set(j, opStr[j] + ". " + opsStr);
                }
                question.setTitle(questionTitle.toString());

                Element div = ques.getElementsByTag("div").get(0);

                Elements p2List = div.getElementsByTag("p");
                StringBuilder questionAnalysis = new StringBuilder();
                for (int j = 0; j < p2List.size(); j++) {
                    if (j == 0) {
                        question.setAns(p2List.get(j).html());
                    } else {
                        questionAnalysis.append(p2List.get(j).html()).append(br);
                    }
                }
                question.setAnalysis(questionAnalysis.toString());
                arts.add(question);
            }

        } catch (IOException e) {
            System.out.println("网址无法访问");
        }
        return arts;
    }

    private List<ZQuestion> getQuestionList(int beginPage, int endPage) {
        List<ZQuestion> arts = new ArrayList<ZQuestion>();
        for (int i = beginPage; i <= endPage; i += 1) {
            List<ZQuestion> tempArts = getQuestionList(i);
            arts.addAll(tempArts);
        }
        return arts;
    }

    private String getQuestionStr(List<ZQuestion> artList) {
        List<String> strList = new ArrayList<String>();
        for (int i = 0; i < artList.size(); i++) {
            ZQuestion quesVo = artList.get(i);
            strList.add("【题型】" + quesVo.getQuestionType());
            strList.add("【题干】" + quesVo.getTitle());
            if (quesVo.getOptions().size() > 0) {
                for (int j = 0; j < quesVo.getOptions().size(); j++) {
                    if (j == 0) {
                        strList.add("【选项】" + quesVo.getOptions().get(j));
                    } else {
                        strList.add(quesVo.getOptions().get(j));
                    }
                }

            }

            strList.add("【答案】" + quesVo.getAns());
            strList.add("【解析】" + quesVo.getAnalysis());
            strList.add(br);
        }
        return ZJ_StringUtils.listToStr(strList, br);

    }


    public void list() {
        int beginPage = getParaToInt("beginPage");
        int endPage = getParaToInt("endPage");
        baseUrl = getPara("url");

        List<ZQuestion> arts = getQuestionList(beginPage, endPage);

        String sqls = getQuestionStr(arts);
        sqls = "本次抓取(" + beginPage + "-" + endPage + ")页 \n" + "共抓取" + arts.size() + "道题 \n\n" + sqls;
        renderText(sqls);

    }

}
