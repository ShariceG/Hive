package hiveSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import hive.Location;

public class HiveSimulator {
	
	private SimulatedUser[] users;
	private ArrayList<String> locations;
	
	private int runtimeMs;
	private int numUsers;
	
	public HiveSimulator(int runtimeSec, int numUsers) {
		this.runtimeMs= runtimeSec * 1000;
		this.numUsers = numUsers;
		this.locations = new ArrayList<String>();
		readLocationsIntoMemory(locations);
		setupSimulatedUsers();
	}
	
	public void startSimulation() {
		long deadline = System.currentTimeMillis() + runtimeMs;
		System.out.println("Starting simulation...");
		ExecutorService threadPool = Executors.newCachedThreadPool();
		Future future = null;
		for (final SimulatedUser user : users) {
			future = threadPool.submit(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
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
						pauseSimulation(5);
						if (System.currentTimeMillis() > deadline) {
							System.out.println(user + " Stopping simulation...");
							break;
						}
						
						long timeLeft = (deadline - System.currentTimeMillis()) / 1000;
						System.out.println(user + " " + timeLeft + " seconds left.");
					}
				}
			});
			pauseSimulation(2);
		}
		try {
			future.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
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
			String username = "user" + (i+1);
			String email = username + "@email.com";
			users[i] = new SimulatedUser("user" + (i+1), email, 
					randomLocation());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static Location makeLocation(String geo) {
		String[] split = geo.split(":");
		return new Location(split[0], split[1]);
	}
	
	private Location randomLocation() {
		String geo = locations.get(new Random().nextInt(locations.size()));
		return makeLocation(geo);
	}
	
	private void readLocationsIntoMemory(ArrayList<String> locations) {
		File file = new File("src/hiveSimulator/worldcities.csv");
		try {
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String csv = reader.nextLine();
				csv = csv.replaceAll("\"", "");
				String[] split = csv.split(",");
				String str = split[2] + ":" + split[3];
				locations.add(str);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
