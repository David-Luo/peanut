# 检测器ChangeChecker

运行时入口
```java
Optional<Difference> differences = ChangeCheckerHelper.diff(bean1,bean2);
```

# 生成代码

```java
//BeanCheckerGeneratorTest
@Test
public void testScanAndGenerate() throwsIOException {
    CheckerGenerator g = new CheckerGenerator();
    g.scanAndGenerate("src/main/test","com.javabean.compare.stub.bean","com.javabean.compare.stub.diff");
}
```

CheckerGenerator#scanAndGenerate()参数说明
- sourcePath:源代码文件根目录
- entityPackage:javaBean源代码包
- interfacePackage:个性化声明源代码包


# ServiceLocator
所有的service都通过ServiceLocator获取单例实例.有两种方法获取
- ServiceLocator.get(String className): 直接通过实例化类名获取
- ServiceLocator.getBeanChecker(String beanClass):通过被校验的bean获取.

## beanClass和className的映射
代码生成时自动生成了beanClass和className的映射关系,并保存到“bean-diff.properties”文件中.ServiceLocator通过ChangeCheckerInitialContext获取了该映射关系.

除“bean-diff.properties”的映射外,ChangeCheckerInitialContext还初始化了java默认的原子检查策略,开发人员可以自行补充.

