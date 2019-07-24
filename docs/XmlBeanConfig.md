#### 支持Xml方式配置各种bean

* 支持类型自动识别，支持 ref 属性 或者 ref标签 引入其他bean

```xml
  <bean id="jack" class="com.zollty.mvcdemo.beans.Student">
    <property name="name" value="jack" />
    <property name="clasz" ref="clasz0732" />
  </bean>
  
  <bean id="clasz0732" class="com.zollty.mvcdemo.beans.Class">
    <property name="id" value="10045348540" />
    <property name="name" value="07-3-1" />
  </bean>
```

* 支持数组（List）、Map、Array、Set等

```xml
  <bean id="shelf" class="com.zollty.mvcdemo.beans.BookShelf">
    <property name="id" value="0732" />
    <property name="books">
      <list>
        <value>spring</value>
        <value>docker</value>
      </list>
    </property>
  </bean>
```

* 支持构造函数（Constructor）生成bean实例

```xml
  <bean id="foo" class="com.zollty.mvcdemo.beans.Foo">
    <constructor>
      <value>180</value>
      <value>jdbc/zollty</value>
    </constructor>
    <property name="name" value="docker"></property>
  </bean>
```

* 支持通过静态方法（Static Method）生成bean实例

```xml
  <bean id="foo" class="com.zollty.mvcdemo.StaticBuilder#buildFoo">
    <constructor>
      <value>180</value>
      <value>docker</value>
    </constructor>
  </bean>
```

* 支持通过非静态方法（Method）生成bean实例 （使用已实例化的宿主bean对象）

```xml
  <bean id="fooBuilder" class="com.zollty.mvcdemo.FooBuilder">
  </bean>

  <bean id="foo" class="fooBuilder#build">
    <constructor>
      <value>black</value>
      <value>260.01</value>
    </constructor>
  </bean>
```

* 支持通过非静态方法（Method）生成bean实例 （自动实例化宿主bean对象）

```xml
  <bean id="car" class="com.zollty.mvcdemo.CarFactory#build">
    <property name="color" value="black" />
    <property name="speed" value="260.01" />
  </bean>

  <bean id="car" class="com.zollty.mvcdemo.CarFactory#build">
    <constructor>
      <value>black</value>
      <value>260.01</value>
    </constructor>
  </bean>

  <bean id="car" class="com.zollty.mvcdemo.CarFactory#build">
    <constructor>
      <value>black</value>
      <value>260.01</value>
      <ref bean="driver" />
    </constructor>
  </bean>
```

* 支持引入其他xml文件

```xml
<import resource="classpath:unittest-beans-2.xml" />
```
