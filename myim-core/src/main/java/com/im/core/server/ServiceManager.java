package com.im.core.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceManager {

	private List<IServerService> servers = Collections.synchronizedList(new ArrayList<>());
	
	public void register(IServerService server) {
		
	}
	
	public void start(String name) {
		
	}
	
	public void startAll() {
		
	}
	
	public void stop(String name) {
		
	}
	
	public void stopAll() {
		
	}
}
