package per.nonlone.test.stress;

import lombok.Data;

import java.util.Date;

@Data
public class InsertRequest {

    private String name;

    private String value;

    private Date timestamp;

}
