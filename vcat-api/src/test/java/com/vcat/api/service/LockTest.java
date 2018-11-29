package com.vcat.api.service;

import com.vcat.common.lock.DistLockHelper;
import org.junit.Test;
import org.redisson.core.RLock;

import java.util.concurrent.TimeUnit;

/**
 * Created by ylin on 2015/9/1.
 */
public class LockTest {
    @Test
    public void test() {
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                RLock lock = DistLockHelper.getLock("testL");
                lock.lock(10, TimeUnit.SECONDS);
                System.out.println("locking...");
                lock.unlock();
            }).start();
        }

        try {
            Thread.sleep(9100000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
