package ru.curs.showcase.app.api.common;

import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;

/**
 * Вспомогательный класс для построения окна с интерфейсом. Большинство методов
 * возвращают ссылку на экземпляр самого объекта для того, чтобы методы удобно
 * было бы вызвать "по цепочке". User: Inc Date: 18.11.2009 Time: 0:29:39
 */
public class WidgetBuilder {

	/**
	 * Handler.
	 */
	private abstract class Handler {
		public abstract void add(Widget w);

		public void text(final String text) {
			label(text);
		}

		public HasWidgets getHasWidgets() {
			throw new UnsupportedOperationException(
					"'getHasWidgets' is unsupported by " + getClass());
		}

		// public void enable(boolean e)
		// {
		// throw new UnsupportedOperationException("'enable' is unsupported by "
		// + getClass());
		// }
	}

	/**
	 * hh.
	 */
	private final LinkedList<Handler> hh = new LinkedList<Handler>();
	/**
	 * root.
	 */
	private Widget root;

	/**
	 * lastAdded.
	 */
	private Widget lastAdded;

	public WidgetBuilder() {
		push(new Root());
	}

	private Handler peek() {
		return hh.element();
	}

	private void push(final Handler h) {
		hh.addFirst(h);
	}

	private void pop() {
		hh.remove();
	}

	private void add(final Widget w) {
		peek().add(w);
		lastAdded = w;
	}

	/**
	 * Root extends Handler.
	 */
	private class Root extends Handler {
		@Override
		public void add(final Widget w) {
			root = w;
		}
	}

	/**
	 * Добавляет элемент caption.
	 * 
	 * @param captionText
	 *            текст caption-а
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder caption(final String captionText) {
		CaptionPanel cp = new CaptionPanel(captionText);
		add(cp);
		push(new CaptionPanelHandler(cp));
		return this;
	}

	/**
	 * Добавляет элемент caption и устанавливает contentWidget.
	 * 
	 * @param captionText
	 *            текст надписи
	 * @param w
	 *            contentWidget
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder caption(final String captionText, final Widget w) {
		CaptionPanel cp = new CaptionPanel(captionText);
		cp.setContentWidget(w);
		add(cp);
		return this;
	}

	/**
	 * Добавляет элемент Label.
	 * 
	 * @param labelText
	 *            текст надписи
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder label(final String labelText) {
		add(new Label(labelText));
		return this;
	}

	/**
	 * CaptionPanelHandler extends Handler.
	 */
	private final class CaptionPanelHandler extends Handler {
		/**
		 * cp.
		 */
		private final CaptionPanel cp;

		private CaptionPanelHandler(final CaptionPanel cp1) {
			this.cp = cp1;
		}

		@Override
		public void add(final Widget w) {
			cp.setContentWidget(w);
		}
	}

	/**
	 * Добавляет субвиджет.
	 * 
	 * @param w
	 *            виджет
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder widget(final Widget w) {
		add(w);
		return this;
	}

	/**
	 * Добавляет набор виджетов.
	 * 
	 * @param ww
	 *            набор виджетов
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder widgets(final Widget... ww) {
		for (Widget w : ww) {
			add(w);
		}
		return this;
	}

	/**
	 * Устанавливает элемент Label с текстом на последний добавленный виджет.
	 * 
	 * @param text
	 *            текст
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder text(final String text) {
		peek().text(text);
		return this;
	}

	/**
	 * Устанавливает элементы Label с текстом на последний добавленный виджет.
	 * 
	 * @param texts
	 *            тексты
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder texts(final String... texts) {
		Handler h = peek();
		for (String t : texts) {
			h.text(t);
		}
		return this;
	}

	/**
	 * Добавляет VerticalPanel.
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder vertical() {
		VerticalPanel p = new VerticalPanel();
		add(p);
		push(new HasWidgetsHandler(p));
		return this;
	}

	/**
	 * Добавляет HorizontalPanel.
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder horizontal() {
		HorizontalPanel p = new HorizontalPanel();
		add(p);
		push(new HasWidgetsHandler(p));
		return this;
	}

	/**
	 * Устанавливает выравнивание.
	 * 
	 * @param align
	 *            выравнивание
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder align(final HasHorizontalAlignment.HorizontalAlignmentConstant align) {
		((HasHorizontalAlignment) getWidget()).setHorizontalAlignment(align);
		return this;
	}

	/**
	 * Устанавливает пространство между элементами.
	 * 
	 * @param spacing
	 *            пространство между элементами.
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder spacing(final int spacing) {
		getWidget(CellPanel.class).setSpacing(spacing);
		return this;
	}

	/**
	 * Устанавливает ширину.
	 * 
	 * @param width
	 *            ширина
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder width(final String width) {
		getWidget().setWidth(width);
		return this;
	}

	/**
	 * Устанавливает высоту.
	 * 
	 * @param height
	 *            высота
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder height(final String height) {
		getWidget().setHeight(height);
		return this;
	}

	/**
	 * Устанавливает ширину элемента, а также ширину клетки CellPanel, в которой
	 * он находится.
	 * 
	 * @param width
	 *            ширина
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder smartWidth(final String width) {
		width(width);
		cellWidth(width);
		return this;
	}

	/**
	 * Устанавливает ширину ячейки, в которой находится текущий элемент.
	 * 
	 * @param width
	 *            ширина
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder cellWidth(final String width) {
		getContainerWidget(CellPanel.class).setCellWidth(getWidget(), width);
		return this;
	}

	/**
	 * HasWidgetsHandler extends Handler.
	 */
	private final class HasWidgetsHandler extends Handler {
		/**
		 * hw.
		 */
		private final HasWidgets hw;

		private HasWidgetsHandler(final HasWidgets hw1) {
			this.hw = hw1;
		}

		@Override
		public void add(final Widget w) {
			hw.add(w);
		}

		@Override
		public HasWidgets getHasWidgets() {
			return hw;
		}
	}

	/**
	 * Заканчивает построение виджета.
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder end() {
		pop();
		return this;
	}

	/**
	 * Добавляет кнопку с обработчиком.
	 * 
	 * @param label
	 *            Надпись на кнопке.
	 * @param ch
	 *            Обработчик события нажатия на кнопку.
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder button(final String label, final ClickHandler ch) {
		add(new Button(label, ch));
		return this;
	}

	/**
	 * Добавляет кнопку с командой.
	 * 
	 * @param label
	 *            Надпись на кнопке.
	 * @param command
	 *            Команда, срабатывающая при нажатии на кнопку.
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder button(final String label, final Command command) {
		return button(label, new CommandClickHandler(command));
	}

	/**
	 * Добавляет кнопку с действием.
	 * 
	 * @param action
	 *            Действие, связанное с данной кнопкой.
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder button(final Action action) {
		ActionButtonAdapter a = new ActionButtonAdapter(action);
		add(a.getButton());
		return this;
	}

	/**
	 * Добавляет кнопку с действием.
	 * 
	 * @param action
	 *            Действие, связанное с данной кнопкой.
	 * @param style
	 *            Стиль кнопки
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder button(final Action action, final ActionButtonStyle style) {
		ActionButtonAdapter a = new ActionButtonAdapter(action, style);
		add(a.getButton());
		return this;
	}

	/**
	 * Добавляет гиперссылку с командой.
	 * 
	 * @param label
	 *            Надпись на гиперссылке.
	 * @param command
	 *            Команда, срабатывающая при нажатии на гиперссылку.
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder link(final String label, final Command command) {
		Anchor a = new Anchor(label);
		a.addClickHandler(new CommandClickHandler(command));
		add(a);
		return this;
	}

	/**
	 * Добавляет грид.
	 * 
	 * @param columns
	 *            количество колонок
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder grid(final int columns) {
		Grid g = new Grid(0, columns);
		add(g);
		push(new GridHandler(g));
		return this;
	}

	/**
	 * GridHandler extends Handler.
	 */
	private final class GridHandler extends Handler {

		/**
		 * g.
		 */
		private final Grid g;
		/**
		 * column.
		 */
		private int column;
		/**
		 * row.
		 */
		private int row;

		private GridHandler(final Grid g1) {
			this.g = g1;
		}

		@Override
		public void add(final Widget w) {
			checkRows();
			g.setWidget(row, column, w);
			next();
		}

		@Override
		public void text(final String text) {
			checkRows();
			g.setText(row, column, text);
			next();
		}

		private void checkRows() {
			if (row >= g.getRowCount()) {
				g.resizeRows(row + 1);
			}
		}

		private void next() {
			column++;
			if (column == g.getColumnCount()) {
				column = 0;
				row++;
			}
		}
	}

	// todo remove this method
	/**
	 * div.
	 * 
	 * @param w
	 *            w
	 * @param styleName
	 *            styleName
	 * 
	 * @return WidgetBuilder
	 */
	WidgetBuilder div(final Widget w, final String styleName) {
		SimplePanel p = new SimplePanel();
		p.setStyleName(styleName);
		p.setWidget(w);
		add(p);
		return this;
	}

	/**
	 * Добавляет div с определённым стилем.
	 * 
	 * @param styleName
	 *            имя стиля
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder div(final String styleName) {
		SimplePanel p = new SimplePanel();
		p.setStyleName(styleName);
		add(p);
		push(new DivHandler(p));
		return this;
	}

	/**
	 * DivHandler extends Handler.
	 */
	private final class DivHandler extends Handler {
		/**
		 * p.
		 */
		private final SimplePanel p;

		private DivHandler(final SimplePanel p1) {
			this.p = p1;
		}

		@Override
		public void add(final Widget w) {
			p.setWidget(w);
		}
	}

	/**
	 * Устанавливает стиль на виджет.
	 * 
	 * @param styleName
	 *            имя стиля
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder style(final String styleName) {
		getWidget().setStyleName(styleName);
		return this;
	}

	/**
	 * Устанавливает доступность виджета.
	 * 
	 * @param e
	 *            true, если виджет должен быть доступен.
	 * 
	 * @return WidgetBuilder
	 */
	public WidgetBuilder enable(final boolean e) {
		((FocusWidget) lastAdded).setEnabled(e);
		return this;
	}

	/**
	 * Завершает построение.
	 * 
	 * @return Widget
	 */
	public Widget done() {
		return root;
	}

	/**
	 * Указывает, есть ли субвиджеты.
	 * 
	 * @return HasWidgets
	 */
	public HasWidgets getHasWidgets() {
		return peek().getHasWidgets();
	}

	/**
	 * Возвращает виджет.
	 * 
	 * @return Widget
	 */
	public Widget getWidget() {
		return lastAdded;
	}

	/**
	 * Возвращает виджет, сведённый к определённому типу.
	 * 
	 * @param <W>
	 *            тип, к которому необходимо свести виджет
	 * @param clazz
	 *            класс соответствующего типа
	 * 
	 * @return <W extends Widget> W
	 */
	@SuppressWarnings("unchecked")
	public <W extends Widget> W getWidget(final Class<W> clazz) {
		// noinspection unchecked
		return (W) lastAdded;
	}

	/**
	 * Возвращает непосредственный контейнер.
	 * 
	 * @param <W>
	 *            класс виджета
	 * @param clazz
	 *            класс виджета
	 * 
	 * @return <W extends Widget> W
	 */
	@SuppressWarnings("unchecked")
	public <W extends Widget> W getContainerWidget(final Class<W> clazz) {
		// noinspection unchecked
		return (W) getHasWidgets();
	}
}
