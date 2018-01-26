package ru.curs.showcase.util;

import java.awt.Color;
import java.io.*;

import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.*;

import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;
import ru.curs.showcase.util.exception.SVGConvertException;

/**
 * Класс с утилитами для работы с SVG графикой. Используется библиотека Batik
 * (http://xmlgraphics.apache.org/batik/).
 * 
 * @author den
 * 
 */
public final class SVGConvertor {

	private final GeoMapExportSettings exportSettings;

	public SVGConvertor() {
		super();
		exportSettings = new GeoMapExportSettings();
	}

	public SVGConvertor(final GeoMapExportSettings aExportSettings) {
		super();
		exportSettings = aExportSettings;
	}

	public ByteArrayOutputStream svgStringToJPG(final String svg) {
		InputStream is = TextUtils.stringToStream(svg);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		output = (ByteArrayOutputStream) svgToJPGBaseMethod(is, output);
		return output;
	}

	public ByteArrayOutputStream svgStringToPNG(final String svg) {
		InputStream is = TextUtils.stringToStream(svg);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		output = (ByteArrayOutputStream) svgToPNGBaseMethod(is, output);
		return output;
	}

	private OutputStream svgToJPGBaseMethod(final InputStream is, final OutputStream os) {
		try {
			ImageTranscoder t = new JPEGTranscoder();
			final double percentDivider = 100.0;
			t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
					new Float(exportSettings.getJpegQuality() / percentDivider));
			if (exportSettings.getBackgroundColor() != null) {
				t.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR,
						Color.decode(exportSettings.getBackgroundColor()));
			}
			setTranscodeOptions(t);
			TranscoderInput input = new TranscoderInput(is);
			TranscoderOutput output = new TranscoderOutput(os);
			t.transcode(input, output);
			os.flush();
		} catch (TranscoderException | IOException e) {
			throw new SVGConvertException(e);
		}
		return os;
	}

	private void setTranscodeOptions(final ImageTranscoder t) {
		if (exportSettings.getHeight() != null) {
			t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT,
					Float.valueOf(exportSettings.getHeight()));
		}
		if (exportSettings.getWidth() != null) {
			t.addTranscodingHint(ImageTranscoder.KEY_WIDTH,
					Float.valueOf(exportSettings.getWidth()));
		}
		t.addTranscodingHint(ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE, true);
	}

	private OutputStream svgToPNGBaseMethod(final InputStream is, final OutputStream os) {
		try {
			ImageTranscoder t = new PNGTranscoder();
			setTranscodeOptions(t);
			TranscoderInput input = new TranscoderInput(is);
			TranscoderOutput output = new TranscoderOutput(os);
			t.transcode(input, output);
			os.flush();
		} catch (TranscoderException | IOException e) {
			throw new SVGConvertException(e);
		}
		return os;
	}

	public void svgFileToJPGFile(final String svgFile, final String jpegFile) {
		InputStream is = FileUtils.loadClassPathResToStream(svgFile);
		try {
			try (OutputStream output = new FileOutputStream(jpegFile)) {
				svgToJPGBaseMethod(is, output);
			}
		} catch (IOException e) {
			throw new SVGConvertException(e);
		}
	}

	public void svgFileToPNGFile(final String svgFile, final String pngFile) {
		InputStream is = FileUtils.loadClassPathResToStream(svgFile);
		try {
			try (OutputStream output = new FileOutputStream(pngFile)) {
				svgToPNGBaseMethod(is, output);
			}
		} catch (IOException e) {
			throw new SVGConvertException(e);
		}
	}
}
