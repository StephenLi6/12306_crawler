package com.yanmingchen.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class Login {

    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    // 创建httpClient
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    /**
     * 1 -- x轴：40 ,  y轴：40
     * 2 -- x轴：120 , y轴：40
     * 3 -- x轴：190 , y轴：40
     * 4 -- x轴：270 , y轴：40
     * <p>
     * 5 -- x轴：40 ,  y轴：120
     * 6 -- x轴：120 , y轴：120
     * 7 -- x轴：190 , y轴：120
     * 8 -- x轴：270 , y轴：120
     */
    private static Map<Integer, String> map = new HashMap<Integer, String>();

    /**
     * 添加8张验证码对应的坐标
     */
    static {
        // 把8张验证码图片存入map中
        map.put(1, "40,40");
        map.put(2, "120,40");
        map.put(3, "190,40");
        map.put(4, "270,40");
        map.put(5, "40,120");
        map.put(6, "120,120");
        map.put(7, "190,120");
        map.put(8, "270,120");
    }

    @Value("${user.username}")
    private String username;
    @Value("${user.password}")
    private String password;
    @Value("${user.appid}")
    private String appid;
    @Value("${login.loginUrl}")
    private String loginUrl;
    @Value("${login.checkCodeAnswerUrl}")
    private String checkCodeAnswerUrl;
    @Value("${login.checkCodeImgUrl}")
    private String checkCodeImgUrl;
    @Value("${login.validateCheckCodeUrl}")
    private String validateCheckCodeUrl;

    /**
     * 获取验证码坐标
     *
     * @return
     * @throws Exception
     */
    public String getCoordinate() throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(checkCodeAnswerUrl);

        // 设置参数
        // 获取验证码图片字节数组
        byte[] bytes = getCheckCodeImg();
        File file = new File("checkCode.jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(bytes);
        bos.flush();
        bos.close();
        HttpEntity httpEntity = MultipartEntityBuilder.create().addBinaryBody("file", file).build();
        httpPost.setEntity(httpEntity);

        // 发送请求获取返回的页面
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String html = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));

        // 解析html并且返回结果
        List<Integer> numbers = parseHtml(html);

        // 拼接坐标
        String coordinate = "";
        for (int i = 0; i < numbers.size(); i++) {
            if (i == numbers.size() - 1) {
                coordinate += map.get(numbers.get(i));
                break;
            }
            coordinate += map.get(numbers.get(i)) + ",";
        }
        LOGGER.info("验证码坐标为：" + coordinate);

        return coordinate;
    }

    /**
     * 获取验证码图片
     *
     * @return
     */
    private byte[] getCheckCodeImg() throws Exception {
        // 发送请求，获取验证码图片
        HttpGet loginHtmlHttpGet = new HttpGet(checkCodeImgUrl);
        CloseableHttpResponse response = httpClient.execute(loginHtmlHttpGet);
        // 获取验证码json
        String checkCodeJson = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
        // 解析json
        Map<String, String> checkCodeMap = (Map<String, String>) JSONObject.parse(checkCodeJson);
        // 获取图片路径（base64格式）
        String imageUrl = checkCodeMap.get("image");

        // 解析图片为字节数组
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(imageUrl);

        return bytes;
    }

    /**
     * 解析获取验证码分析结果
     *
     * @param html
     * @return
     */
    private List<Integer> parseHtml(String html) {
        List<Integer> numbers = new ArrayList<Integer>();

        Document document = Jsoup.parse(html);
        Elements bs = document.select("font[color='red'] b");
        for (Element b : bs) {
            String numberStrs = b.text();
            String[] numberArr = numberStrs.split(" ");
            for (String numberStr : numberArr) {
                numbers.add(Integer.valueOf(numberStr));
            }
        }
        return numbers;
    }

    /**
     * 验证码校验
     *
     * @param coordinate
     * @return
     * @throws Exception
     */
    public boolean doCheckCode(String coordinate) {
        try {
            // 设置参数
//        callback: jQuery19105185792198028378_1542268509092
//        answer: 263,115
//        rand: sjrand
//        login_site: E
//        _: 1542268509094
            // 校验验证码
            HttpGet checkCodeHttpGet = new HttpGet(validateCheckCodeUrl + coordinate);
            CloseableHttpResponse checkCodeResponse = httpClient.execute(checkCodeHttpGet);
            String checkCodeJson = EntityUtils.toString(checkCodeResponse.getEntity(), Charset.forName("UTF-8"));
            Map<String, String> checkCodeMap = (Map<String, String>) JSONObject.parse(checkCodeJson);
            String result_message = checkCodeMap.get("result_message");
            if ("验证码校验成功".equals(result_message)) {
                LOGGER.info("12306验证码校验成功！");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("12306验证码校验失败！");
        }

        return false;
    }

    /**
     * 登录
     *
     * @param coordinate
     */
    public boolean doLogin(String coordinate) throws Exception {
        try {
            // 登录
            HttpPost loginHttpPost = new HttpPost(loginUrl);

            //设置参数
//        username: 1
//        password: 123456
//        appid: otn
//        answer: 263,115
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("username", username));
            parameters.add(new BasicNameValuePair("password", password));
            parameters.add(new BasicNameValuePair("appid", appid));
            parameters.add(new BasicNameValuePair("answer", coordinate));
            loginHttpPost.setEntity(new UrlEncodedFormEntity(parameters));

            // 执行登录
            CloseableHttpResponse loginResponse = httpClient.execute(loginHttpPost);
            // 获取登录返回的json
            String loginJson = EntityUtils.toString(loginResponse.getEntity(), Charset.forName("UTF-8"));
            // 解析json
            Map<String, String> loginMap = (Map<String, String>) JSONObject.parse(loginJson);
            if ("登录成功".equals(loginMap.get("result_message"))) {
                LOGGER.info("12306登录成功！");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("12306登录失败！");
        }

        return false;
    }

}
