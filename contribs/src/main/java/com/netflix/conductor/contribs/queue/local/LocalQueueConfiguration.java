package com.netflix.conductor.contribs.queue.local;

import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.Task.Status;
import com.netflix.conductor.contribs.queue.amqp.config.AMQPEventQueueProperties;
import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rx.Observable;
import rx.Subscriber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 只为了demo展示，在server单机，且不停机是可以用，关机队列内容小时
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "conductor.event-queues.local.enabled", havingValue = "true", matchIfMissing = true)
public class LocalQueueConfiguration {

    private static LinkedBlockingQueue<Message> memoryQueue = new LinkedBlockingQueue<>();

    @Bean
    public Map<Status, ObservableQueue> getQueues() {
        return Map.of(Status.COMPLETED, new ObservableQueue() {
            // rxjava真他妈绕
            @Override
            public Observable<Message> observe() {
                return Observable.create(

                        new Observable.OnSubscribe<Message>() {
                    @Override
                    public void call(Subscriber<? super Message> subscriber) {
                        new Thread(()->{
                            Message msg = null;
                            try {
                                msg = memoryQueue.take();
                            } catch (InterruptedException e) {}
                            subscriber.onNext(msg);
                            subscriber.onCompleted();
                        }).start();
                    }
                });
            }

            @Override
            public String getType() {
                return "local";
            }

            @Override
            public String getName() {
                return "local";
            }

            @Override
            public String getURI() {
                return "local://";
            }

            @Override
            public List<String> ack(List<Message> messages) {
                return messages.stream().map(Message::getId).collect(Collectors.toList());
            }

            @Override
            public void publish(List<Message> messages) {
                memoryQueue.addAll(messages);
            }

            @Override
            public void setUnackTimeout(Message message, long unackTimeout) {

            }

            @Override
            public long size() {
                return memoryQueue.size();
            }

            @Override
            public void start() {

            }

            @Override
            public void stop() {

            }

            @Override
            public boolean isRunning() {
                return true;
            }
        });
    }


}
