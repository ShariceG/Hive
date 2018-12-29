package hiveSimulator;

import java.util.Random;

public class HiveSimulator {
	
	private String[] locations = {
			"44.968046:-94.420307",
			"44.33328:-89.132008",
			"33.755787:-116.359998",
			"33.844843:-116.54911",
			"44.92057:-93.44786",
			"44.240309:-91.493619",
			"44.968041:-94.419696",
			"44.333304:-89.132027",
			"33.755783:-116.360066",
			"33.844847:-116.549069",
			"44.920474:-93.447851",
			"44.240304:-91.493768"
	};
	
	private SimulatedUser[] users;
	
	private int runtimeMs;
	private int numUsers;
	
	public HiveSimulator(int runtimeSec, int numUsers) {
		this.runtimeMs= runtimeSec * 1000;
		this.numUsers = numUsers;
		setupSimulatedUsers();
	}
	
	public void startSimulation() {
		long deadline = System.currentTimeMillis() + runtimeMs;
		System.out.println("Starting simulation...");
		while (true) {
			
			for (SimulatedUser user : users) {
				int randomNum = new Random().nextInt(1000);
				if (randomNum < 250) {
					user.writePost();
				} else if (randomNum < 500) {
					user.writeComment();
				} else if (randomNum < 750) {
					user.changeLocation(randomLocation());
					user.performActionOnAPost();
				} else {
					user.performActionOnAPost();
					user.changeLocation(randomLocation());
				}
			}
			
			pauseSimulation(2);
			
			if (System.currentTimeMillis() > deadline) {
				System.out.println("Stopping simulation...");
				break;
			}
			
			long timeLeft = (deadline - System.currentTimeMillis()) / 1000;
			System.out.println(timeLeft + " seconds left.");
		}
	}
	
	private void pauseSimulation(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void setupSimulatedUsers() {
		users = new SimulatedUser[numUsers];
		System.out.println("Creating " + numUsers + " users.");
		for (int i = 0; i < users.length; i++) {
			users[i] = new SimulatedUser("user" + (i+1), "1111111111", randomLocation());
		}
	}
	
	private String randomLocation() {
		return locations[new Random().nextInt(locations.length)];
	}

}
