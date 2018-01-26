package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.aspectj.lang.annotation.*;
import org.slf4j.*;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

import ru.curs.showcase.app.api.BrowserType;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Перехватчик для исключений при вызове сервлетов.
 * 
 * @author den
 * 
 */
@Aspect
public final class ServletExceptionInterceptor {

	private static final String ERROR_MES = "Сообщение об ошибке";
	private static final Logger LOGGER =
		LoggerFactory.getLogger(ServletExceptionInterceptor.class);

	@Pointcut("args(request, response) && execution(public void javax.servlet.http.HttpServlet.do*(..))")
	private void servletExecutionPointcut(final HttpServletRequest request,
			final HttpServletResponse response) {
	};

	@Before("servletExecutionPointcut(request, response)")
	public void logInput(final HttpServletRequest request, final HttpServletResponse response) {
	}

	@AfterThrowing(pointcut = "servletExecutionPointcut(request, response)", throwing = "e")
	public void logException(final HttpServletRequest request, final HttpServletResponse response,
			final Throwable e) throws IOException {
		Throwable exc = e;
		if ((exc instanceof ServletException) && (exc.getCause() != null)) {
			exc = exc.getCause();
		}
		if (!(exc instanceof GeneralException) && !(exc instanceof BaseException)) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(ERROR_MES, exc);
			}
		}

		String mess = null;
		if (request.getRequestURL().toString().toLowerCase()
				.contains("/secured/upload".toLowerCase())
				|| request.getRequestURL().toString().toLowerCase()
						.contains("/secured/submit".toLowerCase())
				|| request.getRequestURL().toString().toLowerCase()
						.contains("/secured/sqlTransform".toLowerCase())
				|| request.getRequestURL().toString().toLowerCase()
						.contains("/secured/jythonTransform".toLowerCase())
				|| request.getRequestURL().toString().toLowerCase()
						.contains("/secured/xslttransformer".toLowerCase())
				|| request.getRequestURL().toString().toLowerCase()
						.contains("/secured/xslTransform".toLowerCase())
				|| request.getRequestURL().toString().toLowerCase()
						.contains("/secured/JSGridService".toLowerCase())
				|| request.getRequestURL().toString().toLowerCase()
						.contains("/secured/JSSelectorService".toLowerCase())
				|| request.getRequestURL().toString().toLowerCase()
						.contains("/secured/JSTreeSelectorService".toLowerCase())
				|| request.getRequestURL().toString().toLowerCase()
						.contains("/secured/JSLyraGridService".toLowerCase())) {

			try {
				mess = RPC.encodeResponseForSuccess(
						FakeService.class.getMethod("serializeThrowable"), exc);
			} catch (NoSuchMethodException | SecurityException | SerializationException e1) {
				throw GeneralExceptionFactory.build(e1);
			}

		} else {

			mess = exc.getLocalizedMessage();

		}

		String userAgent = ServletUtils.getUserAgent(request);
		BrowserType browserType = BrowserType.detect(userAgent);
		if ((browserType == BrowserType.CHROME) || (browserType == BrowserType.FIREFOX)
				|| (browserType == BrowserType.IE)) {
			mess = "<root>" + mess + "</root>";
		}

		boolean needOKStatus = false;
		if ((browserType == BrowserType.IE) && request.getRequestURL().toString().toLowerCase()
				.contains("gridFileDownload".toLowerCase())) {
			needOKStatus = true;
		}

		ServletUtils.fillErrorResponce(response, mess, needOKStatus);

	}
}
