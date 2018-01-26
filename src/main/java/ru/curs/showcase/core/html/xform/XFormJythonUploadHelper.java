package ru.curs.showcase.core.html.xform;

import java.io.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.util.*;

/**
 * Загрузка файла с использованием Jython скрипта для компонента XForm.
 * 
 * @author bogatov
 * 
 */
public class XFormJythonUploadHelper extends JythonQuery<JythonErrorResult> {
	private static final String NO_UPLOAD_PROC_ERROR = "Не задана процедура загрузки файлов на сервер для linkId=";

	private final DataPanelElementInfo elementInfo;
	private final XFormContext context;
	private final String jythonProcName;
	private final DataFile<InputStream> file;

	protected XFormJythonUploadHelper(final XFormContext aContext,
			final DataPanelElementInfo aElement, final ID aLinkId,
			final DataFile<InputStream> aFile) throws IOException {
		super(JythonErrorResult.class);
		this.context = aContext;
		this.elementInfo = aElement;
		this.file = aFile;

		DataPanelElementProc proc = aElement.getProcs().get(aLinkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_UPLOAD_PROC_ERROR + aLinkId);
		}
		this.jythonProcName = proc.getName();

		StreamConvertor sc = new StreamConvertor(aFile.getData());
		file.setData(sc.getCopy());
	}

	@Override
	protected Object execute() {
		XFormUploadAttributes attr = new XFormUploadAttributes();
		setParameters(attr);
		return getProc().doUpload(context, attr);
	}

	@Override
	protected String getJythonProcName() {
		return this.jythonProcName;
	}

	private void setGeneralParameters(final InputAttributes aAttributes) {
		aAttributes.setAddContext(context.getAdditional());
		aAttributes.setFilterinfo(context.getFilter());
		aAttributes.setMainContext(context.getMain());
		aAttributes.setSessionContext(context.getSession());
	}

	private void setDawnloadParameters(final XFormDownloadAttributes aAttributes) {
		setGeneralParameters(aAttributes);
		aAttributes.setFormData(context.getFormData());
		aAttributes.setElementId(elementInfo.getId().getString());
	}

	private void setParameters(final XFormUploadAttributes aAttributes) {
		setDawnloadParameters(aAttributes);
		aAttributes.setFilename(file.getName());
		aAttributes.setFile(file.getData());
	}
}
