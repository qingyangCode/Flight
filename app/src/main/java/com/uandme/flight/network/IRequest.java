package com.uandme.flight.network;

/**
 * Created by QingYang on 15/7/21.
 */
public interface IRequest<T> {
    void response(T t);
}
