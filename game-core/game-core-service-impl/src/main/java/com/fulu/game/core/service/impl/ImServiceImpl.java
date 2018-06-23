package com.fulu.game.core.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fulu.game.common.utils.HttpUtils;
import com.fulu.game.common.utils.IMUtil;
import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.entity.vo.ImUserVo;
import com.fulu.game.core.service.ImService;
import com.xiaoleilu.hutool.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("imService")
public class ImServiceImpl implements ImService {

    @Autowired
    IMUtil imUtil;

    @Override
    public String getToken() {
        String tokenUrl = imUtil.getTokenUrl();
        String body = imUtil.getTokenBodyStr();
        Map headerMap = new HashMap();
        headerMap.put("Content-Type", "application/json");
        String jsonResult = HttpUtils.post(tokenUrl, body, headerMap);
        JSONObject resultJo = new JSONObject(jsonResult);
        if (resultJo.containsKey("access_token")) {
            String token = resultJo.getStr("access_token");
            imUtil.setImToken(token);
            return token;
        } else {
            return null;
        }
    }

    @Override
    public ImUserVo getUsers(Integer limit, String cursor) {
        String token = imUtil.getImToken();
        if (null == token) {
            token = getToken();
        }
        String Authorization = "Bearer " + token;
        Map headerMap = new HashMap();
        headerMap.put("Authorization", Authorization);
        headerMap.put("Accept", "application/json");
        String cursorCondition = (null == cursor ? "" : "&cursor=" + cursor);
        String userUrl = imUtil.getUserUrl() + "?limit=" + limit + cursorCondition;
        String result = HttpUtils.get(userUrl, null, headerMap);
        return handleUsers(result);
    }

    /**
     * 用户批量注册
     *
     * @param users
     */
    @Override
    public List<ImUser> registUsers(List<ImUser> users) {
        if (CollectionUtil.isEmpty(users)){
            return null;
        }
        String token = imUtil.getImToken();
        if (null == token) {
            token = getToken();
        }
        String Authorization = "Bearer " + token;
        Map headerMap = new HashMap();
        headerMap.put("Authorization", Authorization);
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-Type", "application/json");
        String userUrl = imUtil.getUserUrl();
        String body = getUserJsonStr(users);
        int status = HttpUtils.post1(userUrl, body, headerMap).getStatus();
        if (status == 200){
            return users;
        }else{
            return null;
        }
    }

    /**
     * users返回串解析成VO
     *
     * @param resultStr
     * @return
     */
    private ImUserVo handleUsers(String resultStr) {
        ImUserVo imUserVo = new ImUserVo();
        JSONObject resultJo = new JSONObject(resultStr);
        String nextCursor = resultJo.getStr("cursor");
        imUserVo.setCursor(nextCursor);
        JSONArray entityJa = resultJo.getJSONArray("entities");
        ListIterator it = entityJa.listIterator();
        List<ImUser> userList = new ArrayList<ImUser>();
        while (it.hasNext()) {
            JSONObject jo = new JSONObject(it.next());
            ImUser imUser = jo.toBean(ImUser.class);
            userList.add(imUser);
        }
        imUserVo.setImUserList(userList);
        return imUserVo;
    }

    /**
     * 组装待注册的用户信息为JSON
     * 分配imid 和 psw
     *
     * @param userList
     * @return
     */
    private String getUserJsonStr(List<ImUser> userList) {
        JSONArray ja = new JSONArray();
        for (ImUser user : userList) {
            JSONObject jo = new JSONObject();
            String imId = user.getUserId() + "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", "");
            String imPsw = IMUtil.generatePsw();
            user.setUsername(imId);
            user.setImPsw(imPsw);
            jo.put("username", imId);
            jo.put("password", imPsw);
            ja.add(jo);
        }
        return ja.toString();
    }

}
