package shopee;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author jxlgcmh
 * @date 2020-02-15 14:43
 * @description
 */
public class Main2 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        List<Integer> list = new ArrayList<>();
        String line = in.nextLine();
        String[] splits = line.split(" ");
        for (String split : splits) {
            list.add(Integer.parseInt(split));
        }
        int[] arr = new int[list.get(list.size() - 1)];
        for (Integer item : list) {
            if (arr[item - 1] <= item - 1) {
                arr[item - 1]++;
            }
        }
        int sum = 0;
        for (int tmp : arr) {
            sum += tmp;
        }
        System.out.println(sum);
    }
}
