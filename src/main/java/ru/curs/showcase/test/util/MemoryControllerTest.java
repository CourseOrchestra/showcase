package ru.curs.showcase.test.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ru.curs.showcase.runtime.MemoryController;
import ru.curs.showcase.test.AbstractTest;

/**
 * Тесты для MemoryController.
 * 
 * @author den
 * 
 */
public class MemoryControllerTest extends AbstractTest {

	@Test
	public void testAll() {
		assertNotNull(MemoryController.getAllFreeHeap());
		assertNotNull(MemoryController.getCommitedHeap());
		assertNotNull(MemoryController.getFreeInCommitedHeap());
		assertNotNull(MemoryController.getMaxHeap());
		assertNotNull(MemoryController.getUsedHeap());

		assertNotNull(MemoryController.getAllFreePermGen());
		assertNotNull(MemoryController.getCommitedPermGen());
		assertNotNull(MemoryController.getFreeInCommitedPermGen());
		assertNotNull(MemoryController.getInitPermGen());
		assertNotNull(MemoryController.getMaxPermGen());
		assertNotNull(MemoryController.getUsedPermGen());
	}

}
