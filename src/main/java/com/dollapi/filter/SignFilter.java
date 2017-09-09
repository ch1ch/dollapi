package com.dollapi.filter;

import com.alibaba.fastjson.JSON;
import com.dollapi.util.ApiContents;
import com.dollapi.util.BaseUtil;
import com.dollapi.util.Results;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class SignFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Enumeration paramNames = servletRequest.getParameterNames();
        String sign = servletRequest.getParameter("sign");
        if (!sign.equals(getSign(paramNames))) {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            httpResponse.getWriter().write(JSON.toJSONString(new Results(ApiContents.SIGN_ERROR.value(), ApiContents.SIGN_ERROR.desc())));
            return;
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }

    public static String getSign(Enumeration paramNames) {
        //所有参数除了sign，acs码从小到大排序+当前时间
        StringBuffer sign = new StringBuffer("");
        List<String> stringList = new ArrayList<>();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            stringList.add(paramName);
        }
        stringList.sort((String v1, String v2) -> v1.compareTo(v2));
        stringList.forEach(e -> {
            if (!e.equals("sign")) {
                sign.append(e.toUpperCase());
            }
        });
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy#MM#ddHH:mm");
        sign.append(formatter.format(new Date()));
        String s = BaseUtil.getMD5(sign.toString());
        return s;
    }




}
