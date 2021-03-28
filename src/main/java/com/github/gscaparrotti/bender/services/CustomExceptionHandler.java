package com.github.gscaparrotti.bender.services;

import com.github.gscaparrotti.bender.controller.MainController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Aspect
@Order(Integer.MIN_VALUE) //max precedence over other advices
@Component
public class CustomExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);
    private static final StackWalker stackWalker = StackWalker.getInstance();

    @SuppressWarnings("rawtypes")
    @Around(value = "execution(* com.github.gscaparrotti.bender.services..*(..))")
    public Object handleExceptionInSpringController(final ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (final Throwable throwable) {
            if (throwable instanceof DataAccessException) {
                LOGGER.warn(throwable.toString());
                if (stackWalker.walk(stackFrames -> stackFrames.anyMatch(f -> f.getClassName().contains("bender.legacy")))) {
                    final String newThrowableString = throwable.toString().replaceAll("(.{100})", "$1\u2193\n");
                    MainController.getInstance().showMessageOnMainView("Errore nell'elaborazione dei dati: \n" + newThrowableString);
                }
                return new Result(Result.ResultType.CONFLICT);
            } else {
                LOGGER.error(throwable.toString());
                return new Result(Result.ResultType.ERROR);
            }
        }
    }

}
