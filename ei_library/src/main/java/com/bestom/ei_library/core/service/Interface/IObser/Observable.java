package com.bestom.ei_library.core.service.Interface.IObser;

/**
 * 被观察者 接口
 */
public interface Observable {

    public void registerObserver(Observer o);

    public void removeObserver(Observer o);

    public void notifyObserver();

}
