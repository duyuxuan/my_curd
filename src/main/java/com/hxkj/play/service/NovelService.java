package com.hxkj.play.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hxkj.common.util.ToolRandom;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 追书神器 api
 * TODO 提高代码质量
 */
public class NovelService {

    private final static Logger LOG = Logger.getLogger(NovelService.class);

    // 请求编码
    private final static String charset = "UTF-8";
    // UA
    private final static String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36", "" +
            "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
            "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36",
            "Mozilla/5.0 (iPad; CPU OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
    };

    private NovelService() {
    }


    /**
     * Get 请求代理
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getProxy(String url) throws IOException {
        LOG.debug(url);
        Content content = Request.Get(url)
                .setHeader("User-Agent", userAgents[ToolRandom.number(0, userAgents.length)])
                .execute().returnContent();
        String resStr = content.asString(Charset.forName(charset));
        LOG.debug(resStr);
        return resStr;
    }


    /**
     * 关键字模糊查询小说
     *
     * @param keyword
     * @param start
     * @param limit
     * @return
     */
    public static Map<String, Object> fuzzySearch(String keyword, Integer start, Integer limit) {
        String url = "http://api.zhuishushenqi.com/book/fuzzy-search?query="
                + keyword + "&start=" + start + "&limit=" + limit;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String jsonStr = getProxy(url);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (!jsonObject.getBoolean("ok")) {
                map.put("code", -1);
                map.put("message", jsonObject.getString("msg"));
            } else {
                map.put("code", 1);
                Integer total = jsonObject.getInteger("total");
                // 允许翻1000 条
                if (total > 1000) {
                    map.put("total", 1000);
                } else {
                    map.put("total", jsonObject.get("total"));
                }
                map.put("rows", jsonObject.get("books"));
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            map.put("code", -1);
            map.put("message", e.getMessage());
        }
        return map;

    }


    /**
     * 小说分类
     *
     * @param category
     * @param start
     * @param limit
     * @return
     */
    public static Map<String, Object> category(String category, Integer start, Integer limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String url = "http://api.zhuishushenqi.com/book/by-categories?" +
                    category +
                    "&type=hot" +
                    "&start=" + start + "&limit=" + limit;
            String jsonStr = getProxy(url);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (!jsonObject.getBoolean("ok")) {
                map.put("code", -1);
                map.put("message", jsonObject.getString("msg"));
            } else {
                map.put("code", 1);
                Integer total = jsonObject.getInteger("total");
                // 允许翻1000 条
                if (total > 1000) {
                    map.put("total", 1000);
                } else {
                    map.put("total", jsonObject.get("total"));
                }
                map.put("rows", jsonObject.get("books"));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            map.put("code", -1);
            map.put("message", e.getMessage());
        }
        return map;

    }

    /**
     * 小说详情
     *
     * @param nid 小说id
     * @return
     */
    public static Map<String, Object> novel(String nid) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String url = "http://api.zhuishushenqi.com/book/" + nid;
            String jsonStr = getProxy(url);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (!jsonObject.getBoolean("ok")) {
                map.put("code", -1);
                map.put("message", jsonObject.getString("msg"));
            } else {
                map.put("code", 1);
                map.put("bookDetail", jsonObject);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            map.put("code", -1);
            map.put("message", e.getMessage());
        }
        return map;
    }

    /**
     * 章节列表
     *
     * @param nid
     * @return
     */
    public static Map<String, Object> chapters(String nid) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String url = "http://api.zhuishushenqi.com/mix-atoc/" + nid + "?view=chapters";
            String jsonStr = getProxy(url);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (!jsonObject.getBoolean("ok")) {
                map.put("code", -1);
                map.put("message", jsonObject.getString("msg"));
            } else {
                map.put("code", 1);
                JSONObject jsonObjectTemp = (JSONObject) jsonObject.get("mixToc");
                map.put("rows", jsonObjectTemp.get("chapters"));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            map.put("code", -1);
            map.put("message", e.getMessage());
        }
        return map;
    }

    /**
     * 章节详情
     *
     * @param url
     * @return
     */
    public static Map<String, Object> chapter(String url) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            url = URLEncoder.encode(url, "utf-8");
            String dUrl = "http://chapter2.zhuishushenqi.com/chapter/" + url;
            String jsonStr = getProxy(dUrl);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (!jsonObject.getBoolean("ok")) {
                map.put("code", -1);
                map.put("message", jsonObject.getString("message"));
            } else {
                map.put("code", 1);
                map.put("chapterDetail", jsonObject.get("chapter"));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            map.put("code", -1);
            map.put("message", e.getMessage());
        }
        return map;
    }

    /**
     * 小说文本放入 map
     *
     * @param nid
     * @return
     */
    public static Map<String, Object> saveInMap(String nid) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder();
        Map<String, Object> stepOneMap = chapters(nid);
        Integer stepOneCode = (Integer) stepOneMap.get("code");
        if (stepOneCode == -1) {
            return stepOneMap;
        }
        JSONArray jsonArray = (JSONArray) stepOneMap.get("rows");

        Iterator it = jsonArray.iterator();
        int count = 1;
        while (it.hasNext()) {
            JSONObject chapterObj = (JSONObject) it.next();
            if (!chapterObj.getBoolean("unreadble")) {
                Map<String, Object> stepTwoMap = chapter(chapterObj.getString("link"));
                Integer stepTwoCode = (Integer) stepTwoMap.get("code");
                if (stepTwoCode == 1) {
                    JSONObject cObj = (JSONObject) stepTwoMap.get("chapterDetail");
                    sb.append(chapterObj.getString("title") + "(" + count + ") \n" + cObj.getString("body") + " \n");
                    System.out.println("count: " + count + chapterObj.getString("title"));
                    count++;
                }
            }
        }
        map.put("code", 1);
        map.put("content", sb);
        return map;
    }


    /**
     * 小说文本存入 文件中
     *
     * @param nid
     * @param txtFile
     * @return
     */
    public static Map<String, Object> saveToTxt(String nid, File txtFile) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> stepOneMap = chapters(nid);
        Integer stepOneCode = (Integer) stepOneMap.get("code");
        if (stepOneCode == -1) {
            return stepOneMap;
        }
        JSONArray jsonArray = (JSONArray) stepOneMap.get("rows");

        Iterator it = jsonArray.iterator();
        int count = 1;
        while (it.hasNext()) {
            JSONObject chapterObj = (JSONObject) it.next();
            if (!chapterObj.getBoolean("unreadble")) {
                Map<String, Object> stepTwoMap = chapter(chapterObj.getString("link"));
                Integer stepTwoCode = (Integer) stepTwoMap.get("code");
                if (stepTwoCode == 1) {
                    try {
                        JSONObject cObj = (JSONObject) stepTwoMap.get("chapterDetail");
                        FileUtils.write(txtFile, chapterObj.getString("title") + "(" + count + ") \n" + cObj.getString("body") + " \n", "UTF-8", true);
                        System.out.println("count: " + count + chapterObj.getString("title"));
                        count++;
                    } catch (IOException e) {
                        e.printStackTrace();
                        LOG.error(e.getMessage());
                    }
                }
            }
        }
        map.put("code", 1);
        map.put("path", txtFile.getAbsolutePath());
        return map;
    }


    /**
     * web 环境下应该使用 全局 的线程池, 使用Callable 而不是 Runnable
     *
     * @param nid
     * @return
     */
    public static Map<String, Object> saveInMapQuick(String nid) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder();
        Map<String, Object> stepOneMap = chapters(nid);
        Integer stepOneCode = (Integer) stepOneMap.get("code");
        if (stepOneCode == -1) {
            return stepOneMap;
        }
        JSONArray jsonArray = (JSONArray) stepOneMap.get("rows");
        Iterator it = jsonArray.iterator();
        // 创建任务，执行或者放入到线程池任务队列中
        while (it.hasNext()) {
            JSONObject jsonObject = (JSONObject) it.next();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (!jsonObject.getBoolean("unreadble")) {
                        Map<String, Object> stepTwoMap = chapter(jsonObject.getString("link"));
                        Integer stepTwoCode = (Integer) stepTwoMap.get("code");
                        if (stepTwoCode == 1) {
                            JSONObject cObj = (JSONObject) stepTwoMap.get("chapterDetail");
                            jsonObject.put("body", cObj.getString("body"));
                        }
                    }
                }
            });
        }
        // 线程池状态变更为SHUTDOWN，不再接受新任务。当任务队列不再有任务（所有任务执行完毕）,任务状态变更为 TERMINATED
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                Iterator it2 = jsonArray.iterator();
                int count = 1;
                while (it2.hasNext()) {
                    JSONObject jsonObject = (JSONObject) it2.next();
                    sb.append(jsonObject.getString("title") + "(" + count + ") \n" + jsonObject.getString("body") + " \n");
                    //System.out.println("count: " + count + jsonObject.getString("title"));
                    count++;
                }
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        map.put("code", 1);
        map.put("content", sb);
        return map;
    }


    public static Map<String, Object> saveToTxtQuick(String nid, File txtFile) {
        Map<String, Object> map = new HashMap<String, Object>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Map<String, Object> stepOneMap = chapters(nid);
        Integer stepOneCode = (Integer) stepOneMap.get("code");
        if (stepOneCode == -1) {
            return stepOneMap;
        }
        JSONArray jsonArray = (JSONArray) stepOneMap.get("rows");
        Iterator it = jsonArray.iterator();
        while (it.hasNext()) {
            JSONObject jsonObject = (JSONObject) it.next();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (!jsonObject.getBoolean("unreadble")) {
                        Map<String, Object> stepTwoMap = chapter(jsonObject.getString("link"));
                        Integer stepTwoCode = (Integer) stepTwoMap.get("code");
                        if (stepTwoCode == 1) {
                            JSONObject cObj = (JSONObject) stepTwoMap.get("chapterDetail");
                            jsonObject.put("body", cObj.getString("body"));
                        }
                    }
                }
            });
        }
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                Iterator it2 = jsonArray.iterator();
                int count = 0;
                while (it2.hasNext()) {
                    JSONObject jsonObject = (JSONObject) it2.next();
                    try {
                        FileUtils.write(txtFile, jsonObject.getString("title") + "(" + (count + 1) + ") \n" + jsonObject.getString("body") + " \n", "UTF-8", true);
                        count++;
                    } catch (IOException e) {
                        e.printStackTrace();
                        LOG.error(e.getMessage());
                    }
                }
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        map.put("code", 1);
        map.put("path", txtFile.getAbsolutePath());
        return map;
    }


    public static void main(String[] args) {
        // 发送异常邮件测试
        int x;
        for (int i = 0; i < 3; i++) {
            try {
                x = i / i;
                System.out.println(x);
            } catch (Exception e) {
                LOG.error("index: " + i, e);
            }
            LOG.info("good morning: " + i);
        }
    }

}
