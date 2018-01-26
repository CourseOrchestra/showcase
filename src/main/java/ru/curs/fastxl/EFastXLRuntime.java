package ru.curs.fastxl;

/**
 * Ошибка выполнения процедуры создания отчёта.
 * 
 */
public class EFastXLRuntime extends Exception {
	private static final long serialVersionUID = 2747877507823456310L;

	public EFastXLRuntime(final String string) {
		super(string);
	}

	public EFastXLRuntime(final Exception e) {
		super(e);
	}
}
