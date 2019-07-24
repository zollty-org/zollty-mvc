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
