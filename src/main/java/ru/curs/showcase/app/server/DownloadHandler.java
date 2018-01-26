package ru.curs.showcase.app.server;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.xform.XFormDownloadCommand;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Класс-обработчик на получение файла из БД с помощью хранимой процедуры.
 * Используется для скачивания файлов из XForms.
 * 
 * @author den
 * 
 */
public final class DownloadHandler extends AbstractDownloadHandler {
	/**
	 * Ссылка на файл.
	 */
	private ID linkId;

	@Override
	protected void processFiles() {
		XFormDownloadCommand command =
			new XFormDownloadCommand(getContext(), getElementInfo(), linkId);
		setOutputFile(command.execute());
	}

	@Override
	public XFormContext getContext() {
		return (XFormContext) super.getContext();
	}

	@Override
	protected Class<? extends CompositeContext> getContextClass() {
		return XFormContext.class;
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		super.getParams();
		linkId = new ID(getRequest().getParameter("linkId"));
	}

	public ID getLinkId() {
		return linkId;
	}

}
