package ru.curs.showcase.security.esia;

/**
 * Информация о пользователе, получаемая из ESIA.
 * 
 */
public class ESIAUserInfo {

	private long oid = 0;
	private String snils = null;

	private boolean trusted = false;

	private String firstName = null;
	private String lastName = null;
	private String middleName = null;
	private String gender = null;

	private String birthDate = null;
	private String birthPlace = null;

	private String phone = null;
	private String email = null;

	public String getLogin() {
		String login = snils;
		if (login == null) {
			login = email;
		}
		if (login == null) {
			login = phone;
		}
		return login;
	}

	public long getOid() {
		return oid;
	}

	public void setOid(final long aOid) {
		oid = aOid;
	}

	public String getSnils() {
		return snils;
	}

	public void setSnils(final String aSnils) {
		snils = aSnils;
	}

	public boolean isTrusted() {
		return trusted;
	}

	public void setTrusted(final boolean aTrusted) {
		trusted = aTrusted;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String aPhone) {
		phone = aPhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String aEmail) {
		email = aEmail;
	}

}