package com.fyts.mail.common.config;

import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.extra.template.engine.enjoy.EnjoyEngine;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@Configuration
public class TemplateCfg {

    /*
    // Hutool 封装过的
    @Bean
    public TemplateEngine templateEngine() {
        //自动根据用户引入的模板引擎库的jar来自动选择使用的引擎
        //TemplateConfig为模板引擎的选项，可选内容有字符编码、模板路径、模板加载方式等，默认通过模板字符串渲染
        final TemplateConfig config = new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH);
        TemplateEngine engine = TemplateUtil.createEngine(config);

        //假设我们引入的是Beetl引擎，使用的字符串模板，则：
        // Template template = engine.getTemplate("Hello ${name}");
        // String result = template.render(Dict.create().set("name", "Hutool")); //输出：Hello Hutool

        // Template template = engine.getTemplate("templates/demo.html");
        // String result = template.render(Dict.create().set("name", "Hutool"));//Dict本质上为Map，此处可用Map
        return engine;
    }*/

    // jfnail enjoy 模板引擎
    @Bean
    public Engine templateEngine() {
        // 创建一个 Engine 对象并进行配置
        Engine forEmail = Engine.create("forEmail");
        // 支持模板热加载，绝大多数生产环境下也建议配置成 true，除非是极端高性能的场景
        forEmail.setToClassPathSourceFactory();
        forEmail.setBaseTemplatePath("templates");
        forEmail.setDevMode(true);
        // 配置极速模式，性能提升 13%
        forEmail.setFastMode(true);

        return forEmail;
    }



}
