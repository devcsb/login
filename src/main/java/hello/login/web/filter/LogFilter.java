package hello.login.web.filter;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter { // 필터를 사용하려면 필터 인터페이스를 구현해야 한다

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
        Filter.super.init(filterConfig);
    }

    /**
     * HTTP 요청이 오면 doFilter가 호출된다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        // ServletRequest는 HTTP 요청이 아닌 경우까지 고려해서 만든 인터페이스이다. 다양한 기능 사용을 위해 다운캐스팅한다.
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            chain.doFilter(request, response); // 다음 필터가 있으면 필터를 호출하고, 필터가 없으면 서블릿을 호출한다.

        } catch (Exception e) {
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
        Filter.super.destroy();
    }
}
    /* 같은 요청의 로그에 모두 같은 식별자를 자동으로 남기려면... logback mdc 를 사용하면 된다.
    하나의 요청은 하나의 쓰레드를 사용한다.
    다수의 클라이언트가 동시에 여러 호출을 하면, 멀티 쓰레드 프로그램에서는 쓰레드들이 서로 컨텍스트를 스위칭하며 실행되므로,
    로그 메세지가 섞이게 된다.
    이를 해결하려면,
    1. 요청을 처음 받았을때 Correlation ID를 생성하고,
    2. 이를 ThreadLocal에 저장했다가,
    3. 로그를 쓸때 매번 이 ID를 ThreadLocal에서 꺼내서 같이 출력하면 된다.

    하지만 이 작업을 일일히 구현하기엔 매우 번거롭다.
    그래서 logback, slf4j 등에서는 이런 기능을 MDC(Mapped Diagnostic Context) 로 제공한다.
    MDC에는 고유식별자(CorrelationID) 뿐만 아니라 map 형식으로 여러 메타 데이터를 넣을 수 있다.
    실행되는 요청이 어떤 사용자로 부터 들어온것인지, 또는 상품 주문시 상품 주문 ID를 넣는다던지,
    요청에 대한 다양한 컨텍스트 정보를 MDC에 저장하고 로그 출력시 함께 출력하면 더 의미 있는 로그를 출력할 수 있다.
     */
