package com.yujunyang.iddd.dealer.infrastructure.service;

import java.net.URLEncoder;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.dingtalk.message.AbstractMessage;
import com.yujunyang.iddd.dealer.domain.dingtalk.message.DingtalkMessageService;
import com.yujunyang.iddd.dealer.domain.dingtalk.message.Markdown;
import com.yujunyang.iddd.dealer.domain.dingtalk.message.Text;
import com.yujunyang.iddd.dealer.infrastructure.remote.dingtalk.DingtalkApi;
import com.yujunyang.iddd.dealer.infrastructure.remote.dingtalk.RobotSendRequestBody;
import com.yujunyang.iddd.dealer.infrastructure.remote.dingtalk.RobotSendResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoteApiDingtalkMessageService implements DingtalkMessageService {
    private DingtalkApi dingtalkApi;

    @Autowired
    public RemoteApiDingtalkMessageService(
            DingtalkApi dingtalkApi) {
        this.dingtalkApi = dingtalkApi;
    }

    @Override
    public void send(
            String accessToken,
            String secret,
            AbstractMessage message) {
        RobotSendRequestBody requestBody = new RobotSendRequestBody();
        requestBody.setMsgType(message.getType());
        Optional.ofNullable(message.getAt()).ifPresent(n -> {
            RobotSendRequestBody.At requestBodyAt = new RobotSendRequestBody.At();
            requestBodyAt.setAtAll(n.isAtAll());
            requestBodyAt.setAtMobiles(n.getAtMobiles());
            requestBodyAt.setAtUserIds(n.getAtUserIds());
            requestBody.setAt(requestBodyAt);
        });
        if (message.getType().equals("text")) {
            Text text = (Text) message;
            RobotSendRequestBody.Text requestBodyText = new RobotSendRequestBody.Text();
            requestBodyText.setContent(text.getContent());
            requestBody.setText(requestBodyText);
        } else if (message.getType().equals("markdown")) {
            Markdown markdown = (Markdown) message;
            RobotSendRequestBody.Markdown requestBodyMarkdown = new RobotSendRequestBody.Markdown();
            requestBodyMarkdown.setTitle(markdown.getTitle());
            requestBodyMarkdown.setText(markdown.getText());
            requestBody.setMarkdown(requestBodyMarkdown);
        }

        long timestamp = System.currentTimeMillis();
        RobotSendResponse response = dingtalkApi.robotSend(
                accessToken,
                timestamp,
                sign(timestamp, secret),
                requestBody
        );
        if (response.getErrCode() != 0) {
            CheckUtils.isTrue(
                    response.getErrCode() == 0,
                    "钉钉消息发送失败, errCode({0,number,#})errMsg({1})",
                    response.getErrCode(),
                    response.getErrMsg()
            );
        }
    }

    private String sign(long timestamp, String secret) {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");

            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData), "UTF-8"), "UTF-8");
            return sign;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
