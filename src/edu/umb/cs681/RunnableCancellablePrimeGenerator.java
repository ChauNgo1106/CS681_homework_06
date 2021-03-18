package edu.umb.cs681;

import java.util.concurrent.locks.ReentrantLock;

public class RunnableCancellablePrimeGenerator extends RunnablePrimeGenerator {
	private boolean done = false;
	private ReentrantLock lock = new ReentrantLock();

	public RunnableCancellablePrimeGenerator(long from, long to) {
		super(from, to);
	}

	public void setDone() {
		this.lock.lock();
		try {
			done = true;
		} finally {
			this.lock.unlock();
		}
	}

	public void generatePrimes() {
		for (long n = from; n <= to; n++) {
			this.lock.lock();
			try {
				// Stop generating prime numbers if done==true
				if (this.done) {
					System.out.println("Stopped generating prime numbers.");
					this.primes.clear();
					break;
				}
				if (isPrime(n)) {
					this.primes.add(n);
				}
			} finally {
				this.lock.unlock();
			}
		}
	}

	public static void main(String[] args) {
		RunnableCancellablePrimeGenerator gen1 = new RunnableCancellablePrimeGenerator(1, 100);
		Thread thread1 = new Thread(gen1);

		RunnableCancellablePrimeGenerator gen2 = new RunnableCancellablePrimeGenerator(1, 50);
		Thread thread2 = new Thread(gen2);

		thread1.start();
		thread2.start();

		gen1.setDone();
		gen2.setDone();

		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		gen1.getPrimes().forEach((Long prime) -> System.out.print(prime + ", "));
		System.out.println("\n");
		gen2.getPrimes().forEach((Long prime) -> System.out.print(prime + ", "));

		System.out.println("\n" + gen1.getPrimes().size() + " prime numbers are found.");
		System.out.println("\n" + gen2.getPrimes().size() + " prime numbers are found.");
	}
}
