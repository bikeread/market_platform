package com.carclub.repository.mapper;

import com.carclub.repository.model.MtArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章 Mapper 接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface MtArticleMapper extends BaseMapper<MtArticle> {
   void increaseClick(@Param("articleId") Integer articleId);
}
