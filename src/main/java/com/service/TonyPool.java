package com.service;

import redis.clients.jedis.Jedis;

public interface TonyPool {
/*1.概要设计
	从连接池中获取连接
	2.释放连接
	3.连接池初始化(最大连接数,最大等待数,超时)
	4.销毁连接池
2.接口设计
3.TDD测试驱动
4.详细设计
	如果保持连接


*/
	/**
	 * @param i
	 * @param j
	 */
	public void init(int i, int j);

	public Jedis borrowResource() throws InterruptedException, Exception;

}
