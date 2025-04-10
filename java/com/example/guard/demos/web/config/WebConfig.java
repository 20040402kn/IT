package com.example.guard.demos.web.config;

import com.example.guard.demos.web.Interceptor.LoginInterceptor;
import com.example.guard.demos.web.Interceptor.StudentLoginInterceptor;
import com.example.guard.demos.web.Interceptor.TeacherLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private TeacherLoginInterceptor teacherLoginInterceptor;

    @Autowired
    private StudentLoginInterceptor studentLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");

        registry.addInterceptor(teacherLoginInterceptor)
                .addPathPatterns("/teacher/**")
                .excludePathPatterns("/teacher/login");

        registry.addInterceptor(studentLoginInterceptor)
                .addPathPatterns("/student/**")
                .excludePathPatterns("/student/login");
    }
}

   