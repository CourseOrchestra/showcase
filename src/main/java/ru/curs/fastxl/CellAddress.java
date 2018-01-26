package ru.curs.fastxl;

import java.util.regex.*;

/**
 * Преобразует адрес ячейки в формате A1 в пару "строка, столбец" и обратно.
 * 
 */
public final class CellAddress {

	private static final int RADIX = 'Z' - 'A' + 1;
	private static final Pattern CELL_ADDRESS = Pattern.compile("([A-Z]+)([0-9]+)");

	private int irow;
	private int icol;

	public CellAddress(final String address) {
		setAddress(address);
	}

	public CellAddress(final int col, final int row) {
		this.icol = col;
		this.irow = row;
	}

	/**
	 * Возвращает номер строки.
	 */
	public int getRow() {
		return irow;
	}

	/**
	 * Возвращает номер колонки.
	 */
	public int getCol() {
		return icol;
	}

	/**
	 * Устанавливает номер строки.
	 * 
	 * @param row
	 *            номер строки
	 */
	public void setRow(final int row) {
		this.irow = row;
	}

	/**
	 * Устанавливает номер колонки.
	 * 
	 * @param col
	 *            номер колонки
	 * 
	 */
	public void setCol(final int col) {
		this.icol = col;
	}

	/**
	 * Возвращает адрес.
	 */
	public String getAddress() {
		int c = icol;
		String sc = "";
		do {
			int digit = c % RADIX;
			char d;
			if (digit == 0) {
				c -= RADIX;
				d = 'Z';
			} else {
				d = (char) (digit + 'A' - 1);
			}

			sc = d + sc;

			c /= RADIX;

		} while (c > 0);
		return sc + String.valueOf(irow);
	}

	/**
	 * Устанавливает адрес.
	 * 
	 * @param address
	 *            адрес
	 */
	public void setAddress(final String address) {
		Matcher m = CELL_ADDRESS.matcher(address);
		m.matches();
		irow = Integer.parseInt(m.group(2));

		icol = 0;
		String a = m.group(1);
		for (int i = 0; i < a.length(); i++) {
			icol = icol * RADIX;
			char c = a.charAt(i);
			int d = c - 'A' + 1;
			icol += d;
		}
	}

}
