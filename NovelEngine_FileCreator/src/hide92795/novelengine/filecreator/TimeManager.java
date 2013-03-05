package hide92795.novelengine.filecreator;

public class TimeManager {
	private static long before;

	public static void start() {
		before = System.currentTimeMillis();
	}

	public static void end(String bef, String aft) {
		long after = System.currentTimeMillis();
		System.out.println(bef + (double) (after - before) / 1000 + aft);
	}
}
