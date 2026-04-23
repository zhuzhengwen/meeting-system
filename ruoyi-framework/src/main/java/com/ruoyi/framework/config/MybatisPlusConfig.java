package com.ruoyi.framework.config;

import java.util.Date;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * MyBatis-Plus 自动填充处理器
 * 通过 MP 的 insert/update 方法时自动填充 createTime / updateTime
 */
@Component
public class MybatisPlusConfig implements MetaObjectHandler
{
    @Override
    public void insertFill(MetaObject metaObject)
    {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject)
    {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
