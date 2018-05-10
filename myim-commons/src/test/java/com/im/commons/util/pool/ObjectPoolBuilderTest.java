package com.im.commons.util.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPoolBuilderTest {
	private static final Logger logger = LoggerFactory.getLogger(ObjectPoolBuilderTest.class);
	private FixedObjectPoolBuilder<TestObject> pool = null;
	private ExecutorService service = Executors.newFixedThreadPool(100);

	private static int tcount = 0;

	@Test
	public void testBorrowAndReturn() throws Exception {
		pool = new FixedObjectPoolBuilder<TestObject>(100);
		pool.build(TestObject.class);
		for (int i = 0; i < 1000; i++) {
			TestObject testObj = pool.borrowObject(true);
			testObj.setMsg("xxx");
//			final int t = i;
//			service.submit(new Runnable() {
//				@Override
//				public void run() {
//					logger.debug("---> " + t);
//					TestObject testObj = pool.borrowObject(true);
//					if (testObj == null) {
//						logger.error("errorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
//						return;
//					}
//					testObj.setMsg("xxx");
//					synchronized (this) {
//						try {
//							Thread.sleep(new Random().nextInt(5) * 100);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//					pool.returnObject(testObj);
//
//					logger.debug("tcount: " + tcount);
//				}
//			});
		}
		service.awaitTermination(3, TimeUnit.MINUTES);
	}

	public static class TestObject extends BaseObject {
		private String msg;
		private int count = 0;

		public void setMsg(String msg) {
			this.msg = msg;
			logger.debug("set msg: " + msg + ", count: " + (count++) + ", index: " + getIndex() + ", tcount: " + (tcount++));
		}

		@Override
		protected void reset() {
			this.msg = null;
		}

	}
}
