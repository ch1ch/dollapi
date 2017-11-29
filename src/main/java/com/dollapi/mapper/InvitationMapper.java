package com.dollapi.mapper;

import com.dollapi.domain.Invitation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface InvitationMapper {

    public void save(Invitation invitation);

    public List<Invitation> selectByUserId(Long userId);

    List<Invitation> selectByRecommendUserId(Long recommendUserId);

}
