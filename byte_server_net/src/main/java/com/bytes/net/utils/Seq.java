package com.bytes.net.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用来标识每一个唯一的请求
 */
public class Seq {

	public long timestamp;
	public int sequence;
	public int slice;
	public int idc;

    private static ReentrantLock lock=new ReentrantLock();

    private static AtomicInteger lastSeq = new AtomicInteger(0);
    private static AtomicInteger lastSlice = new AtomicInteger(0);
    private static long lastts=0;
    private Random r = new Random(System.currentTimeMillis());

	public static final long MASK_TIME_STAMP = 0x7FFFFFFFFFC00000L;
	public static final long MASK_SEQUENCE = 0x3FL << 16;
	public static final long MASK_IDC = 0xFL << 12;
	public static final long MASK_SLICE = 0xFFFL;

	public Seq(){
	}

	public Seq(long timestamp, int sequence, int slice, int idc) {
		this.timestamp = timestamp;
		this.sequence = sequence;
		this.slice = slice;
		this.idc = idc;
	}

    public static Seq nextSeq(){
        try {
            lock.lock();
            long timeStamp = System.currentTimeMillis();
            if (timeStamp != lastts) {
                lastSeq.set(0);
                lastts = timeStamp;
            }
            int seq = lastSeq.getAndAdd(1) % (2 << 8);

            int s = lastSlice.getAndAdd(1);
            int slice = s % 99;
            return new Seq(timeStamp,seq,slice,0);

        }finally {
            lock.unlock();
        }
    }

	public static Seq parse(long ssid){
		Seq rtn = new Seq();
		rtn.timestamp = (MASK_TIME_STAMP & ssid) >> 22;
		rtn.sequence = (int) ((MASK_SEQUENCE & ssid) >> 16);
		rtn.idc = (int) ((MASK_IDC & ssid) >> 12);
		rtn.slice = (int) (MASK_SLICE & ssid);
		return rtn;
	}

	public long toLong(){
		return ((timestamp << 22) & MASK_TIME_STAMP)
				^ ((sequence << 16) & MASK_SEQUENCE)
				^ ((idc << 12) & MASK_IDC)
				^ (slice & MASK_SLICE);
	}

	@Override
	public String toString() {
		return "Seq "+this.toLong()+" {" +
				"timestamp=" + timestamp +
				"(" + formatTimestamp(timestamp) +
				"), sequence=" + sequence +
				", slice=" + slice +
				", idc=" + idc+
				'}';
	}


	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
	public static String formatTimestamp(long millisec){
		return format.format(new Date(millisec));
	}

	public static void main(String[] args) {
		System.out.println((MASK_SEQUENCE|MASK_SLICE|MASK_TIME_STAMP )== Long.MAX_VALUE);
        System.out.println(Seq.nextSeq());
	}
}
