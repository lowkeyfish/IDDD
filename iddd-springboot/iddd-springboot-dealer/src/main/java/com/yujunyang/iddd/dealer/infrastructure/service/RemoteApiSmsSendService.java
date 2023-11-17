package com.yujunyang.iddd.dealer.infrastructure.service;

import com.yujunyang.iddd.dealer.domain.sms.Sms;
import com.yujunyang.iddd.dealer.domain.sms.SmsSendService;
import org.springframework.stereotype.Service;

@Service
public class RemoteApiSmsSendService implements SmsSendService {

    @Override
    public void send(Sms sms) {

    }
}
