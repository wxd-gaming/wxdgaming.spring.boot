package code.size;

import org.junit.Test;

public class MemoryTest {

    public static void main(String[] args) {
        // -XX:AutoBoxCacheMax=300000
        // int initialCapacity = 5000_0000;
        // List<Integer> integers = new ArrayList<>(initialCapacity);
        // for (int i = 0; i < initialCapacity; i++) {
        //     integers.add(RandomUtils.random(1, 30_0000));
        // }
        // System.out.println(new ByteFormat().addFlow(Data2Size.totalSize0(integers)).toString());

        {
            Integer i1 = Integer.parseInt("25000");
            Integer i2 = Integer.parseInt("25000");
            System.out.println(i1 == i2);
        }

        {
            Integer i1 = 25000;
            Integer i2 = 25000;
            System.out.println(i1 == i2);
        }
    }

    @Test
    public void t11() {
        {
            Integer i1 = Integer.parseInt("25000");
            Integer i2 = Integer.parseInt("25000");
            System.out.println(i1 == i2);
        }

        {
            Integer i1 = 25000;
            Integer i2 = 25000;
            System.out.println(i1 == i2);
        }
    }

}
