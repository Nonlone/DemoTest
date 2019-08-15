package per.nonlone.test.stress;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name="test")
@Data
public class TestA {

    @Id
    private Long id;

    private String name;

    private String value;

    private Date timestamp;

    private Date createTime;

}
