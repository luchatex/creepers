package com.fosun.fc.projects.creepers.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fosun.fc.projects.creepers.entity.TCreepersTaskList;

/**
 *
 * <p>
 * description: T_CREEPERS_TASK_LIST 爬虫任务队列表
 * <p>
 * 
 * @author LiZhanPing
 * @since 2016-12-14 10:28:17
 * @see
 */

public interface CreepersTaskListDao
        extends JpaRepository<TCreepersTaskList, Long>, JpaSpecificationExecutor<TCreepersTaskList> {

    TCreepersTaskList findTop1ByUrl(String url);

    @Modifying
    @Query("select t.flag as flag,count(t.flag) as flagNum from TCreepersTaskList t where t.taskType=:taskType group by t.flag")
    List<Object[]> countByTaskType(@Param("taskType") String taskType);

    @Modifying
    @Query("select count(t.flag) as flagNum from TCreepersTaskList t where t.taskType=:taskType and t.flag=:flag")
    Long countByTaskTypeAndFlag(String taskType, String flag);

    @Modifying(clearAutomatically = true)
    @Query("update TCreepersTaskList t set t.flag = :flag where t.url = :url")
    void updateFlagByUrl(@Param("flag") String flag, @Param("url") String url);

    List<TCreepersTaskList> findByTaskTypeAndFlag(String taskType, String flag);

    List<TCreepersTaskList> findByTaskTypeAndFlagNot(String taskType, String flag);

    List<TCreepersTaskList> findByTaskTypeAndFlagIn(String taskType, Collection<String> flagList);

    List<TCreepersTaskList> findTop100000ByTaskType(String taskListType);

    @Modifying
    @Query("delete from TCreepersTaskList t where t.taskType =:taskType")
    void deleteByTaskType(@Param("taskType") String taskType);

    @Modifying
    @Query("update TCreepersTaskList t set t.flag = :newFlag where t.taskType =:taskType and t.flag in :flagList and rownum <= :rownum")
    void updateTaskListFlagByTaskTypeAndFlagIn(@Param("taskType") String taskType, @Param("flagList") Collection<String> flagList, @Param("newFlag") String newFlag, @Param("rownum")long rownum);
    
    @Modifying
    @Query("update TCreepersTaskList t set t.flag = :newFlag where t.taskType =:taskType and t.flag = :flag")
    void updateTaskListFlagByTaskTypeAndFlag(@Param("taskType") String taskType, @Param("flag") String flag, @Param("newFlag") String newFlag);

    List<TCreepersTaskList> findTop100000ByTaskTypeAndFlagNot(String taskListType, String flag);

    List<TCreepersTaskList> findTop100000ByTaskTypeAndFlagNotOrderByIdAsc(String taskListType, String flag);

    @Modifying
    @Query("update TCreepersTaskList t set t.flag = :newFlag where rownum < 100001 and t.taskType =:taskType and t.flag != :flag order by t.id")
    void updateTop100000TaskListFlagByTaskTypeAndFlagNotOrderById(@Param("taskType") String taskType, @Param("flag") String flag, @Param("newFlag") String newFlag);
    
    @Modifying
    @Query("update TCreepersTaskList t set t.flag = :newFlag where t.taskType =:taskType and t.flag != :flag and rownum <= :rownum")
    void updateTaskListFlagByTaskTypeAndFlagNot(@Param("taskType") String taskType, @Param("flag") String flag, @Param("newFlag") String newFlag, @Param("rownum")long rownum);

    @Query(nativeQuery = true,value ="select min(to_number(substr(t.url, instr(t.url, :keyWord, 30, 1)+:keyWordLen, 100))) from t_creepers_task_list t where t.task_type = :taskType")
    int selectUrlMinIndexByTaskTypeAndKeyWordAndKeyWordLen(@Param("taskType") String taskType,@Param("keyWord") String keyWord,@Param("keyWordLen") int keyWordLen);
    
    @Query(nativeQuery = true,value ="select max(to_number(substr(t.url, instr(t.url, :keyWord, 30, 1)+:keyWordLen, 100))) from t_creepers_task_list t where t.task_type = :taskType")
    int selectUrlMaxIndexByTaskTypeAndKeyWordAndKeyWordLen(@Param("taskType") String taskType,@Param("keyWord") String keyWord,@Param("keyWordLen") int keyWordLen);
    
    @Query("select count(1) from TCreepersTaskList t where t.taskType = :taskType and t.flag != :flag")
    int countByTaskTypeAndFlagNot(@Param("taskType")String taskType, @Param("flag")String flag);
    
    @Query("select t from TCreepersTaskList t where t.taskType = :taskType and t.flag != :flag and rownum <= :rownum")
    List<TCreepersTaskList> findByTaskTypeAndFlagNotAndRownum(@Param("taskType")String taskType, @Param("flag")String flag, @Param("rownum")long rownum);
    
    @Query("select count(1) from TCreepersTaskList t where t.taskType = :taskType and t.flag in :flagList")
    int countByTaskTypeAndFlagIn(@Param("taskType")String taskType, @Param("flagList")Collection<String> flagList);
    
    @Query("select t from TCreepersTaskList t where t.taskType = :taskType and t.flag in :flagList and rownum <= :rownum")
    List<TCreepersTaskList> findByTaskTypeAndFlagInAndRownum(@Param("taskType")String taskType, @Param("flagList")Collection<String> flagList, @Param("rownum")long rownum);
}
