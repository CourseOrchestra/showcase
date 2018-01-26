package ru.curs.showcase.core.frame;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.command.InputParam;

/**
 * Команда получения одного "специального" фрейма главной формы.
 * 
 * @author den
 * 
 */
public final class MainPageFrameGetCommand extends AbstractMainPageFrameCommand<String> {

	private final MainPageFrameType type;

	@InputParam
	public MainPageFrameType getType() {
		return type;
	}

	public MainPageFrameGetCommand(final CompositeContext aContext, final MainPageFrameType aType) {
		super(aContext);
		type = aType;
	}

	@Override
	protected void mainProc() throws Exception {
		String result = getRawMainPageFrame(getContext(), type);
		MainPageFrameFactory factory = new MainPageFrameFactory(true);
		setResult(factory.build(result));
	}

}
