package com.yujunyang.iddd.common.domain.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DomainEventPublisher {

    private static final ThreadLocal<DomainEventPublisher> instance = new ThreadLocal<DomainEventPublisher>() {
        @Override
        protected DomainEventPublisher initialValue() {
            return new DomainEventPublisher();
        }
    };

    private boolean publishing;

    private List subscribers;

    public static DomainEventPublisher instance() {
        return instance.get();
    }

    public <T> void publish(final T aDomainEvent) {
        if (!this.isPublishing() && this.hasSubscribers()) {

            try {
                this.setPublishing(true);

                Class<?> eventType = aDomainEvent.getClass();

                List<DomainEventSubscriber<T>> allSubscribers = this.subscribers();

                for (DomainEventSubscriber<T> subscriber : allSubscribers) {
                    Class<?> subscribedToType = subscriber.subscribedToEventType();

                    if (eventType == subscribedToType || subscribedToType == DomainEvent.class) {
                        subscriber.handleEvent(aDomainEvent);
                    }
                }

            } finally {
                this.setPublishing(false);
            }
        }
    }

    public void publishAll(Collection<DomainEvent> aDomainEvents) {
        for (DomainEvent domainEvent : aDomainEvents) {
            this.publish(domainEvent);
        }
    }

    public void reset() {
        if (!this.isPublishing()) {
            this.setSubscribers(null);
        }
    }

    public <T> void subscribe(DomainEventSubscriber<T> aSubscriber) {
        if (!this.isPublishing()) {
            this.ensureSubscribersList();

            this.subscribers().add(aSubscriber);
        }
    }

    private DomainEventPublisher() {
        super();

        this.setPublishing(false);
        this.ensureSubscribersList();
    }

    private void ensureSubscribersList() {
        if (!this.hasSubscribers()) {
            this.setSubscribers(new ArrayList());
        }
    }

    private boolean isPublishing() {
        return this.publishing;
    }

    private void setPublishing(boolean aFlag) {
        this.publishing = aFlag;
    }

    private boolean hasSubscribers() {
        return this.subscribers() != null;
    }

    private List subscribers() {
        return this.subscribers;
    }

    private void setSubscribers(List aSubscriberList) {
        this.subscribers = aSubscriberList;
    }
}
