package ru.curs.showcase.app.api;

/**
 * Информация о деталях пользователя (таких как e-mail, sid пользователя, полное
 * имя пользователя, телефон), например получаемая из AuthServer.
 * 
 * @author anlug
 * 
 */
public final class UserInfo implements SerializableElement {

	private static final long serialVersionUID = -5321878237734917288L;

	/**
	 * Группа провайдеров пользователя или домен.
	 */
	private String groupProviders;
	/**
	 * Логин пользователя.
	 */
	private String login;
	/**
	 * SID пользователя.
	 */
	private String sid;
	/**
	 * Имя пользователя.
	 */
	private String name;
	/**
	 * Почтовый адрес пользователя.
	 */
	private String email;
	/**
	 * Телефон пользователя.
	 */
	private String phone;
	// /**
	// * Дополнительный параметр.
	// */
	// private String additionalParameter;
	private String[] additionalParameters;

	/**
	 * СНИЛС пользователя.
	 */
	private String snils = null;
	/**
	 * Пол пользователя.
	 */
	private String gender = null;

	/**
	 * Имя пользователя.
	 */
	private String firstName = null;
	/**
	 * Фамилия пользователя.
	 */
	private String lastName = null;
	/**
	 * Отчество пользователя.
	 */
	private String middleName = null;

	/**
	 * Дата рождения пользователя.
	 */
	private String birthDate = null;
	/**
	 * Место рождения пользователя.
	 */
	private String birthPlace = null;

	/**
	 * Является ли пользователь подтвержденным.
	 */
	private boolean trusted = false;

	/**
	 * Код ответа AuthServer.
	 */
	private int responseCode;

	public UserInfo(final String aLogin, final String aSid, final String aName,
			final String aEmail, final String aPhone, final String agroupProviders,
			final String... anAdditionalParameter) {
		this.login = aLogin;
		this.sid = aSid;
		this.name = aName;
		this.email = aEmail;
		this.phone = aPhone;
		this.groupProviders = agroupProviders;
		this.additionalParameters = anAdditionalParameter;
	}

	public UserInfo(final String aLogin, final String aSid, final String aName,
			final String aEmail, final String aPhone, final String... anAdditionalParameter) {
		this.login = aLogin;
		this.sid = aSid;
		this.name = aName;
		this.email = aEmail;
		this.phone = aPhone;
		this.groupProviders = null;
		this.additionalParameters = anAdditionalParameter;
	}

	public UserInfo(final String aLogin, final String aSid, final String aName,
			final String aEmail, final String aPhone, final String agroupProviders) {
		this.login = aLogin;
		this.sid = aSid;
		this.name = aName;
		this.email = aEmail;
		this.phone = aPhone;
		this.groupProviders = agroupProviders;
	}

	public UserInfo() {
		super();
	}

	public String getLogin() {
		return login;
	}

	public String getSid() {
		return sid;
	}

	public String getEmail() {
		return email;
	}

	public String getCaption() {
		return login;
	}

	public String getFullName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(final int aResponseCode) {
		responseCode = aResponseCode;
	}

	public String getGroupProviders() {
		return groupProviders;
	}

	public void setGrouProviders(final String agroupProviders) {
		this.groupProviders = agroupProviders;
	}

	public String[] getAdditionalParameters() {
		return additionalParameters;
	}

	public String getSnils() {
		return snils;
	}

	public void setSnils(final String aSnils) {
		snils = aSnils;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String aGender) {
		gender = aGender;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(final String aBirthDate) {
		birthDate = aBirthDate;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(final String aBirthPlace) {
		birthPlace = aBirthPlace;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String aFirstName) {
		firstName = aFirstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String aLastName) {
		lastName = aLastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(final String aMiddleName) {
		middleName = aMiddleName;
	}

	public boolean isTrusted() {
		return trusted;
	}

	public void setTrusted(final boolean aTrusted) {
		trusted = aTrusted;
	}

}