package com.service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import redis.clients.jedis.Jedis;

public class OnePool implements TonyPool {
	//记数器，当前连接数量
	AtomicInteger atomicInteger = new AtomicInteger(0);
	AtomicInteger count = new AtomicInteger(0);
	//用两个容器来存储
	
	int maxActive;
	int maxWait;
	
	//空闲
	LinkedBlockingQueue<Jedis> idle;
	//繁忙
	LinkedBlockingQueue<Jedis> busy;
	@Override
	public void init(int max, int maxWait) {
		this.maxActive = max;
		this.maxWait = maxWait;
		idle = new LinkedBlockingQueue<>();
		busy = new LinkedBlockingQueue<>();
	}

	@Override
	public Jedis borrowResource() throws Exception {
		//定义一个当前时间
		long now = System.currentTimeMillis();
		//1.从池中获取redis连接
		Jedis jedis = null;
		//记录一个进入方法的时间
		long time = System.currentTimeMillis();
		while(jedis == null) {
			//从空闲队列中获取连接
			jedis = idle.poll();//没有的话返回null 非阻塞的
			if(jedis != null) {
				busy.offer(jedis);
				return jedis;
			}
		}
		//如果连接池没有满，就构建一个新的连接
			//要有一个连接记数器
		if(atomicInteger.get() < maxActive) {
			//多线程下的   加1操作
			if(atomicInteger.incrementAndGet() <= maxActive) {
				jedis = new Jedis("127.0.0.1",6379);
				int incrementAndGet = count.incrementAndGet();
				System.out.println("创建一个jedis次数为  ：  "+incrementAndGet);
				//存入繁忙队列
				busy.offer(jedis);
				return jedis;
			}else {
				atomicInteger.decrementAndGet();
			}
		}
		
		//4.等待其它线程释放连接
		jedis = idle.poll(maxWait - (System.currentTimeMillis()- now),TimeUnit.MILLISECONDS);
		
		if(jedis == null) {
			//判断是否超时
			if((System.currentTimeMillis() - now)> maxWait){
				throw new Exception("获取连接异常");
			}
		}else {
			busy.offer(jedis);
		}
		
		return jedis;
	}

	public void release(Jedis jedis) {
		if(busy.remove(jedis)) {
			idle.offer(jedis);//从繁忙队列删除，放到空闲队列中
		}else {
			//没有成功处理  -1
			atomicInteger.decrementAndGet();
		}
	}
	
	public void close() {
		
	}
}
