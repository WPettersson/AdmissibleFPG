package se.ewpettersson.admissiblefpg.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class Timer {
	/** Get CPU time in nanoseconds. */
	long start,time;
	
	public Timer() {
		start=getCpuTime();
	}
	
	
	
	public long getCpuTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadCpuTime( ) : 0L;
	}
	
	/** Get user time in nanoseconds. */
	public long getUserTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadUserTime( ) : 0L;
	}

	/** Get system time in nanoseconds. */
	public long getSystemTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        (bean.getCurrentThreadCpuTime( ) - bean.getCurrentThreadUserTime( )) : 0L;
	}



	public void start() {
		start = getCpuTime();
	}



	public void stop() {
		time = getCpuTime() - start;
	}



	public long getTime() {
		return time*1000;
	}



	public void reset() {
		start = getCpuTime();
		
	}
	
}
