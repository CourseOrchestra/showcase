package ru.curs.showcase.core.html.xform;

import java.util.UUID;

import ru.curs.showcase.app.api.datapanel.*;

/**
 * Фабрика для создания описания элементов XForms для особых случаев.
 * 
 * @author den
 * 
 */
public final class XFormInfoFactory {

	private XFormInfoFactory() {
		throw new UnsupportedOperationException();
	}

	/**
	 * TODO Временная процедура для генерации информации об обрабатываемом sql
	 * submisssion. В будущем при вызове submisssion должен передаваться
	 * идентификатор элемента.
	 * 
	 */
	public static DataPanelElementInfo generateXFormsSQLSubmissionInfo(final String aProcName) {
		DataPanelElementInfo result = new DataPanelElementInfo();
		result.setId(UUID.randomUUID().toString());
		result.setType(DataPanelElementType.XFORMS);
		result.setProcName(aProcName);
		result.setTemplateName("fake");
		return result;
	}

	/**
	 * TODO Временная процедура для генерации информации об обрабатываемом xslt
	 * submisssion. В будущем при вызове submisssion должен передаваться
	 * идентификатор элемента.
	 * 
	 */
	public static DataPanelElementInfo generateXFormsTransformationInfo(final String aXsltFile) {
		DataPanelElementInfo result = new DataPanelElementInfo();
		result.setId(UUID.randomUUID().toString());
		result.setType(DataPanelElementType.XFORMS);
		result.setTransformName(aXsltFile);
		result.setTemplateName("fake");
		return result;
	}

}
