package ru.curs.showcase.app.client.panels;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * Класс, ссылающийся на иконки на кнопках прокрутки для закладок в
 * {@link CursScrolledTabLayoutPanel}.
 * 
 * @author anlug
 */
interface Images extends ClientBundle {
	/**
	 * Возвращает ImageResource для иконки на правой кнопке прокрутки для
	 * закладок в {@link CursScrolledTabLayoutPanel} .
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources//left_arrow_for_tablayoutpanel.png")
	ImageResource getRightScrollArrow();

	/**
	 * Возвращает ImageResource для иконки на левой кнопке прокрутки для
	 * закладок в {@link CursScrolledTabLayoutPanel} .
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources//right_arrow_for_tablayoutpanel.png")
	ImageResource getLeftScrollArrow();
}

/**
 * {@link TabLayoutPanel} которая показывает кнопки прокрутки для закладок.
 */
public class CursScrolledTabLayoutPanel extends TabLayoutPanel {

	/**
	 * GWT сервис для доступа к иконкам, хранящимся на сервере.
	 */
	private static Images images = (Images) GWT.create(Images.class);

	/**
	 * Расстояние между иконками.
	 */
	private static final int IMAGE_PADDING_PIXELS = 4;

	/**
	 * Иконка-кнопка прокрутки влево.
	 */
	private Image scrollLeftButton;

	/**
	 * Иконка-кнопка прокрутки вправо.
	 */
	private Image scrollRightButton;

	/**
	 * HandlerRegistration для обработчика на изменение размера клиентского окна
	 * браузера.
	 */
	private HandlerRegistration windowResizeHandler;

	/**
	 * ImageResource для иконки кнопки прокрутки влево.
	 */
	private final ImageResource leftArrowImage;
	/**
	 * ImageResource для иконки кнопки прокрутки вправо.
	 */
	private final ImageResource rightArrowImage;

	/**
	 * LayoutPanel.
	 */
	private final LayoutPanel panel;

	/**
	 * Панель FlowPanel, содержащая иконки.
	 */
	private FlowPanel tabBar;

	public CursScrolledTabLayoutPanel(final double barHeight, final Unit barUnit) {

		super(barHeight, barUnit);

		this.leftArrowImage = images.getLeftScrollArrow();
		this.rightArrowImage = images.getRightScrollArrow();

		// The main widget wrapped by this composite, which is a LayoutPanel
		// with the tab bar & the tab content
		panel = (LayoutPanel) getWidget();

		// Find the tab bar, which is the first flow panel in the LayoutPanel
		for (int i = 0; i < panel.getWidgetCount(); ++i) {
			Widget widget = panel.getWidget(i);
			if (widget instanceof FlowPanel) {
				tabBar = (FlowPanel) widget;
				break; // tab bar found
			}
		}

		initScrollButtons();
	}

	public FlowPanel getTabBar() {
		return tabBar;
	}

	/**
	 * Устанавливает tabBar.
	 * 
	 * @param atabBar
	 *            the tabBar to set
	 */
	public void setTabBar(final FlowPanel atabBar) {
		this.tabBar = atabBar;
	}

	@Override
	public void add(final Widget child, final Widget tab) {
		super.add(child, tab);
		checkIfScrollButtonsNecessary();
	}

	@Override
	public boolean remove(final Widget w) {
		boolean b = super.remove(w);
		checkIfScrollButtonsNecessary();
		return b;
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		if (windowResizeHandler == null) {
			windowResizeHandler = Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(final ResizeEvent event) {
					// GeneralDataPanel.getTabPanel().saveTabBarCurrentScrollingPosition();
					// MessageBox.showSimpleMessage("windowResizeHandler",
					// GeneralDataPanel
					// .getTabPanel().getTabBar().getElement().getStyle().getLeft());
					checkIfScrollButtonsNecessary();
				}
			});
		}
	}

	@Override
	protected void onUnload() {
		super.onUnload();

		if (windowResizeHandler != null) {
			windowResizeHandler.removeHandler();
			windowResizeHandler = null;
		}
	}

	private ClickHandler createScrollClickHandler(final int diff) {
		return new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				Widget lastTab = getLastTab();
				if (lastTab == null) {
					return;
				}

				int newLeft = parsePosition(tabBar.getElement().getStyle().getLeft()) + diff;
				int rightOfLastTab = getRightOfWidget(lastTab);

				// Prevent scrolling the last tab too far away form the right
				// border,
				// or the first tab further than the left border position

				final int n20 = 20;
				if (newLeft <= 0 && (getTabBarWidth() - newLeft < (rightOfLastTab + n20))) {
					scrollTo(newLeft);
				}
			}
		};
	}

	/** Create and attach the scroll button images with a click handler. */
	private void initScrollButtons() {
		final int n10 = 10;

		scrollLeftButton = new Image(leftArrowImage);
		// int leftImageWidth = scrollLeftButton.getWidth();
		panel.insert(scrollLeftButton, 0);

		panel.setWidgetTopHeight(scrollLeftButton,
				(getTabBarHeight() + scrollLeftButton.getHeight()) / 2, Unit.PX,
				scrollLeftButton.getHeight(), Unit.PX);
		final int diffplus = +50;
		scrollLeftButton.addClickHandler(createScrollClickHandler(diffplus));
		scrollLeftButton.setVisible(false);

		scrollRightButton = new Image(rightArrowImage);
		panel.insert(scrollRightButton, 0);

		panel.setWidgetLeftWidth(
				scrollLeftButton,
				getTabBarWidth()
						- (scrollLeftButton.getWidth() + scrollRightButton.getWidth() + IMAGE_PADDING_PIXELS)
						- n10, Unit.PX, scrollLeftButton.getWidth(), Unit.PX);
		panel.setWidgetLeftWidth(scrollRightButton,
				getTabBarWidth() - scrollRightButton.getWidth() - n10, Unit.PX,
				scrollRightButton.getWidth(), Unit.PX);

		panel.setWidgetTopHeight(scrollRightButton,
				(getTabBarHeight() + scrollLeftButton.getHeight()) / 2, Unit.PX,
				scrollRightButton.getHeight(), Unit.PX);
		final int diffminus = -50;
		scrollRightButton.addClickHandler(createScrollClickHandler(diffminus));
		scrollRightButton.setVisible(false);

	}

	/**
	 * Проверяет и если надо показывает кнопки прокрутки для закладок в
	 * {@link CursScrolledTabLayoutPanel}.
	 */
	public void checkIfScrollButtonsNecessary() {
		// Defer size calculations until sizes are available, when calculating
		// immediately after
		// add(), all size methods return zero
		Scheduler gg = Scheduler.get();
		gg.scheduleDeferred(new Scheduler.ScheduledCommand() {

			@Override
			public void execute() {
				boolean isScrolling = isScrollingNecessary();
				// When the scroll buttons are being hidden, reset the scroll
				// position to zero to
				// make sure no tabs are still out of sight
				if (scrollRightButton.isVisible() && !isScrolling) {
					resetScrollPosition();
				}
				scrollRightButton.setVisible(isScrolling);
				scrollLeftButton.setVisible(isScrolling);
				final int n10 = 10;
				// if (isScrolling) {
				panel.setWidgetLeftWidth(
						scrollLeftButton,
						getTabBarWidth()
								- (scrollLeftButton.getWidth() + scrollRightButton.getWidth() + IMAGE_PADDING_PIXELS)
								- n10, Unit.PX, scrollLeftButton.getWidth(), Unit.PX);
				panel.setWidgetLeftWidth(scrollRightButton,
						getTabBarWidth() - scrollRightButton.getWidth() - n10, Unit.PX,
						scrollRightButton.getWidth(), Unit.PX);
				// }

			}

		});

		/*
		 * DeferredCommand.addCommand(new Command() {
		 * 
		 * @Override public void execute() { boolean isScrolling =
		 * isScrollingNecessary(); // When the scroll buttons are being hidden,
		 * reset the scroll // position to zero to // make sure no tabs are
		 * still out of sight if (scrollRightButton.isVisible() && !isScrolling)
		 * { resetScrollPosition(); } scrollRightButton.setVisible(isScrolling);
		 * scrollLeftButton.setVisible(isScrolling); final int n10 = 10; if
		 * (isScrolling) { panel.setWidgetLeftWidth( scrollLeftButton,
		 * getTabBarWidth() - (scrollLeftButton.getWidth() +
		 * scrollRightButton.getWidth() + IMAGE_PADDING_PIXELS) - n10, Unit.PX,
		 * scrollLeftButton.getWidth(), Unit.PX);
		 * panel.setWidgetLeftWidth(scrollRightButton, getTabBarWidth() -
		 * scrollRightButton.getWidth() - n10, Unit.PX,
		 * scrollRightButton.getWidth(), Unit.PX); }
		 * 
		 * } });
		 */
	}

	private void resetScrollPosition() {
		scrollTo(0);
	}

	private void scrollTo(final int pos) {
		tabBar.getElement().getStyle().setLeft(pos, Unit.PX);
	}

	private boolean isScrollingNecessary() {
		Widget lastTab = getLastTab();
		if (lastTab == null) {
			return false;
		}

		return getRightOfWidget(lastTab) > getTabBarWidth();
	}

	private int getRightOfWidget(final Widget widget) {
		return widget.getElement().getOffsetLeft() + widget.getElement().getOffsetWidth();
	}

	private int getTabBarWidth() {
		return tabBar.getElement().getParentElement().getClientWidth();
	}

	private int getTabBarHeight() {
		return tabBar.getElement().getParentElement().getClientHeight();
	}

	private Widget getLastTab() {
		if (tabBar.getWidgetCount() == 0) {
			return null;
		}

		return tabBar.getWidget(tabBar.getWidgetCount() - 1);
	}

	private static int parsePosition(final String apositionString) {
		int position;
		String positionString = apositionString;
		try {
			for (int i = 0; i < positionString.length(); i++) {
				char c = positionString.charAt(i);
				if (c != '-' && !(c >= '0' && c <= '9')) {
					positionString = positionString.substring(0, i);
				}
			}

			position = Integer.parseInt(positionString);
		} catch (NumberFormatException ex) {
			position = 0;
		}
		return position;
	}

	/**
	 * saveTabBarCurrentScrollingPosition.
	 */
	public void saveTabBarCurrentScrollingPosition() {

		Widget lastTab = getLastTab();
		if (lastTab == null) {
			return;
		}

		final int newLeft = parsePosition(tabBar.getElement().getStyle().getLeft());

		Scheduler gg = Scheduler.get();
		gg.scheduleDeferred(new Scheduler.ScheduledCommand() {

			@Override
			public void execute() {

				// int newLeft =
				// parsePosition(tabBar.getElement().getStyle().getLeft());

				scrollTo(newLeft);

			}

		});
	}
}
