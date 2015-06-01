package com.bytes.net.client;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangxiaojie
 *         2015-05-31 22:54
 *         功能介绍:
 */
public class SyncTest {

    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        protected boolean tryAcquire(int acquires) {
            return getState()==done?true:false;
        }

        protected  boolean tryRelease(int releases) {
            if (getState() == pending) {
                if (compareAndSetState(pending, 1)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone(){
            getState();
            return getState()==done;
        }
    }

    @Test
    public void testSync() throws InterruptedException {
        Sync sync = new Sync();
        TimeUnit timeUnit = TimeUnit.SECONDS;
        sync.tryAcquireNanos(-1,timeUnit.toNanos(10));
        System.out.println("ok");
    }


    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    class Thread1 extends Thread{
        @Override
        public void run() {
            lock.lock();
            try{
                condition.await();
                System.out.println("aaa");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    class Thread2 extends Thread{
        @Override
        public void run() {

            lock.lock();
            try{
                condition.signalAll();
                System.out.printf("bbb");
            }catch (Throwable t){
                t.printStackTrace();
            }
            finally {
                lock.unlock();
            }
        }
    }

    @Test
    public void testCondition() throws InterruptedException {
        Thread t2 = new Thread2();
        t2.start();

        Thread t1 = new Thread1();
        t1.start();


        t1.join();
        t2.join();

    }

}
