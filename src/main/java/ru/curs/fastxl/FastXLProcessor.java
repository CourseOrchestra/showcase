package ru.curs.fastxl;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.*;

/**
 * Класс обработчика заданий. Одновременно запускается несколько обработчиков.
 * 
 */
public final class FastXLProcessor {

	private static final Pattern SHEET_NAME = Pattern.compile("xl/worksheets/sheet[0-9]+.xml",
			Pattern.CASE_INSENSITIVE);

	// Параметры задания
	private final GridRecordSet recordSet;
	private final OutputStream resultStream;

	/**
	 * Хак для ByteArrayOutputStream.
	 * 
	 */
	private static class ByteArrayOutputStreamHack extends ByteArrayOutputStream {
		byte[] getBuffer() {
			return buf;
		}
	}

	private final int bufferSize = 2048;
	private final byte[] buffer = new byte[bufferSize];

	public FastXLProcessor(final GridRecordSet gridRecordSet, final OutputStream outputStream) {
		this.recordSet = gridRecordSet;
		this.resultStream = outputStream;
	}

	/**
	 * Выполняет обработку задания по формирования xlsx-файла.
	 * 
	 * @throws EFastXLRuntime
	 *             В случае, если что-то пошло не так.
	 */
	// CHECKSTYLE:OFF
	public void execute() throws EFastXLRuntime {

		// ФАЗА 1: Чтение шита.
		XLSharedStrings sharedStrings = null;
		final HashMap<String, WorksheetProcessor> wsProcs =
			new HashMap<String, WorksheetProcessor>();
		InputStream fis = getTemplateInputStream();
		try {
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ientry;
			try {
				while ((ientry = zis.getNextEntry()) != null) {
					if ("xl/sharedStrings.xml".equalsIgnoreCase(ientry.getName())) {
						ByteArrayOutputStreamHack bos = readZis(zis);
						sharedStrings =
							new XLSharedStrings(new ByteArrayInputStream(bos.getBuffer(), 0,
									bos.size()));
					} else if (SHEET_NAME.matcher(ientry.getName()).matches()) {
						ByteArrayOutputStreamHack bos = readZis(zis);
						wsProcs.put(ientry.getName(), new WorksheetProcessor(
								new ByteArrayInputStream(bos.getBuffer(), 0, bos.size()),
								recordSet));
					}
				}
			} catch (IOException e) {
				throw new EFastXLRuntime("I/O Exception while decompressing template: "
						+ e.getMessage());
			}
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw new EFastXLRuntime("Failed to close template: " + e.getMessage());
			}
		}

		// ФАЗА 2: Трансформация, в ходе которой мутирует SharedStrings
		if (wsProcs.isEmpty()) {
			new EFastXLRuntime("Template has wrong format: no worksheet found.");
		}
		if (sharedStrings == null) {
			throw new EFastXLRuntime("Template has wrong format: no shared strings found.");
		}

		final HashMap<String, ByteArrayOutputStreamHack> resultSheets =
			new HashMap<String, ByteArrayOutputStreamHack>(wsProcs.size());
		for (Entry<String, WorksheetProcessor> e : wsProcs.entrySet()) {
			ByteArrayOutputStreamHack bos = new ByteArrayOutputStreamHack();
			e.getValue().transform(bos, sharedStrings);
			resultSheets.put(e.getKey(), bos);
		}
		// Больше процессоры страниц не нужны, они своё дело сделали...
		wsProcs.clear();

		// ФАЗА 3: Запись готового файла.
		fis = getTemplateInputStream();
		try {
			ZipInputStream zis = new ZipInputStream(fis);
			OutputStream os = resultStream;
			ZipOutputStream zos = new ZipOutputStream(os);
			ZipEntry ientry;
			try {
				while ((ientry = zis.getNextEntry()) != null) {
					ZipEntry oentry = new ZipEntry(ientry.getName());
					zos.putNextEntry(oentry);
					if ("xl/sharedStrings.xml".equals(ientry.getName())) {
						ByteArrayOutputStreamHack bos = new ByteArrayOutputStreamHack();
						sharedStrings.saveXML(bos);
						zos.write(bos.getBuffer(), 0, bos.size());
					} else if (SHEET_NAME.matcher(ientry.getName()).matches()) {
						ByteArrayOutputStreamHack bos = resultSheets.get(ientry.getName());
						// Нуллом bos быть не может --- мы же тот же самый zip
						// второй раз читаем...
						zos.write(bos.getBuffer(), 0, bos.size());
					} else {
						ByteArrayOutputStreamHack bos = readZis(zis);
						zos.write(bos.getBuffer(), 0, bos.size());
					}
					zos.closeEntry();
				}
				zos.finish();
			} catch (IOException e) {
				throw new EFastXLRuntime("I/O Exception while repacking template: "
						+ e.getMessage());
			}
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw new EFastXLRuntime("Failed to close template: " + e.getMessage());
			}
		}

	}

	private ByteArrayOutputStreamHack readZis(final ZipInputStream zis) throws IOException {
		ByteArrayOutputStreamHack os = new ByteArrayOutputStreamHack();
		int bytesRead;
		while ((bytesRead = zis.read(buffer)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		return os;
	}

	private InputStream getTemplateInputStream() {
		return FastXLProcessor.class.getResourceAsStream("template.xlsx");
	}

}
