import com.dollapi.Application;
import com.dollapi.domain.UserInfo;
import com.dollapi.domain.UserThird;
import com.dollapi.mapper.UserInfoMapper;
import com.dollapi.mapper.UserThirdMapper;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
public class UserTest {

    @Autowired
    private UserThirdMapper userThirdMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserService userService;

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


}
