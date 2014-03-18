zollty-mvc
==========
---
### What is ZolltyMVC ###
> The ZolltyMVC is a lightweight Java MVC Framework. It can help development teams build simple, portable, fast and flexible Java applications.
 
> ##### 1.It's an IoC container 
> Its DI function is very powerful, support method injection, support to create bean using method's return value...  
> Its BeanFactory is highly extensible, support ClassPathXmlApplicationContext, ClassPathAnnotationApplicationContext, WebXmlApplicationContext, and so on. 
 
> ##### 2.It's a lightweight MVC Framework 
> Support REST-ful, nice url customization, chained interceptor, ...and so on.    
> It's reliable and efficient, much more easy than SpringMVC, and it's faster.    
> Only one jar, no dependence, which includes it's most functions!    
> It's flexible, you can overwrite its API to extend its functionality.   
   
---
### How to use it ###
> ###### a hello word sample  
```java
@Controller
public class UserController{

   @RequestMapping("/admin/logout")
   public View logout(HttpServletRequest request){
          try {
            request.getSession().invalidate();
          }catch (Exception e) {
		  }
          return new RedirectView("/admin?info=bye");
   }
   
   @RequestMapping("/admin/login")
   public View login(
           @HttpParam("userName")String userName,
           @HttpParam("password")String password) {
		   
          // do something...
		  
          return new JspView("/admin/main.jsp");
   }
}
```