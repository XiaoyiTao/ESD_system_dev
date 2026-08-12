package com.foxconn.iad.esd.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EsdProperties.class)
public class EsdConfiguration {

    /**
     * 注册 MyBatis-Plus 分页拦截器。
     *
     * <p>分页查询使用 MySQL 方言生成 count 和 limit 语句，避免业务服务手写分页 SQL。</p>
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 该项目当前数据库固定为 MySQL；未来切换数据库时应同步调整方言。
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
