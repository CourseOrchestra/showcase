package ru.curs.showcase.app.client.utils;

import java.util.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.api.CompleteHandler;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;

/**
 * Вспомогательный класс, содержащий форму для загрузки файлов на сервер с
 * помощью компоненты FileUpload.
 * 
 * @author den
 * 
 */
public final class UploadHelper extends RunServletByFormHelper {

	private static final String UPLOAD_ERROR =
	// AppCurrContext.getInstance().getBundleMap().get("uploadError");
		CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
				"An error occurred during downloading files");

	private static final String SC_UPLOADER_CSS = "sc-uploader-comp";

	/**
	 * Обработчик окончания загрузки файлов.
	 */
	private CompleteHandler submitHandler = null;

	/**
	 * Информация о регистрации обработчика.
	 */
	private HandlerRegistration handlerRegistration = null;

	public UploadHelper() {
		super();
	}

	/**
	 * Массив компонент для загрузки файлов на сервер.
	 */
	private final Map<String, FileUpload> uploaders = new TreeMap<String, FileUpload>();

	/**
	 * Очищает настройки. Необходимо вызывать в начале каждого использования.
	 */
	@Override
	public void clear() {
		super.clear();
		uploaders.clear();
	}

	@Override
	protected void initFormProps() {
		super.initFormProps();
		setEncoding(FormPanel.ENCODING_MULTIPART);
	}

	/**
	 * Процедура выбора файла для закачивания на сервер.
	 * 
	 * @param linkId
	 *            - ссылка на файл.
	 * @param closeHandler
	 *            - обработчик выбора файла.
	 */
	public void runUpload(final String linkId, final ChangeHandler closeHandler) {
		FileUpload uploader = uploaders.get(linkId);
		if (uploader == null) {
			uploader = new FileUpload();
			uploader.setStylePrimaryName(SC_UPLOADER_CSS);
			uploader.setName(ExchangeConstants.FILE_DATA_PARAM_PREFIX + linkId);
			getPanel().add(uploader);
			uploaders.put(linkId, uploader);
			if (handlerRegistration != null) {
				handlerRegistration.removeHandler();
			}
			handlerRegistration = uploader.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(final ChangeEvent aEvent) {
					closeHandler.onChange(aEvent);
				}
			});
		}
		uploader.setVisible(true);
	}

	public Map<String, FileUpload> getUploaders() {
		return uploaders;
	}

	/**
	 * Скрывает все компоненты FileUpload.
	 */
	public void hide() {
		for (FileUpload cur : uploaders.values()) {
			cur.setVisible(false);
		}
	}

	/**
	 * Определяет - выбран ли хотя бы один файл в компоненте.
	 * 
	 * @return - результат проверки.
	 */
	public boolean isFilesSelected() {
		for (FileUpload current : uploaders.values()) {
			if ((current.getFilename() != null) && (!current.getFilename().isEmpty())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Функция submit, получающая обработчик на завершение процесса загрузки.
	 * 
	 * @param aSubmitHandler
	 *            - обработчик.
	 */
	public void submit(final CompleteHandler aSubmitHandler) {
		if (isFilesSelected()) {
			submitHandler = aSubmitHandler;
			super.submit();
		}
	}

	/**
	 * Настройка обработчиков формы.
	 */
	@Override
	protected void initFormHandlers() {
		addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(final SubmitCompleteEvent event) {
				if ((event.getResults() == null) || (event.getResults().isEmpty())
						|| ("<pre></pre>".equalsIgnoreCase(event.getResults().trim()))) {
					submitHandler.onComplete(true);
				} else {
					MessageBox.showSimpleMessage(UPLOAD_ERROR, event.getResults());
				}
			}

		});
	}
}
