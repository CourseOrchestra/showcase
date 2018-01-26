package ru.curs.showcase.app.api.grid;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Условие фильтра.
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class Filter implements SerializableElement {
	private static final long serialVersionUID = -1269907873174787667L;

	private String id = null;
	private String link = null;
	private String column = null;
	private String condition = null;
	private String value = null;
	private Date dateValue = null;
	private List<String> listOfValues = new ArrayList<String>();
	private List<String> listOfValuesId = new ArrayList<String>();

	public Filter() {

	}

	public Filter(final Filter src) {
		id = src.id;
		link = src.link;
		column = src.column;
		condition = src.condition;
		value = src.value;
		dateValue = src.dateValue;
		listOfValues.clear();
		listOfValues.addAll(src.getListOfValues());
		listOfValuesId.clear();
		listOfValuesId.addAll(src.getListOfValuesId());
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getLink() {
		return link;
	}

	public void setLink(final String aLink) {
		link = aLink;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(final String aColumn) {
		column = aColumn;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(final String aCondition) {
		condition = aCondition;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String aValue) {
		value = aValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(final Date aDateValue) {
		dateValue = aDateValue;
	}

	public List<String> getListOfValues() {
		return listOfValues;
	}

	public void setListOfValues(final List<String> aListOfValues) {
		listOfValues = aListOfValues;
	}

	public List<String> getListOfValuesId() {
		return listOfValuesId;
	}

	public void setListOfValuesId(final List<String> aListOfValuesId) {
		listOfValuesId = aListOfValuesId;
	}

}
