package com.learning.common;

import com.learning.exception.ParamException;
import com.learning.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        String url = httpServletRequest.getRequestURI().toString();
        ModelAndView mv;
        String defaultMsg = "System error";

        // .json, .page
        //这里我们要求项目中所有请求json数据，都使用.json结尾
        if(url.endsWith(".json")){
            if(e instanceof PermissionException || e instanceof ParamException){
                JsonData jsonData = JsonData.fail(e.getMessage());
                mv = new ModelAndView("jsonView", jsonData.toMap());
                log.error(e.getMessage());
            }else{
                log.error("unknown json exception, url:" + url, e);
                JsonData jsonData = JsonData.fail(defaultMsg);
                mv = new ModelAndView("jsonView", jsonData.toMap());
            }
        }else if(url.endsWith(".page")){//这里我们要求项目中所有请求page页面，都使用.page结尾
            log.error("unknown page exception, url:" + url, e);
            JsonData jsonData = JsonData.fail(defaultMsg);
            mv = new ModelAndView("exception",jsonData.toMap());
            log.error(e.getMessage());
        }else{
            log.error("unknown exception, url:" + url, e);
            JsonData jsonData = JsonData.fail(defaultMsg);
            mv = new ModelAndView("jsonView", jsonData.toMap());
        }
        return mv;
    }
}
