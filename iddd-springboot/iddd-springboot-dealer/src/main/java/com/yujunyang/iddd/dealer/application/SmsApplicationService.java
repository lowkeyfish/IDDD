package com.yujunyang.iddd.dealer.application;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.application.command.SmsSendCommand;
import com.yujunyang.iddd.dealer.domain.sms.Sms;
import com.yujunyang.iddd.dealer.domain.sms.SmsRepository;
import com.yujunyang.iddd.dealer.domain.sms.SmsSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SmsApplicationService {
    private SmsRepository smsRepository;
    private SmsSendService smsSendService;

    @Autowired
    public SmsApplicationService(
            SmsRepository smsRepository,
            SmsSendService smsSendService) {
        this.smsRepository = smsRepository;
        this.smsSendService = smsSendService;
    }

    @Transactional
    public void send(SmsSendCommand command) {
        CheckUtils.notNull(command, "command 必须不为 null");

        Sms sms = smsRepository.findById(command.getSmsId());
        if (sms == null) {
            return;
        }

        sms.send(smsSendService);

        smsRepository.save(sms);
    }
}
