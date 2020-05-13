# _Karting Json_

## Parsing JSON String
Generally, JSON strings can be divided into arrays and objects, we will give you some examples.

JSON Object:
```json
{
  "number": 0.1,
  "null": null,
  "hello": "world"
}
```

or JSON Array:
```json
[
  "hello",
  {
    "pi": 3.1415926
  },
  2.7
]
```

You can use `JSON` to parse json string:
```java
public class Demo {
    public static void main(String... args) {
        String json = readJsonString();
        
        if (maybeJsonObject(json)) {
            JSONObject obj = JSON.parseJSONObject(json);
        } else {
            JSONArray array = JSON.parseJSONArray(json);
        }
    }
    
    // ...More...
    
    public static String readJsonString() {
        // ...return a json string...
    }
    
    public static boolean maybeJsonObject(String json) {
        // ...judge the type of json-string...
    }
}
```

`JSONObject` implements `java.util.Map` and `JSONArray` implements `java.util.List`, so you can use the corresponding methods to get the json value.

## Time & Date Formatter
Kartingjson has supported formatting time or date, including `LocalDateTime`, `LocalDate` and `LocalTime` in Java8-API.

A simple entity class, it has a field whose type is `LocalDateTime`:
```java
public class BlogPostEntity {
    private String id = "No.12345";
    private String author = "Virjid";
    private LocalDateTime createAt = LocalDateTime.of(2020, 5, 10, 10, 0, 0);

    // ...More...
}
```

The entity will be converted to a json string like this:
```json
{
  "author": "Virjid",
  "id": "No.12345",
  "createAt": "2020-05-10 10:00:00"
}
```

Kartingjson provides some default time or date formatters:

| Type | Formatter |
| :--------: | :--------: |
| LocalDateTime | yyyy-MM-dd HH:mm:ss |
| LocalDate | yyyy-MM-dd |
| LocalTime | HH:mm:ss |


You can customize the formatter by `@TimeFormat`:
```java
public class BlogPostEntity {
    private String id = "No.12345";
    private String author = "Virjid";
    
    @TimeFormat("yyyyMMddHHmmss")
    private LocalDateTime createAt = LocalDateTime.of(2020, 5, 10, 10, 0, 0);

    // ...More...
}
```

the result:
```json
{
  "author": "Virjid",
  "id": "No.12345",
  "createAt": "20200510100000"
}
```

## Support for Spring Boot

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