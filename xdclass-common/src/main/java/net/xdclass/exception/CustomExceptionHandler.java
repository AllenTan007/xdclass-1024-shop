package net.xdclass.exception;


import lombok.extern.slf4j.Slf4j;
import net.xdclass.util.JsonData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {


    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public JsonData handler(Exception e){

        if (e instanceof BizException){
            BizException bizException = (BizException) e ;
            log.error("业务异常:{}",e);
            return JsonData.buildCodeAndMsg(bizException.getCode(),bizException.getMsg());
        }else {
            log.error("非业务异常:{}",e);
            return JsonData.buildError("全局异常,位置错误");
        }
    }
}
