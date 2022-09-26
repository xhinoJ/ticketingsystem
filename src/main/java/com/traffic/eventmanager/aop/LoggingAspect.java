package com.traffic.eventmanager.aop;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final String CONTROLLER_POINTCUT = "within(com.traffic.eventmanager.controller.*)";
    private static final String EXECUTION_ANNOTATION_POINTCUT = "@annotation(com.traffic.eventmanager.aop.LogExecutionTime)";

    @Around(EXECUTION_ANNOTATION_POINTCUT)
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object proceed = joinPoint.proceed();
        stopWatch.stop();
        log.info("\"{}\" executed in {} ms", joinPoint.getSignature(), stopWatch.getTotalTimeMillis());

        return proceed;
    }

    @Around(CONTROLLER_POINTCUT)
    @SneakyThrows
    public Object logAroundExec(ProceedingJoinPoint pjp) {
        log.info("before {}", constructLogMsg(pjp));
        var proceed = pjp.proceed();
        log.info("after {} wiht result: {}", constructLogMsg(pjp), proceed.toString());
        return proceed;
    }

    @AfterThrowing(pointcut = CONTROLLER_POINTCUT, throwing = "e")
    public void logAfterException(JoinPoint jp, Exception e) {
        log.error("Exception during: {} with ex: {}", constructLogMsg(jp), e.toString());
    }

    private String constructLogMsg(JoinPoint jp) {
        var args = Arrays.stream(jp.getArgs()).map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        return "@" + method.getName() +
                ":" +
                args;
    }

}
