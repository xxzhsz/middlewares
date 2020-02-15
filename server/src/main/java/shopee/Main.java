package shopee;

/**
 * @author jxlgcmh
 * @date 2020-02-15 14:22
 * @description
 */

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        // 两段字符
        String[] splits = line.split("\\,");
        // 没给字符串有多少段
        String[] arr1 = splits[0].trim().split("\\.");
        String[] arr2 = splits[1].trim().split("\\.");
        int len = arr1.length;
        int start = 0;
        while (start < len) {
            if (Integer.parseInt(arr1[start]) > Integer.parseInt(arr2[start])) {
                System.out.println(1);
                break;
            } else if (Integer.parseInt(arr1[start]) < Integer.parseInt(arr2[start])) {
                System.out.println(-1);
                break;
            } else {
                start++;
                if (start == len) {
                    System.out.println(0);
                    break;
                }
            }
        }

    }
}
