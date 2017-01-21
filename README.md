ZolltyMVC 
----------------------------

A lightweight Java MVC Framework. 
It can help development teams build simple, portable, fast and flexible Java applications.
 
#### 1. Dependency Injection and Inversion of Control （Concise and Powerful）

ZolltyMVC Framework support Java Beans management, through xml config or code annotation you can define bean instances, and inject bean instances into other bean instances.

Its DI function is very powerful, include common usages like Spring, and also support advanced usages, such as create bean using method's return value, inject beans for method parameter...
  
Its BeanFactory is highly extensible, support ClassPathXmlApplicationContext, ClassPathAnnotationApplicationContext, WebXmlApplicationContext, and so on. 
 
#### 2. Lightweight MVC Framework support RESTful web service （Easy and very efficient）

Only **one jar**, much more lightweight than Spring and Struts Framework! 

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

#### See the docs for more details  

Quik learn, see [wiki pages](https://github.com/zollty-org/zollty-mvc/wiki)

Quik start, see [zollty-mvc-demo](https://github.com/zollty/zollty-mvc-demo)


License
--------------------------

ZolltyMVC is Open Source software released under the GNU GENERAL PUBLIC LICENSE Version 2.0 (GPL 2.0) (see the 'LICENSE' file).


Contribution
--------------------------

We are actively looking for contributors, so if you have any ideas, bug reports, or patches you would like to contribute please do not hesitate to do that.

Author(s):

* zollty (Zollty Tsou) <zolltytsou@gmail.com>
