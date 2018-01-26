package ru.curs.showcase.util;

import java.io.*;

import org.w3c.css.sac.*;
import org.w3c.flute.parser.Parser;

import ru.curs.showcase.util.exception.CSSReadException;

/**
 * Класс для считывания свойств из CSS. Код в основном заимствован из GWT.
 * 
 * @author den
 * 
 */
public final class CSSPropReader {

	/**
	 * Результат.
	 */
	private String result;

	/**
	 * Основная функция считывателя.
	 * 
	 * @param cssPath
	 *            - путь к CSS файлу относительно ClassPath.
	 * @return - значение свойства.
	 * 
	 * @param selectorName
	 *            - имя селектора.
	 * @param propName
	 *            - имя свойства внутри селектора.
	 * 
	 */
	public String read(final String cssPath, final String selectorName, final String propName) {
		Parser p = new Parser();

		p.setDocumentHandler(new DocumentHandler() {

			private boolean isHeaderGapSelector = false;

			@Override
			public void startSelector(final SelectorList selectors) {
				String name;
				for (int i = 0; i < selectors.getLength(); i++) {
					name = valueOf(selectors.item(i));
					if (selectorName.equals(name)) {
						isHeaderGapSelector = true;
						return;
					}
				}
			}

			@Override
			public void startPage(final String aArg0, final String aArg1) {
			}

			@Override
			public void startMedia(final SACMediaList aArg0) {
			}

			@Override
			public void startFontFace() {
			}

			@Override
			public void startDocument(final InputSource aArg0) {
			}

			@Override
			public void property(final String name, final LexicalUnit value, final boolean aArg2) {
				if (isHeaderGapSelector) {
					if (propName.equals(name)) {
						result = value.toString();
						return;
					}
				}
			}

			@Override
			public void namespaceDeclaration(final String aArg0, final String aArg1) {
			}

			@Override
			public void importStyle(final String aArg0, final SACMediaList aArg1,
					final String aArg2) {
			}

			@Override
			public void ignorableAtRule(final String aArg0) {
			}

			@Override
			public void endSelector(final SelectorList aArg0) {
				isHeaderGapSelector = false;
			}

			@Override
			public void endPage(final String aArg0, final String aArg1) {
			}

			@Override
			public void endMedia(final SACMediaList aArg0) {
			}

			@Override
			public void endFontFace() {
			}

			@Override
			public void endDocument(final InputSource aArg0) {
			}

			@Override
			public void comment(final String aArg0) {
			}
		});

		try {
			if (!(new File(cssPath).exists())) {
				throw new CSSReadException(String.format("Файл CSS '%s' не найден", cssPath));
			}
			p.parseStyleSheet(cssPath);
		} catch (CSSException e) {
			throw new CSSReadException(e);
		} catch (IOException e) {
			throw new CSSReadException(e);
		}
		return result;
	}

	private static boolean isIdentPart(final char c) {
		return Character.isLetterOrDigit(c) || (c == '\\') || (c == '-') || (c == '_');
	}

	private static boolean isIdentStart(final char c) {
		return Character.isLetter(c) || (c == '\\') || (c == '_');
	}

	private static String escapeIdent(final String aSelector) {
		String selector = aSelector;
		assert selector.length() > 0;

		StringBuilder toReturn = new StringBuilder();

		if (selector.charAt(0) == '-') {
			// Allow leading hyphen
			selector = selector.substring(1);
			toReturn.append('-');
		}

		if (!isIdentStart(selector.charAt(0))) {
			toReturn.append('\\');
		}
		toReturn.append(selector.charAt(0));

		if (selector.length() > 1) {
			for (char c : selector.substring(1).toCharArray()) {
				if (!isIdentPart(c)) {
					toReturn.append('\\');
				}
				toReturn.append(c);
			}
		}
		return toReturn.toString();
	}

	private static String valueOf(final Condition condition) {
		if (condition instanceof AttributeCondition) {
			return valueOfAttributeCondition(condition);
		} else if (condition instanceof CombinatorCondition) {
			return valueOfCombinatorCondition(condition);
		} else if (condition instanceof LangCondition) {
			LangCondition c = (LangCondition) condition;
			return ":lang(" + c.getLang() + ")";
		}
		return null;

	}

	private static String valueOfCombinatorCondition(final Condition condition) {
		CombinatorCondition c = (CombinatorCondition) condition;
		switch (condition.getConditionType()) {
		case Condition.SAC_AND_CONDITION:
			return valueOf(c.getFirstCondition()) + valueOf(c.getSecondCondition());
		case Condition.SAC_OR_CONDITION:
			// Unimplemented in CSS2?
			return null;
		default:
			return null;
		}
	}

	private static String valueOfAttributeCondition(final Condition condition) {
		AttributeCondition c = (AttributeCondition) condition;
		switch (c.getConditionType()) {
		case Condition.SAC_ATTRIBUTE_CONDITION:
			return "[" + c.getLocalName()
					+ (c.getValue() != null ? "=\"" + c.getValue() + '"' : "") + "]";
		case Condition.SAC_ONE_OF_ATTRIBUTE_CONDITION:
			return "[" + c.getLocalName() + "~=\"" + c.getValue() + "\"]";
		case Condition.SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION:
			return "[" + c.getLocalName() + "|=\"" + c.getValue() + "\"]";
		case Condition.SAC_ID_CONDITION:
			return "#" + c.getValue();
		case Condition.SAC_CLASS_CONDITION:
			return "." + c.getValue();
		case Condition.SAC_PSEUDO_CLASS_CONDITION:
			return ":" + c.getValue();
		default:
			return null;
		}
	}

	private static String valueOf(final Selector selector) {
		if (selector instanceof ConditionalSelector) {
			ConditionalSelector s = (ConditionalSelector) selector;
			String simpleSelector = valueOf(s.getSimpleSelector());

			if ("*".equals(simpleSelector)) {
				// Don't need the extra * for compound selectors
				return valueOf(s.getCondition());
			} else {
				return simpleSelector + valueOf(s.getCondition());
			}

		} else if (selector instanceof DescendantSelector) {
			DescendantSelector s = (DescendantSelector) selector;
			switch (s.getSelectorType()) {
			case Selector.SAC_CHILD_SELECTOR:
				if (s.getSimpleSelector().getSelectorType() == Selector.SAC_PSEUDO_ELEMENT_SELECTOR) {
					return valueOf(s.getAncestorSelector()) + ":" + valueOf(s.getSimpleSelector());
				} else {
					return valueOf(s.getAncestorSelector()) + ">" + valueOf(s.getSimpleSelector());
				}
			case Selector.SAC_DESCENDANT_SELECTOR:
				return valueOf(s.getAncestorSelector()) + " " + valueOf(s.getSimpleSelector());
			default:
				break;
			}

		} else if (selector instanceof ElementSelector) {
			ElementSelector s = (ElementSelector) selector;
			if (s.getLocalName() == null) {
				return "*";
			} else {
				return escapeIdent(s.getLocalName());
			}

		} else if (selector instanceof SiblingSelector) {
			SiblingSelector s = (SiblingSelector) selector;
			return valueOf(s.getSelector()) + "+" + valueOf(s.getSiblingSelector());
		}
		return null;
	}
}
