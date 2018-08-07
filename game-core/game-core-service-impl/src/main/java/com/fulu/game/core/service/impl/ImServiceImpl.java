package com.fulu.game.core.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.exception.IMException;
import com.fulu.game.common.utils.HttpUtils;
import com.fulu.game.common.utils.IMUtil;
import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.entity.vo.ImUserVo;
import com.fulu.game.core.service.ImService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class ImServiceImpl implements ImService {

    @Autowired
    private IMUtil imUtil;

    @Override
    public String getToken() {
        String tokenUrl = imUtil.getTokenUrl();
        String body = imUtil.getTokenBodyStr();
        Map<String, String> headerMap = new HashMap();
        headerMap.put("Content-Type", "application/json");

        HttpResponse httpResponse = HttpUtils.post(tokenUrl, body, headerMap);
        if (httpResponse.getStatus() != 200) {
            log.error("服务器端IM注册获取TOKEN失败:body:{};", httpResponse.body());
            return null;
        }
        String jsonResult = httpResponse.body();
        JSONObject resultJo = new JSONObject(jsonResult);
        if (resultJo.containsKey("access_token")) {
            return resultJo.getStr("access_token");
        }
        return null;

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
        String result = HttpUtils.get(userUrl, null, headerMap).body();
        return handleUsers(result);
    }

    /**
     * 用户批量注册
     *
     * @param users
     */
    @Override
    public List<ImUser> registUsers(List<ImUser> users) {
        if (CollectionUtil.isEmpty(users)) {
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
        int status = HttpUtils.post(userUrl, body, headerMap).getStatus();
        if (status == 200) {
            return users;
        } else {
            return null;
        }
    }

    @Override
    public boolean sendMsgToImUser(String imId, String action) {
        log.info("正在发送IM通知消息给老板，imId:{}", imId);
        String token = imUtil.getImToken();
        if (StringUtils.isBlank(token)) {
            token = getToken();
        }

        String Authorization = "Bearer " + token;
        Map<String, Object> headerMap = new HashMap();
        headerMap.put("Authorization", Authorization);
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-Type", "application/json");

        String userUrl = imUtil.getMessageUrl();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("target_type", "users");
        String[] userArr = {imId};
        paramMap.put("target", userArr);

        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("type", "cmd");
        msgMap.put("action", action);
        paramMap.put("msg", msgMap);

        Map<String, String> extMap = new HashMap<>();
        extMap.put("flag", Constant.SERVICE_USER_ACCEPT_ORDER);
        paramMap.put("ext", extMap);

        paramMap.put("from", "admin");

        JSONObject jsonObject = new JSONObject(paramMap);
        String body = jsonObject.toString();
        HttpResponse httpResponse = HttpUtils.post(userUrl, body, headerMap);
        System.out.println(httpResponse);
        return true;
    }


    public ImUser registerUser(String imId, String imPsw) {
        String token = getToken();
        if (token == null) {
            throw new IMException(IMException.ExceptionCode.IM_REGISTER_FAIL);
        }
        String Authorization = "Bearer " + token;
        Map headerMap = new HashMap();
        headerMap.put("Authorization", Authorization);
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-Type", "application/json");
        String userUrl = imUtil.getUserUrl();
        String body = getUserJsonStr(imId, imPsw);
        HttpResponse httpResponse = HttpUtils.post(userUrl, body, headerMap);
        if (httpResponse.getStatus() != 200) {
            log.error("服务器端IM注册失败:body:{};imId:{};imPsw:{}", httpResponse.body(), imId, imPsw);
            throw new IMException(IMException.ExceptionCode.IM_REGISTER_FAIL);
        }
        ImUser imUser = new ImUser();
        imUser.setUsername(imId);
        imUser.setImPsw(imPsw);
        return imUser;
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


    private String getUserJsonStr(String imId, String imPsw) {
        JSONArray ja = new JSONArray();
        JSONObject jo = new JSONObject();
        jo.put("username", imId);
        jo.put("password", imPsw);
        ja.add(jo);
        return ja.toString();
    }

}
