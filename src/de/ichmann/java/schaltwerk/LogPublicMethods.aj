/*****************************************************************************
 * Schaltwerk - A free and extensible digital simulator
 * Copyright (c) 2013 Christian Wichmann
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *****************************************************************************/
package de.ichmann.java.schaltwerk;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect to log all calls of public methods in entire project.
 * 
 * @author Christian Wichmann
 */
public aspect LogPublicMethods {

	private boolean logMethodCalls = false;

	// @Pointcut("(execution(public * *(..)))")
	// public void publicMethodExecuted() { };

	pointcut publicMethodExecuted(): execution(public * *(..));

	@Before("publicMethodExecuted()")
	public void logBeginPublicMethod(final JoinPoint joinPoint) {

		if (logMethodCalls) {
			Logger LOG = LoggerFactory.getLogger(joinPoint.getSignature()
					.getDeclaringType());
			LOG.debug("Entering: " + joinPoint);
		}
	}

	@After("publicMethodExecuted()")
	public void logEndPublicMethod(final JoinPoint joinPoint) {

		if (logMethodCalls) {
			Logger LOG = LoggerFactory.getLogger(joinPoint.getSignature()
					.getDeclaringType());
			LOG.debug("Leaving: " + joinPoint);
		}
	}

	/*
	 * Possible would be also to use @Around("publicMethodExecuted()") but then
	 * method thisJoinPoint.proceed() had to be called midway to actually call
	 * surrounded method!
	 */
}
