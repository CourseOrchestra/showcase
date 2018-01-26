package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SizeEstimate;
import ru.curs.showcase.app.api.element.*;
import ru.curs.showcase.app.api.event.Event;

/**
 * Класс данных грида.
 * 
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GridData extends DataPanelCompBasedElement implements SizeEstimate {

	private static final long serialVersionUID = 3613871503044359445L;

	private String data = null;

	public String getData() {
		return data;
	}

	public void setData(final String aData) {
		data = aData;
	}

	@Override
	public long sizeEstimate() {
		if (data == null) {
			return 0;
		} else {
			return data.length();
		}
	}

	@Override
	protected EventManager<? extends Event> initEventManager() {
		return null;
	}

}
