package hello.login;

import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class WebConfig {

    /**
     * FilterRegistrationBean을 사용해서 필터를 등록한다.
     *
     * /@ServletComponentScan @WebFilter(filterName = "logFilter", urlPatterns = "/*") 로
     * 필터 등록이 가능하지만 필터 순서 조절이 안된다. 따라서 FilterRegistrationBean 을 사용하자.
     */
    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter()); // 등록할 필터를 지정.
        filterRegistrationBean.setOrder(1); // 필터 동작 순서 지정. 낮을 수록 먼저 동작한다.
        filterRegistrationBean.addUrlPatterns("/*"); // 필터를 적용할 URL 패턴을 지정 . 한 번에 여러 패턴을 지정할 수 있다.

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/*"); // 일단 모든 요청에 적용하고, 필터 내부에서 whitelist만 제외하고 적용하는 식으로 구현

        return filterRegistrationBean;
    }
}
