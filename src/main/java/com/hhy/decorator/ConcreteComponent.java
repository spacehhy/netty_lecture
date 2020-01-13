package com.hhy.decorator;

/**
 * 具体构件角色
 */
public class ConcreteComponent implements Component {

    public void doSomething() {
        System.out.println("功能A");
    }
}
