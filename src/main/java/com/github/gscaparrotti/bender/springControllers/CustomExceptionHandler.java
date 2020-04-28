package com.github.gscaparrotti.bender.springControllers;

import com.github.gscaparrotti.bender.controller.MainController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CustomExceptionHandler {

    @Around(value = "execution(* com.github.gscaparrotti.bender.springControllers..*(..))")
    public Object handleExceptionInSpringController(final ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (final Throwable throwable) {
            final HttpHeaders header = new HttpHeaders();
            header.add("Java-Exception", throwable.toString());
            if (throwable instanceof DataIntegrityViolationException) {
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
                return new ResponseEntity(header, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
