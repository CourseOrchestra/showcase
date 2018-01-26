package ru.curs.showcase.app.api.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.event.Event;
import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;
import ru.curs.showcase.app.api.grid.LyraGridAddData;
import ru.curs.showcase.app.api.selector.DataRequest;

/**
 * Содержит fake функции для того, чтобы заработали "ручные вызовы" сериализации
 * GWT. Не удалять!.
 */
public interface FakeService extends RemoteService {

	void serializeGeoMapExportSettings(GeoMapExportSettings settings);

	<E extends Event> List<E> serializeEvents();

	UserMessage serializeUserMessage();

	Throwable serializeThrowable();

	LyraGridAddData serializeLyraGridAddData();

	void serializeDataRequest(DataRequest req);

}
