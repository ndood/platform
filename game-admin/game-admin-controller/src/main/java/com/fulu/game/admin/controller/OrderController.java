package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController {

    @RequestMapping("status-all")
    public Result statusList(){
        JSONArray ja = new JSONArray();
        for (OrderStatusEnum statusEnum: OrderStatusEnum.values() ){
            JSONObject jo = new JSONObject();
            jo.put("status",statusEnum.getStatus());
            jo.put("msg",statusEnum.getMsg());
            jo.put("name",statusEnum.name());
            ja.add(jo);
        }
        return Result.success().data(ja);
    }

}
