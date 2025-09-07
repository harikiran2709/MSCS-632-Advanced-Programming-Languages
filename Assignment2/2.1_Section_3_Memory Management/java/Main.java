import java.util.*;

public class Main {
    static List<byte[]> holder = new ArrayList<>(); // retained = logical "leak"

    public static void main(String[] args) throws Exception {
        allocateAndRelease();  // becomes unreachable → GC can reclaim
        allocateAndRetain();   // kept reachable → GC cannot reclaim
        System.out.println("holder size = " + holder.size());

        // Ask GC to run (not guaranteed), then pause for profiling
        System.gc();
        Thread.sleep(30000);
    }

    static void allocateAndRelease() {
        for (int i = 0; i < 200; i++) {
            byte[] b = new byte[200_000];  // ~200 KB
        } // b goes out of scope; eligible for GC
    }

    static void allocateAndRetain() {
        for (int i = 0; i < 30; i++) {
            holder.add(new byte[1_000_000]); // ~1MB each, retained
        }
    }
}
