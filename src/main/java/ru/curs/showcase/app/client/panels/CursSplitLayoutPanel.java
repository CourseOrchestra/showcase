package ru.curs.showcase.app.client.panels;

/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import ru.curs.showcase.app.client.AppCurrContext;

import com.google.gwt.core.client.*;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * A panel that adds user-positioned splitters between each of its child
 * widgets. Также в данную панель добавлена возможность обработки события на
 * перемещение Splitter-а.
 * 
 */
public class CursSplitLayoutPanel extends DockLayoutPanel {

	/**
	 * Класс горизонтального Splitter.
	 * 
	 * @author anlug
	 */
	class HSplitter extends Splitter {
		public HSplitter(final Widget target, final boolean reverse) {
			super(target, reverse);
			getElement().getStyle().setPropertyPx("width", splitterSize);
			setStyleName("gwt-SplitLayoutPanel-HDragger");
		}

		@Override
		protected int getAbsolutePosition() {
			return getAbsoluteLeft();
		}

		@Override
		protected int getEventPosition(final Event event) {
			return event.getClientX();
		}

		@Override
		protected int getTargetPosition() {
			return getTarget().getAbsoluteLeft();
		}

		@Override
		protected int getTargetSize() {
			return getTarget().getOffsetWidth();
		}
	}

	/**
	 * Виджет Splitter-а.
	 * 
	 * @author anlug
	 */
	abstract class Splitter extends Widget {

		/**
		 * int - offset.
		 */
		private int offset;
		/**
		 * boolean - mouseDown.
		 */
		private boolean mouseDown;
		/**
		 * ScheduledCommand - layoutCommand.
		 */
		private ScheduledCommand layoutCommand;

		/**
		 * boolean - reverse.
		 */
		private final boolean reverse;
		/**
		 * int - minSize.
		 */
		private int minSize;

		/**
		 * Widget target.
		 */
		private Widget target;

		public Splitter(final Widget atarget, final boolean areverse) {
			this.target = atarget;
			this.reverse = areverse;

			setElement(Document.get().createDivElement());
			sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE | Event.ONDBLCLICK);
		}

		public Widget getTarget() {
			return target;
		}

		/**
		 * Устанавливает target.
		 * 
		 * @param atarget
		 *            the target to set
		 */
		public void setTarget(final Widget atarget) {
			this.target = atarget;
		}

		@Override
		public void onBrowserEvent(final Event event) {
			// Window.alert(Integer.toString(splitterSize));
			switch (event.getTypeInt()) {
			case Event.ONMOUSEDOWN:
				mouseDown = true;
				offset = getEventPosition(event) - getAbsolutePosition();
				Event.setCapture(getElement());
				event.preventDefault();
				break;

			case Event.ONMOUSEUP:
				mouseDown = false;
				Event.releaseCapture(getElement());
				event.preventDefault();
				break;

			case Event.ONMOUSEMOVE:
				if (mouseDown) {
					int size;
					if (reverse) {
						size =
							getTargetPosition() + getTargetSize() - getEventPosition(event)
									- offset;
					} else {
						size = getEventPosition(event) - getTargetPosition() - offset;
					}
					setAssociatedWidgetSize(size);
					event.preventDefault();

					if (!(splitterDragHandler == null)) {
						splitterDragHandler.splitterDragEvent();
					}

				}
				break;
			default:
				break;
			}
		}

		/**
		 * Устанавливает minSize.
		 * 
		 * @param aminSize
		 *            - minSize.
		 */
		public void setMinSize(final int aminSize) {
			this.minSize = aminSize;
			LayoutData layout = (LayoutData) target.getLayoutData();

			// Try resetting the associated widget's size, which will enforce
			// the new
			// minSize value.
			setAssociatedWidgetSize((int) layout.size);
		}

		/**
		 * Возвращает абсолютную позицию.
		 * 
		 * @return - int
		 */
		protected abstract int getAbsolutePosition();

		/**
		 * Возвращает позицию события.
		 * 
		 * @param event
		 *            - Event
		 * @return - int
		 */
		protected abstract int getEventPosition(Event event);

		/**
		 * Возвращает позицию target.
		 * 
		 * @return - int
		 */
		protected abstract int getTargetPosition();

		/**
		 * Возвращает размер target.
		 * 
		 * @return - int
		 */
		protected abstract int getTargetSize();

		private void setAssociatedWidgetSize(final int size) {
			int size1 = size;
			if (size1 < minSize) {
				size1 = minSize;
			}

			LayoutData layout = (LayoutData) target.getLayoutData();
			if (size1 == layout.size) {
				return;
			}

			layout.size = size;

			// Defer actually updating the layout, so that if we receive many
			// mouse events before layout/paint occurs, we'll only update once.
			if (layoutCommand == null) {
				layoutCommand = new Command() {
					@Override
					public void execute() {
						layoutCommand = null;
						forceLayout();
					}
				};
				Scheduler.get().scheduleDeferred(layoutCommand);
			}
		}
	}

	/**
	 * Класс вертикального Splitter.
	 * 
	 * @author anlug
	 */
	class VSplitter extends Splitter {
		public VSplitter(final Widget target, final boolean reverse) {
			super(target, reverse);
			getElement().getStyle().setPropertyPx("height", splitterSize);
			setStyleName("gwt-SplitLayoutPanel-VDragger");
		}

		@Override
		protected int getAbsolutePosition() {
			return getAbsoluteTop();
		}

		@Override
		protected int getEventPosition(final Event event) {
			return event.getClientY();
		}

		@Override
		protected int getTargetPosition() {
			return getTarget().getAbsoluteTop();
		}

		@Override
		protected int getTargetSize() {
			return getTarget().getOffsetHeight();
		}
	}

	/**
	 * Размер (ширина) splitter по умолчанию.
	 */
	private static final int DEFAULT_SPLITTER_SIZE = 8;

	/**
	 * Размер (ширина) splitter.
	 */
	private int splitterSize;

	/**
	 * SplitterDragHandler - обработчик на перемещение Splitter.
	 */
	private SplitterDragHandler splitterDragHandler;

	/**
	 * Construct a new {@link SplitLayoutPanel} with the default splitter size
	 * of 8px.
	 */
	public CursSplitLayoutPanel() {
		this(DEFAULT_SPLITTER_SIZE);
		if (AppCurrContext.getInstance().getServerCurrentState().getPageSplitterWidth() != null) {
			setSplitterSize(AppCurrContext.getInstance().getServerCurrentState()
					.getPageSplitterWidth());
		}
	}

	/**
	 * Construct a new {@link SplitLayoutPanel} with the specified splitter size
	 * in pixels.
	 * 
	 * @param splitterSize
	 *            the size of the splitter in pixels
	 * @param splitterDragHandler
	 *            the splitterDragHandler
	 */
	public CursSplitLayoutPanel(final int asplitterSize) {
		super(Unit.PX);
		this.splitterSize = asplitterSize;
		// this.splitterDragHandler = asplitterDragHandler;
		setStyleName("gwt-SplitLayoutPanel");
	}

	/**
	 * Устаналвивает обработчик перетаскивания Splitter-а.
	 * 
	 * @param asplitterDragHandler
	 *            the splitterDragHandler to set
	 */
	public void setSplitterDragHandler(final SplitterDragHandler asplitterDragHandler) {
		this.splitterDragHandler = asplitterDragHandler;
	}

	/**
	 * Return the size of the splitter in pixels.
	 * 
	 * @return the splitter size
	 */
	public int getSplitterSize() {
		return splitterSize;
	}

	public void setSplitterSize(int aSplitterSize) {
		splitterSize = aSplitterSize;
	}

	@Override
	public void insert(final Widget child, final Direction direction, final double size,
			final Widget before) {
		super.insert(child, direction, size, before);
		if (direction != Direction.CENTER) {
			insertSplitter(child, before);
		}
	}

	@Override
	public boolean remove(final Widget child) {
		assert !(child instanceof Splitter) : "Splitters may not be directly removed";

		int idx = getWidgetIndex(child);
		if (super.remove(child)) {
			// Remove the associated splitter, if any.
			// Now that the widget is removed, idx is the index of the splitter.
			if (idx < getWidgetCount()) {
				// Call super.remove(), or we'll end up recursing.
				super.remove(getWidget(idx));
			}
			return true;
		}
		return false;
	}

	/**
	 * Sets the minimum allowable size for the given widget.
	 * 
	 * <p>
	 * Its associated splitter cannot be dragged to a position that would make
	 * it smaller than this size. This method has no effect for the
	 * {@link DockLayoutPanel.Direction#CENTER} widget.
	 * </p>
	 * 
	 * @param child
	 *            the child whose minimum size will be set
	 * @param minSize
	 *            the minimum size for this widget
	 */
	public void setWidgetMinSize(final Widget child, final int minSize) {
		assertIsChild1(child);
		Splitter splitter = getAssociatedSplitter(child);
		// The splitter is null for the center element.
		if (splitter != null) {
			splitter.setMinSize(minSize);
		}
	}

	/**
	 * assertIsChild1. см. сокументацию gwt.
	 * 
	 * @param widget
	 *            - widget
	 */
	void assertIsChild1(final Widget widget) {
		assert (widget == null) || (widget.getParent() == this) : "The specified widget is not a child of this panel";
	}

	private Splitter getAssociatedSplitter(final Widget child) {
		// If a widget has a next sibling, it must be a splitter, because the
		// only
		// widget that *isn't* followed by a splitter must be the CENTER, which
		// has
		// no associated splitter.
		int idx = getWidgetIndex(child);
		if (idx > -1 && idx < getWidgetCount() - 1) {
			Widget splitter = getWidget(idx + 1);
			assert splitter instanceof Splitter : "Expected child widget to be splitter";
			return (Splitter) splitter;
		}
		return null;
	}

	private void insertSplitter(final Widget widget, final Widget before) {
		assert getChildren().size() > 0 : "Can't add a splitter before any children";

		LayoutData layout = (LayoutData) widget.getLayoutData();
		Splitter splitter = null;
		switch (getResolvedDirection(layout.direction)) {
		case WEST:
			splitter = new HSplitter(widget, false);
			break;
		case EAST:
			splitter = new HSplitter(widget, true);
			break;
		case NORTH:
			splitter = new VSplitter(widget, false);
			break;
		case SOUTH:
			splitter = new VSplitter(widget, true);
			break;
		default:
			assert false : "Unexpected direction";
		}

		super.insert(splitter, layout.direction, splitterSize, before);
	}

	public SplitterDragHandler getSplitterDragHandler() {
		return splitterDragHandler;
	}
}
