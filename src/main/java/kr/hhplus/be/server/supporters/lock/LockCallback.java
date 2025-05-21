package kr.hhplus.be.server.supporters.lock;

@FunctionalInterface
public interface LockCallback<T> {

    T doInLock() throws Throwable;
}