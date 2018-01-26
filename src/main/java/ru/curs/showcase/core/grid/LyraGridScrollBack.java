package ru.curs.showcase.core.grid;

import java.io.IOException;

import ru.curs.lyra.BasicGridForm;
import ru.curs.showcase.app.api.grid.GridToExcelExportType;
import ru.curs.showcase.core.command.GeneralExceptionFactory;

/**
 * Класс для обработки обратного движения ползунка.
 * 
 */
public class LyraGridScrollBack implements Runnable {

	public static final int DGRID_MAX_TOTALCOUNT = 50000;
	public static final int DGRID_SMALLSTEP = 100;
	private static final int LYRA_SMALLFACTOR = 100;

	private javax.websocket.Session webSocketSession;

	private BasicGridForm basicGridForm;

	private LyraGridAddInfo lyraGridAddInfo = new LyraGridAddInfo();

	public javax.websocket.Session getWebSocketSession() {
		return webSocketSession;
	}

	public void setWebSocketSession(final javax.websocket.Session aWebSocketSession) {
		webSocketSession = aWebSocketSession;
	}

	public LyraGridAddInfo getLyraGridAddInfo() {
		return lyraGridAddInfo;
	}

	public void setLyraGridAddInfo(final LyraGridAddInfo aLyraGridAddInfo) {
		lyraGridAddInfo = aLyraGridAddInfo;
	}

	public BasicGridForm getBasicGridForm() {
		return basicGridForm;
	}

	public void setBasicGridForm(final BasicGridForm aBasicGridForm) {
		basicGridForm = aBasicGridForm;
	}

	@Override
	public void run() {

		if (lyraGridAddInfo.getExcelExportType() == GridToExcelExportType.ALL) {
			return;
		}

		System.out.println("LyraGridScrollBack.ddddddddddddd2");
		System.out.println("className: " + basicGridForm.getClass().getSimpleName());
		System.out.println("lyraOldPosition: " + lyraGridAddInfo.getLyraOldPosition());
		System.out.println("lyraNewPosition: " + basicGridForm.getTopVisiblePosition());
		System.out.println("diff: "
				+ (basicGridForm.getTopVisiblePosition() - lyraGridAddInfo.getLyraOldPosition()));
		System.out.println("getApproxTotalCount: " + basicGridForm.getApproxTotalCount());

		// ---------------------------------------

		int lyraApproxTotalCount = basicGridForm.getApproxTotalCount();
		if (lyraApproxTotalCount == 0) {
			return;
		}

		if (Math.abs(basicGridForm.getTopVisiblePosition()
				- lyraGridAddInfo.getLyraOldPosition()) <= lyraApproxTotalCount
						/ LYRA_SMALLFACTOR) {
			lyraGridAddInfo.setLyraOldPosition(basicGridForm.getTopVisiblePosition());
			return;
		}

		if (lyraGridAddInfo.getExcelExportType() == GridToExcelExportType.CURRENTPAGE) {
			return;
		}

		int dgridNewPosition;
		if (lyraApproxTotalCount <= DGRID_MAX_TOTALCOUNT) {
			dgridNewPosition = basicGridForm.getTopVisiblePosition();
		} else {
			double d = basicGridForm.getTopVisiblePosition();
			d = (d / lyraApproxTotalCount) * lyraGridAddInfo.getDgridOldTotalCount();
			dgridNewPosition = (int) d;
		}

		lyraGridAddInfo.setLyraOldPosition(basicGridForm.getTopVisiblePosition());

		if (webSocketSession != null) {
			try {
				if (webSocketSession.isOpen()) {
					webSocketSession.getBasicRemote().sendText(String.valueOf(dgridNewPosition));
				} else {
					System.out.println("webSocketSession is closed");
					lyraGridAddInfo.setNeedRecreateWebsocket(true);
				}
			} catch (IOException e) {
				throw GeneralExceptionFactory.build(e);
			}
		} else {
			if (dgridNewPosition != 0) {
				System.out.println("webSocketSession is null");
			}
		}

	}
}
