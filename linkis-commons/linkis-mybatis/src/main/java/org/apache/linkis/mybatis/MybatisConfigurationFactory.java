/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.linkis.mybatis;

import org.apache.linkis.mybatis.conf.MybatisConfiguration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageInterceptor;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
@EnableTransactionManagement
public class MybatisConfigurationFactory {

  private static final Logger logger = LoggerFactory.getLogger(MybatisConfigurationFactory.class);

  @Autowired private DataSource dataSource;

  @Bean(name = "sqlSessionFactory")
  @Primary
  public MybatisSqlSessionFactoryBean sqlSessionFactory() {
    String typeAliasesPackage =
        MybatisConfiguration.BDP_SERVER_MYBATIS_TYPEALIASESPACKAGE.getValue();
    // Configure the mapper scan to find all mapper.xml mapping
    // files(??????mapper???????????????????????????mapper.xml????????????)
    String mapperLocations = MybatisConfiguration.BDP_SERVER_MYBATIS_MAPPER_LOCATIONS.getValue();
    // Load the global configuration file(???????????????????????????)
    String configLocation = MybatisConfiguration.BDP_SERVER_MYBATIS_CONFIGLOCATION.getValue();
    try {
      MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
      sessionFactoryBean.setDataSource(dataSource);

      logger.info("Mybatis typeAliasesPackage=" + typeAliasesPackage);
      logger.info("Mybatis mapperLocations=" + mapperLocations);
      logger.info("Mybatis configLocation=" + configLocation);
      // Read configuration(????????????)
      sessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);

      // Set the location of the mapper.xml file(??????mapper.xml??????????????????)
      if (StringUtils.isNotBlank(mapperLocations)) {
        String[] mapperArray = mapperLocations.split(",");
        List<Resource> resources = new ArrayList<>();
        for (String mapperLocation : mapperArray) {
          CollectionUtils.addAll(
              resources, new PathMatchingResourcePatternResolver().getResources(mapperLocation));
        }
        sessionFactoryBean.setMapperLocations(resources.toArray(new Resource[0]));
      }
      /* Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperLocations);
      sessionFactoryBean.setMapperLocations(resources);*/
      //            Set the location of the mybatis-config.xml configuration
      // file(??????mybatis-config.xml??????????????????)
      sessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));

      //            Add paging plugin, print sql plugin(???????????????????????????sql??????)
      Interceptor[] plugins = new Interceptor[] {pageInterceptor()};
      sessionFactoryBean.setPlugins(plugins);

      return sessionFactoryBean;
    } catch (IOException e) {
      logger.error("mybatis resolver mapper*xml is error", e);
      return null;
    } catch (Exception e) {
      logger.error("mybatis sqlSessionFactoryBean create error", e);
      return null;
    }
  }

  @Bean
  @Primary
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  @Bean
  @Primary
  public PlatformTransactionManager annotationDrivenTransactionManager() {
    return new DataSourceTransactionManager(dataSource);
  }
  //    Log the log to be executed (if you don't want to intercept it, comment out this method)
  //    ???????????????sql??????????????????(???????????????????????????????????????)
  //    @Bean
  //    public SqlPrintInterceptor sqlPrintInterceptor(){
  //        return new SqlPrintInterceptor();
  //    }

  @Bean
  public PageInterceptor pageInterceptor() {
    PageInterceptor pageInterceptor = new PageInterceptor();
    Properties p = new Properties();
    //        p.setProperty("offsetAsPageNum", "true");
    //        p.setProperty("rowBoundsWithCount", "true");
    p.setProperty("reasonable", "true");
    p.setProperty("pageSizeZero", "true");
    p.setProperty("helperDialect", "mysql");
    pageInterceptor.setProperties(p);
    return pageInterceptor;
  }
}
