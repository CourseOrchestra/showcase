package ru.curs.showcase.test.runtime;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.test.AbstractTest;

/**
 * Тест для универсальных идентификаторов.
 * 
 * @author den
 * 
 */
public class IDTest extends AbstractTest {

	private static final String TEST_ID3 = "TeSt";
	private static final String TEST_ID2 = "Test";
	private static final String TEST_ID1 = "test";

	@Test
	public void testCI() {
		ID id1 = new ID(TEST_ID1);
		ID id2 = new ID(TEST_ID2);
		assertEquals(id1, id2);
	}

	@Test
	public void testCS() {
		IDSettings.getInstance().setCaseSensivity(true);
		ID id1 = new ID(TEST_ID1);
		ID id2 = new ID(TEST_ID2);
		assertNotSame(id1, id2);
	}

	@Test
	public void testCIInMap() {
		Map<ID, CompositeContext> map = createSimpleMap();
		assertEquals(1, map.size());
		assertTrue(map.containsKey(new ID(TEST_ID3)));
	}

	private Map<ID, CompositeContext> createSimpleMap() {
		Map<ID, CompositeContext> map = new TreeMap<>();
		ID id1 = new ID(TEST_ID1);
		ID id2 = new ID(TEST_ID2);
		map.put(id1, new CompositeContext());
		map.put(id2, null);
		return map;
	}

	@Test
	public void testCSInMap() {
		IDSettings.getInstance().setCaseSensivity(true);
		Map<ID, CompositeContext> map = createSimpleMap();
		assertEquals(2, map.size());
		assertFalse(map.containsKey(new ID(TEST_ID3)));
	}

	private Set<ID> createSimpleHash() {
		Set<ID> set = new HashSet<>();
		ID id1 = new ID(TEST_ID1);
		ID id2 = new ID(TEST_ID2);
		set.add(id1);
		set.add(id2);
		return set;
	}

	private Set<ID> createSimpleTreeSet() {
		Set<ID> set = new TreeSet<>();
		ID id1 = new ID(TEST_ID1);
		ID id2 = new ID(TEST_ID2);
		set.add(id1);
		set.add(id2);
		return set;
	}

	@Test
	public void testCIHashSet() {
		Set<ID> set = createSimpleHash();
		assertEquals(1, set.size());
		set.remove(new ID(TEST_ID1));
		assertEquals(0, set.size());
	}

	@Test
	public void testCSHashSet() {
		IDSettings.getInstance().setCaseSensivity(true);
		Set<ID> set = createSimpleHash();
		assertEquals(2, set.size());
		set.remove(new ID(TEST_ID3));
		assertEquals(2, set.size());
	}

	@Test
	public void testCITreeSet() {
		Set<ID> set = createSimpleTreeSet();
		assertEquals(1, set.size());
		set.remove(new ID(TEST_ID3));
		assertEquals(0, set.size());
	}

	@Test
	public void testCSTreeSet() {
		IDSettings.getInstance().setCaseSensivity(true);
		Set<ID> set = createSimpleTreeSet();
		assertEquals(2, set.size());
	}

}
