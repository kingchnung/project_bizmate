package com.bizmate.common.config;

import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.sales.dto.SalesDTO;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import com.bizmate.salesPages.management.sales.salesItem.dto.SalesItemDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.bizmate")
public class RootConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)   // LOOSE → STRICT
                .setSkipNullEnabled(true);

        // SalesDTO → Sales: order는 수동으로만 세팅
        modelMapper.typeMap(SalesDTO.class, Sales.class)
                .addMappings(m -> m.skip(Sales::setOrder));

        // SalesItemDTO → SalesItem: 부모 연결은 코드에서만
        modelMapper.typeMap(SalesItemDTO.class, SalesItem.class)
                .addMappings(m -> m.skip(SalesItem::setSales));

        return modelMapper;
    }
}
