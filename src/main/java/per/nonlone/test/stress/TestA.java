package per.nonlone.test.stress;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="test")
@Data
public class TestA {

    @Id
    private Long id;

    private String name;

    private String value;

    private String timestamp;

    private String createTime;

}
