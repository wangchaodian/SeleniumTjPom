/**  
 * Copyright �? 2011 鎴愰兘鏄犳疆绉戞妧鏈夐檺鍏�?. All rights reserved.
 *
 * @Title: Proxy.java
 * @Porject: A_TestDemo
 * @Package: com.proxy
 * @Description: TODO
 * @author: Administrator  
 * @date: 2017骞�3鏈�6鏃� 涓婂�?11:43:47
 * @version: V1.0  
 */
package Sql;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Proxy
 * @Description: TODO
 * @author: <a href="mailto:Administrator@sunsharp.cn">Administrator</a>
 * @date: 2017骞�3鏈�6鏃� 涓婂�?11:43:47
 */
public interface Proxy {

    void insert(Object obj,String tableName);
    void insertWithId(Object obj,String tableName);
    void insert(String sql);
    void batchInsert(List<String> sqls);
    void save(Object obj,String tableName);
    void update(Object obj,String tableName);
    void update(String sql);
    void batchUpdate(List<String> sqls);
    void delete(Object obj,String tableName);
    boolean dealSql(String sql);
    Object getSingle(String sql);
    List<String> getListString(String sql);
    <T> List<T> findAllBySql(String sql,Class<T> clazz);
    <T> T getEntity(String sql,Class<T> clazz);
    List<Map<String, Object>> getListMap(String sql);
    List<Map<String, Object>> getListLinkedMap(String sql);
    
}
