package ru.curs.showcase.app.api;

/**
 * Сообщение для пользователя решения, созданного с помощью Showcase.
 * 
 * @author den
 * 
 */
public class UserMessage implements SerializableElement {

	private static final long serialVersionUID = 7453520039334421681L;

	/**
	 * Идентификатор сообщения.
	 */
	private String id;

	/**
	 * Текст сообщения.
	 */
	private String text;

	/**
	 * Тип сообщения.
	 */
	private MessageType type;

	/**
	 * Заголовок сообщения.
	 */
	private String caption = null;

	/**
	 * Подтип сообщения.
	 */
	private String subtype = null;

	public UserMessage() {
		super();
	}

	public UserMessage(final String aText) {
		super();
		text = aText;
		type = MessageType.ERROR;
	}

	public UserMessage(final String aText, final MessageType aType) {
		super();
		text = aText;
		type = aType;
	}

	public UserMessage(final String aText, final MessageType aType, final String aCaption) {
		super();
		text = aText;
		type = aType;
		caption = aCaption;
	}

	public UserMessage(final String aText, final MessageType aType, final String aCaption,
			final String aSubtype) {
		super();
		text = aText;
		type = aType;
		caption = aCaption;
		subtype = aSubtype;
	}

	public UserMessage(final String aId, final String aText) {
		super();
		id = aId;
		text = aText;
		type = MessageType.ERROR;
	}

	public UserMessage(final String aId, final String aText, final MessageType aType) {
		super();
		id = aId;
		text = aText;
		type = aType;
	}

	public UserMessage(final String aId, final String aText, final MessageType aType,
			final String aCaption) {
		super();
		id = aId;
		text = aText;
		type = aType;
		caption = aCaption;
	}

	public UserMessage(final String aId, final String aText, final MessageType aType,
			final String aCaption, final String aSubtype) {
		super();
		id = aId;
		text = aText;
		type = aType;
		caption = aCaption;
		subtype = aSubtype;
	}

	public String getText() {
		return text;
	}

	public void setText(final String aText) {
		text = aText;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(final MessageType aType) {
		type = aType;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(final String aCaption) {
		caption = aCaption;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(final String aSubtype) {
		subtype = aSubtype;
	}

}
