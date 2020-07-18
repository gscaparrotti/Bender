package com.github.gscaparrotti.bender.springControllers;

import com.github.gscaparrotti.bender.controller.MainController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CustomExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @SuppressWarnings("rawtypes")
    @Around(value = "execution(* com.github.gscaparrotti.bender.springControllers..*(..))")
    public Object handleExceptionInSpringController(final ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (final Throwable throwable) {
            final HttpHeaders header = new HttpHeaders();
            header.add("Java-Exception", throwable.toString());
            if (throwable instanceof DataAccessException) {
                LOGGER.warn(throwable.toString());
                final StackTraceElement[] stackTrace = throwable.getStackTrace();
                for (final StackTraceElement stackTraceElement : stackTrace) {
                    //verify if the exception has been thrown as a consequence of an interaction with the GUI
                    if (stackTraceElement.toString().contains("bender.legacy")) {
                        MainController.getInstance().showMessageOnMainView("Errore nell'elaborazione dei dati: " + throwable);
                        break;
                    }
                }
                return new ResponseEntity(header, HttpStatus.CONFLICT);
            } else {
                LOGGER.error(throwable.toString());
                return new ResponseEntity(header, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
