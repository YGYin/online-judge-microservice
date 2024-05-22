package com.ygyin.ojjudgeservice.judge.sandbox;


import com.ygyin.ojjudgeservice.judge.sandbox.impl.ExternalSandboxImpl;
import com.ygyin.ojjudgeservice.judge.sandbox.impl.RemoteSandboxImpl;
import com.ygyin.ojjudgeservice.judge.sandbox.impl.SampleSandboxImpl;

/**
 * 代码沙箱静态工厂模式，根据字符串参数创建指定的实现代码沙箱实例
 * 因为没有多个代码沙箱实体类，所以没有必要使用抽象工厂
 */
public class SandboxFactory {
    /**
     * 用于创建代码沙箱实例
     * @param boxType 沙箱类型对应字符串
     * @return 返回的为沙箱接口而非具体示例
     * 如果代码沙箱实例不会出现线程安全问题，可以考虑使用单例工厂模式
     */
    public static Sandbox newInstance(String boxType) {
        switch (boxType) {
            case "sample":
                return new SampleSandboxImpl();
            case "remote":
                return new RemoteSandboxImpl();
            case "external":
                return new ExternalSandboxImpl();
            default:
                return new SampleSandboxImpl();
        }
    }
}
