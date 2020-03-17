import com.fyts.mail.EmailApplication;
import com.fyts.mail.entity.Email;
import com.fyts.mail.entity.User;
import com.fyts.mail.service.IMailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmailApplication.class)
public class EmailTest {
    @Autowired
    private IMailService emailService;

    @Test
    public void testSimple(){
        final User user = User.builder().protocol("smtp").host("smtp.163.com").port(25).username("lzxorz@163.com").password("YYERDRDYTDLHWQGG").nickname("O(∩_∩)O哈哈~").build();
        final Email email = Email.buildSimpleMail(user, new String[]{"594685906@qq.com"}, "测试简单邮件", "简单邮件内容");
        emailService.sendSimpleMail(email);
    }

    /*@Test
    public void testHtml(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("userName","程序员小明");
        map.put("gender","男");
        emailService.prepareAndSend(map);
    }*/


    // //发送简单邮件
    // sendMailService.sendSimpleMail("ai10*****32@126.com","标题","内容");
    // //发送html邮件
    // String content = "<html>\n" +
    //         "<body>\n"
    //         +"<h2>html邮件内容</h2><br><marquee>火车开走了□□□□□□□o0O```</marquee>\n"
    //         +"</body></html>";
    // sendMailService.sendHtmlMail("1******6@qq.com","给你的",content);
    // //发送附件邮件示例
    // sendMailService.sendAttachmentsMail("117***86@qq.com","给你的",content,"C:\\Users\\Administrator\\Pictures\\999.jpg");
    //
    // //发送图片邮件示例
    // String resId ="id001";
    // String content = "<html>\n" +
    //         "<body><h2>html邮件内容</h2><br><img src=\'cid:"+resId+"\'></img></body>" +
    //         "</html>";
    // //发图片邮件
    // sendMailService.sendInlinResourceMail("ai*****2@126.com","标题",
    // content,"C:\\Users\\Administrator\\Pictures\\\\999.jpg",resId);

}