package com.sziov.common.sharding.multidatasource.sdao;

import com.sziov.common.sharding.multidatasource.entity.po.ItemPo;

public interface ItemDao {
    
    void createIfNotExistsTable();
    
    void truncateTable();
    
    Long insert(ItemPo model);

    Long update(ItemPo model);
    
    void delete(Long itemId);
    
    void dropTable();

    ItemPo selectById(Long itemId);
}
