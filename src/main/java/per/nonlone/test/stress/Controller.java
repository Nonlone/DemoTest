package per.nonlone.test.stress;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class Controller {

    @Autowired
    protected PlatformTransactionManager platformTransactionManager;
    @Autowired
    private TestAMapper testAMapper;
    @Autowired
    private TestBMapper testBMapper;
    @Autowired
    private TestService testService;
    @Autowired
    private TestSubService testSubService;

    @RequestMapping("/insert")
    public Object insert(@RequestBody InsertRequest insertRequest) {

        TestA testA = new TestA();
        BeanUtils.copyProperties(insertRequest, testA);
        testA.setCreateTime(new Date());
        testAMapper.insert(testA);


        TestB testB = new TestB();
        BeanUtils.copyProperties(insertRequest, testB);
        testB.setCreateTime(new Date());
        testBMapper.insert(testB);

        JSONObject json = new JSONObject();
        json.put("testA", testA);
        json.put("testB", testB);

        return json;
    }


    @RequestMapping("/insertWithTransc")
    public Object insertWithTrans(@RequestBody InsertRequest insertRequest) {

        TestA testA = new TestA();
        BeanUtils.copyProperties(insertRequest, testA);
        testA.setCreateTime(new Date());
        testAMapper.insert(testA);


        List<TestA> testAList = testSubService.testInsertInTransactionA();


        TestB testB = new TestB();
        BeanUtils.copyProperties(insertRequest, testB);
        testB.setCreateTime(new Date());
        testBMapper.insert(testB);

        JSONObject json = new JSONObject();
        json.put("testB", testB);
        json.put("testAList", testAList);
        json.put("testA", testA);


        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(platformTransactionManager);
        transactionTemplate.setIsolationLevel(Isolation.REPEATABLE_READ.value());
        transactionTemplate.execute(t -> {

            TestA a = new TestA();
            a.setName(RandomStringUtils.randomAlphanumeric(10));
            a.setValue(RandomStringUtils.randomAlphanumeric(32));
            a.setTimestamp(new Date());
            a.setCreateTime(new Date());
            testAMapper.insert(a);


            return null;
        });

        return json;
    }


}
