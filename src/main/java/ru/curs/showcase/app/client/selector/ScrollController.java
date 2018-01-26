package ru.curs.showcase.app.client.selector;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

/**
 * ScrollController.
 */
class ScrollController {

	/**
	 * ScrollListener.
	 */
	interface ScrollListener {
		/**
		 * onPositionChange.
		 * 
		 * @param startRecord
		 *            startRecord
		 */
		void onPositionChange(int startRecord);
	}

	/**
	 * SCROLL_SPACE_ROW_HEIGHT.
	 */
	// Изначально было 10
	private static final int SCROLL_SPACE_ROW_HEIGHT = 30;

	/**
	 * scroll.
	 */
	private final ScrollPanel scroll;
	/**
	 * options.
	 */
	private final BaseSelectorComponent.Options options;

	/**
	 * scrollSpace.
	 */
	private final Image scrollSpace = new Image();

	/**
	 * debug.
	 */
	private Label debug;
	/**
	 * totalCount.
	 */
	private int totalCount;

	/**
	 * timer.
	 */
	private ScrollTimer timer;

	/**
	 * scrollMax.
	 */
	private int scrollMax;
	/**
	 * scrollPos.
	 */
	private int scrollPos;

	/**
	 * scrollListener.
	 */
	private ScrollListener scrollListener;

	/**
	 * skipScrollHandling.
	 */
	private boolean skipScrollHandling = false;

	ScrollController(final ScrollPanel scrollPanel, final BaseSelectorComponent.Options options0,
			final BaseSelectorComponent.Resources resources) {
		this.scroll = scrollPanel;
		this.options = options0;

		scrollSpace.setResource(resources.transparentImage());
		scroll.setWidget(scrollSpace);
		scroll.setAlwaysShowScrollBars(true);
		scroll.setHeight(options.getDataHeight());
		scroll.addScrollHandler(new ScrollHandler() {
			@SuppressWarnings("deprecation")
			@Override
			public void onScroll(final ScrollEvent event) {
				if (skipScrollHandling) {
					return;
				}

				scrollMax = scroll.getWidget().getOffsetHeight() - scroll.getOffsetHeight();
				scrollPos = scroll.getScrollPosition();

				debug.setText("HEY: " + scrollPos + ", " + scrollMax);

				if (timer == null) {
					timer = new ScrollTimer();
					timer.scheduleRepeating(options.getScrollRequestDelay());
				} else {
					timer.wasScroll();
				}
			}
		});
	}

	void setDebug(final Label debug1) {
		this.debug = debug1;
	}

	void setScrollListener(final ScrollListener scrollListener1) {
		this.scrollListener = scrollListener1;
	}

	/**
	 * update.
	 * 
	 * @param totalCount1
	 *            totalCount1
	 * @param reset
	 *            reset
	 */
	void update(final int totalCount1, final boolean reset) {
		if (this.totalCount != totalCount1) {
			this.totalCount = totalCount1;
			scrollSpace.setPixelSize(1, totalCount * SCROLL_SPACE_ROW_HEIGHT);
		}

		if (reset) {
			try {
				skipScrollHandling = true;
				scroll.scrollToTop();
			} finally {
				skipScrollHandling = false;
			}
		}
	}

	private void processScroll() {
		int total = totalCount + options.getvisibleBottomBlankCount();
		int length = options.getVisibleRecordCount();
		int start = (int) (((double) scrollPos / (double) scrollMax) * totalCount);

		start = Math.min(start, total - length);

		debug.setText(debug.getText() + " -> PROCESS (start at " + start + ")");

		if (scrollListener != null) {
			scrollListener.onPositionChange(start);
		}

	}

	/**
	 * ScrollTimer extends Timer.
	 */
	private class ScrollTimer extends Timer {
		/**
		 * wasScroll.
		 */
		private boolean wasScroll = false;

		void wasScroll() {
			wasScroll = true;
		}

		@Override
		public void run() {
			if (wasScroll) {
				wasScroll = false;
			} else {
				this.cancel();
				timer = null;
				processScroll();
			}
		}
	}

}
