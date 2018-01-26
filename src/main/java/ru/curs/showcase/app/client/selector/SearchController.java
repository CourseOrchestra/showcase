package ru.curs.showcase.app.client.selector;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

/**
 * SearchController.
 */
class SearchController {

	/**
	 * SearchListener.
	 */
	interface SearchListener {
		/**
		 * onSearchTextChange.
		 * 
		 * @param searchText
		 *            searchText
		 */
		void onSearchTextChange(String searchText);
	}

	/**
	 * search.
	 */
	private final TextBox search;
	/**
	 * options.
	 */
	private final BaseSelectorComponent.Options options;

	/**
	 * debug.
	 */
	private Label debug;

	/**
	 * timer.
	 */
	private SearchTimer timer;
	/**
	 * listener.
	 */
	private SearchListener listener;

	/**
	 * text.
	 */
	private String text;

	public void setText(final String text1) {
		this.text = text1;
	}

	public SearchController(final TextBox search0, final BaseSelectorComponent.Options options1) {
		this.search = search0;
		this.options = options1;

		this.text = search0.getText();

		search.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(final ChangeEvent event) {
				onSearchTextChange();
			}
		});
		search.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {
				onSearchTextChange();
			}
		});
	}

	void setDebug(final Label label) {
		this.debug = label;
	}

	public void setSearchListener(final SearchListener listener1) {
		this.listener = listener1;
	}

	/**
	 * clear.
	 */
	void clear() {
		search.setText("");
		search.setFocus(true);
	}

	private void onSearchTextChange() {
		if (text.equals(search.getText())) {
			return;
		}
		text = search.getText();

		debug.setText("VAL: " + text);

		if (timer == null) {
			timer = new SearchTimer();
			timer.scheduleRepeating(options.getDataRequestDelay());
		} else {
			timer.wasAction();
		}
	}

	private void processSearch() {
		debug.setText(debug.getText() + " -> PROCESS '" + search.getText() + "'");
		if (listener != null) {
			listener.onSearchTextChange(search.getText());
		}
	}

	/**
	 * SearchTimer extends Timer.
	 */
	private class SearchTimer extends Timer {
		/**
		 * wasAction.
		 */
		private boolean wasAction = false;

		void wasAction() {
			wasAction = true;
		}

		@Override
		public void run() {
			if (wasAction) {
				wasAction = false;
			} else {
				this.cancel();
				timer = null;
				processSearch();
			}
		}
	}

}
