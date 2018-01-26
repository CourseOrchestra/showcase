package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.jython.*;

/**
 * Загрузка файла с использованием Jython скрипта для компонента grid.
 * 
 * @author bogatov
 * 
 */
public class GridJythonDownloadHelper extends JythonQuery<JythonDownloadResult> {
	private static final String NO_DOWNLOAD_PROC_ERROR = "Не задана процедура скачивания файлов с сервера для linkId=";

	private final DataPanelElementInfo elementInfo;
	private final CompositeContext context;
	private final String jythonProcName;
	private final String recordId;

	protected GridJythonDownloadHelper(final CompositeContext aContext,
			final DataPanelElementInfo aElement, final ID aLinkId,
			final String aRecordId) {
		super(JythonDownloadResult.class);
		this.context = aContext;
		this.elementInfo = aElement;
		this.recordId = aRecordId;

		DataPanelElementProc proc = aElement.getProcs().get(aLinkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_DOWNLOAD_PROC_ERROR
					+ aLinkId);
		}
		this.jythonProcName = proc.getName();
	}

	@Override
	protected Object execute() {
		GridDownloadAttributes attr = new GridDownloadAttributes();
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

	private void setParameters(final GridDownloadAttributes aAttributes) {
		setGeneralParameters(aAttributes);
		aAttributes.setRecordId(recordId);
		aAttributes.setElementId(elementInfo.getId().getString());
	}
}
