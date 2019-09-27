package com.telegram;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Parent {
    static {
        System.out.println("3");
    }

    {
        System.out.println("4");
    }

    Parent() {
        super();
        System.out.println("5");
    }

    static void hello() {
        System.out.println("3");
    }

    void salam() {
        System.out.println("1");
    }

    void start() {
        salam();
        hello();
        System.out.println("2");
    }
}

public class SlideTest extends Parent {
    private static int numberOfCreatedObjects = 0;

    static {
        System.out.println("1");
    }

    {
        System.out.println("2");
    }

    {
        numberOfCreatedObjects++;
    }

    SlideTest() {
        this(2);
        System.out.println("6");
    }

    SlideTest(int x) {
        super();
        System.out.println("7");
    }

    static void hello() {
        System.out.println("4");
    }

    public static int getNumberOfCreatedObjects() {
        return numberOfCreatedObjects;
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        list.stream().filter(a -> a <= 20).forEach(System.out::println);
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, Integer> s : map.entrySet()) {

        }
        //        System.out.println(Arrays.toString(new Scanner(System.in).nextLine().split("\\s")));
//        new SlideTest();
//        new SlideTest();
//        new SlideTest();
//        System.gc();
//        TimeUnit.SECONDS.sleep(2);
//        System.out.println("num = "+ getNumberOfCreatedObjects());
//        new SlideTest();
//        new SlideTest().run();
//        int z= 5, y = 9, u = 15;
//        Integer x = z;
//        Integer w = new Integer(y);
//        Integer v = w;
//        z++;
//        System.out.println((z + " " + x));
//        v++;
//        System.out.println((v + " " + w + " " + y));
//        w+=z;
//        if(w == u) System.out.println("!!!");
//        **********************************
//        String string=new String("ali");
//        String str=new String("ali");
//        System.out.println((str == string));
//            Console console = System.console();
//            char[] chs = console.readPassword();
//            System.out.println(String.valueOf(chs));
//        System.out.format("min double = %5.2f", 10123.23456);
//        String str = "hello";

//        byte b = 65;
//        for (int i = 0; i < 255; i++) {
//            char c = (char) i;
//            System.out.println(i + ": " + c);
//        }
    }

    @Override
    void salam() {
        System.out.println("5");
    }

    void run() {
        start();
        hello();
        salam();
    }

    public void varArgs(String... args) {
        System.out.println(args[0]);
    }

    @Override
    protected void finalize() throws Throwable {
        numberOfCreatedObjects--;
    }
}
