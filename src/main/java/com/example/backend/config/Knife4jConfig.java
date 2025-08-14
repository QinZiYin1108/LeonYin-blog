package com.example.backend.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Arrays;
import java.util.List;

/**
 * Knife4j配置类
 */
@Configuration
@EnableKnife4j
@EnableSwagger2WebMvc
public class Knife4jConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.backend.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                .forCodeGeneration(true); // 启用文件上传支持
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("个人博客系统API文档")
                .description("Leon's Blog 后端接口文档\n\n" +
                           "### 使用说明\n" +
                           "1. 先调用登录接口获取token\n" +
                           "2. 点击右上角「Authorize」按钮\n" +
                           "3. 在弹出框中输入：Bearer {your-token}\n" +
                           "4. 点击「Authorize」完成认证\n\n" +
                           "### 文件上传说明\n" +
                           "**Swagger界面文件上传方法：**\n" +
                           "1. 展开文件上传接口\n" +
                           "2. 点击 'Try it out'\n" +
                           "3. 在请求体中选择 'form-data'\n" +
                           "4. 添加 'file' 字段，类型选择 'File'\n" +
                           "5. 点击 'Choose File' 选择文件\n" +
                           "6. 添加 'folder' 字段，类型选择 'Text'，输入文件夹名\n" +
                           "7. 点击 'Execute' 执行\n\n" +
                           "**更方便的测试方式：**\n" +
                           "访问 [文件上传测试页面](/api/upload-test.html) 进行可视化测试")
                .version("1.0.0")
                .contact(new Contact("Leon", "https://leon.com", "leon@example.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }

    /**
     * 配置JWT认证方式
     */
    private List<SecurityScheme> securitySchemes() {
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        return Arrays.asList(apiKey);
    }

    /**
     * 配置认证上下文
     */
    private List<SecurityContext> securityContexts() {
        return Arrays.asList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!/auth).*$"))
                        .build()
        );
    }

    /**
     * 默认的认证配置
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }
} 
 
 
 