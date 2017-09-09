package com.dollapi.exception;

import com.dollapi.util.Results;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DollException.class)
    @ResponseBody
    public Results jsonErrorHandler(HttpServletRequest req, DollException e) throws Exception {
        return new Results(e.getCode(), e.getError());
    }


}
