package ru.curs.showcase.app.server;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.LyraGridContext;
import ru.curs.showcase.core.grid.LyraVueGridExcelExportCommand;

/**
 * Обработчик запроса на получение Excel файла по данным в лира-гриде.
 * Вызывается из сервлета. Отдельный класс (и один объект на один запрос) нужны
 * для того, чтобы избежать проблем многопоточности.
 * 
 */
public class LyraVueGridToExcelHandler extends GridToExcelHandler {

	@Override
	public LyraGridContext getContext() {
		return (LyraGridContext) super.getContext();
	}

	@Override
	protected Class<? extends CompositeContext> getContextClass() {
		return LyraGridContext.class;
	}

	@Override
	protected void processFiles() {
		LyraVueGridExcelExportCommand command =
			new LyraVueGridExcelExportCommand(getContext(), getElementInfo(), getExportType());
		setOutputFile(command.execute());
	}

}