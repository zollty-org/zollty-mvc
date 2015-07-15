ZolltyMVC
==========

Attention, <span style="color:red;">v1.2.1.RELEASE</span> is not compatible with v1.3.x or later version yet. If you want to upgrade to new version,
 you may modify some codes (a few modify at resource load and some util methods).

What is ZolltyMVC 
----------------------------

The ZolltyMVC is a lightweight Java MVC Framework.  
It can help development teams build simple, portable, fast and flexible Java applications.
 
##### 1.It's an IoC container 
Its DI function is very powerful, support method injection, support to create bean using method's return value...  
Its BeanFactory is highly extensible, support ClassPathXmlApplicationContext, ClassPathAnnotationApplicationContext, WebXmlApplicationContext, and so on. 
 
##### 2.It's a lightweight MVC Framework 
Support REST-ful, nice url customization, chained interceptor, ...and so on.  
It's reliable and efficient, much more easy than SpringMVC, and it's faster.  
Only one jar, no dependence, which includes it's most functions!  
It's flexible, you can overwrite its API to extend its functionality.   


How to use it 
---------------------------------------

###### a hello word sample  
```java
 @Controller
 public class HelloWorldController {
    
    @RequestMapping("/lesson1/hello-jsp")
    public View helloJsp() {

        // Return a JSP View
        return new JspView("/lesson1/hello.jsp");
    }

    @RequestMapping("/lesson1/hello-json")
    public View helloJosn() {

        // Return a JSON View
        return new JsonView("{\"title\": \"hello\", \"name\": \"ZolltyMVC\"}");
    }
    
    
    @RequestMapping("GET:/user/{userName}") // Only allow GET method 
    public View helloSomeOne(@URIParam("userName") String userName) {
        
        // Get userName from URI
        return new TextView("Hello "+ userName);
    }
    
	// Only allow POST method 
    @RequestMapping("POST: /admin/login")
    public View login(@HttpParam("userName") String userName, 
            @HttpParam("password") String password) { 
            // Automatic packaging of HTTP parameters

        // TODO login service...

        return new JspView("/admin/home.jsp");
    }

    @RequestMapping("/admin/logout")
    public View logout(HttpServletRequest request) { 
    	// HttpServletRequest can be used directly

        // TODO logout service...
        
        return new RedirectView("/admin?info=bye");
    }
    
 }
```
###### See the docs for more details  
