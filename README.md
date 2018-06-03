ZolltyMVC 
----------------------------

A lightweight Java MVC Framework. 
It can help development teams build simple, portable, fast and flexible Java applications.
 
#### 1. Dependency Injection and Inversion of Control （Concise and Powerful）

ZolltyMVC Framework support Java Beans management, through xml config or code annotation you can define bean instances, and inject bean instances into other bean instances.

Its DI function is very powerful, include common usages like Spring, and also support advanced usages, such as create bean using method's return value, inject beans for method parameter...
  
Its BeanFactory is highly extensible, support ClassPathXmlApplicationContext, ClassPathAnnotationApplicationContext, WebXmlApplicationContext, and so on. 
 
#### 2. Lightweight MVC Framework support RESTful web service （Easy and very efficient）

Only **one jar**, much more lightweight than Spring and Struts Framework! 强调一下：速度飞快！（一个案例：一个简单OA项目，用ZolltyMVC启动只需要320毫秒-“启动不足一秒”，而用SpringBoot启动需要34秒！）

Support REST-ful, nice url customization. Its URI matching algorithm is very efficient, tens times faster than AntPathMather (which Spring used).

The URI matching also support approximate matching（模糊匹配）, and extracting parameters from URI. There are many nice functions.

It's ease to use. Through long term practice and research, after many improvements, we have taken that flexibility to extremes! Its usage is much easier than Spring or Struts Framework, you will like it!

#### 3. Interceptor and Aspect-Oriented Programming （Practical）

ZolltyMVC Framework does not dependency on AspectJ and CGLIB, so we don't use proxy which may cause many problems. But we still support Aspect-Oriented Programming at controller layer. You can define interceptors base on Java annotation at any controller method. Interceptors can be defined at any stage of the execution of the controller method (before, after, after throwing exception, and so on).

The Framework also support to define common interceptors matched the special URIs. And support asynchronous call.

See [ZolltyMVC AOP Design](https://github.com/zollty-org/zollty-mvc/blob/master/docs/AOP-design.md) for more details.


#### 4. Model Driven and multiple Views （Powerful and flexible）

ZolltyMVC Framework can automatic packaging request parameters into 'POJO' beans. So you can package form parameters into a Java bean. It also support primitive type automatic injection.

ZolltyMVC Framework provide many views to use. Like JspView, JsonView, HtmlView, ForwardView and so on. You can also customize and extend Views. Its usage is more flexible than Spring's ModelAndView because in SpringMVC, the returned View cannot be of many types.


#### 5. 易于扩展、定制、迁移和集成

ZolltyMVC是在"Servlet集中控制转发器"基础上演变而来的，设计时特别考虑了它和Servlet的互相转换，以及和Struts、Spring等框架的同时存在和兼容。

1. 兼容Servlet，Servelt可以直接转换成ZolltyMVC的Controller，反之也不难。
2. 可以和Spring，Struts框架并存。（SpringMVC+Struts兼容并不是很好，比如存在WAS下的".do"的问题，而ZolltyMVC+Struts则兼容很好）
3. 支持 Webjar，使用ZolltyMVC可以将web项目（包括class、html、picture、css、js）打包成一个jar，例如阿里的druid-monitor，如果使用ZolltyMVC的话，则完全不用再自己去写控制Servlet、HTML、CSS、JS的代码。
4. ZolltyMVC框架预留了很多扩展和自定义的空间，许多组件框架只是提供了默认实现而已，如果需要个性化，则继承、实现、重载、重写都很方便。

另外，ZolltyMVC<b>刻意</b>保留了部分比较好的Spring的使用习惯，有Spring使用经验的人，掌握ZolltyMVC只是分分钟的事。


ZolltyMVC设计初衷（2013）
--------------------------

#### 1. SSH框架的地位不在，Spring越来越复杂，支持RESTful的轻量级框架开始发展

时值2013年，当时Struts已经逐渐被SpringMVC替代，随着RESTful的流行，许多支持RESTful的框架出现，SSH框架的霸主地位不在，Spring也面临着越来越重的包袱，开始酝酿全新的4.x版本。更为轻量级的框架逐渐出现在生产项目中，比如Google推出的[Guice](https://github.com/google/guice)，是一款轻量级的Java Ioc容器，它的描述如下：

  1. 速度快，号称比spring快100倍。
  2. 无外部配置(如需要使用外部可以可以选用Guice的扩展包)，完全基于annotation特性，支持重构，代码静态检查。
  3. 简单，快速，基本没有学习成本。

Guice和Spring各有所长，Guice更适合与嵌入式或者高性能但项目简单的方案。

同样，还有58同城开源的轻量级Web框架[Argo](https://github.com/58code/Argo)，同时期在OSChina开源的[JFinal](https://www.oschina.net/p/jfinal)，主打特点是极速、简单、方便，目前已经被业界广泛使用。

#### 2. ZolltyMVC主打中小型项目，特别是嵌入式项目

刚开始在做项目时，我们选择了SSH框架，但是随后很快意识到，SSH框架和项目绑定死了，如果要把我们的项目嵌入到其他项目中，则必须要求其他项目也采用一样的SSH架构，甚至要求使用的Spring版本一样。

所以，我们摒弃了SSH和SpringMVC，我们的项目开始只用Servlet！但是Servlet使用起来确实不太方便，我便编写了一套Servlet集中控制转发器，再配合Java的反射机制、注解机制，自动根据访问的uri寻找对应的controller和method。这就是ZolltyMVC的原型。

#### 3. ZolltyMVC不追求大而全，只把简洁、高效和实用做到极致

ZolltyMVC相比其他框架，没有历史包袱，要足够简单、实用、高效，所以在这些方面下足了功夫。

  1.  ZolltyMVC要足够快，那就不能像Spring那样，在controller层添加那么多东西，ZolltyMVC不支持MultipartRequest，也不支持代理，不追求大而全，只追求实用和简洁，彻底地提高性能。
  2.  ZolltyMVC要足够快，那就要有更快的URI匹配算法，Spring采用的AntPathMather还是太慢了，我花了很多心思，写出来了一个全新的URI匹配算法（命名为ZolltyPathMather），比经典的AntPathMather还要快10倍！
  3.  ZolltyMVC要足够简单、足够顺手，让开发者无需记忆太多API，写起代码来飞快。经过我不断的尝试和改进，许多功能和API达到了简洁易用的效果。比如限制Controller的某个方法只能通过POST方式访问，则在Method上直接注解定义为：@RequestMapping("POST:/rest/api/login")，<b>这个独创的方法来源于写代码时的灵感</b>。另外，之所以采用了像RequestMapping这样的名字，其实是为了和Spring的使用习惯保持一致，让新手容易上手。


--------------------------


How to use it 
---------------------------------------

##### a controller demo 

```java
@Controller
public class HelloWorldController {
 
     // 属性注入，支持按类型注入
    @Inject
    private DiService diService;
    
    @Inject("diService")
    private DiService anOtherDiService;
    
    @RequestMapping("/hello-json")
    public View helloJosn() {
    
        // Return a JSON View
        return new JsonView(jsonString);
    }
    
    // Only allow GET method 
    @RequestMapping("GET:/hello-jsp")
    public View helloJsp() {
    
        // Return a JSP View
        return new JspView("/lesson1/hello.jsp");
    }
    
    // Get param from URI
    @RequestMapping("/user/{name}")
    public View helloSomeOne(@URIParam("name") String name) {
        
        // Return a Plain Text View
        return new TextView("Hello "+ name);
    }
    
}
```

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

* 还有其他许多功能，此处不再一一介绍，具体参见 [zolltymvc-beans.xsd](http://assets.zollty.com/misc/p/schema/zolltymvc-beans-1.2.xsd) 和 Demo。

See the XML Schema and use it to validate your XML configuration: 

 http://assets.zollty.com/misc/p/schema/zolltymvc-beans-1.2.xsd


### See the docs for more details  

Quik learn, see [wiki pages](https://github.com/zollty-org/zollty-mvc/wiki)

Quik start, see [zollty-mvc-demo](https://github.com/zollty/zollty-mvc-demo)


License
--------------------------

ZolltyMVC is Open Source software released under the GNU GENERAL PUBLIC LICENSE Version 2.0 ([GPL 2.0](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)).


Contribution
--------------------------

We are actively looking for contributors, so if you have any ideas, bug reports, or patches you would like to contribute please do not hesitate to do that.

Author(s):

* zollty (Zollty Tsou) <zolltytsou@gmail.com>

