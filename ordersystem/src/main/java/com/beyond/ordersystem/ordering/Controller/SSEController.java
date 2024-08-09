package com.beyond.ordersystem.ordering.Controller;

import com.beyond.ordersystem.ordering.Dto.OrderListResDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SSEController {
    // SseEmitter : 연결된 사용자 정보를 의미
    // concurrentHaMap : Thread-safe한 map(동시성 이슈 x) 내부적으로 syncronize
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Qualifier("4")
    private final RedisTemplate<String, Object> sseRedisTemplate;

    public SSEController(@Qualifier("4") RedisTemplate<String, Object> sseRedisTemplate) {
        this.sseRedisTemplate = sseRedisTemplate;
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
}
