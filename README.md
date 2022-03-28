# MemoryShell
内存马学习，持续更新。


# 目录

## 文章
- [java内存马分析(一) 环境搭建](https://mp.weixin.qq.com/s?__biz=MzUxNjA5MDA3MA==&mid=2247484667&idx=1&sn=fa76652ce94d0f52052ab5fa2f9ce8bb&chksm=f9adf542ceda7c54ef7ba8c982c103b71620b0f62121ad75de84b8f0debe21f9439e52b3caee&token=297424637&lang=zh_CN#rd)
- 

## servlet内存马
原理：
创建servlet马封装成wrapper，获取StandardContext使用addChild添加内存马，最后配置映射关系。
详细步骤见代码。
![](img/servlet.png)

# 挖掘思路
- 通过正向添加，然后从上下文对象里面找。找对应存储的变量，分析对象构成。然后结合反射进行动态注册。