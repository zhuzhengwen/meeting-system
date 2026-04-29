package com.ruoyi.system.mapper;

import java.util.Arrays;
import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import org.apache.ibatis.annotations.Param;

/**
 * 通用 Mapper 基接口（封装 MyBatis-Plus BaseMapper）
 *
 * <p>继承 MP 的 BaseMapper，额外提供：
 * <ul>
 *   <li>{@link #selectList(Object)} — 实体参数的列表查询，由各 Mapper 的 XML 实现</li>
 *   <li>{@link #softDeleteById(Long)} / {@link #softDeleteByIds(Long[])} — 逻辑删除别名，
 *       底层调用 MP 的 deleteById/deleteBatchIds，配合 @TableLogic 自动执行软删</li>
 * </ul>
 * </p>
 *
 * @param <T> 实体类型，需标注 {@code @TableName} 与 {@code @TableId}
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T>
{
    /**
     * 按实体条件查询列表（非空字段参与过滤）。
     *
     * <p>与 MP 的 {@code selectList(Wrapper)} 使用不同方法名，避免 XML 覆盖冲突。
     * 由各 Mapper XML 中 id="selectByCondition" 的语句实现，支持 LIKE、日期范围等。</p>
     */
    List<T> selectByCondition(T query);

    /** 逻辑删除单条（底层调用 MP deleteById + @TableLogic） */
    default int softDeleteById(Long id)
    {
        return deleteById(id);
    }

    /** 批量逻辑删除（底层调用 MP deleteBatchIds + @TableLogic） */
    default int softDeleteByIds(Long[] ids)
    {
        return deleteBatchIds(Arrays.asList(ids));
    }
}
