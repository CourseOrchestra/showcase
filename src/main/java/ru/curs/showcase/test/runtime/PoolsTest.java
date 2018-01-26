package ru.curs.showcase.test.runtime;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.junit.*;
import org.python.util.PythonInterpreter;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Модуль для тестирования пулов: соединений, трансформаций...
 * 
 * @author den
 * 
 */
public class PoolsTest extends AbstractTestWithDefaultUserData {

	private static final String BAL_XSL = "bal.xsl";
	private static final String PAS_XSL = "pas.xsl";

	@Test
	public void xslPoolShouldKeepTransformationForReuse()
			throws TransformerConfigurationException, IOException {
		checkPool(XSLTransformerPoolFactory.getInstance());
	}

	@Test
	public void xslPoolShouldReturnGoodTransformer() throws IOException, TransformerException {
		XSLTransformerPoolFactory factory = XSLTransformerPoolFactory.getInstance();
		Transformer transformer = factory.acquire();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (InputStream is = UserDataUtils.loadUserDataToStream("user.messages.xml")) {
				transformer.transform(new StreamSource(is), new StreamResult(baos));
			}
		} finally {
			factory.release(transformer);
		}
	}

	@Test
	public void xsltFor2TransformFilesShouldBeDifferent()
			throws TransformerConfigurationException, IOException {
		XSLTransformerPoolFactory factory = XSLTransformerPoolFactory.getInstance();
		factory.clear();
		Transformer xf = factory.acquire(PAS_XSL);
		factory.release(xf, PAS_XSL);
		Transformer xf2 = factory.acquire(BAL_XSL);
		assertNotSame(xf, xf2);
		factory.release(xf2, BAL_XSL);
		assertEquals(2, factory.getAllCount());
		Transformer xf3 = factory.acquire(PAS_XSL);
		assertEquals(xf, xf3);
		Transformer xf4 = factory.acquire(BAL_XSL);
		assertEquals(xf2, xf4);
		factory.release(xf3, PAS_XSL);
		factory.release(xf4, BAL_XSL);
	}

	@Test(expected = IOException.class)
	public void xslPoolShouldRaiseExceptionForWrongTransformFile()
			throws TransformerConfigurationException, IOException {
		XSLTransformerPoolFactory factory = XSLTransformerPoolFactory.getInstance();
		factory.acquire("fake_trans.xsl");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkPool(final Pool pool) throws TransformerConfigurationException, IOException {
		assertNotNull(pool);
		pool.clear();
		assertEquals(0, pool.getAllCount());
		Object tr1 = pool.acquire();
		Object tr2 = pool.acquire();
		assertNotNull(tr1);
		assertNotNull(tr2);
		assertNotSame(tr1, tr2);
		assertEquals(0, pool.getAllCount());
		pool.release(tr1);
		assertEquals(1, pool.getAllCount());
		pool.release(tr2);
		assertEquals(2, pool.getAllCount());
		Object tr3 = pool.acquire();
		assertEquals(tr1, tr3);
		Object tr4 = pool.acquire();
		assertEquals(tr2, tr4);
		Object tr5 = pool.acquire();
		assertNotSame(tr1, tr5);
	}

	@Test
	@Ignore
	// !!!
	public void jythonPoolShouldKeepInterpretersForReuse()
			throws TransformerConfigurationException, IOException {
		checkPool(JythonIterpretatorFactory.getInstance());
	}

	@Test
	@Ignore
	// !!!
	public void jythonInterprepersFor2UserdatasShouldBeDifferent()
			throws TransformerConfigurationException, IOException {
		checkUserdatasPool(JythonIterpretatorFactory.getInstance());
	}

	@SuppressWarnings("unchecked")
	private void checkUserdatasPool(@SuppressWarnings("rawtypes") final Pool pool)
			throws TransformerConfigurationException, IOException {
		Object pi = pool.acquire();
		pool.release(pi);
		AppInfoSingleton.getAppInfo().setCurUserDataId(TEST1_USERDATA);
		Object pi2 = pool.acquire();
		assertNotSame(pi, pi2);
		pool.release(pi2);
	}

	@Test
	public void dbConnectionPoolShouldKeepConnectionsForReuse()
			throws TransformerConfigurationException, IOException {
		checkPool(ConnectionFactory.getInstance());
	}

	// !!! @Test
	public void dbConnectionsFor2UserdatasShouldBeDifferent()
			throws TransformerConfigurationException, IOException {
		checkUserdatasPool(ConnectionFactory.getInstance());
	}

	@Test
	public void closedConnnectionShouldntMakeErrors() throws TransformerConfigurationException,
			IOException, SQLException {
		Pool<Connection> pool = ConnectionFactory.getInstance();
		Connection conn = pool.acquire();
		assertFalse(conn.isClosed());
		assertTrue(conn.isValid(0));

		conn.close();
		assertTrue(conn.isClosed());
		assertFalse(conn.isValid(0));
		pool.release(conn);

		Connection conn2 = pool.acquire();
		assertNotSame(conn, conn2);
		assertFalse(conn2.isClosed());
		assertTrue(conn2.isValid(0));
		pool.release(conn2);
	}

	@Test
	public void jdbcPoolShouldReturnGoodConnection() throws TransformerConfigurationException,
			IOException, SQLException {
		Pool<Connection> pool = ConnectionFactory.getInstance();
		Connection conn = pool.acquire();
		conn.createStatement().execute("select 1");
		pool.release(conn);
	}

	@Test
	@Ignore
	// !!!
	public void jythonPoolShouldReturnGoodInterpreper() throws TransformerConfigurationException,
			IOException {
		Pool<PythonInterpreter> pool = JythonIterpretatorFactory.getInstance();
		PythonInterpreter interpreter = pool.acquire();
		interpreter.eval("2+3");
		pool.release(interpreter);
	}
}
