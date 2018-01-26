package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.jython.*;

/**
 * Загрузка файла с использованием Jython скрипта для компонента XForm.
 * 
 * @author bogatov
 * 
 */
public class XFormJythonDownloadHelper extends
		JythonQuery<JythonDownloadResult> {
	private static final String NO_DOWNLOAD_PROC_ERROR = "Не задана процедура скачивания файлов c сервера для linkId=";

	private final DataPanelElementInfo elementInfo;
	private final XFormContext context;
	private final String jythonProcName;

	protected XFormJythonDownloadHelper(final XFormContext aContext,
			final DataPanelElementInfo aElement, final ID aLinkId) {
		super(JythonDownloadResult.class);
		this.context = aContext;
		this.elementInfo = aElement;

		DataPanelElementProc proc = aElement.getProcs().get(aLinkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_DOWNLOAD_PROC_ERROR
					+ aLinkId);
		}
		this.jythonProcName = proc.getName();
	}

	@Override
	protected Object execute() {
		XFormDownloadAttributes attr = new XFormDownloadAttributes();
		setParameters(attr);
		return getProc().getInputStream(context, attr);
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

	private void setParameters(final XFormDownloadAttributes aAttributes) {
		setGeneralParameters(aAttributes);
		aAttributes.setFormData(context.getFormData());
		aAttributes.setElementId(elementInfo.getId().getString());
	}
}
