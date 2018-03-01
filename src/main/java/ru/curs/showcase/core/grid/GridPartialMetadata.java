package ru.curs.showcase.core.grid;

/**
 * GridMetadata для forceLoadSettings = true.
 */
public class GridPartialMetadata {

	private int totalCount;

	private String header = null;
	private String footer = null;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(final int aTotalCount) {
		totalCount = aTotalCount;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(final String aHeader) {
		header = aHeader;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(final String aFooter) {
		footer = aFooter;
	}

}
