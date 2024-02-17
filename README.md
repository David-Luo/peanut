# All about JavaBean
BeanUtil基本能力： 

- 复制: bean-base
- 合并: bean-merge
- 比较: bean-compare
- 转换: bean-map
- 校验: bean-validate
- 持久化: bean-persist

模块划分
- runtime
- codegen


```java
interface BeanUtil {
    /**
    * 深层拷贝当前对象.
    */
    public static <T> T clone(T bean);

    /**
    * 将当前form对象合并到to对象中。
    */
    public  static  <T> T merge(T from, T to);

    /**
    * 将当前对象转换为toType对象.
    * T可以是接口或者抽象类。
    */
    public  static  <T> T map(Object from, Class<T> toType);

    /**
    *  校验当前对象是否符合要求。
    */
    public  static Option<ValidationResult> validate(Object bean);

    /**
    * 判断bean1是否和bean2对象有差异。
    */
    public  static  <T> Option<DiffResult> diff(T bean1, T bean2);
}
```




Pojo相等的层次：
- referenceEquals： 技术性引用相等 o bean1 == bean2
- identityEquals: 技术性业务相等 Object.equals()
- uniqueEquals: 业务相等唯一性相等（业务主键相等）： BeanCompare.identityEquals(Obejct A, Object B)
- valueEquals： 值相等： BeanCompare.valueEquals(Obejct A, Object B)

JavaBean的生成代码相关要素：
toString/equals/hashCode


技术层：
- javabean： DTO、DomainObject、PO、Enumeration
- service： RPC Interface、Repository Interface、Domain Service Interface、Domain Rule
- bean operation：复制、合并、比较、转换、校验、持久化


业务层：

- 实体信息：业务代码集、业务数据集、数据模型、业务模型、业务规则集、产品数据集、组织架构
- 过程信息：系统能力集、业务流程集、

OMG:
- 概念模型
- 逻辑模型
- 物理模型

ArchiMate：
- 业务层（Business)：提供对外部客户的产品和服务，这些服务由组织内的业务角色通过业务流程来实现
- 应用层(Application)：支持业务服务的应用
- 技术层(Technology）：通过硬件和软件的交互来运行应用程序

类型：
- Active structure：subject
- Behaviour: verb
- Passive structure：object

核心注解

- @Entity：实体
- @Id
- @IdClass
- @GeneratedValue：主键生成策略
- @Repository：仓储
- @Service：服务



Value TODO List:
- commont读取生成
- 注解读取生成
- 通过CSV文件生成JavaBean
- 构造器代码读取、生成
- toString/equeals/clone/builder/add/find代码生成
