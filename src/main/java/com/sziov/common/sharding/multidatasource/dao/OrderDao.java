package com.sziov.common.sharding.multidatasource.dao;

import com.sziov.common.sharding.multidatasource.entity.po.OrderPo;

public interface OrderDao {
    
    void createIfNotExistsTable();
    
    void truncateTable();
    
    Long insert(OrderPo model);
    
    void delete(Long orderId);
    
    void dropTable();

    OrderPo selectById(Long orderId);
}
