package com.yanmingchen.utils;

import com.alibaba.fastjson.JSONObject;
import com.yanmingchen.pojo.LeftNew;
import com.yanmingchen.pojo.Station;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CheckLeftNew {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckLeftNew.class);

    // 创建代理httpClient
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    /**
     * 城市车站及编号(key:车站编号，value:车站对象)
     */
    private static Map<String, Station> stations = new HashMap<String, Station>();

    /**
     * 车站名称及编号(key:车站名称，value:车站编号)
     */
    private static Map<String, String> stationsNameAndCode = new HashMap<String, String>();

    /**
     * 读取全国所有的站信息，并存入stations中，key为站的编号，value为Station对象
     */
    static {
        try {
            InputStream inputStream = CheckLeftNew.class.getClassLoader().getResourceAsStream("stations.txt");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            while ((str = br.readLine()) != null) {
                String[] stationsArr = str.split("@");
                for (String stationStr : stationsArr) {
                    String[] stationArr = stationStr.split("\\|");
                    Station station = new Station();
                    station.setStationName(stationArr[1]);
                    station.setStationCode(stationArr[2]);
                    station.setPinyin(stationArr[3]);
                    station.setSuoxie(stationArr[4]);
                    station.setNo(stationArr[5]);

                    stations.put(stationArr[2], station);
                    stationsNameAndCode.put(stationArr[1], stationArr[2]);
                }
            }

            br.close();
            isr.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查票
     *
     * @throws Exception
     */
    public List<LeftNew> checkTicket(LeftNew checkLeftNew) throws Exception {
//        HttpGet leftNewHttpGet = new HttpGet("https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=2018-12-01&leftTicketDTO.from_station=WAQ&leftTicketDTO.to_station=IZQ&purpose_codes=ADULT");
        HttpGet leftNewHttpGet = new HttpGet("https://kyfw.12306.cn/otn/leftTicket/query" +
                "?leftTicketDTO.train_date=" + checkLeftNew.getStart_train_date() +
                "&leftTicketDTO.from_station=" + stationsNameAndCode.get(checkLeftNew.getFrom_station_name()) +
                "&leftTicketDTO.to_station=" + stationsNameAndCode.get(checkLeftNew.getTo_station_name()) +
                "&purpose_codes=ADULT");
        CloseableHttpResponse indexResponse = httpClient.execute(leftNewHttpGet);
        String ticketJson = EntityUtils.toString(indexResponse.getEntity(), Charset.forName("UTF-8"));

        Map<String, Object> ticketMap = (Map<String, Object>) JSONObject.parse(ticketJson);
        Map<String, Object> data = (Map<String, Object>) ticketMap.get("data");
        List<String> result = (List<String>) data.get("result");

        List<LeftNew> leftNews = new ArrayList<LeftNew>();
        for (String r : result) {
            String[] dataArr = r.split("\\|");
//            private String train_no;  // 240000G1010C,
//            private String station_train_code;   // G101,
//            private String start_station_telecode;   // VNP,
//            private String start_station_name;   // 北京南,
//            private String end_station_telecode;   // AOH,
//            private String end_station_name;   // 上海虹桥,
//            private String from_station_telecode;   // VNP,
//            private String from_station_name;   // 北京南,
//            private String to_station_telecode;   // AOH,
//            private String to_station_name;   // 上海虹桥,
//            private String start_time;   // 06:44,
//            private String arrive_time;   // 12:38,
//            private String day_difference;   // 0,
//            private String train_class_name;   // ,
//            private String lishi;   // 05:54,
//            private String canWebBuy;   // IS_TIME_NOT_BUY,
//            private String lishiValue;   // 354,
//            private String yp_info;   // O055300032M0933000349174800012,
//            private String control_train_day;   // 20161229,
//            private String start_train_date;   // 20161201,
//            private String seat_feature;   // O3M393,
//            private String yp_ex;   // O0M090,
//            private String train_seat_feature;   // 3,
//            private String seat_types;   // OM9,
//            private String location_code;   // P2,
//            private String from_station_no;   // 01,
//            private String to_station_no;   // 11,
//            private String control_day;   // 59,
//            private String sale_time;   // 1230,
//            private String is_support_card;   // 1,
//            private String controlled_train_flag;   // 0,
//            private String controlled_train_message;   // 正常车次，不受控,
//            private String gg_num;   // --,
//            private String gr_num;   // --,
//            private String qt_num;   // --,
//            private String rw_num;   // --,
//            private String rz_num;   // --,
//            private String tz_num;   // --,
//            private String wz_num;   // --,
//            private String yb_num;   // --,
//            private String yw_num;   // --,
//            private String yz_num;   // --,
//            private String ze_num;   // 有,
//            private String zy_num;   // 有,
//            private String swz_num;   // 12

            LeftNew leftNew = new LeftNew();
            leftNew.setTrain_no(dataArr[2]);
            leftNew.setStation_train_code(dataArr[3]);
            leftNew.setStart_station_telecode(dataArr[4]);
            leftNew.setStart_station_name(stations.get(dataArr[4]).getStationName());
            leftNew.setEnd_station_telecode(dataArr[5]);
            leftNew.setEnd_station_name(stations.get(dataArr[5]).getStationName());
            leftNew.setFrom_station_telecode(dataArr[6]);
            leftNew.setFrom_station_name(stations.get(dataArr[6]).getStationName());
            leftNew.setTo_station_telecode(dataArr[7]);
            leftNew.setTo_station_name(stations.get(dataArr[7]).getStationName());
            leftNew.setStart_time(dataArr[8]);
            leftNew.setArrive_time(dataArr[9]);
//            leftNew.setDay_difference(dataArr[12]);
//            leftNew.setTrain_class_name(dataArr[13]);
            leftNew.setLishi(dataArr[10]);
            leftNew.setCanWebBuy(dataArr[1]);
            leftNew.setLishiValue(dataArr[10]);
//            leftNew.setYp_info(dataArr[15]);
//            leftNew.setControl_train_day(dataArr[16]);
            leftNew.setStart_train_date(dataArr[13]);
//            leftNew.setSeat_feature(dataArr[18]);
//            leftNew.setYp_ex(dataArr[19]);
//            leftNew.setTrain_seat_feature(dataArr[20]);
//            leftNew.setSeat_types(dataArr[21]);
//            leftNew.setLocation_code(dataArr[22]);
            leftNew.setFrom_station_no(stations.get(dataArr[6]).getNo());
            leftNew.setTo_station_no(stations.get(dataArr[7]).getNo());
//            leftNew.setControl_day(dataArr[25]);
//            leftNew.setSale_time(dataArr[26]);
//            leftNew.setIs_support_card(dataArr[27]);
//            leftNew.setControlled_train_flag(dataArr[28]);
//            leftNew.setControlled_train_message(dataArr[29]);
//            leftNew.setGg_num(dataArr[30]);
//            leftNew.setGr_num(dataArr[31]);
//            leftNew.setQt_num(dataArr[32]);
            leftNew.setRw_num(dataArr[23]);
//            leftNew.setRz_num(dataArr[34]);
//            leftNew.setTz_num(dataArr[35]);
            leftNew.setWz_num(dataArr[26]);
//            leftNew.setYb_num(dataArr[37]);
            leftNew.setYw_num(dataArr[28]);
            leftNew.setYz_num(dataArr[29]);
            leftNew.setZe_num(dataArr[30]);
            leftNew.setZy_num(dataArr[31]);
            leftNew.setSwz_num(dataArr[32]);

            leftNews.add(leftNew);
        }

        LOGGER.info("查询票信息成功！  开车日期：" + checkLeftNew.getStart_train_date() + "  出发站：" + checkLeftNew.getFrom_station_name() + "  到达站：" + checkLeftNew.getTo_station_name());

        return leftNews;
    }

}
