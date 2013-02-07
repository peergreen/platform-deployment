package com.peergreen.deployment.internal.model.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 14/01/13
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public class PersistenceException extends Exception {
    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
