package edu.uth.clamp.nlp.structure;

import java.util.HashMap;
import java.util.Map;

public class TimeStatus {
	
	
	public static class StatusPair {
		public long count = 0;
		public long ms = 0;
		public StatusPair() {
		}
		
		public void add( long ms ) {
			this.count += 1;
			this.ms += ms;
		}
	}
	
	public static Map<String, StatusPair> statusMap = new HashMap<String, StatusPair>();
	public static void ProcPerformance( String procName, long ms ) {
		if( !statusMap.containsKey( procName ) ) {
			StatusPair pair = new StatusPair();
			statusMap.put( procName, pair );
		}
		statusMap.get( procName ).add( ms );
	}
	public static void dump() {
		for( String key : statusMap.keySet() ) {
			System.out.println( key + "\t" + statusMap.get(key).count + "\t" + statusMap.get(key).ms + "\t" + ( statusMap.get(key).ms * 1.0 / statusMap.get(key).count ) );
		}
		System.out.println();
	}

}
