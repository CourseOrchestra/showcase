package ru.curs.showcase.runtime;

import java.lang.management.*;
import java.util.Iterator;

/**
 * Вспомогательный класс для контроля и управления памятью JVM.
 * 
 * @author den
 * 
 */
public final class MemoryController {

	private static final String NOT_DEFINED = "не определено";
	private static final int KB_MB = 1024;

	private static String mbOutHandler(final long value) {
		return value / KB_MB / KB_MB + " Мб";
	}

	public static String getMaxHeap() {
		return mbOutHandler(Runtime.getRuntime().maxMemory());
	}

	public static String getAllFreeHeap() {
		return mbOutHandler(Runtime.getRuntime().maxMemory()
				- (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}

	public static String getUsedHeap() {
		return mbOutHandler(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
	}

	public static String getFreeInCommitedHeap() {
		return mbOutHandler(Runtime.getRuntime().freeMemory());
	}

	public static String getCommitedHeap() {
		return mbOutHandler(Runtime.getRuntime().totalMemory());
	}

	private static boolean isPermGenBean(final MemoryPoolMXBean bean) {
		return bean.getName().contains("Perm Gen");
	}

	private static MemoryPoolMXBean getPermGenBean() {
		Iterator<MemoryPoolMXBean> iter = ManagementFactory.getMemoryPoolMXBeans().iterator();
		while (iter.hasNext()) {
			MemoryPoolMXBean bean = iter.next();
			if (isPermGenBean(bean)) {
				return bean;
			}
		}
		return null;
	}

	public static String getUsedPermGen() {
		MemoryPoolMXBean bean = getPermGenBean();
		if (bean != null) {
			return mbOutHandler(bean.getUsage().getUsed());
		}
		return NOT_DEFINED;
	}

	public static String getMaxPermGen() {
		MemoryPoolMXBean bean = getPermGenBean();
		if (bean != null) {
			return mbOutHandler(bean.getUsage().getMax());
		}
		return NOT_DEFINED;
	}

	public static String getAllFreePermGen() {
		MemoryPoolMXBean bean = getPermGenBean();
		if (bean != null) {
			return mbOutHandler(bean.getUsage().getMax() - bean.getUsage().getUsed());
		}
		return NOT_DEFINED;
	}

	public static String getInitPermGen() {
		MemoryPoolMXBean bean = getPermGenBean();
		if (bean != null) {
			return mbOutHandler(bean.getUsage().getInit());
		}
		return NOT_DEFINED;
	}

	public static String getCommitedPermGen() {
		MemoryPoolMXBean bean = getPermGenBean();
		if (bean != null) {
			return mbOutHandler(bean.getUsage().getCommitted());
		}
		return NOT_DEFINED;
	}

	public static String getFreeInCommitedPermGen() {
		MemoryPoolMXBean bean = getPermGenBean();
		if (bean != null) {
			return mbOutHandler(bean.getUsage().getCommitted() - bean.getUsage().getUsed());
		}
		return NOT_DEFINED;
	}

	private MemoryController() {
		throw new UnsupportedOperationException();
	}
}
