package com.fulu.game.common.component;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.properties.Config;
import com.fulu.game.common.utils.EncryptUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j
@Component
public class CloopenSmsComponent {

    private static final int Request_Get = 0;
    private static final int Request_Post = 1;
    private static final String Account_Info = "AccountInfo";
    private static final String Create_SubAccount = "SubAccounts";
    private static final String Get_SubAccounts = "GetSubAccounts";
    private static final String Query_SubAccountByName = "QuerySubAccountByName";
    private static final String SMSMessages = "SMS/Messages";
    private static final String TemplateSMS = "SMS/TemplateSMS";
    private static final String Query_SMSTemplate = "SMS/QuerySMSTemplate";
    private static final String LandingCalls = "Calls/LandingCalls";
    private static final String VoiceVerify = "Calls/VoiceVerify";
    private static final String IvrDial = "ivr/dial";
    private static final String BillRecords = "BillRecords";
    private static final String queryCallState = "ivr/call";
    private static final String callResult = "CallResult";
    private static final String mediaFileUpload = "Calls/MediaFileUpload";
    private String SERVER_IP;
    private String SERVER_PORT;
    private String ACCOUNT_SID;
    private String ACCOUNT_TOKEN;
    private String SUBACCOUNT_SID;
    private String SUBACCOUNT_Token;
    public String App_ID;

    private BodyType BODY_TYPE = BodyType.Type_JSON;

    public String Callsid;


    public enum BodyType {
        Type_XML, Type_JSON;
    }

    public enum AccountType {
        Accounts, SubAccounts;
    }

    private final Config configProperties;

    @Autowired
    public CloopenSmsComponent(Config configProperties) {
        this.configProperties = configProperties;
        SERVER_IP = configProperties.getCloopen().getServerIp();
        SERVER_PORT = configProperties.getCloopen().getServerPort();
        ACCOUNT_SID = configProperties.getCloopen().getAccountSid();
        ACCOUNT_TOKEN = configProperties.getCloopen().getAccountToken();
        App_ID = configProperties.getCloopen().getAppId();
    }


    public Boolean sendTemplateSMS(String to, String templateId, String[] datas) throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        String result = "";
        Boolean flag = false;
        int status = 0;
        try {
            HttpPost httppost = (HttpPost) getHttpRequestBase(1, TemplateSMS);
            String requestBodyStr = "";
            if (BODY_TYPE == BodyType.Type_JSON) {
                JsonObject json = new JsonObject();
                json.addProperty("appId", App_ID);
                json.addProperty("to", to);
                json.addProperty("templateId", templateId);
                if (datas != null) {
                    StringBuilder sb = new StringBuilder("[");
                    for (String s : datas) {
                        sb.append("\"" + s + "\"" + ",");
                    }
                    sb.replace(sb.length() - 1, sb.length(), "]");
                    JsonParser parser = new JsonParser();
                    JsonArray Jarray = parser.parse(sb.toString())
                            .getAsJsonArray();
                    json.add("datas", Jarray);
                }
                requestBodyStr = json.toString();
            } else {
                StringBuilder sb = new StringBuilder(
                        "<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
                sb.append("<appId>").append(App_ID).append("</appId>")
                        .append("<to>").append(to).append("</to>")
                        .append("<templateId>").append(templateId)
                        .append("</templateId>");
                if (datas != null) {
                    sb.append("<datas>");
                    for (String s : datas) {
                        sb.append("<data>").append(s).append("</data>");
                    }
                    sb.append("</datas>");
                }
                sb.append("</TemplateSMS>").toString();
                requestBodyStr = sb.toString();
            }
            //打印包体
            log.debug("请求的包体：" + requestBodyStr);
            BasicHttpEntity requestBody = new BasicHttpEntity();
            requestBody.setContent(new ByteArrayInputStream(requestBodyStr.getBytes("UTF-8")));
            requestBody.setContentLength(requestBodyStr.getBytes("UTF-8").length);
            httppost.setEntity(requestBody);

            HttpResponse response = httpclient.execute(httppost);
            //获取响应码
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                flag = true;
            }
            log.debug("Https请求返回状态码：" + status);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
        } catch (IOException e) {
            log.error(getMyError("172001", "网络错误" + "Https请求返回码：" + status), e);
        } catch (Exception e) {
            log.error(getMyError("172002", "无返回"), e);
        } finally {
            if (httpclient != null) {
                httpclient.getConnectionManager().shutdown();
            }
        }
        try {
            String resultMsg = jsonToMap(result).toString();
            if (!flag) {
                log.error("短信请求错误{}", resultMsg);
            } else {
                log.debug(resultMsg);
            }
        } catch (Exception e) {
            log.error(getMyError("172003", "返回包体错误"), e);
        }

        return flag;
    }

    private HttpRequestBase getHttpRequestBase(int get, String action) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return getHttpRequestBase(get, action, AccountType.Accounts);
    }

    private HttpRequestBase getHttpRequestBase(int get, String action,
                                               AccountType mAccountType) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        String timestamp = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String sig = "";
        String acountName = "";
        String acountType = "";
        if (mAccountType == AccountType.Accounts) {
            acountName = ACCOUNT_SID;
            sig = ACCOUNT_SID + ACCOUNT_TOKEN + timestamp;
            acountType = "Accounts";
        } else {
            acountName = SUBACCOUNT_SID;
            sig = SUBACCOUNT_SID + SUBACCOUNT_Token + timestamp;
            acountType = "SubAccounts";
        }
        String signature = EncryptUtil.md5Digest(sig);

        String url = getBaseUrl().append("/" + acountType + "/")
                .append(acountName).append("/" + action + "?sig=")
                .append(signature).toString();
        if (callResult.equals(action)) {
            url = url + "&callsid=" + Callsid;
        }
        if (queryCallState.equals(action)) {
            url = url + "&callid=" + Callsid;
        }
//        if (mediaFileUpload.equals(action)) {
//            url = url + "&appid=" + App_ID + "&filename=" + Filename;
//        }
        log.debug(getmethodName(action) + " url = " + url);
        //System.out.println(getmethodName(action) + " url = " + url);
        HttpRequestBase mHttpRequestBase = null;
        if (get == Request_Get)
            mHttpRequestBase = new HttpGet(url);
        else if (get == Request_Post)
            mHttpRequestBase = new HttpPost(url);
        if (IvrDial.equals(action)) {
            setHttpHeaderXML(mHttpRequestBase);
        } else if (mediaFileUpload.equals(action)) {
            setHttpHeaderMedia(mHttpRequestBase);
        } else {
            setHttpHeader(mHttpRequestBase);
        }

        String src = acountName + ":" + timestamp;

        String auth = EncryptUtil.base64Encode(src, "utf-8");
        mHttpRequestBase.setHeader("Authorization", auth);
        log.debug("请求的Url：" + mHttpRequestBase);//打印Url
        return mHttpRequestBase;

    }

    private String getmethodName(String action) {
        if (action.equals(Account_Info)) {
            return "queryAccountInfo";
        } else if (action.equals(Create_SubAccount)) {
            return "createSubAccount";
        } else if (action.equals(Get_SubAccounts)) {
            return "getSubAccounts";
        } else if (action.equals(Query_SubAccountByName)) {
            return "querySubAccount";
        } else if (action.equals(SMSMessages)) {
            return "sendSMS";
        } else if (action.equals(TemplateSMS)) {
            return "sendTemplateSMS";
        } else if (action.equals(LandingCalls)) {
            return "landingCalls";
        } else if (action.equals(VoiceVerify)) {
            return "voiceVerify";
        } else if (action.equals(IvrDial)) {
            return "ivrDial";
        } else if (action.equals(BillRecords)) {
            return "billRecords";
        } else {
            return "";
        }
    }


    private void setHttpHeaderXML(AbstractHttpMessage httpMessage) {
        httpMessage.setHeader("Accept", "application/xml");
        httpMessage.setHeader("Content-Type", "application/xml;charset=utf-8");
    }

    private void setHttpHeaderMedia(AbstractHttpMessage httpMessage) {
        if (BODY_TYPE == BodyType.Type_JSON) {
            httpMessage.setHeader("Accept", "application/json");
            httpMessage.setHeader("Content-Type", "application/octet-stream;charset=utf-8;");
        } else {
            httpMessage.setHeader("Accept", "application/xml");
            httpMessage.setHeader("Content-Type", "application/octet-stream;charset=utf-8;");
        }
    }

    private void setHttpHeader(AbstractHttpMessage httpMessage) {
        if (BODY_TYPE == BodyType.Type_JSON) {
            httpMessage.setHeader("Accept", "application/json");
            httpMessage.setHeader("Content-Type",
                    "application/json;charset=utf-8");

        } else {
            httpMessage.setHeader("Accept", "application/xml");
            httpMessage.setHeader("Content-Type",
                    "application/xml;charset=utf-8");
        }
    }

    private StringBuffer getBaseUrl() {
        StringBuffer sb = new StringBuffer("https://");
        sb.append(SERVER_IP).append(":").append(SERVER_PORT);
        sb.append("/2013-12-26");
        return sb;
    }

    private String getMyError(String code, String msg) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("statusCode", code);
        hashMap.put("statusMsg", msg);
        return "错误码:code" + "【msg】";
    }


    private HashMap<String, Object> jsonToMap(String result) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        JsonParser parser = new JsonParser();
        JsonObject asJsonObject = parser.parse(result).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entrySet = asJsonObject.entrySet();
        HashMap<String, Object> hashMap2 = new HashMap<String, Object>();

        for (Map.Entry<String, JsonElement> m : entrySet) {
            if ("statusCode".equals(m.getKey())
                    || "statusMsg".equals(m.getKey()))
                hashMap.put(m.getKey(), m.getValue().getAsString());
            else {
                if ("SubAccount".equals(m.getKey())
                        || "totalCount".equals(m.getKey())
                        || "smsTemplateList".equals(m.getKey())
                        || "token".equals(m.getKey())
                        || "callSid".equals(m.getKey())
                        || "state".equals(m.getKey())
                        || "downUrl".equals(m.getKey())) {
                    if (!"SubAccount".equals(m.getKey())
                            && !"smsTemplateList".equals(m.getKey()))
                        hashMap2.put(m.getKey(), m.getValue().getAsString());
                    else {
                        try {
                            if ((m.getValue().toString().trim().length() <= 2)
                                    && !m.getValue().toString().contains("[")) {
                                hashMap2.put(m.getKey(), m.getValue()
                                        .getAsString());
                                hashMap.put("data", hashMap2);
                                break;
                            }
                            if (m.getValue().toString().contains("[]")) {
                                hashMap2.put(m.getKey(), new JsonArray());
                                hashMap.put("data", hashMap2);
                                continue;
                            }
                            JsonArray asJsonArray = parser.parse(
                                    m.getValue().toString()).getAsJsonArray();
                            ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
                            for (JsonElement j : asJsonArray) {
                                Set<Map.Entry<String, JsonElement>> entrySet2 = j
                                        .getAsJsonObject().entrySet();
                                HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                                for (Map.Entry<String, JsonElement> m2 : entrySet2) {
                                    hashMap3.put(m2.getKey(), m2.getValue()
                                            .getAsString());
                                }
                                arrayList.add(hashMap3);
                            }
                            hashMap2.put(m.getKey(), arrayList);
                        } catch (Exception e) {
                            JsonObject asJsonObject2 = parser.parse(
                                    m.getValue().toString()).getAsJsonObject();
                            Set<Map.Entry<String, JsonElement>> entrySet2 = asJsonObject2
                                    .entrySet();
                            HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                            for (Map.Entry<String, JsonElement> m2 : entrySet2) {
                                hashMap3.put(m2.getKey(), m2.getValue()
                                        .getAsString());
                            }
                            hashMap2.put(m.getKey(), hashMap3);
                            hashMap.put("data", hashMap2);
                        }

                    }
                    hashMap.put("data", hashMap2);
                } else {

                    JsonObject asJsonObject2 = parser.parse(
                            m.getValue().toString()).getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> entrySet2 = asJsonObject2
                            .entrySet();
                    HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                    for (Map.Entry<String, JsonElement> m2 : entrySet2) {
                        hashMap3.put(m2.getKey(), m2.getValue().getAsString());
                    }
                    if (hashMap3.size() != 0) {
                        hashMap2.put(m.getKey(), hashMap3);
                    } else {
                        hashMap2.put(m.getKey(), m.getValue().getAsString());
                    }
                    hashMap.put("data", hashMap2);
                }
            }
        }
        return hashMap;
    }


}
