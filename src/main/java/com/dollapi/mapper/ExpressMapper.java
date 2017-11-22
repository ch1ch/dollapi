package com.dollapi.mapper;

import com.dollapi.domain.Express;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ExpressMapper {
    public void save(Express express);

    public List<Express> selectByUserId(Long userId);

    public Express selectById(Long id);

    public List<Express> selectAll(Map<String, Object> params);

    public void update(Express express);



}
