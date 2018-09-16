package com.learning.common;

import com.learning.model.SysUser;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder {

    private static ThreadLocal<SysUser> userHolder = new ThreadLocal<>();

    private static ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void add (SysUser sysUser){
        userHolder.set(sysUser);
    }

    public static void add (HttpServletRequest httpServletRequest) {
        requestHolder.set(httpServletRequest);
    }

    public static SysUser getCurrentUser () {
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest () {
        return requestHolder.get();
    }

    public static void remove () {
        userHolder.remove();
        requestHolder.remove();
    }
}
