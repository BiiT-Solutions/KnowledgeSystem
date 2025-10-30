package com.biit.ks.logger;

/*-
 * #%L
 * Knowledge System (Logger)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Logs all file managed by Spring. In this project only are DAOs.
 */
@Aspect
@Component
public class OpenSearchClientLogging extends AbstractLogging {

    /**
     * Following is the definition for a pointcut to select all the methods
     * available. So advice will be called for all the methods.
     */
    @Pointcut("execution(* com.biit.ks.persistence.opensearch.OpenSearchClient.*(..))")
    private void searches() {
    }

    /**
     * This is the method that I would like to execute before a selected method
     * execution.
     *
     * @param joinPoint the joinPoint
     */
    @Before(value = "searches()")
    public void beforeAdvice(JoinPoint joinPoint) {

    }

    @Around(value = "searches()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        final StopWatch stopWatch = new StopWatch();
        Object returnValue = null;
        stopWatch.start();
        returnValue = joinPoint.proceed();
        stopWatch.stop();
        log(stopWatch.getTotalTimeMillis(), joinPoint);
        return returnValue;
    }

    /**
     * This is the method that I would like to execute after a selected method
     * execution.
     */
    @After(value = "searches()")
    public void afterAdvice() {
    }

    /**
     * This is the method that I would like to execute when any method returns.
     *
     * @param retVal the returning value.
     */
    @AfterReturning(pointcut = "searches()", returning = "retVal")
    public void afterReturningAdvice(Object retVal) {
        if (retVal != null) {
            log("Returning: '{}' ", retVal.toString());
        } else {
            log("Returning: 'void'.");
        }
    }

    /**
     * This is the method that I would like to execute if there is an exception
     * raised by any method.
     *
     * @param ex the exception
     */
    @AfterThrowing(pointcut = "searches()", throwing = "ex")
    public void afterThrowingAdvice(IllegalArgumentException ex) {
        log("There has been an exception: '{}' ", ex.getMessage());
    }

}
