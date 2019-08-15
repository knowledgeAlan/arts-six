package com.zzm;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author zhongzuoming <zhongzuoming, 1299076979@qq.com>
 * @version v1.0
 * @Description baipao
 * @encoding UTF-8
 * @date 2019-08-14
 * @time 09:24
 * @修改记录 <pre>
 * 版本       修改人         修改时间         修改内容描述
 * --------------------------------------------------
 * <p>
 * --------------------------------------------------
 * </pre>
 */
public class Tests {


    /**
    * @Description: 删除所有元素
    * @param list  集合
    * @param element   删除元素
    */
   /* public void removeAll(List<Integer> list, int element) {
        while (list.contains(element)) {
            list.remove(element);
        }
    }*/

  /*  void removeAll(List<Integer> list, Integer element) {
        while (list.contains(element)) {
            list.remove(element);
        }
    }*/

  /*  void removeAll(List<Integer> list, Integer element) {
        int index;
        while ((index = list.indexOf(element)) >= 0) {
            list.remove(index);
        }
    }*/

   /* void removeAll(List<Integer> list, int element) {
        while (list.remove(element));
    }*/
  /*  void removeAll(List<Integer> list, int element) {
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(element, list.get(i))) {
                list.remove(i);
            }
        }
    }*/

/*
    void removeAll(List<Integer> list, int element) {
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(element, list.get(i))) {
                list.remove(i);
                i--;
            }
        }
    }*/


    /*void removeAll(List<Integer> list, int element) {
        for (int i = 0; i < list.size();) {
            if (Objects.equals(element, list.get(i))) {
                list.remove(i);
            } else {
                i++;
            }
        }
    }*/

  /*  void removeAll(List<Integer> list, int element) {
        for (Integer number : list) {
            if (Objects.equals(number, element)) {
                list.remove(number);
            }
        }
    }*/

  /*  void removeAll(List<Integer> list, int element) {
        for (Iterator<Integer> i = list.iterator(); i.hasNext();) {
            Integer number = i.next();
            if (Objects.equals(number, element)) {
                i.remove();
            }
        }
    }*/

   /* List<Integer> removeAll(List<Integer> list, int element) {
        List<Integer> remainingElements = new ArrayList<>();
        for (Integer number : list) {
            if (!Objects.equals(number, element)) {
                remainingElements.add(number);
            }
        }
        return remainingElements;
    }*/

   /* void removeAll(List<Integer> list, int element) {
        List<Integer> remainingElements = new ArrayList<>();
        for (Integer number : list) {
            if (!Objects.equals(number, element)) {
                remainingElements.add(number);
            }
        }

        list.clear();
        list.addAll(remainingElements);
    }*/

   /* List<Integer> removeAll(List<Integer> list, int element) {
        return list.stream()
                .filter(e -> !Objects.equals(e, element))
                .collect(Collectors.toList());
    }*/

    void removeAll(List<Integer> list, int element) {
        list.removeIf(n -> Objects.equals(n, element));
    }
    public List<Integer> list(Integer ... a  ){
        return Lists.newArrayList(Arrays.asList(a)) ;
    }


    @Test
    public void testLoop(){

        List<Integer> list = list(1,1,2,3);

        int valueToRemove = 1;
        // when
//        assertThatThrownBy(() -> removeAll(list, valueToRemove))
//                .isInstanceOf(IndexOutOfBoundsException.class);
        removeAll(list, valueToRemove);
        assertThat(list).isEqualTo(list(2, 3));
    }
}
