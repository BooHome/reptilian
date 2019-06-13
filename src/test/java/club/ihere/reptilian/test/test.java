package club.ihere.reptilian.test;

import com.jshq.core.util.DateUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: fengshibo
 * @date: 2018/10/29 17:46
 * @description:
 */
public class test {

    @Test
    public void doTest1() {
        List<Integer> integerList0 = new ArrayList<>();
        List<Integer> integerList1 = new ArrayList<>();
        integerList0.add(0);
        integerList0.add(2);
        integerList0.add(4);
        integerList0.add(6);
        integerList1.add(1);
        integerList1.add(3);
        integerList1.add(5);
        integerList1.add(7);
        List<Integer> collect = Stream.concat(integerList0.stream(), integerList1.stream()).sorted((x, y) -> x.compareTo(y)).collect(Collectors.toList());
        collect.stream().forEach(x -> System.out.println(x));
    }
  @Test
    public void doTest2() {
      Date date = new Date();
      Date date1 = DateUtil.transformDate(date, DateUtil.DateScale.SECOND);
      System.out.println(DateUtil.formatFullDateTime(date1));
      System.out.println(DateUtil.formatFullDateTime(date));
  }

}
