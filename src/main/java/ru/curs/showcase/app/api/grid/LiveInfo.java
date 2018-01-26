package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Информация о LiveGrid - offset, limit, totalCount и идентификатор первого
 * элемента на странице.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LiveInfo implements SerializableElement {

	private static final long serialVersionUID = -8228599035165877092L;

	@XmlAttribute
	private int offset = -1;
	@XmlAttribute
	private int limit;
	@XmlAttribute
	private int totalCount;

	@XmlAttribute
	private int pageNumber = 1;

	public LiveInfo(final int aDefOffset, final int aDefLimit) {
		offset = aDefOffset;
		limit = aDefLimit;
		totalCount = 0;
	}

	public LiveInfo() {
		super();
	}

	@Override
	public String toString() {
		return "LiveInfo [offset=" + offset + ", limit=" + limit + ", totalCount=" + totalCount
				+ "]";
	}

	public int getFirstRecord() {
		// return limit * (offset - 1) + 1;
		return offset + 1;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(final int aOffset) {
		offset = aOffset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(final int aLimit) {
		limit = aLimit;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(final int aTotalCount) {
		totalCount = aTotalCount;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(final int aPageNumber) {
		pageNumber = aPageNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + offset;
		result = prime * result + limit;
		result = prime * result + totalCount;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LiveInfo)) {
			return false;
		}
		LiveInfo other = (LiveInfo) obj;
		if (offset != other.offset) {
			return false;
		}
		if (limit != other.limit) {
			return false;
		}
		return true;
	}
}
