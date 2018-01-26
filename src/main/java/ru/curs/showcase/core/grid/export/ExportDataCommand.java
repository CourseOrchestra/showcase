package ru.curs.showcase.core.grid.export;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.Locale;

import ru.curs.fastxl.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.command.ServiceLayerCommand;

/**
 * Команда выгрузки данных в Excel.
 * 
 * @author bogatov
 * 
 */
public class ExportDataCommand extends ServiceLayerCommand<Void> {
	private static final int BUFFER_SIZE = 1024;

	private final DataPanelElementInfo elInfo;
	private final OutputStream out;
	private final ExportType exportType;

	public ExportDataCommand(final CompositeContext oContext, final DataPanelElementInfo oElInfo,
			final OutputStream oOut, final ExportType eExportType) {
		super(oContext);
		this.elInfo = oElInfo;
		this.exportType = eExportType;
		this.out = oOut;
	}

	@Override
	protected void mainProc() throws Exception {
		SourceSelector<ExportDataGateway> selector = new ExportDataSelector(this.elInfo);
		ExportDataGateway gateway = selector.getGateway();
		gateway.getExportData(getContext(), this.elInfo, new ResultSetHandler() {

			@Override
			public void onProcess(final ResultSet rs) throws Exception {
				if (rs != null) {
					if (ExportType.XLST.equals(exportType)) {
						JDBCRecordSet jdbcRecordSet = new JDBCRecordSet(rs);
						FastXLProcessor fastXLProcessor = new FastXLProcessor(jdbcRecordSet, out);
						fastXLProcessor.execute();
					} else {
						// По умолчанию выгрузка в csv
						InputStream in = getCsvInputStream(rs);
						try {
							writeData(in, out);
						} finally {
							if (in != null) {
								in.close();
							}
						}
					}
				}

			}

		});
	}

	private void writeData(final InputStream in, final OutputStream oOut) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int len;
		while ((len = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
			oOut.write(buffer, 0, len);
		}
	}

	private InputStream getCsvInputStream(final ResultSet rs) throws SQLException {
		StringBuilder sb = new StringBuilder();
		ResultSetMetaData rsMetaData = rs.getMetaData();
		int columCount = rsMetaData.getColumnCount();
		int[] columtTypes = new int[columCount];
		for (int i = 0; i < columCount; i++) {
			if (i != 0) {
				sb.append(";");
			}
			sb.append(rsMetaData.getColumnName(i + 1));
			columtTypes[i] = rsMetaData.getColumnType(i + 1);
		}
		sb.append("\n");
		while (rs.next()) {
			for (int i = 0; i < columCount; i++) {
				if (i != 0) {
					sb.append(";");
				}
				Object obj = rs.getObject(i + 1);
				if (obj != null) {
					switch (columtTypes[i]) {
					case Types.FLOAT:
					case Types.DOUBLE:
					case Types.NUMERIC:
						DecimalFormat format = new DecimalFormat();
						format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale
								.getDefault()));
						sb.append(format.format(obj));
						break;
					default:
						sb.append(obj.toString());
					}
				}
			}
			sb.append("\n");
		}

		return new ByteArrayInputStream(sb.toString().getBytes());
	}
}
