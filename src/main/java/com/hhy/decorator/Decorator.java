package com.hhy.decorator;

/**
 * 装饰角色
 * 1.装饰角色要实现抽象构件角色
 * 2.装饰角色要持有一个抽象构件角色的引用
 * 该类为装饰模式核心所在
 */
public class Decorator implements Component {

    private Component component;

    public Decorator(Component component) {
        this.component = component;
    }

    public void doSomething() {
        component.doSomething();
    }
}
