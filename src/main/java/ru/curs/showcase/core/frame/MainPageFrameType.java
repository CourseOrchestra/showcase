package ru.curs.showcase.core.frame;

import javax.xml.bind.annotation.*;

/**
 * Типы "статических" фреймов главной страницы. Статическими они являются по
 * сравнению с динамическими частями - навигатором и инф. панелью.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public enum MainPageFrameType {
	/**
	 * Заставка решения (splash), отображаемая в центре главной страницы сразу
	 * после ее загрузки.
	 */
	WELCOME,
	/**
	 * Заголовок, отображаемый всегда в верхней части главной страницы.
	 */
	HEADER,
	/**
	 * Нижний колонтитул, отображаемый всегда в нижней части главной страницы.
	 */
	FOOTER
}
