package hiveSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
			users[i] = new SimulatedUser("user" + (i+1), "1111111111", 
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
