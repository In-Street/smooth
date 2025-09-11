package com.smooth.feature.model.sealedExample;

/**
 *  1. sealed 密封类，精准控制继承关系，permits 配置可允许继承的子类；
 *
 *  2. 被允许继承的子类必须需要选择一种继承策略：
 *      final ：到我为止，不能再继承了
 *      sealed ： 子类我也要控制谁能继承我
 *      no-sealed: 子类我开放继承，谁都可以继承我
 */
public sealed class Shape
    permits Circle,Triangle {  //  permits 设置允许继承的子类


}
