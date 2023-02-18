package hello.login.web.filter;

import hello.login.domain.session.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {
    //init, destroy는 interface의 default method 이므로, 반딋 구현하지 않아도 된다.

    private static final String[] whitelist = {"/", "members/add", "/login", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작{}", requestURI);

            if (isLoginCheckPath(requestURI)) {

                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {

                    log.info("미인증 사용자 요청 {}", requestURI);
                    //로그인으로 redirect
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI); //redirect 해줄 URL을 쿼리스트링으로 전달.
                    return; //미인증 사용자의 경우 다음으로 진행하지 않고 끝낸다.
                }
            }
            chain.doFilter(request, response);

        } catch (Exception e) {
            throw e; //예외를 그대로 삼켜서 로깅 가능하지만, WAS까지 예외를 전파시켜 주어야 한다. 전파시키지 않으면 정상 흐름처럼 동작하므로.
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    /**
     * 화이트리스트의 경우 인증 체크X
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}

/**
 * 필터에는 아주 강력한 기능이 있는데
 * chain.doFilter(request, response); 를 호출해서 다음 필터 또는 서블릿을 호출할 때,
 * request ,response 를 다른 객체로 바꿀 수 있다.
 * ServletRequest , ServletResponse 를 구현한 다른 객체를 만들어서 doFilter()로 넘기면
 * 해당 객체가 다음 필터 또는 서블릿에서 사용된다.
 * 이는 스프링 인터셉터는 제공하지 않는 기능이다.
 */


