package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import ru.curs.showcase.app.api.CanBeCurrent;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.event.ActivityDBGateway;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.*;

/**
 * Тесты для ReflectionUtils.
 * 
 * @author den
 * 
 */
public class ReflectionUtilsTest extends AbstractTest {

	@Test
	public void testEquals() throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		assertTrue(ReflectionUtils.equals(CompositeContext.createCurrent(),
				CompositeContext.createCurrent()));
		assertFalse(ReflectionUtils.equals(null, ""));
		assertFalse(ReflectionUtils.equals(new CompositeContext(), new XFormContext()));
	}

	@Test
	public void testGetPropValueForField() throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException {
		CompositeContext context = CompositeContext.createCurrent();
		assertEquals(
				CanBeCurrent.CURRENT_ID,
				ReflectionUtils.getPropValueForField(context,
						context.getClass().getDeclaredField("main")));
		assertEquals(CanBeCurrent.CURRENT_ID,
				ReflectionUtils.getPropValueByFieldName(context, "main"));
		assertNull(ReflectionUtils.getPropValueByFieldName(context, "filter"));
	}

	@Test(expected = NoSuchMethodException.class)
	public void testGetPropValueForFieldError() throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		ReflectionUtils.getPropValueByFieldName(new CompositeContext(), "fakeProp");
	}

	/**
	 * Класс для тестов ReflectionUtils.getProcessDescForClass.
	 * 
	 * @author den
	 * 
	 */
	@Description(process = TestClass.TEST_PROCESS)
	private final class TestClass {
		static final String TEST_PROCESS = "Скрытый процесс";

		private TestClass() {
			super();
		}
	}

	@Test
	public void testGetProcessDescForClass() {
		assertEquals("Вызов хранимых процедур на сервере SQL",
				ReflectionUtils.getProcessDescForClass(ActivityDBGateway.class));
		assertEquals("не задан", ReflectionUtils.getProcessDescForClass(ReflectionUtilsTest.class));
		assertEquals(TestClass.TEST_PROCESS,
				ReflectionUtils.getProcessDescForClass(TestClass.class));
	}
}
