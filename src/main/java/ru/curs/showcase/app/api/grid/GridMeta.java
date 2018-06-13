package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SizeEstimate;
import ru.curs.showcase.app.api.element.*;
import ru.curs.showcase.app.api.event.Event;

/**
 * Класс метаданных грида.
 * 
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GridMeta extends DataPanelCompBasedElement implements SizeEstimate {
	private static final long serialVersionUID = -5466048956325093943L;

	private String metadata = null;

	public String getMeta() {
		return metadata;
	}

	public void setMeta(final String aMetadata) {
		metadata = aMetadata;
	}

	@Override
	public long sizeEstimate() {
		if (metadata == null) {
			return 0;
		} else {
			return metadata.length();
		}
	}

	@Override
	protected EventManager<? extends Event> initEventManager() {
		return null;
	}

}
