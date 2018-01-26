package ru.curs.showcase.app.api;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Абстрактный базовый интерфейс. Первоначальное назначение - единая точка для
 * задания IsSerializable или Serializable.
 * 
 * @author den
 * 
 */
public interface SerializableElement extends IsSerializable, Serializable {

}
