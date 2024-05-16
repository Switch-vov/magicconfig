package com.switchvov.magicconfig.server;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Slf4j
public class MagicconfigServerApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(MagicconfigServerApplication.class, args);
    }


    @Bean
    public ThreadPoolTaskExecutor mvcTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setQueueCapacity(100);
        executor.setMaxPoolSize(25);
        return executor;

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(mvcTaskExecutor());
        configurer.setDefaultTimeout(10000L);
    }

    @ControllerAdvice
    static class GlobalExceptionHandler {
        @ResponseStatus(HttpStatus.NOT_MODIFIED)//返回304状态码
        @ResponseBody
        @ExceptionHandler(AsyncRequestTimeoutException.class) //捕获特定异常
        public void handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e, HttpServletRequest request) {
            log.debug("handleAsyncRequestTimeoutException");
        }
    }

}
