package ru.curs.showcase.core.event;

import ru.curs.showcase.app.api.event.Activity;
import ru.curs.showcase.core.celesta.CelestaHelper;

/**
 * Шлюз для WebText и xforms, источник данных для которого является Celesta.
 * 
 * @author bogatov
 * 
 */
public class ActivityCelestaGateway implements ActivityGateway {

	@Override
	public void exec(final Activity activity) {
		CelestaHelper<Void> helper = new CelestaHelper<Void>(activity.getContext(), Void.class);
		helper.runPython(activity.getName());
	}

}
