package ru.curs.showcase.core.frame;

import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.exception.NotImplementedYetException;
import ru.curs.showcase.util.xml.GeneralXMLHelper;

/**
 * "Выбиральщик" для фрейма главной страницы. Отвечает за считывание названия
 * источника и создание требуемого шлюза.
 * 
 * @author den
 * 
 */
public class MainPageFrameSelector extends SourceSelector<MainPageFrameGateway> {
	private static final String PROC_DISABLED = "none";

	/**
	 * Тип фрейма.
	 */
	private final MainPageFrameType type;

	private boolean enabled = true;

	public MainPageFrameSelector(final MainPageFrameType aType) {
		super();
		type = aType;
		read();
	}

	private void read() {
		String frameParam =
			String.format("%s.%s", type.toString().toLowerCase(), GeneralXMLHelper.SOURCE_TAG);
		setSourceName(UserDataUtils.getOptionalProp(frameParam));
		if (getSourceName() == null) {
			setSourceName(getDefaultValue());
		} else if (PROC_DISABLED.equalsIgnoreCase(getSourceName().trim())) {
			enabled = false;
		}
	}

	private String getDefaultValue() {
		return type.toString().toLowerCase() + "." + getFileExt();
	}

	public MainPageFrameType getType() {
		return type;
	}

	/**
	 * Создает и возвращает шлюз для текущего источника.
	 */
	@Override
	public MainPageFrameGateway getGateway() {
		MainPageFrameGateway gateway;
		switch (sourceType()) {
		case JYTHON:
			throw new NotImplementedYetException();
		case FILE:
			gateway = new MainPageFrameFileGateway();
			break;
		case CELESTA:
			// throw new NotImplementedYetException();
			gateway = new MainPageFrameCelestaGateway();
			break;
		default:
			gateway = new MainPageFrameDBGateway();
		}
		gateway.setSourceName(getSourceName());
		return gateway;
	}

	@Override
	protected String getFileExt() {
		return "html";
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean aEnabled) {
		enabled = aEnabled;
	}
}
