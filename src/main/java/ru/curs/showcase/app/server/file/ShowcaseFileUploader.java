package ru.curs.showcase.app.server.file;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.python.core.PyObject;
import org.slf4j.*;

import ru.curs.celesta.CelestaException;
import ru.curs.showcase.core.jython.JythonDTO;
import ru.curs.showcase.runtime.*;

public class ShowcaseFileUploader extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseFileUploader.class);

	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		InputStream is = request.getInputStream();
		// byte[] buffer = IOUtils.toByteArray(is);
		// FileOutputStream fs = new FileOutputStream("F:\\zaprosy.txt");
		// byte[] tempbuffer = new byte[4096];
		// int bytesRead;
		// while ((bytesRead = is.read(tempbuffer)) != -1) {
		// fs.write(tempbuffer, 0, bytesRead);
		// }
		// is.close();
		// fs.close();
		//
		// if (1 == 1) {
		// return;
		// }

		JythonDTO responseData = null;

		String procName = UserDataUtils.getGeneralOptionalProp("file.upload.proc").trim();

		if (procName.endsWith(".cl") || procName.endsWith(".celesta")) {
			final int i3 = 3;
			final int i8 = 8;
			if (procName.endsWith(".cl")) {
				procName = procName.substring(0, procName.length() - i3);

			}
			if (procName.endsWith(".celesta")) {
				procName = procName.substring(0, procName.length() - i8);
			}
		}

		try {
			PyObject pObj =
				AppInfoSingleton.getAppInfo().getCelestaInstance()
						.runPython(request.getSession().getId(), procName, is);

			Object obj = pObj.__tojava__(Object.class);
			if (obj == null) {
				return;
			}
			if (obj.getClass().isAssignableFrom(JythonDTO.class)) {
				responseData = (JythonDTO) obj;
				if (responseData.getData() != null)
					response.getWriter().print(
							"<script>alert('" + responseData.getData() + "');</script>");

			}

		} catch (CelestaException e) {
			if (e.getMessage().contains("Session") & e.getMessage().contains("is not logged in")) {
				LOGGER.error("При запуске процедуры Celesta для загрузки файла на сервер произошла ошибка: "
						+ e.getMessage());
				throw new RuntimeException(
						"При запуске процедуры Celesta для загрузки файла на сервер произошла ошибка: "
								+ e.getMessage());
			}

			LOGGER.error("При запуске процедуры Celesta для загрузки файла на сервер произошла ошибка: "
					+ e.getMessage());
			throw new RuntimeException(
					"При запуске процедуры Celesta для загрузки файла на сервер произошла ошибка: "
							+ e.getMessage());

		}

	}
}
