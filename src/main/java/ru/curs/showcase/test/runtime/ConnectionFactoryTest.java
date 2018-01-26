package ru.curs.showcase.test.runtime;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Test;

import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

import com.ziclix.python.sql.PyConnection;

/**
 * JUnit тест фабрики соединений с БД.
 * 
 * @author bogatov
 * 
 */
public class ConnectionFactoryTest extends AbstractTestWithDefaultUserData {

	/**
	 * Тест получения соединения.
	 */
	@Test
	public void connectionTest() {
		ConnectionFactory cf = ConnectionFactory.getInstance();

		cf.clear();

		Connection conn = cf.acquire();
		try {
			assertNotNull(conn);
		} finally {
			cf.release(conn);
		}
		assertEquals(1, cf.getAllCount());
	}

	/**
	 * Тест получения соединения для использования в Jython.
	 */
	@Test
	public void pyConnectionTest() {
		PyConnection conn = ConnectionFactory.getPyConnection();
		try {
			assertNotNull(conn);
		} finally {
			conn.close();
		}
	}
}
