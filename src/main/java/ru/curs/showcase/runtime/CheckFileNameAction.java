package ru.curs.showcase.runtime;

import java.io.*;

import ru.curs.showcase.util.FileAction;

/**
 * Действие по проверке названия файла.
 * 
 */

public class CheckFileNameAction implements FileAction {

	@Override
	public FileAction cloneForHandleChildDir(final String aChildDir) {
		CheckFileNameAction res = new CheckFileNameAction();
		return res;
	}

	@Override
	public void perform(final File sourceFile) throws IOException {
		if (sourceFile.isDirectory()) {
			return;
		}

		if (UserDataUtils.checkValueForSpace(sourceFile.getName())) {
			throw new FileNameContainsSpaceException(sourceFile.toString());
		}

		if (UserDataUtils.checkValueForCyrillicSymbols(sourceFile.getName())) {
			throw new FileNameContainsCyrillicSymbolException(sourceFile.toString());
		}

	}
}