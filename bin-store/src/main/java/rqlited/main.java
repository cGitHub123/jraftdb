package rqlited;

import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        while (true) {
            System.out.println("请输入：");
            Scanner inp = new Scanner(System.in);
            String str = inp.next();
            System.out.println("你输入了：" + str);
        }
    }

}
