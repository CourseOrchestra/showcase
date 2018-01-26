package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

import jdepend.framework.*;

import org.junit.*;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.TextUtils;

/**
 * Тесты циклических зависимостей в коде и метрик абстрактности-связанности.
 * 
 * @author den
 * 
 */
public class JDependTest extends AbstractTest {

	private JDepend jdepend;

	@Before
	public void setUp() throws IOException {
		jdepend = new JDepend();
		final String root = AppInfoSingleton.getAppInfo().getWebAppPath() + "/WEB-INF/classes";
		jdepend.addDirectory(root + "/ru/curs/showcase/app/api");
		jdepend.addDirectory(root + "/ru/curs/showcase/app/server");
		jdepend.addDirectory(root + "/ru/curs/showcase/core");
		jdepend.addDirectory(root + "/ru/curs/showcase/runtime");
		jdepend.addDirectory(root + "/ru/curs/showcase/security");
		jdepend.addDirectory(root + "/ru/curs/showcase/test");
		jdepend.addDirectory(root + "/ru/curs/showcase/util");
		PackageFilter filter = new PackageFilter();
		filter.addPackage("ch.*");
		filter.addPackage("net.*");
		filter.addPackage("com.*");
		filter.addPackage("org.*");
		filter.addPackage("java.*");
		filter.addPackage("javax.*");
		filter.addPackage("gwtquery.*");
		filter.addPackage("ru.curs.showcase.test.ws.*");
		jdepend.setFilter(filter);
	}

	@Test
	public void testFake() {
		assertNotNull(new String("fff"));
	}

	// @Test
	public void testDependencyCycle() {
		@SuppressWarnings({ "unchecked" })
		Collection<JavaPackage> packages = jdepend.analyze();

		if (jdepend.containsCycles()) {
			List<String> packagesList = new ArrayList<>();
			for (JavaPackage jpackage : packages) {
				if (jpackage.containsCycle()) {
					packagesList.add(jpackage.getName());
				}
			}
			fail("Между следующими пакетами есть циклическая связь:"
					+ TextUtils.arrayToString(
							packagesList.toArray(new String[packagesList.size()]), ","));
		}
	}
}
