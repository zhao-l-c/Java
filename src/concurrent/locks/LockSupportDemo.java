package concurrent.locks;

import java.util.concurrent.locks.LockSupport;


public class LockSupportDemo {
	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread() {
			public synchronized void run() {
				int count = 0;
				boolean interrupt = false;
				while(true) {
					long start = System.currentTimeMillis();
					try {
						if(interrupt) {
							return;
						}
						Thread.sleep(200);
//						wait(200);
					} catch (InterruptedException e) {
						System.out.println("线程中断");
					}
					System.out.println(getName() + " is running..." + (System.currentTimeMillis() - start));
					count ++;
					if(count == 5) {
						System.out.println(getName() + " is park...");
						LockSupport.park(this);
						if(Thread.interrupted()) {
							interrupt = true;
						}
					}
				}
			}
		};
		t.start();

		Thread.sleep(1300);
//		LockSupport.unpark(t);
		t.interrupt();
	}
}
