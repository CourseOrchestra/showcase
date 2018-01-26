package ru.curs.showcase.util.xml;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.ExcludeFromSerialization;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * Используется при логировании входа в систему.
 */
@XmlRootElement(name = Action.CONTEXT_TAG)
@XmlAccessorType(XmlAccessType.FIELD)
public final class CompositeContextOnBasisOfUserAndSessionDetails extends CompositeContext {
	private static final long serialVersionUID = -2393106043162467339L;

	/**
	 * Используется при логировании входа в систему.
	 */
	@XmlTransient
	@ExcludeFromSerialization
	private UserAndSessionDetails userAndSessionDetails = null;

	public UserAndSessionDetails getUserAndSessionDetails() {
		return userAndSessionDetails;
	}

	public CompositeContextOnBasisOfUserAndSessionDetails(
			final UserAndSessionDetails aUserAndSessionDetails) {
		super();

		userAndSessionDetails = aUserAndSessionDetails;
	}

	public CompositeContextOnBasisOfUserAndSessionDetails() {
		super();
	}

}
