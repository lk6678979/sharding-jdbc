import com.sziov.Application;
import com.sziov.common.sharding.multidatasource.dao.OrderDao;
import com.sziov.common.sharding.multidatasource.entity.po.ItemPo;
import com.sziov.common.sharding.multidatasource.entity.po.OrderPo;
import com.sziov.common.sharding.multidatasource.sdao.ItemDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ItemDao itemDao;

    @Test
    public void contextLoads() {
        System.out.println("测试开始");
        OrderPo orderPo = new OrderPo();
        orderPo.setTradedate("2019-09-20");
        orderPo.setStatus("已配送");
        orderPo.setUserId(1);
        orderDao.insert(orderPo);

        ItemPo itemPo = new ItemPo();
        itemPo.setOrderId(orderPo.getOrderId());
        itemPo.setPrice("98");
        itemPo.setProName("神界原罪2");
        itemPo.setUserId(1);
        itemDao.insert(itemPo);

        System.out.println("测试结束");
    }

}
