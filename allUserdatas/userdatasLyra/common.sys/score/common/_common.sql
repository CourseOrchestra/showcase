CREATE GRAIN common VERSION '1.02';

-- *** TABLES ***
CREATE TABLE htmlHints(
  /**Текст подсказок*/
  elementId VARCHAR(50) NOT NULL,
  htmlText TEXT,
  showOnLoad INT,
  fullScreen INT,
  CONSTRAINT pk_htmlHints PRIMARY KEY (elementId)
);

CREATE TABLE htmlHintsUsers(
  /**Настройки отображения подсказок пользователям*/
  elementId VARCHAR(50) NOT NULL,
  sid VARCHAR(200) NOT NULL,
  showOnLoad INT,
  CONSTRAINT pk_htmlHintsUsers PRIMARY KEY (elementId, sid)
) WITH NO VERSION CHECK;

CREATE TABLE linesOfNumbersSeries(
  /**Линии серий номеров*/
  seriesId VARCHAR(50) NOT NULL,
  numberOfLine INT NOT NULL,
  startingDate DATETIME NOT NULL,
  startingNumber INT NOT NULL,
  endingNumber INT NOT NULL,
  incrimentByNumber INT NOT NULL,
  lastUsedNumber INT,
  prefix VARCHAR(20) NOT NULL DEFAULT '',
  postfix VARCHAR(20) NOT NULL DEFAULT '',
  isFixedLength BIT NOT NULL DEFAULT 'TRUE',
  isOpened BIT NOT NULL DEFAULT 'TRUE',
  lastUsedDate DATETIME,
  CONSTRAINT pk_linesOfNumbersSeries PRIMARY KEY (seriesId, numberOfLine)
);

CREATE TABLE numbersSeries(
  /**Серии номеров*/
  id VARCHAR(50) NOT NULL,
  description VARCHAR(250),
  CONSTRAINT pk_numbersSeries PRIMARY KEY (id)
);

-- *** FOREIGN KEYS ***
ALTER TABLE htmlHintsUsers ADD CONSTRAINT fk_common_hintsUsers_hint FOREIGN KEY (elementId) REFERENCES common.htmlHints(elementId);
ALTER TABLE htmlHintsUsers ADD CONSTRAINT fk_common_hintsUsers_subjects FOREIGN KEY (sid) REFERENCES security.subjects(sid) ON DELETE CASCADE;
ALTER TABLE linesOfNumbersSeries ADD CONSTRAINT fk_common_lines_series FOREIGN KEY (seriesId) REFERENCES common.numbersSeries(id);
-- *** INDICES ***
-- *** VIEWS ***
