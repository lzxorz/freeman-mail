package com.fyts.mail.common.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fyts.mail.common.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.BindException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.*;
import sun.security.validator.ValidatorException;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

    private final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);
    
    @Value("${spring.profiles.active}")
    private String env;//当前激活的配置文件

    // @Value("${tools.file.local.path}")
    // private String filepath;
    // @Value("${tools.file.prefixPlaceholder}")
    // private String prefixPlaceholder;
    // @Value("${tools.file.prefix}")
    // private String prefix;


    /**
     * 使用阿里 FastJson 作为JSON MessageConverter
     * @author 刘志新
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        /**
         * 1.先定义一个convert转换消息的对象
         * 2.添加fastjson的配置信息，比如：是否要格式化返回的json数据
         * 3.在convert中添加配置信息
         * 4.将convert添加到converters当中
         */
        //1.先定义一个convert转换消息的对象
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        //2.添加fastjson的配置信息，比如：是否要格式化返回的json数据
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializeFilters(new ValueFilter() {
            private final String[] fields = {"img","photo","avatar","logo","companyLogo"};
            /**
             * 返回数据fastjson序列化时,根据属性名 修改 属性值
             * 可以在这里解码 例如：  value = HtmlUtils.htmlUnescape((String)value); // HtmlUtils.htmlUnescape("尼古拉&middot;特斯拉");
             * 可以在这里拼接url前缀
             * @author 刘志新
             * @Param  object
             * @Param  name  属性名
             * @Param  value 属性值
             * @return
             */
            @Override
            public Object process(Object object, String name, Object value) {

                // if (value instanceof String && StrUtil.equalsAny(name,fields)) {
                //     value = ((String) value).replace(prefixPlaceholder, prefix);
                // }
                return value;
            }
        });
        config.setSerializerFeatures(SerializerFeature.PrettyFormat);
        config.setSerializerFeatures(SerializerFeature.WriteMapNullValue,//保留空的字段
                SerializerFeature.WriteNullStringAsEmpty,//String null -> ""
                SerializerFeature.WriteNullNumberAsZero);//Number null -> 0
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");

        //解决Long转json精度丢失的问题
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        config.setSerializeConfig(serializeConfig);

        // 中文乱码解决方案
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);//设定json格式且编码为UTF-8
        converter.setSupportedMediaTypes(mediaTypes);

        //3.在convert中添加配置信息
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(Charset.forName("UTF-8"));

        //4.将convert添加到converters当中
        converters.add(0, converter);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决静态资源无法访问（可选）
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        // 直接在浏览器访问：根目录/swagger-ui.html
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        // 需要用到的webjars（包含js、css等）
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // registry.addResourceHandler("/files/**").addResourceLocations("file:"+filepath+"/"); //"file:/home/lzx/tempFiles/"
    }

    //统一异常处理
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new HandlerExceptionResolver() {
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
                Result result = new Result();
                /*if (e instanceof ServiceException) {//业务失败的异常，如“账号或密码错误”
                    result.setCode(ResultCode.FAIL).setMessage(e.getMessage());
                    logger.info(e.getMessage());
                    SUCCESS(200),//成功
                    FAIL(400),//失败
                    UNAUTHORIZED(401),//未认证（签名错误）
                    LOCKSCREEN(402),//用户锁屏
                    AUTHORIZEDFAIL(403),//用户认证失败
                    NOT_FOUND(404),//接口不存在
                    PARAMTYPE_MISMATCH(405),//参数类型不匹配
                    PARAM_INVALID(406),//参数校验失败
                    PARAMS_IS_INVALID(407) ,//金币不足
                    INTERNAL_SERVER_ERROR(500);//服务器内部错误
                } else */if (e instanceof BindException) {
                    result = Result.builder().code(405).message("参数类型不匹配").build();
                } else if (e instanceof ValidatorException) {
                    result = Result.builder().code(407).message("参数无效").build();
                }else if (e instanceof NoHandlerFoundException) {
                    result = Result.builder().code(404).message("接口 [" + request.getRequestURI() + "] 不存在").build();
                } else if (e instanceof ServletException) {
                    result = Result.builder().code(400).message(e.getMessage()).build();
                } /* else if (e instanceof UnauthorizedException) {
                	logger.error("权限不足！");
                    result.setCode(ResultCode.UNAUTHORIZED).setMessage("权限不足！");
                }*/ else if (e instanceof AuthenticationException) {
                    result = Result.builder().code(403).message("用户认证失败").build();
                } else {
                    result = Result.builder().code(500).message("接口 [" + request.getRequestURI() + "] 内部错误，请联系管理员").build();
                    String message;
                    if (handler instanceof HandlerMethod) {
                        HandlerMethod handlerMethod = (HandlerMethod) handler;
                        message = String.format("接口 [%s] 出现异常，方法：%s.%s，异常摘要：%s",
                                request.getRequestURI(),
                                handlerMethod.getBean().getClass().getName(),
                                handlerMethod.getMethod().getName(),
                                e.getMessage());
                    } else {
                        message = e.getMessage();
                    }
                    logger.error(message);
                }

                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-type", "application/json;charset=UTF-8");
                response.setStatus(200);
                try {
                    response.getWriter().write(JSON.toJSONString(result));
                } catch (IOException ex) {
                    logger.error(ex.getMessage());
                }
                return new ModelAndView();
            }

        });
    }

    //解决跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600)
                .allowCredentials(true);
    }

    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	
    	
    }
    
    /*private void responseResult1(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }*/

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        return ip;
    }
    
}
