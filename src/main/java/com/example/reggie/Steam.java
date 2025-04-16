package com.example.reggie;

import com.example.reggie.entity.Dish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Steam {
    public static void main(String[] args) {
        List<Dish> dishes = new ArrayList<>();
        Dish dish1 = new Dish();
        dish1.setName("小强");
        Dish dish2 = new Dish();
        dish2.setName("效果");
        dishes.add(dish1);
        dishes.add(dish2);
        System.out.println(dishes);
        //相当于不同的容器装在这个相同的地址
        List<Dish> dishes1 = dishes.stream().map(item -> {//最终是一个新的集合,和原先的集合没有任何影响
            item.setId(1L);
            return item;
        }).collect(Collectors.toList());
        //collect(Collectors.toList())他这个最终返回的是新建早的集合,而不是当前对象,当前对象的执行不是this

        //最终输出结果为false
        System.out.println(dishes1 == dishes);

        System.out.println("+++++++++++++++++++++++++");

        //stream流的结尾collect(Collectors.toMap(键,值))  键 Function<Input,Result> 值Function<Input,Result>
        List<String> list = new ArrayList<>();
        Collections.addAll(list,"小明-1","小强-2","小光-3");

        Map<String,String> map = list.stream().collect(Collectors.toMap(
                new Function<String, String>() {

                    @Override
                    public String apply(String s) {
                        return s.split("-")[1];
                    }
                },
                new Function<String, String>() {

                    @Override
                    public String apply(String s) {
                        return s.split("-")[0];
                    }
                }
        ));
        System.out.println(map);
    }
}
