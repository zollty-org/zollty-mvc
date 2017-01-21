AOP类型简介
----------------------------
在Controller的方法

* 执行之前：MvcBefore

* 执行之后、渲染之前：MvcBeforeRender、MvcAfterThrow

* 执行之后：MvcAfter

* 执行前后：MvcAround


#### 拦截器用法概述
如下示例：

通用AOP拦截器（拦截所有匹配URI）

```java
@AopMapping({"/admin/*"})
public class KxxxBefore implements MvcBefore {

}
```

带AOP的Controller和Method

```java
@CBefore({HxxxBefore.class})
@Controller
public class OneController {
     
   @CBefore({AxxxBefore.class, BxxxBefore.class})
   @RequestMapping("/admin/[vv]")
   public void doService() {
   
   }
   
}
```

拦截器按功能分为两类：

1. 通用拦截器
2. 业务拦截器

`拦截器按作用范围大小分为三类（在三个不同地方定义的拦截器） ：`

1. 通用拦截器：在AOP类上用 @AOPMapping({"/admin/*"}) 定义的拦截器，作用范围为所有匹配的uri对应的controller method。
2. Controller拦截器：基于特定Controller来定义Controller层面的拦截器。在Controller类上用 如@CBefore({HxxxBefore.class})标注 的拦截器。
3. ControllerMethod拦截器：基于某个Controller的特定Method来定义方法层面的拦截器。在Controller类的@RequestMapping方法上用 如@CBefore({HxxxBefore.class})标注 的拦截器。

##### 1. MvcBefore

MvcBefore 在执行 Controller Method 之前执行。

业务`场景 ：`

1. 权限检查：检查session是否过期。过期则直接返回错误视图。
2. 权限检查：检查是否有跨站点脚本攻击的非法参数，如果有则返回错误视图。
3. 预处理：读取请求信息、Cookie等，并做一些解析后存入请求对象中，方便后续流程使用。
4. 日志记录：记录请求信息。

可以做成异步处理。

可以有多个MvcBefore 与Controller的方法相关联。按照先后顺序执行这些MvcBefore拦截器。（Controller层面的拦截器，其执行顺序要先于Method层面的拦截器。通用拦截器，理应最先执行，然后才执行业务拦截器。）

执行`顺序 ：`通用拦截器、Controller拦截器、ControllerMethod拦截器。在每一级别上都是按从小到大先后顺序执行。

错误处理：

* 如果MvcBefore执行出错，可以返回一个View，MVC框架会提交这个View，终止后续执行（后面的MvcBefore等都不会执行了）。
* 如果MvcBefore抛出了未知异常，框架会catch异常并打印日志，并返回错误视图，终止后续执行。

##### 2. MvcBeforeRender

MvcBeforeRender 在 执行Controller Method、还未渲染视图时 执行。

业务`场景 ：`

1. 附加回传参数：在返回视图之前，往Repsonse里面加参数。
2. 收尾工作，打印返回的数据，删除生成的临时文件等。可以做成异步处理。

在执行完Controller的method之后，返回一个View，在这个View还没有调用render方法commit时，可以执行MvcBeforeRender拦截器。
同样，MvcBeforeRender可以有多个，按顺序先后执行。

执行顺序：ControllerMethod拦截器、Controller拦截器、通用拦截器。在每一级别上都是按从小到大先后顺序执行。

错误处理：

同MvcBefore，返回View或者遇到未知异常则终止程序执行，否则继续后续流程。

##### 3. MvcAfterThrow

MvcAfterThrow 在 执行Controller Method、还未渲染视图时 遇到未知异常时执行（包括MvcAround的异常，不包括MvcBefore等出现的异常）。

业务`场景 ：`

1. 统一的错误处理：在执行Controller Method时如果出现未捕获的异常，则执行MvcAfterThrow来处理。

在执行完Controller的method时，如果报未知异常，框架捕捉到之后，则执行MvcAfterThrow拦截器。
同样，MvcAfterThrow可以有多个，

执行顺序：ControllerMethod拦截器、Controller拦截器、通用拦截器。在每一级别上都是按从小到大先后顺序执行。

错误处理：

同MvcBefore，返回View或者遇到未知异常则终止程序执行，否则继续后续流程。

##### 4. MvcAfter

MvcAfter 在 执行完Controller、视图渲染完之后 执行。

业务`场景 ：`

1. 收尾工作，打印返回的数据，删除生成的临时文件等。可以做成异步处理。

在执行完Controller的method时，且Render完之后，则可以执行MvcAfter拦截器。
同样，MvcAfter可以有多个，

执行顺序：ControllerMethod拦截器、Controller拦截器、通用拦截器。在每一级别上都是按从小到大先后顺序执行。

错误处理：

如果MvcAfter抛出了未知异常，框架会catch异常并打印日志，仅此而已。

##### 5. MvcAround

MvcAround 在 执行Controller Method的前后 执行（把Controller Method包裹在MvcAround之中执行）。

业务`场景 ：`

1. 性能监控：记录处理时间，如果超时则打印log或者发送邮件。
2. 开关：如OpenSessionInView，在进入处理器打开Session，在完成后关闭Session。

定义了MvcAround的Controller的method，不会直接执行Controller的method，而是会调用MvcAround的方法，
在MvcAround的方法中再去调用Controller的method。
如果有多个MvcAround，则递归调用，

执行顺序：通用拦截器、Controller拦截器、ControllerMethod拦截器。在每一级别上都是按从小到大先后顺序执行。

错误处理：

如果MvcAround返回View则终止程序执行。

如果抛出了未知异常，框架会catch异常，如果定义了MvcAfterThrow则交由MvcAfterThrow进行处理，否则直接返回错误视图。


多个AOP执行顺序举例
----------------------------
如下Controller的doService方法引入了 4个MvcBefore AOP

```java
// 通用AOP拦截器
@AOPMapping(uri={"/admin/*"})
public class KxxxBefore implements MvcBefore {
}

// 带AOP的Controller
@CBefore(cls={HxxxBefore.class})
@Controller
public class OneController {
     
   @CBefore(cls={AxxxBefore.class, BxxxBefore.class})
   @RequestMapping("/admin/[vv]")
   public void doService() {
   
   }
   
}
```

按我的设计，默认情况下：

KxxxBefore最先执行，然后是HxxxBefore，最后是AxxxBefore、BxxxBefore（当有定义了多个拦截器时，按注解上的顺序，依次执行）。
	
也就是说BxxxBefore是最后执行的。

另外，还有“通用拦截器”：

```java
@AOPMapping({"0:/admin/*", "2:/lesson1/hello"})
public class KxxxBefore implements MvcBefore {

}
```

通用拦截器，理应最先执行，然后才执行业务拦截器。

如果要调整，也不是没办法，“通用拦截器”可以自定义order，形如 "12:/admin/*"，那么order值等于12。Order默认值为 100。


其他
----------------------------

MvcBefore拦截器使用建议：

能使用Servlet规范中的过滤器Filter实现的功能建议就用Filter实现，
因为HandlerInteceptor只有在Zollty Web MVC环境下才能使用，因此Filter是最通用的、最先应该使用的。
如登录这种拦截器最好使用Filter来实现。

当然，MvcBefore可以 针对某些 特定方法 拦截，更灵活。也是一个必备的功能。而且AOP类的实例 由框架托管，如同普通Controller一样，属于单实例，可以注入其他Service Bean。
