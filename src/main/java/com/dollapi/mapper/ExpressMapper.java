package com.dollapi.mapper;

import com.dollapi.domain.Express;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ExpressMapper {
    public void save(Express express);

    public List<Express> selectByUserId(Long userId);
}
