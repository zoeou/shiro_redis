package com.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import redis.clients.jedis.Jedis;

public class Test  {
	static int ThreadNums = 50;
	protected static final CountDownLatch MC_COUNT_DOWN_LATCH = new CountDownLatch(ThreadNums);
	private static final AtomicInteger count = new AtomicInteger(0);
	
	public static void main(String[] args) {
		final TonyPool tonyPool = new OnePool();
		tonyPool.init(10,2000);
		for (int i = 0; i < ThreadNums; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					int i = 0;
					while(i<10) {
						i++;
						Jedis jedis = null;
						try {
							MC_COUNT_DOWN_LATCH.countDown();
							MC_COUNT_DOWN_LATCH.await();
							jedis = tonyPool.borrowResource();
							jedis.incr("aaa");
							count.incrementAndGet();
						}catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}).start();
		}
	}

}
