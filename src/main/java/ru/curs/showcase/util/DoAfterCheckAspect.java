package ru.curs.showcase.util;

import java.lang.reflect.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Аспект для подключения пост-проверки к методу, возвращающему boolean, с
 * помощью аннотации DoAfterCheck.
 * 
 * @author den
 * 
 */
@Aspect
public class DoAfterCheckAspect {

	@SuppressWarnings("unused")
	@Pointcut("execution(public boolean ru.curs.showcase..*()) && @annotation(annotation)")
	private void doAfterCheckCondition(final DoAfterCheck annotation) {
	};

	@Around("doAfterCheckCondition(annotation) && !cflow(adviceexecution())")
	public boolean check(final DoAfterCheck annotation, final ProceedingJoinPoint jp)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		Method method = ((MethodSignature) jp.getSignature()).getMethod();
		if ((boolean) method.invoke(jp.getTarget())) {
			return true;
		}
		return (Boolean) Class.forName(annotation.className())
				.getDeclaredMethod(annotation.methodName(), Object.class)
				.invoke(null, jp.getTarget());
	}
}
