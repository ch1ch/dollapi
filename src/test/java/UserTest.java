import com.dollapi.Application;
import com.dollapi.domain.UserInfo;
import com.dollapi.domain.UserThird;
import com.dollapi.mapper.UserInfoMapper;
import com.dollapi.mapper.UserThirdMapper;

import com.dollapi.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
    public void selectByUnionId(){
        UserInfo userInfo= userInfoMapper.selectByUnionId("fa123nghexu123");

        String aa="asdfasdfsasdfds-123";
        Long v=Long.valueOf(aa.split("-")[1]);

        System.out.println(1);
    }

    @Test
    public void updateImage(){
        userService.getWXImage("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_ca79a146.png","asdf","");
    }



}
