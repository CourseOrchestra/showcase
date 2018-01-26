package ru.curs.showcase.util;

import java.io.*;

/**
 * Преобразователь для потоков Java. Преобразовывает InputStream в OutputStream
 * и обратно, может сделать копию InputStream. После преобразования потока
 * исходный поток закрывается, поэтому дальше нужно вызывать функцию getCopy()
 * столько раз, сколько экземпляров потоков вам нужно.
 * 
 * @author den
 * 
 */
public class StreamConvertor {
	/**
	 * Внутреннее хранилище данных.
	 */
	private final ByteArrayOutputStream internal = new ByteArrayOutputStream();
	/**
	 * Входной InputStream.
	 */
	private final InputStream input;

	public StreamConvertor(final InputStream is) throws IOException {
		input = is;
		copy();
	}

	private void copy() throws IOException {
		int chunk = 0;
		final int bufLen = 2048;
		byte[] data = new byte[bufLen];

		while (-1 != (chunk = input.read(data))) {
			internal.write(data, 0, chunk);
		}
	}

	public InputStream getCopy() {
		return outputToInputStream(internal);
	}

	public ByteArrayOutputStream getOutputStream() {
		return internal;
	}

	/**
	 * Преобразует входной поток в выходной.
	 * 
	 * @param stream
	 *            - входной поток.
	 * @return - выходной поток.
	 * @throws IOException
	 */
	public static ByteArrayOutputStream inputToOutputStream(final InputStream stream)
			throws IOException {
		StreamConvertor dup = new StreamConvertor(stream);
		ByteArrayOutputStream out = dup.getOutputStream();
		return out;
	}

	/**
	 * Преобразует выходной поток в входной.
	 * 
	 * @param stream
	 *            - выходной поток.
	 * @return - выходной поток.
	 */
	public static InputStream outputToInputStream(final ByteArrayOutputStream stream) {
		return new ByteArrayInputStream(stream.toByteArray());
	}

}