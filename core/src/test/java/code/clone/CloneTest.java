package code.clone;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.RandomUtils;

import java.util.ArrayList;

/**
 * 复制测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-25 16:07
 **/
@Slf4j
public class CloneTest {

    @Test
    public void t0() {
        t1();
        t20();
        t21();
    }

    @Test
    public void t1() {
        Student student = new Student(System.nanoTime(), "小明", 1, RandomUtils.random(100, 1000));
        Student clone = student.clone0();
        clone.age = 21;
        log.info(FastJsonUtil.toJson(student));
        log.info(FastJsonUtil.toJson(clone));
    }

    @Test
    public void t20() {
        long nanoTime = System.nanoTime();
        Student student;
        ArrayList<Student> students = new ArrayList<>(100_0000);
        for (int i = 0; i < 100_0000; i++) {
            student = new Student(System.nanoTime(), "小明", i, RandomUtils.random(100, 1000));
            students.add(student);
        }
        log.info(students.size() + " - " + ((System.nanoTime() - nanoTime) / 10000 / 100f));
    }

    @Test
    public void t21() {
        Student student = new Student(System.nanoTime(), "小明", 18, 0);
        long nanoTime = System.nanoTime();
        ArrayList<Student> students = new ArrayList<>(100_0000);
        for (int i = 0; i < 100_0000; i++) {
            Student cloned = student.clone0();
            cloned.setUid(System.nanoTime());
            cloned.setAge(i);
            cloned.setScore(RandomUtils.random(100, 1000));
            students.add(student);
        }
        log.info(students.size() + " - " + ((System.nanoTime() - nanoTime) / 10000 / 100f));
    }

    @Getter
    @Setter
    public class Person implements Cloneable {
        private long uid;
        private final String name;

        public Person(long uid, String name) {
            this.uid = uid;
            this.name = name;
        }

        public <T> T clone0() {
            try {
                return (T) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Getter
    @Setter
    public class Student extends Person {
        private int age;
        private int score;

        public Student(long uid, String name, int age, int score) {
            super(uid, name);
            this.age = age;
            this.score = score;
        }
    }

}
