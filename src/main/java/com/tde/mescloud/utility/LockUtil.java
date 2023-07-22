package com.tde.mescloud.utility;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Setter
@Getter
@Component
public class LockUtil {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition executeCondition = lock.newCondition();
    private boolean isExecutionComplete = false;

    public void waitForExecute() throws InterruptedException {
        lock.lock();
        try {
            while (!isExecutionComplete) {
                executeCondition.await();
            }
            isExecutionComplete = false; // Reset the flag for the next round of execution
        } finally {
            lock.unlock();
        }
    }

    public void signalExecute() {
        lock.lock();
        try {
            isExecutionComplete = true;
            executeCondition.signal();
        } finally {
            lock.unlock();
        }
    }

}
