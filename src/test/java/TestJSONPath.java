import com.alibaba.fastjson.JSONPath;
import org.springframework.core.io.FileSystemResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class TestJSONPath {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String json = ""; //"{\"store\":{\"book\":[{\"title\":\"高效Java\",\"price\":10},{\"title\":\"研磨设计模式\",\"price\":12},{\"title\":\"重构\",\"isbn\":\"553\",\"price\":8},{\"title\":\"虚拟机\",\"isbn\":\"395\",\"price\":22}],\"bicycle\":{\"color\":\"red\",\"price\":19}}}";

        FileSystemResource res = new FileSystemResource(new File("/Users/metalfish/idea-project/freeman-mail/src/test/java/testjsonpath.js"));


        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(res.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String s = "";
            while((s = br.readLine()) != null) {
                sb.append(s);
            }
            json = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 获取json中store下book下的所有title值
        List<Object> titles = (List<Object>) JSONPath.read(json, "$.store.book.title");
//        System.out.println("$.store.book.title = " + titles);

        // 获取json中所有title的值
//        titles = (List<Object>) JSONPath.read(json, "$..title");
//        System.out.println("$..title = " + titles);

        // 获取json中book数组中包含isbn的所有值
//        List<Object> isbns = (List<Object>) JSONPath.read(json, "$.store.book[?(@.isbn)]");
//        System.out.println("$.store.book[?(@.isbn)] = " + isbns);

        // 获取json中book数组中price<10的所有值
        List<Object> prices = (List<Object>) JSONPath.read(json, "$.store.book[?(@.price < 10)]");
        System.out.println("$.store.book[?(@.price < 10)] = " + prices);

        // 获取json中book数组中的title等于“高效Java”的对象
        titles = (List<Object>) JSONPath.read(json, "$.store.book[?(@.type = 'b')]");
        System.out.println("$.store.book[?(@.type = 'b')] = " + titles);

        // 获取json中store下所有price的值
//        prices = (List<Object>) JSONPath.read(json, "$.store..price");
//        System.out.println("$.store..price = " + prices);

        // 获取json中book数组的前两个区间值
//        List<Object> books = (List<Object>) JSONPath.read(json, "$.store.book[:2]");
//        System.out.println("$.store.book[:2] = " + books);

        // 获取书个数
//        int size = (int) JSONPath.read(json, "$.store.book.size()");
//        System.out.println("$.store.book.size() = " + size);

    }

}
