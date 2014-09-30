package junit;

import org.junit.Test;

public class JunitTest {
	
	class InterruptThread extends Thread {
		public synchronized void run() {
			while(true) {
				try {
					// 当前线程执行interrupt方法时，若线程处于执行wait/join/sleep方法和它们的重载方法时，
					// 都会抛出InterruptedException异常
					wait(); 
				} catch (InterruptedException e) {
					System.out.println("异常：" + e);
				}
			}
		}
	}
	
	@Test
	public void interrupt() throws InterruptedException {
		Thread t = new InterruptThread();
		t.start();
		Thread.sleep(1000);
		System.out.println("测试线程的中断状态：" + t.isInterrupted()); // false
		t.interrupt();
	}
}
