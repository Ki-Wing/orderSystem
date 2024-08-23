package com.beyond.ordersystem.ordering.Controller;

import com.beyond.ordersystem.ordering.Dto.OrderListResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SSEController implements MessageListener {
    // SseEmitter : 연결된 사용자 정보를 의미
    // ConcurrentHashMap : Thread-safe한 map(동시성 이슈 x) 내부적으로 syncronize
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    //  여러번 구독 방지하기 위한 ConcurrentHashMap 번수 생성
    private Set<String> subscribeList = ConcurrentHashMap.newKeySet();

    @Qualifier("4")
    private final RedisTemplate<String, Object> sseRedisTemplate;

    private final RedisMessageListenerContainer redisMessageListenerContainer;



    public SSEController(@Qualifier("4") RedisTemplate<String, Object> sseRedisTemplate, RedisMessageListenerContainer redisMessageListenerContainer) {
        this.sseRedisTemplate = sseRedisTemplate;
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    // email에 해당되는 메시지를 listen하는 listener 추가완료
    public void subscribeChannel(String email){
        if(!subscribeList.contains(email)) {
            MessageListenerAdapter listenerAdapter = createListenerAdapter(this);
            redisMessageListenerContainer.addMessageListener(listenerAdapter, new PatternTopic(email));
            subscribeList.add(email);
        }
    }


    private MessageListenerAdapter createListenerAdapter(SSEController sseController){
        return new MessageListenerAdapter(sseController, "onMessage");
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribe(){
        SseEmitter sseEmitter = new SseEmitter(14400*60*1000L);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        emitters.put(email, sseEmitter);
        sseEmitter.onCompletion(()-> emitters.remove(email));
        sseEmitter.onTimeout(()-> emitters.remove(email));

        try{
            sseEmitter.send(SseEmitter.event().name("connect").data("connected!!!"));
        }catch(IOException e){
            e.printStackTrace();
        }
        subscribeChannel(email);
        return sseEmitter;
    }

    public void publishMessage(OrderListResDto dto, String email){
        SseEmitter emitter = emitters.get(email);
        if(emitter !=null){
            try {
                emitter.send(SseEmitter.event().name("ordered").data(dto));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            //convertAndSend 직렬화해서 보내겠다
            sseRedisTemplate.convertAndSend(email, dto);
        }
    }

    public void onMessage(Message message, byte[] pattern) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println("Message received: " + new String(message.getBody()));
            OrderListResDto dto = objectMapper.readValue(message.getBody(), OrderListResDto.class);
            String email = new String(pattern, StandardCharsets.UTF_8);
            SseEmitter emitter = emitters.get(email);
            if(emitter != null) {
                emitter.send(SseEmitter.event().name("ordered").data(dto));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
