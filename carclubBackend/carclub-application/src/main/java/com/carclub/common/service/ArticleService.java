package com.carclub.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carclub.common.dto.ArticleDto;
import com.carclub.common.param.ArticlePage;
import com.carclub.framework.pagination.PaginationResponse;
import com.carclub.repository.model.MtArticle;
import com.carclub.framework.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 文章业务接口
 *
 * Created by FSQ
 * CopyRight carclub
 */
public interface ArticleService extends IService<MtArticle> {

    /**
     * 分页查询文章列表
     *
     * @param articlePage
     * @return
     */
    PaginationResponse<ArticleDto> queryArticleListByPagination(ArticlePage articlePage) throws BusinessCheckException;

    /**
     * 添加文章
     *
     * @param  articleDto
     * @throws BusinessCheckException
     */
    MtArticle addArticle(ArticleDto articleDto) throws BusinessCheckException;

    /**
     * 根据ID获取文章信息
     *
     * @param  id 文章ID
     * @throws BusinessCheckException
     */
    MtArticle queryArticleById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID获取文章详情
     *
     * @param  id 文章ID
     * @throws BusinessCheckException
     */
    ArticleDto getArticleDetail(Integer id) throws BusinessCheckException;

    /**
     * 更新文章
     * @param  articleDto
     * @throws BusinessCheckException
     * */
    MtArticle updateArticle(ArticleDto articleDto) throws BusinessCheckException;

    /**
     * 根据条件搜索文章
     *
     * @param params
     * @return
     * */
    List<MtArticle> queryArticleListByParams(Map<String, Object> params) throws BusinessCheckException;

}
