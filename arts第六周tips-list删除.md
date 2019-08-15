#List删除所有指定元素

环境:jdk8

## 1.概要

java中*List*使用*List.remove()*直接删除指定元素,然而高效删除元素是很难, 在本文章中介绍多种

方法,讨论其中优点和缺点,为了可读性,我创建*list(int…)* 方法在测试类中,返回*ArrayList* 

## 2.使用while循环

知道如何删除一个元素，然后循环删除，看下简单例子

```java
void removeAll(List<Integer> list, int element) {
    while (list.contains(element)) {
        list.remove(element);
    }
}
```



然而执行下面会报错

```java
// given
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;
 
// when
assertThatThrownBy(() -> removeAll(list, valueToRemove))
  .isInstanceOf(IndexOutOfBoundsException.class);

```

造成这个原因在第一个代码块3行，调用**List.remove(int),该参数被当成list索引index,不是删除元素**  

这个测试用例调用*list.remove(1)* ,但是删除元素索引是0,调用*List.remove()* 改变所有元素在删除元素之后

在这个场景我们删除所有元素,除了第一条记录,为什么仅仅只有第一条剩下呢,1代表索引是非法,因此最后会报错

注意,这个问题原因是调用*List.remove()* 参数是基本类型 short, char 或者int,因此编译器第一次认为调用匹配重载方法

可以用传入Integer类型正确执行

```java
void removeAll(List<Integer> list, Integer element) {
    while (list.contains(element)) {
        list.remove(element);
    }
}
```

现在下面可以正确执行难

```java
// given
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;
 
// when
removeAll(list, valueToRemove);
 
// then
assertThat(list).isEqualTo(list(2, 3));
```

*List.contains()* 和 *List.remove()* 都必须找到第一个出现元素,这个代码引起没必要遍历

我们可以做到更好如果我们保存元素第一次出现索引

```java
void removeAll(List<Integer> list, Integer element) {
    int index;
    while ((index = list.indexOf(element)) >= 0) {
        list.remove(index);
    }
}
```

以下代码可以通过

```java
 List<Integer> list = list(1,2,3);
int valueToRemove = 1;
// when
removeAll(list, valueToRemove);
assertThat(list).isEqualTo(list(2, 3));
```

上面情况代码非常整洁和简洁，但是仍然性能很差,因为我们不能跟踪这个循环过程,List.remove()* 必须找到第一个list中元素然后删除,当使用*ArrayList* ，元素改变引起许多引用拷贝，甚至重新分配内存几次

## 3.删除元素直到改变原来*list* 

**List.remove(E element)** 有一个特色我们还没提及到,方法返回布尔值true，***List*** 改变由于包含该元素并删除 操作

注意点，*List.remove(int index)* 返回void，因为根据索引删除是有效，*List* 总会删除元素，否则会抛出异

常*IndexOutOfBoundsException* 

执行删除直到**List** 改变

```java
void removeAll(List<Integer> list, int element) {
    while (list.remove(element));
}
```

结果如下

```java
  // given
  List<Integer> list = list(1, 1, 2, 3);
  int valueToRemove = 1;

  // when
  removeAll(list, valueToRemove);

  // then
  assertThat(list).isEqualTo(list(2, 3));
```

上面代码遇到之前同样问题

## 3.使用for循环

我们可以管理遍历过程通过for循环并且如果匹配元素直接删除

```java
void removeAll(List<Integer> list, int element) {
    for (int i = 0; i < list.size(); i++) {
        if (Objects.equals(element, list.get(i))) {
            list.remove(i);
        }
    }
}
```

结果如下:

```java
// given
List<Integer> list = list(1, 2, 3);
int valueToRemove = 1;
 
// when
removeAll(list, valueToRemove);
 
// then
assertThat(list).isEqualTo(list(2, 3));

```

然而，如果不同输入，得到错误结果输出:

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
 
// when
removeAll(list, valueToRemove);
 
// then
assertThat(list).isEqualTo(list(1, 2, 3));

```

一步一步分析代码:

- i = 0
  - 元素和list.get(i)都是等于1在第3行代码，因此java进入if语句
  - 删除元素索引0
  - list包含1，2和3
- i = 1 
  -   *list.get(i)* 返回2因为list删除一个元素，因此改变所有元素位置

现在面临问题当有两个相邻值，我们都想删除，解决这个问题，我们增加循环变量

当删除元素变量要减一

```java
void removeAll(List<Integer> list, int element) {
    for (int i = 0; i < list.size(); i++) {
        if (Objects.equals(element, list.get(i))) {
            list.remove(i);
            i--;
        }
    }
}
```

当我们不删除变量增加1

```java
void removeAll(List<Integer> list, int element) {
    for (int i = 0; i < list.size();) {
        if (Objects.equals(element, list.get(i))) {
            list.remove(i);
        } else {
            i++;
        }
    }
}
```

注意，在这之后，移除i++语句第2行

结果如下：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
 
// when
removeAll(list, valueToRemove);
 
// then
assertThat(list).isEqualTo(list(2, 3));
```

这个实现好像是对第一眼看上去，这个方法仍然有很严重性能问题：

- 删除元素改变之后所有元素
- 索引访问元素*LinkedList* 意味遍历通过元素一个接一个知道找到该元素

## 4.使用for-each循环

从java5之后可以用for-each循环迭代通过list，下面使用迭代删除元素:

```java
void removeAll(List<Integer> list, int element) {
    for (Integer number : list) {
        if (Objects.equals(number, element)) {
            list.remove(number);
        }
    }
}
```

注意：使用Integer作为循环类型，因此不会得到*NullPointerException* ,同时这个方法调用 *List.remove(E element)* 是我们期望调用方法，不是索引，

代码很简洁，不幸是代码报错：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
 
// when
assertThatThrownBy(() -> removeWithForEachLoop(list, valueToRemove))
  .isInstanceOf(ConcurrentModificationException.class);
```

for-each循环使用迭代器遍历元素，当修改*List* 迭代器得到不一致状态，因此抛出常**ConcurrentModificationException** ，从上面代码得出结论：我们不能修改*List*，当for-each访问元素时候。

## 5.使用迭代器

使用迭代器遍历和修改*List* ：

```java
void removeAll(List<Integer> list, int element) {
    for (Iterator<Integer> i = list.iterator(); i.hasNext();) {
        Integer number = i.next();
        if (Objects.equals(number, element)) {
            i.remove();
        }
    }
}
```

这中方式，迭代器可以跟踪*List*状态（因为这个可以修改*List*）,下面结果可以正常通过：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
 
// when
removeAll(list, valueToRemove);
 
// then
assertThat(list).isEqualTo(list(2, 3));
```

因为每个*List*类提供自己迭代器实现，我们可以安全假定，迭代器实现元素迭代和删除最高效。然而使用*Arraylist* 仍然要移动很多元素（可以数组重新分配内存），同时上面代码有点难度这个不是标准for循环对于大多数开发来说不熟悉。

## 6.搜集

到目前为止，删除元素都会修改原*List* ，我们不必要这样，可以创建新**List** 和搜集元素：

```java
List<Integer> removeAll(List<Integer> list, int element) {
    List<Integer> remainingElements = new ArrayList<>();
    for (Integer number : list) {
        if (!Objects.equals(number, element)) {
            remainingElements.add(number);
        }
    }
    return remainingElements;
}
```

方法结果返回新的*List* ，方法必须返回*list* ，因此我们必须使用方法按照下面：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
 
// when
List<Integer> result = removeAll(list, valueToRemove);
 
// then
assertThat(result).isEqualTo(list(2, 3));
```

注意，现在使用for-each循环不能修改*List* ，我们现在通过它迭代元素，因为没用任何删除,这里没有必要移动元素，因此这个实现性能也很好当我们使用*ArrayList* 

这实现比之前一些方式有些不同：

-   它不会修改原*List* 但是返回新*List*
- 这个方法决定返回*List*的实现，它可以是不同于原*List*

同时我们修改我们实现得到以前方法获得*List*，清除原*LIst*和增加搜集元素到原*List*

```java
void removeAll(List<Integer> list, int element) {
    List<Integer> remainingElements = new ArrayList<>();
    for (Integer number : list) {
        if (!Objects.equals(number, element)) {
            remainingElements.add(number);
        }
    }
 
    list.clear();
    list.addAll(remainingElements);
}
```

和之前一样

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
 
// when
removeAll(list, valueToRemove);
 
// then
assertThat(list).isEqualTo(list(2, 3));
```

不需要修改原*List*，不必要按照位置访问或者改变，同时，这里有两个*Array*分配，当调用*List.clear()* and *List.addAll()*.

## 7.使用stream api

java 8 介绍lambda表达式和stream api，有这写强大特色，我们可以解决我们问题并且用很简洁代码

```java
List<Integer> removeAll(List<Integer> list, int element) {
    return list.stream()
      .filter(e -> !Objects.equals(e, element))
      .collect(Collectors.toList());
}
```

这个方法作用和上一部分一样，当我们收集保存元素，然后把这些结果增加原*List* ，有相同特征，

我们应该返回结果：

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
 
// when
List<Integer> result = removeAll(list, valueToRemove);
 
// then
assertThat(result).isEqualTo(list(2, 3));
```



## 8. 使用*removeIf*



有lambdas和函数接口java8中，java8还有一些扩展api，例如，**List.removeIf()** 方法，最后一个部分看见用这个实现 ，参数需要一个条件，如果条件返回true就直接删除元素，对比之前示例，我们必须返回true但我们想保存元素：

```java
void removeAll(List<Integer> list, int element) {
    list.removeIf(n -> Objects.equals(n, element));
}
```

效果和其他一样:

```java
// given
List<Integer> list = list(1, 1, 2, 3);
int valueToRemove = 1;
 
// when
removeAll(list, valueToRemove);
 
// then
assertThat(list).isEqualTo(list(2, 3));
```

实际上，*List* 本身实现该方法，我们可以放心假定，这种方式性能最好，在以上方法中这个方案是最简洁代码

## 9.总结

本文介绍许多方式解决简单问题，包括错误示例，分析他们找出最好解决方案
[参考地址](https://www.baeldung.com/java-remove-value-from-list)





