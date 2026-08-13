package com.foxconn.iad.module.esd.config;

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
     * 註冊 MyBatis-Plus 分頁攔截器。
     *
     * <p>分頁查詢使用 MySQL 方言生成 count 和 limit 語句，避免業務服務手寫分頁 SQL。</p>
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 該項目當前資料庫固定為 MySQL；未來切換資料庫時應同步調整方言。
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
