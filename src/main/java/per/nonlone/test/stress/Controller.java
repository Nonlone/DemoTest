package per.nonlone.test.stress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private TestAMapper testAMapper;

    @Autowired
    private TestBMapper testBMapper;

    @PostMapping("/insert/")
    public Object insert(){
        return null;
    }


}
