package ru.curs.showcase.core.frame;

import ru.curs.showcase.app.api.MainPage;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.StyleDBGateway;
import ru.curs.showcase.runtime.UserDataUtils;

/**
 * Команда для получения всей информации о главной странице приложения.
 * 
 * @author den
 * 
 */
public final class MainPageGetCommand extends AbstractMainPageFrameCommand<MainPage> {

	public MainPageGetCommand(final CompositeContext aContext) {
		super(aContext);
	}

	@Override
	protected void mainProc() throws Exception {
		MainPage mp = new MainPage();
		String value = UserDataUtils.getOptionalProp(UserDataUtils.HEADER_HEIGHT_PROP);
		if (value != null) {
			mp.setHeaderHeight(value);
		} else {
			mp.setHeaderHeight(UserDataUtils.DEF_HEADER_HEIGTH);
		}
		value = UserDataUtils.getOptionalProp(UserDataUtils.FOOTER_HEIGHT_PROP);
		if (value != null) {
			mp.setFooterHeight(value);
		} else {
			mp.setFooterHeight(UserDataUtils.DEF_FOOTER_HEIGTH);
		}

		MainPageFrameFactory factory = new MainPageFrameFactory(false);
		String html = getRawMainPageFrame(getContext(), MainPageFrameType.HEADER);
		html = factory.build(html);
		/**
		 * Метод UserDataUtils.modifyVariables() переводит строку-аргумент с
		 * помощью Gettext.
		 */
		if (html != null)
			html = UserDataUtils.modifyVariables(html);
		mp.setHeader(html);

		html = getRawMainPageFrame(getContext(), MainPageFrameType.FOOTER);
		html = factory.build(html);
		/**
		 * Метод UserDataUtils.modifyVariables() переводит строку-аргумент с
		 * помощью Gettext.
		 */
		if (html != null)
			html = UserDataUtils.modifyVariables(html);
		mp.setFooter(html);

		html = getRawMainPageFrame(getContext(), MainPageFrameType.WELCOME);
		html = factory.build(html);
		/**
		 * Метод UserDataUtils.modifyVariables() переводит строку-аргумент с
		 * помощью Gettext.
		 */
		if (html != null)
			html = UserDataUtils.modifyVariables(html);
		mp.setWelcome(html);

		String cssProcName = UserDataUtils.getOptionalProp(UserDataUtils.CSS_PROC_NAME_PROP);
		if (cssProcName == null || "none".equals(cssProcName)) {
			mp.setSolutionCSSFileName(null);
			mp.setSolutionGridCSSFileName(null);
			// mp.setProgressBarCSSFileName(null);
		} else {
			StyleDBGateway sgw = new StyleDBGateway();
			String[] cssResult = sgw.getRawData(getContext(), cssProcName);
			sgw.close();
			mp.setSolutionCSSFileName(cssResult[0]);
			mp.setSolutionGridCSSFileName(cssResult[1]);
			// mp.setProgressBarCSSFileName(cssResult[2]);
		}

		setResult(mp);
	}

}
