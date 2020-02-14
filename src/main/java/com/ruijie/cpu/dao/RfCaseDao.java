package com.ruijie.cpu.dao;


import com.ruijie.cpu.entity.RfCase;
import com.ruijie.cpu.entity.mysqlEntity.CaseGroupAns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RfCaseDao extends CrudRepository<RfCase,Long>, JpaRepository<RfCase,Long> {


//    @Query("update HsAlbum album set album.albumName = #{#hsAlbum.albumName} ,album.description = #{#hsAlbum.description } ")
//    public int update(HsAlbum hsAlbum);

    @Query(value = "select count(comp) as rowCount,comp,com,level_min,level0,level1,level2,level3,spec,srs," +
            "sum(l_time) as sumTime from rf_case c " +
            "group by comp,com,level_min,level0,level1,level2,level3,spec,srs ", nativeQuery = true)
    List<CaseGroupAns> findAllByGroup();


    @Query(value = "select count(comp) as rowCount,comp,com,level_min,level0,level1,level2,level3,spec,srs," +
            "sum(l_time) as sumTime from rf_case c where nox86=false " +
            "group by comp,com,level_min,level0,level1,level2,level3,spec,srs ", nativeQuery = true)
    List<CaseGroupAns> findAllByGroupSupportX86();

    @Query(value = "select distinct com from rf_case c where comp=?1" , nativeQuery = true)
    List<String> findAllComsByComp(String comp);

    @Query(value = "select distinct comp from rf_case", nativeQuery = true)
    List<String> findAllComps();

    List<RfCase> findByTag(String tag);
}
