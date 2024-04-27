package com.switchvov.magicconfig.server.dal;

import com.switchvov.magicconfig.server.model.Configs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * mapper for configs table.
 * @author switch
 * @since 2024/4/27
 */
@Repository
@Mapper
public interface ConfigMapper {
    @Select("SELECT * FROM `configs` WHERE app=#{app} AND env=#{env} AND ns=#{ns}")
    List<Configs> list(String app, String env, String ns);

    @Select("SELECT * FROM `configs` WHERE app=#{app} AND env=#{env} AND ns=#{ns} AND pkey=#{pkey}")
    Configs select(String app, String env, String ns, String pkey);

    @Insert("INSERT INTO `configs`(`app`, `env`, `ns`, `pkey`, `pval`) VALUES (#{app}, #{env}, #{ns}, #{pkey}, #{pval})")
    int insert(Configs configs);

    @Insert("UPDATE `configs` SET pval=#{pval} WHERE app=#{app} AND env=#{env} AND ns=#{ns} AND pkey=#{pkey}")
    int update(Configs configs);
}
