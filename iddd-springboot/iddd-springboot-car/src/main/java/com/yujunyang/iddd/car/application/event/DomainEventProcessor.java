package com.yujunyang.iddd.car.application.event;

import com.yujunyang.iddd.common.domain.event.DomainEvent;
import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.domain.event.DomainEventSubscriber;
import com.yujunyang.iddd.common.domain.event.EventStore;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DomainEventProcessor {

    private EventStore eventStore;

    @Autowired
    public DomainEventProcessor(
            EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Before("execution(* com.yujunyang.iddd.car.application.*.*(..))")
    public void listen() {
        DomainEventPublisher.instance().reset();
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<DomainEvent>() {
                    public void handleEvent(DomainEvent aDomainEvent) {
                        store(aDomainEvent);
                    }

                    public Class<DomainEvent> subscribedToEventType() {
                        return DomainEvent.class;
                    }
                });
    }

    private void store(DomainEvent domainEvent) {
        eventStore.add(domainEvent);
    }
}
