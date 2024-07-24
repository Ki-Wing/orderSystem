package com.beyond.ordersystem.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class CommonResDto {
    private int status_code;

    private String status_message;

//    단건 객체 처리 예정이라..object가 모든 class 조상
    // List<Object>로 하나 걍 Object로 하나 똑같은거임
    private Object result;

    public CommonResDto(HttpStatus httpStatus,String message, Object result){
        this.status_code = httpStatus.value();
        this.status_message = message;
        this.result = result;
    }
}
