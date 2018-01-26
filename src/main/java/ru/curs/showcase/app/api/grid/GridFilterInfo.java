package ru.curs.showcase.app.api.grid;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Информация о фильтре.
 * 
 */
@XmlRootElement(name = "gridFilterInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class GridFilterInfo implements SerializableElement {
	private static final long serialVersionUID = -1507629753857193351L;

	@XmlTransient
	private int maxId = 0;
	@XmlElement(name = "filters")
	private List<Filter> filters = new ArrayList<Filter>();

	public int getMaxId() {
		return maxId;
	}

	public void setMaxId(final int aMaxId) {
		maxId = aMaxId;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(final List<Filter> aFilters) {
		filters = aFilters;
	}

}
