# simple-http
java发送http请求的简单使用
最近自己写了一个发送http请求的组件，整个发送请求的实现是用的Okhttp，只是基于Okhttp基础上做了再次封装，使用起来和mybatis一样，只需要编写接口即可，业务层就可以直接调用，并且可以和spring集成。可以用于调用第三方API或者爬虫。

maven依赖
```
        <dependency>
            <groupId>com.github.zw201913</groupId>
            <artifactId>simple-http</artifactId>
            <version>1.0.0.RELEASE</version>
        </dependency>
```

### spring boot
**1.下面来直接看看怎么和springboot集成：**
```
import com.github.zw201913.simplehttp.annotation.EnableSimpleHttp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//开启SimpleHttp
@EnableSimpleHttp
@SpringBootApplication
public class SimpleHttpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleHttpApplication.class, args);
    }
}
```
开启SimpleHttp后，我们就先来使用它访问一下http://www.baidu.com试试：
```
@SimpleHttpService
public interface SimpleHttp {
    /**
     * 发送get请求访问http://www.baidu.com
     * @return
     */
    @Get("http://www.baidu.com")
    String baidu();
}
```
接下来，我们就可以调用这个接口使用了：
```
@Service
public class TestService {
     // 在spring中使用SimpleHttp
    @Autowired private SimpleHttp simpleHttp;

    public String baidu() {
        return simpleHttp.baidu();
    }
}
```
看到这里，就应该很明白了，我们的重点就在于如何编写SimpleHttp接口(ps:接口名称可以自己随便取)。
```
//如果想要url作为参数传入
@Get
String list(@Url String url);
//传入一个参数为id的字段
@Get
String find(@Url String url, @Field("id") Integer id);
//需要设置请求头的
@Get
String find(@Url String url, @Field("id") Integer id, @Header("Content-Type") String contentType);
//多个参数和请求头设置
@Get
String find(@Url String url, @Field Map<String, Object> params, @Header Map<String, String> headers);

```
如果是自定义对象：
```
    //@Data是lombok的注解，可以避免手写get，set方法
    // 请求参数实体类
    @Data
    public class PageParam {
        private String search;
        private int page;
        private int size;
    }
    // 请求头实体类
    @Data
    public class RequestHeader {
        private String cookie;
        private String userAgent;
        private String host;
    }

@Get
String find(@Url String url, @Field PageParam params, @Header RequestHeader headers);

```
如果实体类中字段名称和实际请求名称不同：
```
    // 请求参数实体类
    @Data
    public class PageParam {
        @Field("name")
        private String search;
        private int page;
        private int size;
    }
    // 请求头实体类
    @Data
    public class RequestHeader {
        private String cookie;
        @Header("User-Agent")
        private String userAgent;
        private String host;
    }

```
你甚至还可以这样写：
```
   @Data
    public class PageParamAndRequestHeader {
        @Field("name")
        private String search;
        @Field
        private int page;
        @Field
        private int size;
        @Header
        private String cookie;
        @Header("User-Agent")
        private String userAgent;
        @Header
        private String host;
    }
// 至于下面方法的参数修饰到底是使用@Field 还是@Header ,那就看你的心情了
@Get
String find(@Url String url, @Field PageParamAndRequestHeader paramsAndHeaders);

```
当然，除了Get请求，还有Post，Put，Delete，Patch......还有WebSocket

上传一个文件并且带参数：
```
// 带一个文件上传的，对应spring mvc里面的
// public User add(@RequestPart("user") User user, @RequestPart("image") MultipartFile[] image) 
@Post
String add(@Url String url, @Field("user") Map<String, Object> user, @Field("image") File file);

// 和上面接口一样，只不过把Map改成了自定义的User
@Post
String add(@Url String url, @Field("user")  User user, @Field("image") File file);

```
如果一个文件不够的话：
```
// 对应的服务端接口：
// public User add(@RequestPart("user") User user, @RequestPart("image1") MultipartFile[] image1, @RequestPart("image2") MultipartFile[] image2) 
@Post
String add(@Url String url, @Field("user")  User user, @Field("image1") File[] file, @Field("image2") File[] file);

```
以上接口，除文件外，参数也是一样可以多个的，使用方法同文件类似。
**如果不需要上传文件，并且服务端是@RequestBody：**
```
// 默认是对应服务端的@RequestBody，且格式化方式是JSON
// 如果发现有File或File[]参数，会自动对应@RequestPart，且携带的其他参数格式化方式是JSON
// 对应服务端接口
// public User add(@RequestBody User user)
@Post
String add(@Url String url, @Field User user);

// 同上
@Post
String add(@Url String url, @Field Map<String,Object> user);
```
**如果需要监听文件上传进度怎么办？**
```

import com.github.zw201913.simplehttp.core.http.ProgressListener;

// 添加ProgressListener上传进度监听器
@Post
String add(
            @Field("user") User user,
            @Field("image1") File[] file1,
            @Field("image2") File[] file2, ProgressListener progressListener);

```
只要在参数中传一个进度监听器对象，即可获取当前进度，例如：
```
ProgressListener progressListener = (totalLength, currentLength) -> {
                    int progress = (int) (100 * currentLength / totalLength);
                    System.out.println(progress + "%");
                }
```
当然，只有文件上传会有进度条。
**你或许还需要发送异步请求：**
```
import okhttp3.Callback;

// 添加异步回调Callback对象，这个时候方法就不需要返回值了
@Post
void add(@Url String url, @Field User user, Callback callback);
```
例如：
```
Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {}

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String result = response.body().string();
                            System.out.println(result);
                        }
                    }
                })
```
**很大一种可能，你发送的请求要求的数据格式不是JSON，可能是XML或是别的数据格式**
```
public interface RequestParamsHandler {

    String JSON_UTF8 = "application/json;charset=utf-8";

    /**
     * 处理请求参数
     *
     * @param params
     * @param files
     */
    RequestBody handle(Map<String, Object> params, Map<String, File[]> files);
}
```
例如需要将请求参数转换为XML格式，我们只需要实现自定义的RequestParamsHandler：
```
public class XMLRequestParamsHandler implements RequestParamsHandler {

        @Override
        public RequestBody handle(Map<String, Object> params, Map<String, File[]> files) {
            //实现将params转换成XML格式，并且返回一个RequestBody对象
            return null;
        }
    }
```
```
@Post(handler = XMLRequestParamsHandler.class)
String add(@Url String url, @Field User user)
```
这样的话，我们就可以发送数据格式化要求是XML的请求了。
**除了请求参数可以自定义处理，我们还会希望返回结果也能自定义**
目前默认的返回结果可以是void，String，Response，这三种返回结果程序会自动处理，不需要任何特殊设置。如果需要除此以外的返回类型，需要自定义ResponseHandler
```
public interface ResponseHandler {
    /**
     * 处理响应对象
     *
     * @param response
     * @param <T>
     * @return
     */
    <T> T handle(Response response);
}
```

```
    public class MyResponseHandler implements ResponseHandler {

        @Override
        public User handle(Response response) {
            //处理Response，将返回数据转换成指定对象
            return new User();
        }
    }

@Post
User add(@Url String url, @Field User user, ResponseHandler responseHandler)
```
如果想要自定义OkHttpClient，可以实现BaseOkHttpClientFactory：
```
public abstract class BaseOkHttpClientFactory {

    /** final是为了保持单例 */
    private final OkHttpClient client;

    public BaseOkHttpClientFactory() {
        this.client = httpClient();
    }

    /**
     * 留给子类实现
     *
     * @return
     */
    protected abstract OkHttpClient httpClient();

    /**
     * 获取创建好的OkHttpClient
     *
     * @return
     */
    public OkHttpClient okHttpClient() {
        return this.client;
    }
}
```
```
public class DefaultOkHttpClientFactory extends BaseOkHttpClientFactory {
    @Override
    protected OkHttpClient httpClient() {
        return new OkHttpClient();
    }
}
```
上面是组件默认的。为什么要自定义OkHttpClient，是为了实现例如Https或者请求代理。
```
public class HttpProxyOkHttpClientFactory extends BaseOkHttpClientFactory {
    @Override
    protected OkHttpClient httpClient() {
        OkHttpClient client = new OkHttpClient();
        // 实现请求代理
        return client;
    }
}
```
```
@Post(clientFactory = HttpProxyOkHttpClientFactory.class)
String add(@Url String url, @Field User user);
```
最后，还有WebSocket:
```
import okhttp3.WebSocketListener;

// 创建WebSocket，url是ws://echo.websocket.org
@Ws("ws://echo.websocket.org")
WebSocket newWebSocket(WebSocketListener listener);

// 自定义url创建WebSocket
@Ws
WebSocket newWebSocket(@Url String url, WebSocketListener listener);
```
以上就是[Simple-http](https://github.com/zw201913/simple-http)的简单使用。
