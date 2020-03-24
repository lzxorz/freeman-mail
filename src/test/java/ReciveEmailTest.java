import com.fyts.mail.MailApplication;
import com.fyts.mail.common.constants.Constants;
import com.fyts.mail.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.*;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import static javax.mail.internet.MimeUtility.decodeText;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailApplication.class)
public class ReciveEmailTest {
    @Autowired
    private IMailService emailService;

    @Test
    public void testSimple(){
        // final MailAccount mailAccount = MailAccount.builder().protocol("smtp").serverHost("smtp.163.com").serverPort(25).username("lzxorz@163.com").password("YYERDRDYTDLHWQGG").name("O(∩_∩)O哈哈~").build();
        // final Mail mail = Mail.buildSimpleMail(mailAccount, new String[]{"594685906@qq.com"}, "测试简单邮件", "简单邮件内容");
        // emailService.sendSimpleMail(mail);
    }





    /////////////////////////////////接收邮件参考文章///////////////////////////////////
    //原文链接：https://blog.csdn.net/m0_37758648/article/details/85246210
    //原文链接：https://blog.csdn.net/CLG_CSDN/article/details/85261438
    //原文链接：https://blog.csdn.net/mathlpz126/article/details/89392589
    //原文链接：https://www.cnblogs.com/shihaiming/p/10416383.html
    /**
     * 接收邮件
     * 为啥不用pop3而用imap，是因为imap能把邮件设置为已读，而pop3不能；
     */
    // @Test
    // public void timerTaskInfo(){
    //     //邮件配置信息
    //     String host= "imap.163.com";
    //     String userName="lzxorz@163.com";
    //     String passWord="YYERDRDYTDLHWQGG";
    //     //邮件配置类
    //     Properties properties=new Properties();
    //     //邮件配置缓存
    //     Session session=Session.getDefaultInstance(properties);
    //     session.setDebug(true);
    //     String  fileName=null;
    //     try {
    //         //邮件服务器的类型
    //         Store store = session.getStore("imap");
    //         //连接邮箱服务器
    //         store.connect(host,userName,passWord);
    //         // 获得用户的邮件帐户
    //         Folder folder=store.getFolder("INBOX");
    //         if (folder == null) {
    //             log.info("获取邮箱文件信息为空");
    //         }
    //         // 设置对邮件帐户的访问权限可以读写
    //         folder.open(Folder.READ_WRITE);
    //         Calendar calendar=Calendar.getInstance();
    //         calendar.add(Calendar.DATE,-1);
    //         Date mondayDate = calendar.getTime();
    //         SearchTerm comparisonTermLe = new SentDateTerm(ComparisonTerm.GT, mondayDate);
    //         SearchTerm address=new SubjectTerm( "MU Report");
    //         SearchTerm comparisonAndTerm = new AndTerm(address, comparisonTermLe);
    //         Message[] messages = folder.search(comparisonAndTerm);
    //         for(int i = 0 ; i < messages.length ; i++){
    //             MimeMessage msg = (MimeMessage) messages[i];
    //             //判断是否有附件
    //             boolean isContainerAttachment = isContainAttachment(msg);
    //             if (isContainerAttachment) {
    //                 //保存附件
    //                 fileName=saveAttachment(msg,Constants.STORAGE_FILE);
    //                 //保存接收到的邮件并且收件箱删除邮件
    //                 msg.setFlag(Flags.Flag.DELETED, true);
    //             }
    //             if(!isContainerAttachment) {
    //                 continue;
    //             }
    //         }
    //         folder.close(true);
    //         store.close();
    //         parseTxtService.readTxt(fileName);
    //     }catch (NoSuchProviderException e){
    //         log.error("接收邮箱信息异常:{}",e);
    //     }catch (MessagingException e){
    //         log.error("连接邮箱服务器信息异常:{}",e);
    //     }catch (IOException e){
    //         log.error("接收邮箱信息解析异常:{}",e);
    //     }catch (IllegalStateException e){
    //         log.error("接收邮箱信息为空:{}",e);
    //     }
    // }
    /**
     * 判断邮件中是否包含附件
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    // public static boolean isContainAttachment(Part part) throws MessagingException, IOException {
    //     boolean flag = false;
    //     if (part.isMimeType(Constants.MULTI_PART)) {
    //         MimeMultipart multipart = (MimeMultipart) part.getContent();
    //         int partCount = multipart.getCount();
    //         for (int i = 0; i < partCount; i++) {
    //             BodyPart bodyPart = multipart.getBodyPart(i);
    //             String disp = bodyPart.getDisposition();
    //             if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) ||
    //                     disp.equalsIgnoreCase(Part.INLINE))) {
    //                 flag = true;
    //             } else if (bodyPart.isMimeType(Constants.MULTI_PART)) {
    //                 flag = isContainAttachment(bodyPart);
    //             } else {
    //                 String contentType = bodyPart.getContentType();
    //                 if (contentType.indexOf(Constants.APPLICATION_CONTEXT) != -1) {
    //                     flag = true;
    //                 }
    //                 if (contentType.indexOf(Constants.NAME_CONTEXT) != -1) {
    //                     flag = true;
    //                 }
    //             }
    //             if (flag){
    //                 break;
    //             }
    //         }
    //     } else if (part.isMimeType(Constants.MESSAGE_RFC)) {
    //         flag = isContainAttachment((Part)part.getContent());
    //     }
    //     return flag;
    // }
    // /**
    //  * 保存附件
    //  * @param part 邮件中多个组合体中的其中一个组合体
    //  * @param destDir  附件保存目录
    //  * @throws UnsupportedEncodingException
    //  * @throws MessagingException
    //  * @throws FileNotFoundException
    //  * @throws IOException
    //  */
    // public  String saveAttachment(Part part, String destDir) throws UnsupportedEncodingException,
    //         MessagingException, FileNotFoundException, IOException {
    //     String fileName=null;
    //     if (part.isMimeType(Constants.MULTI_PART)) {
    //         Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
    //         //复杂体邮件包含多个邮件体
    //         int partCount = multipart.getCount();
    //         for (int i = 0; i < partCount; i++) {
    //             //获得复杂体邮件中其中一个邮件体
    //             BodyPart bodyPart = multipart.getBodyPart(i);
    //             //某一个邮件体也有可能是由多个邮件体组成的复杂体
    //             String disp = bodyPart.getDisposition();
    //             if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase
    //                     (Part.INLINE))) {
    //                 InputStream is = bodyPart.getInputStream();
    //                 saveFile(is, destDir, decodeText(bodyPart.getFileName()));
    //                 fileName=decodeText(bodyPart.getFileName());
    //             } else if (bodyPart.isMimeType(Constants.MULTI_PART)) {
    //                 saveAttachment(bodyPart,destDir);
    //             } else {
    //                 String contentType = bodyPart.getContentType();
    //                 if (contentType.indexOf(Constants.NAME_CONTEXT) != -1 || contentType.indexOf
    //                         (Constants.APPLICATION_CONTEXT) != -1) {
    //                     saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
    //                     fileName=decodeText(bodyPart.getFileName());
    //                 }
    //             }
    //         }
    //     } else if (part.isMimeType(Constants.MESSAGE_RFC)) {
    //         saveAttachment((Part) part.getContent(),destDir);
    //     }
    //     return fileName;
    // }
    // /**
    //  * 读取输入流中的数据保存至指定目录
    //  * @param is 输入流
    //  * @param fileName 文件名
    //  * @param destDir 文件存储目录
    //  * @throws FileNotFoundException
    //  * @throws IOException
    //  */
    // private  void saveFile(InputStream is, String destDir, String fileName)
    //         throws FileNotFoundException, IOException {
    //     BufferedInputStream bis = new BufferedInputStream(is);
    //     if(fileName.contains(Constants.TXT_SUFIXX)) {
    //         BufferedOutputStream bos = new BufferedOutputStream(
    //                 new FileOutputStream(new File(destDir + fileName)));
    //         int len = -1;
    //         while ((len = bis.read()) != -1) {
    //             bos.write(len);
    //             bos.flush();
    //         }
    //         bos.close();
    //         bis.close();
    //     }
    // }


}