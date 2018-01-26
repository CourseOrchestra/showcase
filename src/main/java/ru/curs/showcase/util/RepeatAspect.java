package ru.curs.showcase.util;

import java.lang.reflect.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.*;

import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Аспект для @Repeat в модульных тестах.
 * 
 * @author den
 * 
 */
@Aspect
public final class RepeatAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(RepeatAspect.class);

	@SuppressWarnings("unused")
	@Pointcut("execution(@ru.curs.showcase.util.Repeat public void ru.curs.showcase..*())")
	private void repeatPointcut() {
	};

	@Around("repeatPointcut() && !cflow(adviceexecution())")
	public void repeat(final ProceedingJoinPoint jp) throws IllegalAccessException,
			InvocationTargetException {
		Method method = ((MethodSignature) jp.getSignature()).getMethod();

		Repeat rmAnnotation = method.getAnnotation(Repeat.class);
		int count = rmAnnotation.count();
		for (int i = 1; i <= count; i++) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
				LOGGER.trace("Проход " + i);
			}
			method.invoke(jp.getTarget());
		}

	}
}
