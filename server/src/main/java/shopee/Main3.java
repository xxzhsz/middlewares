package shopee;

import java.util.Scanner;

/**
 * @author jxlgcmh
 * @date 2020-02-15 15:18
 * @description
 */
public class Main3 {
    static int count =0;
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int m = in.nextInt();
        int n = in.nextInt();
        int[][] arr = new int[n][m];
        arr[0][0] = 1;
        arr[n - 1][m - 1] = 0;
       // todo
    }
}
