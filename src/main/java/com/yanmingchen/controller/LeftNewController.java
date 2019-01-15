package com.yanmingchen.controller;

import com.yanmingchen.pojo.LeftNew;
import com.yanmingchen.utils.CheckLeftNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/leftNew")
public class LeftNewController {

    @Autowired
    private CheckLeftNew checkLeftNew;

    @RequestMapping("/checkLeftNews")
    public String checkLeftNew(Model model, LeftNew leftNew) throws Exception {
        if (StringUtils.isEmpty(leftNew.getFrom_station_name())) {
            leftNew.setStart_train_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            leftNew.setFrom_station_name("深圳北");
            leftNew.setTo_station_name("广州南");
        }
        List<LeftNew> leftNews = checkLeftNew.checkTicket(leftNew);

        model.addAttribute("leftNews", leftNews);
        model.addAttribute("leftNew", leftNew);
        return "leftNew";
    }

}
