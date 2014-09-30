package concurrent.locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * 利用Lock、Condition和ExecutorService重写生产者和消费者
 */
public class LockAndCondition {
	
	class ConsumerProductor {
		private final static int SIZE = 10;
		private String[] cakeTable = new String[SIZE];
		private int head = 0;
		private int tail = 0;
		private int count = 0;
		protected AtomicLong id = new AtomicLong();
		
		private ReentrantLock lock = new ReentrantLock();
		
		private Condition conditionConsumer = lock.newCondition();
		private Condition conditionProductor = lock.newCondition();
		
		public void consume() {
			lock.lock();
			try {
				while(count <= 0) {
					conditionConsumer.await();
				}
				String cake = cakeTable[tail];
				System.out.println("======" + Thread.currentThread().getName() + " eat " + cake);
				Thread.sleep(100);
				tail = (tail + 1) % SIZE;
				count --;
				conditionProductor.signalAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		
		
		public void product() {
			lock.lock();
			try {
				while(count >= SIZE) {
					conditionProductor.await();
				}
				String cake = "cake" + id.incrementAndGet();
				System.out.println(Thread.currentThread().getName() + " make " + cake);
				Thread.sleep(400);
				cakeTable[head] = cake;
				head = (head + 1) % SIZE;
				count ++;
				conditionConsumer.signalAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
	}
	
	public static void main(String[] args) {
		LockAndCondition lac = new LockAndCondition();
		final ConsumerProductor cp = lac.new ConsumerProductor();
		int threadSize = 3;
		ExecutorService service1 = Executors.newFixedThreadPool(threadSize);
		Runnable r1 = new Runnable() {
			public void run() {
				while(true) {
					cp.consume();
				}
			}
		};
		
		ExecutorService service2 = Executors.newFixedThreadPool(threadSize);
		Runnable r2 = new Runnable() {
			public void run() {
				while(true) {
					cp.product();
				}
			}
		};
		for(int i = 0; i < threadSize; i++) {
			service1.execute(r1);
			service2.execute(r2);
		}
	}
}
