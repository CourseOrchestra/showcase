package ru.curs.showcase.app.api.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.event.Event;
import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;
import ru.curs.showcase.app.api.grid.LyraGridAddData;
import ru.curs.showcase.app.api.selector.DataRequest;

/**
 * Асинхронный компаньон FakeService.
 * 
 * @author den
 * 
 */
public interface FakeServiceAsync {

	void serializeGeoMapExportSettings(GeoMapExportSettings settings,
			AsyncCallback<Void> callback);

	<E extends Event> void serializeEvents(AsyncCallback<List<E>> callback);

	void serializeUserMessage(AsyncCallback<UserMessage> callback);

	void serializeThrowable(AsyncCallback<Throwable> callback);

	void serializeLyraGridAddData(AsyncCallback<LyraGridAddData> callback);

	void serializeDataRequest(DataRequest req, AsyncCallback<Void> callback);

}
