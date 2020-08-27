package com.jiubo.sam.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

/**
 * Swagger3.0配置类
 * 访问链接:http://127.0.0.1:8080/swagger-ui/index.html
 */
//@Configuration
//@EnableOpenApi
//@Profile("dev")
//@EnableSwaggerBootstrapUI
public class Swagger3Config {
    @Bean
    public Docket docket(Environment environment) {
        //设置要使用Swagger的环境（例如：只在测试环境中开启）
        Profiles profiles = Profiles.of("dev");
        //判断自己是否在设置的环境中
        boolean flag = environment.acceptsProfiles(profiles);
        //由于没有设置测试环境，故不使用此判断环境
        flag = true;
        Docket docket = new Docket(new DocumentationType("swagger", "3.0"))
                .apiInfo(apiInfo())
                .pathMapping("/")
                .groupName("SAM API")//设置API文档分组名（如：需要多个分组则新建多个Docket）
                .enable(flag)//是否启用Swagger2（false：不启用）
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jiubo.sam.action")) //配置要扫描接口的方式
                //.paths(PathSelectors.ant("com/jiubo/sam/action/**))//过滤路径（只扫描action下的action接口）
                .build();
        return docket;
    }

    private ApiInfo apiInfo() {
        //作者信息
        Contact contact = new Contact("jiubo", "http://xxx.com", "xxxxx@qq.com");
        //文档基础信息
        ApiInfo apiInfo = new ApiInfo(
                "SAM API文档",
                "SAM 接口文档",
                "1.0",
                "urn:tos",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
        return apiInfo;
    }
}
