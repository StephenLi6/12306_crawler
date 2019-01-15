package com.yanmingchen.test;

import com.yanmingchen.pojo.LeftNew;
import com.yanmingchen.utils.CheckLeftNew;
import com.yanmingchen.utils.Login;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class CrawlerTest {

    @Autowired
    private Login login;

    @Autowired
    private CheckLeftNew checkLeftNew;

    @Test
    public void test() throws Exception {
        // 获取验证码坐标
        String coordinate = login.getCoordinate();

        // 校验验证码
        boolean pass = login.doCheckCode(coordinate);
        // 如果验证码验证成功
        if (pass) {
            // 登录操作
            boolean loginSuccess = login.doLogin(coordinate);
            // 如果登录成功
            if (loginSuccess) {
                // 查票
                LeftNew leftNew = new LeftNew();
                leftNew.setStart_train_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                leftNew.setFrom_station_name("深圳北");
                leftNew.setTo_station_name("虎门");

                List<LeftNew> leftNews = checkLeftNew.checkTicket(leftNew);
                for (LeftNew aNew : leftNews) {
                    System.out.println(aNew);
                }
            }
        }

//		// 查票
//		LeftNew leftNew = new LeftNew();
//		leftNew.setStart_train_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//		leftNew.setFrom_station_name("深圳北");
//		leftNew.setTo_station_name("虎门");
//		List<LeftNew> leftNews = checkLeftNew.checkTicket(leftNew);
//		for (LeftNew aNew : leftNews) {
//			System.out.println(aNew);
//		}
    }

}
