package ru.curs.fastxl;

import ru.curs.celesta.CelestaException;
import ru.curs.celesta.dbutils.BasicCursor;
import ru.curs.lyra.*;

/**
 * LyraGridRecordSet.
 */
public class LyraGridRecordSet implements GridRecordSet {

	private Object[] buf;
	private final BasicCursor c;
	private final LyraFormField[] m;

	private final BasicGridForm basicGridForm;

	private boolean firstRecord = true;

	public LyraGridRecordSet(final BasicCursor cursor, final BasicGridForm aBasicGridForm)
			throws CelestaException {

		basicGridForm = aBasicGridForm;

		c = cursor;
		// c = cursor._getBufferCopy(cursor.callContext());
		// c.copyFiltersFrom(cursor);
		// c.copyOrderFrom(cursor);

		m = basicGridForm.getFieldsMeta().values().stream().filter(LyraFormField::isVisible)
				.toArray(LyraFormField[]::new);
		c.tryFirst();

		initBuf();

	}

	@Override
	public boolean next() throws EFastXLRuntime {
		if (firstRecord) {
			firstRecord = false;
			return true;
		}
		try {
			boolean result = c.next();
			initBuf();
			return result;
		} catch (CelestaException e) {
			throw new EFastXLRuntime(e);
		}
	}

	private void initBuf() throws CelestaException {
		LyraFormData lfd =
			new LyraFormData(c, basicGridForm.getFieldsMeta(), basicGridForm._getId());
		buf = lfd.getFields().stream().filter(fv -> fv.meta().isVisible())
				.map(LyraFieldValue::getValue).toArray();
	}

	@Override
	public boolean isInteger(final int i) throws EFastXLRuntime {
		return m[i - 1].getType() == LyraFieldType.INT;
	}

	@Override
	public boolean isFloat(final int i) throws EFastXLRuntime {
		return m[i - 1].getType() == LyraFieldType.REAL;
	}

	@Override
	public String getColumnName(final int i) throws EFastXLRuntime {
		return m[i - 1].getCaption();
	}

	@Override
	public int getColumnCount() throws EFastXLRuntime {
		return m.length;
	}

	@Override
	public double getDouble(final int i) throws EFastXLRuntime {
		Object v = buf[i - 1];
		if (v == null) {
			return 0;
		} else if (v instanceof Double) {
			return (Double) v;
		} else {
			return Double.parseDouble(v.toString());
		}
	}

	@Override
	public int getInt(final int i) throws EFastXLRuntime {
		Object v = buf[i - 1];
		if (v == null) {
			return 0;
		} else if (v instanceof Integer) {
			return (Integer) v;
		} else {
			return Integer.parseInt(v.toString());
		}
	}

	@Override
	public String getString(final int i) throws EFastXLRuntime {
		Object v = buf[i - 1];
		if (v == null) {
			return "";
		} else {
			return v.toString();
		}
	}

}
