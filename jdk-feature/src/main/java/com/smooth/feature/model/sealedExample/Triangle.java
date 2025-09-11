package com.smooth.feature.model.sealedExample;

/**
 *   子类设置继承策略，设置允许继承自己的子类
 * @author Cheng Yufei
 * @create 2025-09-07 22:47
 **/
public sealed class Triangle extends Shape

        permits RightTriangle {

}
