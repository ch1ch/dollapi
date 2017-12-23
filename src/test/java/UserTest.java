import com.dollapi.Application;
import com.dollapi.domain.RechargeOrder;
import com.dollapi.domain.UserInfo;
import com.dollapi.domain.UserThird;
import com.dollapi.mapper.*;

import com.dollapi.service.OrderService;
import com.dollapi.service.UserService;
import com.dollapi.vo.UserLine;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
//@Transactional
public class UserTest {

    @Autowired
    private UserThirdMapper userThirdMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    @Autowired
    private RechargeMiaoMiaoMapper rechargeMiaoMiaoMapper;

    @Test
    public void callBack() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(4129L);
        userInfo.setDollCount(0L);

        orderService.callBack(userInfo, 1L, "1291adc8c14449b38725d0579565d8cc", 2);
    }

    @Test
    public void rechargeCallBack() {
        orderService.rechargeCallBack("11118c3528b45ffad2025b9f63de514", "testtest");
    }

    @Test
//    @Rollback
    public void findByName() throws Exception {
        UserThird yser = userThirdMapper.selectByUnionId("fangh23");
        System.out.println(1);
    }

    @Test
    public void selectByUnionId() {
        UserInfo userInfo = userInfoMapper.selectByUnionId("fa123nghexu123");

        String aa = "asdfasdfsasdfds-123";
        Long v = Long.valueOf(aa.split("-")[1]);

        System.out.println(1);
    }

    @Test
    public void updateImage() {
        userService.getWXImage("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_ca79a146.png", "asdf", "");
    }

    @Test
    public void test() throws ParseException {
//        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://wd4203789592nwyjxt.wilddogio.com").build();
//        WilddogApp.initializeApp(options);
////        WilddogApp.initializeApp(options, "视频测试102");
//        SyncReference ref = WilddogSync.getInstance().getReference();
////        SyncReference ref = WilddogSync.getInstance().getReference("test");
//        HashMap<String, Object> user = new HashMap<>();
//        user.put("full_name", "Steve Jobs");
//        user.put("gender", "male");
//        ref.child("testname").setValue(user);
////        ref.child("Jobs").setValue(user, new SyncReference.CompletionListener() {
////            @Override
////            public void onComplete(SyncError error, SyncReference ref) {
////                if (error != null) {
////                    System.out.println(error.getMessage());
////                } else {
////                    System.out.println("setValue success");
////                }
////            }
////        });
//        System.out.println(123);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        List<UserLine> userLineList = new ArrayList<>();
        UserLine u1 = new UserLine();
        u1.setUserId(1L);
        u1.setCreateTime(sdf.parse("2017-10-07 02:10:00"));
        UserLine u2 = new UserLine();
        u2.setUserId(2L);
        u2.setCreateTime(sdf.parse("2017-10-07 02:11:00"));
        UserLine u3 = new UserLine();
        u3.setUserId(3L);
        u3.setCreateTime(sdf.parse("2017-10-07 02:01:00"));
        userLineList.add(u1);
        userLineList.add(u2);
        userLineList.add(u3);

        userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
        userLineList.remove(0);


        System.out.println(11);
    }

    @Test
    public void testPay() {
        String payInfo = orderService.rechargePay(1L);
        System.out.println(11);
    }

    @Test
    public void testGetOrder() {
        RechargeOrder order = rechargeOrderMapper.selectById("97f3a0d7577f4351b7b90da37410a192");
        System.out.println(11);
    }

    @Test
    public void ttt() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = sdf.parse("2017-11-06 18:48:00");
        System.out.println((new Date().getTime() - d.getTime()) / 1000);
    }

    @Test
    public void updateCode() {
        Map<String, Object> params = new HashMap<>();
        List<UserInfo> list = userInfoMapper.selectAllUser(params);
        for (UserInfo userInfo : list) {
            if (userInfo.getInvitationCode() == null || userInfo.getInvitationCode().equals("")) {
                userInfo.setInvitationCode(UUID.randomUUID().toString().replaceAll("-", ""));
                userInfoMapper.update(userInfo);
            }
        }
    }

    @Test
    public void selectToDayCountByUserId() {
//        Long i=rechargeMiaoMiaoMapper.selectToDayCountByUserId(1L);
//        System.out.println(1);
        String a = "miaomiaoyouji123";

        a = a.replace("miaomiaoyouji", "");
        System.out.println(1);
    }


}
