package com.blinkfox.fenix.core.builder;

import com.blinkfox.fenix.bean.BuildSource;
import com.blinkfox.fenix.bean.SqlInfo;
import com.blinkfox.fenix.exception.FenixException;
import com.blinkfox.fenix.helper.ParseHelper;
import com.blinkfox.fenix.helper.StringHelper;

import java.util.Collection;

/**
 * 构建使用 XML 拼接 JPQL 或者 SQL 片段的构建器.
 *
 * @author blinkfox on 2019-08-06.
 */
public final class XmlSqlInfoBuilder extends SqlInfoBuilder {

    /**
     * 构造方法.
     *
     * @param source 构建资源
     */
    public XmlSqlInfoBuilder(BuildSource source) {
        super(source);
    }

    /**
     * 通过计算 XML 中 value 属性的值来追加构建常规 SQL 片段需要的 {@link SqlInfo} 信息.
     *
     * @param fieldText 字段文本值
     * @param valueText 参数值
     */
    public void buildNormalSql(String fieldText, String valueText) {
        super.buildNormalSql(fieldText, valueText, ParseHelper.parseExpressWithException(valueText, context));
    }

    /**
     * 追加构建 'LIKE' 模糊查询的 {@link SqlInfo} 信息.
     *
     * @param fieldText 字段文本值
     * @param valueText 参数值
     * @param patternText 模式字符串文本
     */
    public void buildLikeSql(String fieldText, String valueText, String patternText) {
        if (StringHelper.isNotBlank(valueText) && StringHelper.isBlank(patternText)) {
            super.buildLikeSql(fieldText, valueText, ParseHelper.parseExpressWithException(valueText, context));
        } else if (StringHelper.isBlank(valueText) && StringHelper.isNotBlank(patternText)) {
            super.buildLikePatternSql(fieldText, patternText);
        } else {
            throw new FenixException("【Fenix 异常】<like /> 相关的标签中，【value】属性和【pattern】属性不能同时为空或者同时不为空！");
        }
    }

    /**
     * 追加构建 'BETWEEN ? AND ?'、'>='、'<=' 的区间查询的 {@link SqlInfo} 信息.
     *
     * @param fieldText 字段文本值
     * @param startText 开始文本
     * @param endText 结束文本
     */
    public void buildBetweenSql(String fieldText, String startText, String endText) {
        super.buildBetweenSql(fieldText, startText, ParseHelper.parseExpress(startText, context),
                endText, ParseHelper.parseExpress(endText, context));
    }

    /**
     * 追加构建 'IN' 的范围查询的 {@link SqlInfo} 信息.
     *
     * <p>获取 value 的值，判断是否为空，若为空，则不做处理.</p>
     *
     * @param fieldText 字段文本值
     * @param valueText IN 所要查找的范围文本值
     */
    public void buildInSql(String fieldText, String valueText) {
        Object obj = ParseHelper.parseExpressWithException(valueText, context);
        if (obj != null) {
            super.buildInSql(fieldText, valueText, obj);
        }
    }

    /**
     * 构建任意文本和自定义有序参数集合来构建的sqlInfo信息.
     * @param valueText value参数值
     * @return 返回SqlInfo信息
     */
    public void buildTextSqlParams(String valueText) {
        // 获取value值，判断是否为空，若为空，则直接退出本方法
//        Object obj = ParseHelper.parseExpressWithException(valueText, context);
//        obj = obj == null ? new Object(){} : obj;
//
//        Object[] values = this.convertToArray(obj);
//        for (Object objVal: values) {
//            this.sqlInfo.getParams().add(objVal);
//        }
    }

    /**
     * 将对象转成数组.
     *
     * @param obj 对象
     * @return 数组
     */
    @SuppressWarnings("rawtypes")
    private Object[] convertToArray(Object obj) {
        if (obj instanceof Collection) {
            return ((Collection) obj).toArray();
        } else if (obj.getClass().isArray()) {
            return (Object[]) obj;
        } else {
            return new Object[]{obj};
        }
    }

}