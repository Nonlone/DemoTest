package per.nonlone.test.stress;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class QueryByOrderIdRequest {

    @NotBlank(message = "订单Id不能为空")
    private String orderId;

}
