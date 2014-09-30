package concurrent.locks;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * JDK API提供的示例代码
 * @author longcheng
 *
 */
class CachedData {
	Object data;
	volatile boolean cacheValid;
	final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	void processCachedData() {
		rwl.readLock().lock();
		if (!cacheValid) {
			// Must release read lock before acquiring write lock
			rwl.readLock().unlock();
			rwl.writeLock().lock();
			try {
				// Recheck state because another thread might have
				// acquired write lock and changed state before we did.
				if (!cacheValid) {
					// data = ...
					cacheValid = true;
				}
				// Downgrade by acquiring read lock before releasing write lock
				rwl.readLock().lock();
			} finally {
				rwl.writeLock().unlock(); // Unlock write, still hold read
			}
		}

		try {
			// use(data);
		} finally {
			rwl.readLock().unlock();
		}
	}
}

/**
 * 读写锁使用场景：读多写少
 * @author longcheng
 *
 */
class Data {
	
}

class RWDictionary {
	private final Map<String, Data> data = new TreeMap<String, Data>();
	private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock readLock = rwLock.readLock();
	private final Lock writeLock = rwLock.writeLock();

	public Data get(String key) {
		readLock.lock();
		try {
			return data.get(key);
		} finally {
			readLock.unlock();
		}
	}

	public Object[] allKeys() {
		readLock.lock();
		try {
			return data.keySet().toArray();
		} finally {
			readLock.unlock();
		}
	}

	public Data put(String key, Data value) {
		writeLock.lock();
		try {
			return data.put(key, value);
		} finally {
			writeLock.unlock();
		}
	}

	public void clear() {
		writeLock.lock();
		try {
			data.clear();
		} finally {
			writeLock.unlock();
		}
	}
}