package com.fyts.api.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 现已多路径扫描支持
 * 开发和测试环境可用，生产环境自动关闭
 * 插件maven依赖，在service-base项目pom
 * 文档访问url示例1 http://localhost:8082/doc.html
 * 文档访问url示例2 http://localhost:8082/swagger-ui.html
 * Knife4j 可生成接口文档
 *
 * Knife4j是基于swagger的二次封装版本 官方文档 https://doc.xiaominfo.com/knife4j/
 * @author 刘志新
 * @email  lzxorz@163.com
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
// @Profile({"dev","test"})
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {
    // 定义分隔符
    private static final String splitor = ";";

    @Bean
    public Docket createRestApi() {
        List<Parameter> pars = new ArrayList<Parameter>();
        pars.add(new ParameterBuilder().name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build());
        pars.add(new ParameterBuilder().name("api-version").allowableValues(new AllowableListValues(Arrays.asList("V1","V2","V3"), "String")).description("版本号").modelRef(new ModelRef("string")).parameterType("header").required(true).build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // .apis(RequestHandlerSelectors.withClassAnnotation(ApiSwagger.class))
                // .apis(RequestHandlerSelectors.basePackage("com.fyts.api"))
                .apis(basePackage("com.fyts.api;com.fyts.core.common.util"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars) ;
    }
    private ApiInfo apiInfo() {
        Contact contact = new Contact("北京风扬天顺科技有限公司",
                "http://www.fengyangts.com",
                "xxx.@emai.com");
        return new ApiInfoBuilder()
                .title("北京风扬天顺科技有限公司-----API文档")
                .description("互联网+解决方案服务商")
                .termsOfServiceUrl("http://www.fengyangts.com")
                .contact(contact)
                .version("1.0")
                .build();
    }

    // 处理多路径扫描---begin
    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }
    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage)     {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackage.split(splitor)) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }
    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }
    // 处理多路径扫描---over

    /**
     * http://localhost:8082/swagger-ui.html
     * http://localhost:8082/doc.html
     */

}
