package hiveSimulator;

public class HiveSimulatorRunner {

	public static void main(String[] args) {
		HiveSimulator sim = new HiveSimulator(60*30, 5);
		sim.startSimulation();
		// 64 -> 2.5
		// 739 -> 15
		// 1000 posts, 1000 comments, 389 actions -> 35.5 seconds
	}

}
