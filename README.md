# Support for Spring Boot

Add Maven dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <artifactId>jackson-databind</artifactId>
                <groupId>com.fasterxml.jackson.core</groupId>
            </exclusion>
        </exclusions>
    </dependency>
    
    <dependency>
        <groupId>me.virjid.karting</groupId>
        <artifactId>kartingjson</artifactId>
        <version>${kartingjson.version}</version> <!-- 1.0 -->
    </dependency>
</dependencies>
```

Add Kartingjson's HTTP message converter By Configurator:
```java
@Configuration
public class Config implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        KartingJsonHttpMessageConverter converter = new KartingJsonHttpMessageConverter();
        converters.add(converter);
    }
}
```

Now, you can use Kartingjson in a RestController:
```java
@RestController
@RequestMapping("/")
public class Controller {

    @PostMapping("hello")
    public JSONObject hello(@RequestBody JSONObject params) {
        return params;
    }
}
```