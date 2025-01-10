package code.size;

import org.junit.Test;
import wxdgaming.spring.boot.core.format.data.Data2Size;

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

    @Test
    public void tsize() {
        boolean bool = false;
        byte byte1 = 0;
        int i1 = 0;
        long l1 = 0;
        System.out.println(Data2Size.totalSize0(byte1));
        System.out.println(Data2Size.totalSize0(bool));
        System.out.println(Data2Size.totalSize0(i1));
        System.out.println(Data2Size.totalSize0(l1));
        A obj = new A();
        System.out.println(Data2Size.totalSize0(obj));
        System.out.println(Data2Size.totalSize0(obj.bool));
    }

    public static class A {
        boolean bool = false;
        byte byte1 = 0;
        int i1 = 0;
        long l1 = 0;
    }


}
