import com.smooth.feature.enums.DayEnum;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Cheng Yufei
 * @create 2025-09-04 23:07
 **/
public class TestFeature {

    @Test
    public void jdk9() {

        // 创建不可变集合，无需在引用第三方类库。 在进行新增、删除等操作时，报错：UnsupportedOperationException
        List<String> list = List.of("A", "B", "C");
        list.forEach(System.out::println);

        Map<String, String> map = Map.of("A", "a", "B", "b");
        map.forEach((k, v) -> System.out.println(k + " : " + v));

        // Map.ofEntries 创建时，没有个数限制
        Map<String, String> entries = Map.ofEntries(Map.entry("A", "aa"), Map.entry("B", "bb"), Map.entry("C", "cc"));
        entries.forEach((k, v) -> System.out.println(k + " : " + v));
        System.out.println(entries.size());

    }

    /**
     * 1.  将 HTTP  API化
     */
    @Test
    public void jdk11Http() throws IOException, InterruptedException {

        // 构建 HttpClient
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(3000L)).followRedirects(HttpClient.Redirect.NORMAL).build();

        // 构建Get请求
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://www.baidu.com"))
                .header("Accept", "application/json")
                .timeout(Duration.ofMillis(3000L))
                .GET()
                .build();

        // 构建POST请求
        HttpRequest postRequest = HttpRequest.newBuilder().uri(URI.create("https://www.baidu.com"))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                        "  \"id\": 123,\n" +
                        "  \"username\": \"POST请求\"\n" +
                        "}"))
                .build();

        // 同步发送请求： ofString 响应体转为字符串 、ofFile 响应体写入文件 、buffering 缓冲响应体（用于多次读取）
        HttpResponse<String> result = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("状态码：" + result.statusCode());
        System.out.println("响应体：" + result.body());

        // 异步发送请求
        CompletableFuture<HttpResponse<String>> httpResponseCompletableFuture = client.sendAsync(postRequest, HttpResponse.BodyHandlers.ofString());

        // BodySubscribers : 支持自定义响应处理 。 BodyHandlers 中所有方法的实现，本质上都是通过调用 BodySubscribers 的方法来创建底层订阅者，再封装成 BodyHandler 返回。BodyHandlers = 简化的 API 接口，BodySubscribers = 实际执行字节处理的底层实现
        HttpResponse<String> httpResponse = client.send(getRequest, responseInfo -> {
            if (responseInfo.statusCode() == 200) {
                return HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
            }
            return HttpResponse.BodySubscribers.replacing("响应失败");
        });

    }

    @Test
    public void jdk11String() {

        String str = "  CCC \n Hello World'  \n ";
        System.out.println("  ".isBlank()); // true

        // strip() 更方便的处理空白字符和unicode
        System.out.println(str.strip() + "A");
        System.out.println(str.stripLeading() + "A");
        System.out.println(str.stripTrailing() + "A");

        // lines() 处理多行字符串
        str.lines().findFirst().ifPresent(System.out::println);

        System.out.println("C".repeat(2));
    }

    /**
     * 文件 读写
     *
     * @throws IOException
     */
    @Test
    public void jdk11File() throws IOException {
        Path path = Files.writeString(Paths.get("/Users/chengyufei/Downloads/tmp.txt"),
                "写入临时文件", StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        String content = Files.readString(Paths.get("/Users/chengyufei/Downloads/tmp.txt"));
        System.out.println(content);

    }

    /**
     * switch：
     * 1. 可以直接赋值给变量
     * 2.   -> 直接返回结果，且 无需手动添加 break
     * 3.  多个值匹配时， 可以在一个case 后面逗号拼接
     * 4.  代码块中操作时，可以使用  yield 进行最后结果的返回；
     */
    @Test
    public void jdk14() {
        Integer code = 2;
        String dayType = switch (DayEnum.get(code)) {
            case MONDAY, TUESDAY -> {
                String s = "忙起来".concat(" aaa").toUpperCase();
                yield s;
            }
            case WEDNESDAY -> "无聊";
            case THURSDAY -> "快点";
            case SATURDAY, SUNDAY -> "周末了";
            default -> "未知";
        };
        System.out.println(dayType);
    }

    /**
     * 1. 文本块特性：
     * 1. 之前复制过来文本时，会自动加换行符，json格式的还会自动加上转义符 等；
     * 2.  jdk15 使用三个双引号 形成文本块，可以保留原本的格式；
     * <p>
     * 2. 【了解】Hidden 隐藏类
     * Java 15引1入了Hidden 隐藏类特性，这是一个 专为框架和运行时环境设计 的底层机制，主要
     * 为了优化 动态生成短期类（比如 Lambda 表达式、动态代理）的性能问题，普通开发者无需关
     * 心。
     * 在Lambda 表达式、AOP 动态代理、ORM 映射等场景中，框架会动态生成代码载体（比如方法句柄、临时代理类），这些载体需要关联类的元数据才能运行。如果生成频繁，传统类的元数据会被类加载器追踪，需要等待类加载器卸载才能回收，导致元空间堆积和GC 压力。
     * Hidden 类的特点是对其定义类加载器之外的所有代码都不可见，由于不可发现且链接微弱，
     * JVM垃圾回收器能够更高效地卸载隐藏类及其元数据，从而防止短期类堆积对元空间造成压力，优化了需要动态生成大量类的性能;
     *
     *
     */
    @Test
    public void jdk15() {
        String ss = "insert into t_user_1 (id,pwd) values (2,'2') \n" +
                "\ton duplicate key update pwd = '重复'; ";

        String json = "{\n" +
                "  \"id\": 123,\n" +
                "  \"username\": \"json格式串\"\n" +
                "}";

        String newSS = """
                select co.order_status,log.order_status,co.id from c_order co
                join c_order_status_log log on co.id=log.orderId and find_in_set(log.order_status,'1032')
                where co.order_status=0 and co.changshang_id=5 ;
                """;

        String newJson = """
                    {
                      "id": %s,
                      "username":  %s
                    }
                """;
        System.out.println(newJson.formatted(1, "文本块格式的json串"));

    }

    /**
     * 1. record 类
     * 可用于临时创建pojo ，像创建一个方法一样 指定所需属性即可，避免之前 class + lombok 那样繁琐；
     * <p>
     * 2. instances 模式匹配
     * 在匹配时可直接定义变量名，无需在代码块中进行强制转换的这一步操作
     *
     */
    @Test
    public void jdk16() {

        record Person(String username, Integer age) {
        }
        ;
        Person person = new Person("临时用户", 20);
        System.out.println(person.username());
        System.out.println(person.toString()); // Person[username=临时用户, age=20]

        Object obj = "AAA";
        if (obj instanceof String) {
            String str = (String) obj;  // 需要定义一个变量接收显示强转后的内容
            System.out.println("旧版类型匹配");

        }

        if (obj instanceof String str) { // 直接在模式匹配时就可以定义变量，后面直接使用。 str 变量的作用域仅在if条件为true的代码块中；
            str = "新版模式匹配" + str.toUpperCase();
        }
    }

    /**
     * sealed 密封类，{@link com.smooth.feature.model.sealedExample.Shape}
     */
    @Test
    public void jdk17() {
    }

    /**
     * 1. 虚拟线程：
     *      1. 传统Java线程模型，每个线程都对应操作系统的一个真实线程，创建成本高，内存占用大。
     *          如创建100个线程去发送网络请求，大部分线程都会阻塞在等待网络响应上
     *
     * 2. switch 模式匹配：
     * 1. 直接在匹配对象类型的同时声明了变量【简化 instanceof 判断】
     * 2.  支持条件判断，case when
     * <p>
     * 3. record 模式，让数据解构直观，一次性读取record中所有需要的信息
     */
    @Test
    public void jdk21() {
        Object message = 123;

        // 旧版
        if (message instanceof String) {
            message = (String) message;
            System.out.println("文本消息" + message);
        } else if (message instanceof Integer) {
            message = (Integer) message;
            System.out.println("数字消息" + message);
        }

        // 新版 利用switch
        String result = switch (message) {
            case List str -> "集合消息" + str.size();
            case Integer number -> "数字消息" + number;
            case String s when s.length() > 100 -> "长文本消息" + s;
            default -> "未知类型";
        };


        //  record模式数据解构
        record Person(String username, int age) {
        }
        ;
        record Address(String city, String street) {
        }
        ;
        record Employee(Person person, Address address, double salary) {
        }
        ;

        Employee employee = null;

        switch (employee) {
            case Employee(Person(var name, var age), Address(var city, var street), var salary) ->
                    System.out.println(name + age + city + street + salary);

            case Employee(var person, var address, var salary) ->
                    System.out.println(person.username + address.street + salary);  // 直接读取信息
        }


        //利用虚拟线程执行器，在JVM层面创建1000个虚拟线程，由虚拟线程调度器将虚拟线程映射到系统的真实线程，可能只需要8个系统线程，每个系统线程会负责一批虚拟线程。比如系统线程1负责虚拟线程1发起网络请求，在虚拟线程1等待
        // 响应的时候，系统线程1可以去执行虚拟线程2的任务；

        //虚拟线程优势：超级轻量，一个虚拟线程只需几kb，可以轻松创建百万级别虚拟线程而不用担心系统资源
        try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1000; i++) {
                executorService.execute(() -> {
                    HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(3000L)).followRedirects(HttpClient.Redirect.NORMAL).build();
                });
            }
        }

        Thread.ofVirtual().start(() -> {
                //数据库查询
        });


    }
}
