package ru.curs.showcase.app.client.utils;

import ru.curs.showcase.app.client.MessageBox;

import com.google.gwt.user.client.ui.FormPanel;

/**
 * Компонент для скачивания файлов с сервера.
 * 
 * @author den
 * 
 */
public final class DownloadHelper extends RunServletByFormHelper {

	/**
	 * A static instance of this class.
	 * 
	 */
	private static DownloadHelper instance;

	/**
	 * Constructor of this class.
	 * 
	 */
	private DownloadHelper() {
		super();
	}

	/**
	 * Returns a static instance of this class. If the instance does not already
	 * exist, a new instance is created.
	 * 
	 * @return instance The static instance of this class.
	 */
	public static DownloadHelper getInstance() {
		if (instance == null) {
			instance = new DownloadHelper();
			instance.init();
		}
		return instance;
	}

	@Override
	protected void initFormProps() {
		super.initFormProps();
		setEncoding(FormPanel.ENCODING_URLENCODED);
	}

	/**
	 * Настройка внешнего вида формы.
	 */
	@Override
	protected void initFormView() {
		setPixelSize(1, 1);
		setVisible(false);
	}

	@Override
	protected void initFormHandlers() {
		addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(final SubmitCompleteEvent event) {
				String mess = "";
				if (event.getResults() != null) {
					mess = event.getResults().replace("<root>", "").replace("</root>", "");
				}
				MessageBox.showSimpleMessage(getErrorCaption(), mess);
			}

		});
	}

}
