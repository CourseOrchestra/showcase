package ru.curs.showcase.core.grid.toolbar;

import java.io.InputStream;
import java.util.Stack;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.toolbar.*;
import ru.curs.showcase.core.event.ActionFactory;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика создания панели инструментов грида.
 * 
 * @author bogatov
 * 
 */
public class GridToolBarFactory {
	private static final String GRID_TOOLBAR_TAG = "gridtoolbar";
	private static final String GRID_TOOLBAR_ITEM_TAG = "item";
	private static final String GRID_TOOLBAR_ITEM_TEXT_ATTR = "text";
	private static final String GRID_TOOLBAR_ITEM_IMG_ATTR = "img";
	private static final String GRID_TOOLBAR_ITEM_DISABLE_ATTR = "disable";
	private static final String GRID_TOOLBAR_ITEM_VISIBLE_ATTR = "visible";
	private static final String GRID_TOOLBAR_ITEM_HINT_ATTR = "hint";
	private static final String GRID_TOOLBAR_STYLE_ATTR = "style";
	private static final String GRID_TOOLBAR_CLASSNAME_ATTR = "className";
	private static final String GRID_TOOLBAR_ITEM_ICONCLASSNAME_ATTR = "iconClassName";
	private static final String GRID_TOOLBAR_ITEM_ID_ATTR = "id";
	private static final String GRID_TOOLBAR_ITEM_DOWNLOAD_LINK_ID_ATTR = "downloadLinkId";
	private static final String GRID_TOOLBAR_ITEM_POPUPTEXT_ATTR = "popupText";
	private static final String GRID_TOOLBAR_ITEM_FILENAME_ATTR = "fileName";
	private static final String GRID_TOOLBAR_GROUP_TAG = "group";
	private static final String GRID_TOOLBAR_SEPARATOR_TAG = "separator";

	private static final String SAX_ERROR_MES = "XML-датасет панели инструментов грида";

	/**
	 * Handler разбора xml панели инструментов грида.
	 * 
	 */
	private final class XmlHandler extends DefaultHandler {

		private final ActionFactory actionFactory = new ActionFactory(context);

		private GridToolBar gridToolBar;
		private ToolBarItem curItemToolBar;
		private StringBuilder cutValueTag;
		private final Stack<ToolBarGroup> toolBarGroupStack = new Stack<ToolBarGroup>();

		private XmlHandler() {
		}

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes attr) throws SAXException {
			if (GRID_TOOLBAR_TAG.equalsIgnoreCase(name)) {
				this.gridToolBar = new GridToolBar();

				if (attr.getValue(GRID_TOOLBAR_STYLE_ATTR) != null) {
					this.gridToolBar.setStyle(attr.getValue(GRID_TOOLBAR_STYLE_ATTR));
				}
				if (attr.getValue(GRID_TOOLBAR_CLASSNAME_ATTR) != null) {
					this.gridToolBar.setClassName(attr.getValue(GRID_TOOLBAR_CLASSNAME_ATTR));
				}

			} else if (GRID_TOOLBAR_ITEM_TAG.equalsIgnoreCase(name)) {
				ToolBarItem itemToolBar = new ToolBarItem();
				fillBaseItemByAttr(itemToolBar, attr);

				if (attr.getValue(GRID_TOOLBAR_ITEM_DOWNLOAD_LINK_ID_ATTR) != null) {
					itemToolBar.setDownloadLinkId(
							attr.getValue(GRID_TOOLBAR_ITEM_DOWNLOAD_LINK_ID_ATTR));
				}

				if (attr.getValue(GRID_TOOLBAR_ITEM_FILENAME_ATTR) != null) {
					itemToolBar.setFileName(attr.getValue(GRID_TOOLBAR_ITEM_FILENAME_ATTR));
				}

				addItemToolBar(itemToolBar);
				this.curItemToolBar = itemToolBar;
			} else if (GRID_TOOLBAR_GROUP_TAG.equalsIgnoreCase(name)) {
				ToolBarGroup groupToolBar = new ToolBarGroup();
				fillBaseItemByAttr(groupToolBar, attr);
				addItemToolBar(groupToolBar);
				toolBarGroupStack.push(groupToolBar);
			} else if (GRID_TOOLBAR_SEPARATOR_TAG.equalsIgnoreCase(name)) {
				ToolBarSeparator separatorToolBar = new ToolBarSeparator();
				addItemToolBar(separatorToolBar);
			} else if (actionFactory.canHandleStartTag(name)) {
				Action action = actionFactory.handleStartTag(uri, localName, name, attr);
				this.curItemToolBar.setAction(action);
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length)
				throws SAXException {
			if (this.cutValueTag != null) {
				this.cutValueTag.append(ch, start, length);
			}
			actionFactory.handleCharacters(ch, start, length);
		}

		@Override
		public void endElement(final String uri, final String localName, final String name)
				throws SAXException {
			if (GRID_TOOLBAR_ITEM_TAG.equalsIgnoreCase(name)) {
				this.curItemToolBar = null;
			} else if (GRID_TOOLBAR_GROUP_TAG.equalsIgnoreCase(name)) {
				if (!toolBarGroupStack.isEmpty()) {
					toolBarGroupStack.pop();
				}
			} else if (actionFactory.canHandleEndTag(name)) {
				Action action = actionFactory.handleEndTag(uri, localName, name);
				this.curItemToolBar.setAction(action);
			}
		}

		public GridToolBar getGridToolBar() {
			return gridToolBar;
		}

		private void fillBaseItemByAttr(final BaseToolBarItem itemToolBar, final Attributes attr) {
			if (attr.getValue(GRID_TOOLBAR_ITEM_TEXT_ATTR) != null) {
				// Перевод с помощью Gettext
				itemToolBar.setText(
						UserDataUtils.modifyVariables(attr.getValue(GRID_TOOLBAR_ITEM_TEXT_ATTR)));
			}
			String img = attr.getValue(GRID_TOOLBAR_ITEM_IMG_ATTR);
			if (img != null && !img.isEmpty()) {
				img = String.format("%s/%s",
						UserDataUtils.getRequiredProp(UserDataUtils.IMAGES_IN_GRID_DIR), img);
			}
			itemToolBar.setImg(img);
			itemToolBar.setDisable(
					cast(Boolean.class, attr.getValue(GRID_TOOLBAR_ITEM_DISABLE_ATTR)));
			itemToolBar.setVisible(
					cast(Boolean.class, attr.getValue(GRID_TOOLBAR_ITEM_VISIBLE_ATTR)));
			if (attr.getValue(GRID_TOOLBAR_ITEM_HINT_ATTR) != null) {
				// Перевод с помощью Gettext
				itemToolBar.setHint(
						UserDataUtils.modifyVariables(attr.getValue(GRID_TOOLBAR_ITEM_HINT_ATTR)));
			}
			if (attr.getValue(GRID_TOOLBAR_STYLE_ATTR) != null) {
				itemToolBar.setStyle(attr.getValue(GRID_TOOLBAR_STYLE_ATTR));
			}
			if (attr.getValue(GRID_TOOLBAR_CLASSNAME_ATTR) != null) {
				itemToolBar.setClassName(attr.getValue(GRID_TOOLBAR_CLASSNAME_ATTR));
			}
			if (attr.getValue(GRID_TOOLBAR_ITEM_ICONCLASSNAME_ATTR) != null) {
				itemToolBar.setIconClassName(attr.getValue(GRID_TOOLBAR_ITEM_ICONCLASSNAME_ATTR));
			}
			if (attr.getValue(GRID_TOOLBAR_ITEM_ID_ATTR) != null) {
				itemToolBar.setId(attr.getValue(GRID_TOOLBAR_ITEM_ID_ATTR));
			}
			if (attr.getValue(GRID_TOOLBAR_ITEM_POPUPTEXT_ATTR) != null) {
				itemToolBar.setPopupText(attr.getValue(GRID_TOOLBAR_ITEM_POPUPTEXT_ATTR));
			}
		}

		@SuppressWarnings("unchecked")
		private <T> T cast(final Class<T> type, final String value) {
			if (value != null) {
				if (String.class.isAssignableFrom(type)) {
					return (T) value;
				} else if (Boolean.class.isAssignableFrom(type)) {
					return (T) Boolean.valueOf(value);
				} else {
					throw new UnsupportedOperationException(
							"Not supported yet. Type=" + type + ", Value" + value);
				}
			}
			return null;
		}

		private void addItemToolBar(final AbstractToolBarItem itemToolBar) {
			ToolBarGroup curGroupToolBar = null;
			if (!toolBarGroupStack.isEmpty()) {
				curGroupToolBar = toolBarGroupStack.peek();
			}
			if (curGroupToolBar != null) {
				curGroupToolBar.add(itemToolBar);
			} else {
				this.gridToolBar.add(itemToolBar);
			}
		}
	}

	private GridToolBarRawData rawData;
	private CompositeContext context;

	public GridToolBarFactory() {
	}

	public GridToolBarFactory(final GridToolBarRawData oRawData, final CompositeContext aContext) {
		this.rawData = oRawData;
		this.context = aContext;
	}

	public void setRawData(final GridToolBarRawData oRawData) {
		this.rawData = oRawData;
	}

	private void actualizeAction(final AbstractToolBarItem obj) {
		if (obj instanceof ToolBarItem) {
			ToolBarItem item = (ToolBarItem) obj;
			if (item.getAction() != null) {
				item.getAction().actualizeBy(this.context);
			}
		} else if (obj instanceof ToolBarGroup) {
			ToolBarGroup group = (ToolBarGroup) obj;
			for (AbstractToolBarItem item : group.getItems()) {
				actualizeAction(item);
			}
		}
	}

	private void actualizeAction(final GridToolBar gridToolBar) {
		for (AbstractToolBarItem item : gridToolBar.getItems()) {
			actualizeAction(item);
		}
	}

	public GridToolBar build() {
		InputStream xml = XMLUtils.xsdValidateAppDataSafe(
				TextUtils.stringToStream(rawData.getXmlData()), "gridToolBar.xsd");
		XmlHandler handler = new XmlHandler();
		SimpleSAX sax = new SimpleSAX(xml, handler, SAX_ERROR_MES);
		sax.parse();
		GridToolBar gridToolBar = handler.getGridToolBar();
		actualizeAction(gridToolBar);
		return gridToolBar;
	}
}
