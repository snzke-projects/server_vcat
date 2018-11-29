package com.vcat.common.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolUtil {

	private static ExecutorService threadPool;
	private final static int  corePoolSize = 50;//池中所保存的线程数，包括空闲线程
	private final static int maxPoolSize = 100;//池中允许的最大线程数
	private final static long keepAliveTime = 30;//当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间
	private final static int queueSize = 100;//等待的任务队列的长度

	public static void execute(Runnable thread)  {
		if(threadPool==null)
		{
			threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
					keepAliveTime, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(queueSize),
                    new ThreadPoolExecutor.AbortPolicy());
		}
		threadPool.execute( thread );
	}

}
