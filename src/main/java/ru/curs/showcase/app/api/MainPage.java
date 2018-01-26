package ru.curs.showcase.app.api;

import javax.xml.bind.annotation.*;

/**
 * Информация о главной странице.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MainPage implements SerializableElement {

	private static final long serialVersionUID = -3089363754774183721L;

	/**
	 * Высота верхнего колонтитула. Задается в пикселях или в процентах.
	 */
	private String headerHeight;

	/**
	 * Высота нижнего колонтитула. Задается в пикселях или в процентах.
	 */
	private String footerHeight;

	/**
	 * Код заголовка главной страницы.
	 */
	private String header;

	/**
	 * Имя CSS файла для общих настроек стилей решения.
	 */
	private String solutionCSSFileName;

	/**
	 * Имя CSS файла для настроект стилей грида решения.
	 */
	private String solutionGridCSSFileName;

	/**
	 * Имя CSS файла для настроек стилей прогресс-бара для всего решения.
	 */

	private String progressBarCSSFileName;

	/**
	 * Код нижнего колонтитула главной страницы.
	 */
	private String footer;

	/**
	 * Код экрана приветствия.
	 */
	private String welcome;

	public String getHeaderHeight() {
		return headerHeight;
	}

	public void setHeaderHeight(final String aHeaderHeight) {
		headerHeight = aHeaderHeight;
	}

	public String getFooterHeight() {
		return footerHeight;
	}

	public void setFooterHeight(final String aFooterHeight) {
		footerHeight = aFooterHeight;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(final String aHeader) {
		header = aHeader;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(final String aFooter) {
		footer = aFooter;
	}

	public String getWelcome() {
		return welcome;
	}

	public void setWelcome(final String aWelcome) {
		welcome = aWelcome;
	}

	public String getSolutionCSSFileName() {
		return solutionCSSFileName;
	}

	public void setSolutionCSSFileName(final String asolutionCSSFileName) {
		this.solutionCSSFileName = asolutionCSSFileName;
	}

	public String getSolutionGridCSSFileName() {
		return solutionGridCSSFileName;
	}

	public void setSolutionGridCSSFileName(final String asolutionGridCSSFileName) {
		this.solutionGridCSSFileName = asolutionGridCSSFileName;
	}

	public String getProgressBarCSSFileName() {
		return progressBarCSSFileName;
	}

	public void setProgressBarCSSFileName(final String aProgressBarCSSFileName) {
		this.progressBarCSSFileName = aProgressBarCSSFileName;
	}

}
