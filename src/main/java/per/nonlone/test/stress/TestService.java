package per.nonlone.test.stress;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestAMapper testAMapper;

    @Autowired
    private TestBMapper testBMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public List<TestA> testInsertInTransactionA() {
        boolean result = true;
        int max = 2;
        List<TestA> testAList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            TestA testA = new TestA();
            testA.setName(RandomStringUtils.randomAlphanumeric(10));
            testA.setValue(RandomStringUtils.randomAlphanumeric(32));
            testA.setTimestamp(new Date());
            testA.setCreateTime(new Date());
            result &= testAMapper.insert(testA) > 0;
            if (!result) throw new RuntimeException();
            testAList.add(testA);
        }
        return testAList;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public List<TestB> testInsertInTransactionB() {
        boolean result = true;
        int max = 2;
        List<TestB> testBList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            TestB testB = new TestB();
            testB.setName(RandomStringUtils.randomAlphanumeric(10));
            testB.setValue(RandomStringUtils.randomAlphanumeric(32));
            testB.setTimestamp(new Date());
            testB.setCreateTime(new Date());
            result &= testBMapper.insert(testB) > 0;
            if (!result) throw new RuntimeException();
            testBList.add(testB);
        }
        return testBList;
    }

}
