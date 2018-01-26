package ru.curs.fastxl;

import java.io.*;

/**
 * Тест функционала генерации Excel файла.
 * 
 */
public final class Main {

	/**
	 * Тестовый набор данных грида.
	 * 
	 */
	static class Test implements GridRecordSet {

		private final String[] names = { "col1", "col2", "col3" };
		private final int[] v1 = { 12, 1412, 23, 33 };
		private final String[] v2 = { "asdf", "ww22", "wpwp 2pp2", "ppsbwg" };
		private final double[] v3 = { 2.12, 35.671, 8.1, 1.1 };

		private int position = -1;
		private final int fdType = 3;

		@Override
		public boolean next() throws EFastXLRuntime {
			position++;
			return position < v1.length;
		}

		@Override
		public boolean isInteger(final int i) throws EFastXLRuntime {
			return i == 1;
		}

		@Override
		public boolean isFloat(final int i) throws EFastXLRuntime {
			return i == fdType;
		}

		@Override
		public String getColumnName(final int i) throws EFastXLRuntime {

			return names[i - 1];
		}

		@Override
		public int getColumnCount() throws EFastXLRuntime {

			return names.length;
		}

		@Override
		public double getDouble(final int i) throws EFastXLRuntime {
			if (i == fdType) {
				return v3[position];
			} else {
				throw new EFastXLRuntime("");
			}
		}

		@Override
		public int getInt(final int i) throws EFastXLRuntime {
			if (i == 1) {
				return v1[position];
			} else {
				throw new EFastXLRuntime("");
			}
		}

		@Override
		public String getString(final int i) throws EFastXLRuntime {
			if (i == 2) {
				return v2[position];
			} else {
				throw new EFastXLRuntime("");
			}
		}

	}

	private Main() {

	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws EFastXLRuntime
	 */
	public static void main(final String[] args) throws FileNotFoundException, EFastXLRuntime {
		FileOutputStream fos = new FileOutputStream("d:/result.xlsx");
		FastXLProcessor proc = new FastXLProcessor(new Test(), fos);
		proc.execute();
		System.out.println("done!");
	}
}
