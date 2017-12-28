package kr.co.mashup.feedgetapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackages = "kr.co.mashup.feedgetcommon", basePackageClasses = {Jsr310JpaConverters.class})
@EnableJpaRepositories(basePackages = "kr.co.mashup.feedgetcommon")
@EnableJpaAuditing
@Slf4j
        /*
spring boot jpa multi module gradle ddl-auto
로 검색

http://jojoldu.tistory.com/123
https://roadmichi.blogspot.kr/2015/11/gradle-multiproject.html
https://spring.io/guides/gs/multi-module/#scratch
 */
public class FeedgetApiApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        ApplicationContext ctx = SpringApplication.run(FeedgetApiApplication.class, args);
        DispatcherServlet dispatcherServlet = (DispatcherServlet) ctx.getBean("dispatcherServlet");
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

        log.info("Start up spring boot");
    }
}
