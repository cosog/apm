package com.cosog.dao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.annotation.Resource;

import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.internal.OracleClob;
import oracle.sql.BLOB;
import oracle.sql.CLOB;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cosog.model.AlarmShowStyle;
import com.cosog.model.KeyParameter;
import com.cosog.model.Org;
import com.cosog.model.ProductionOutWellInfo;
import com.cosog.model.Pump;
import com.cosog.model.User;
import com.cosog.model.WellTrajectory;
import com.cosog.model.Wellorder;
import com.cosog.model.WellsiteGridPanelData;
import com.cosog.model.balance.BalanceResponseData;
import com.cosog.model.balance.CycleEvaluaion;
import com.cosog.model.balance.TorqueBalanceResponseData;
import com.cosog.model.calculate.CommResponseData;
import com.cosog.model.calculate.TimeEffResponseData;
import com.cosog.model.calculate.TimeEffTotalResponseData;
import com.cosog.model.calculate.WellAcquisitionData;
import com.cosog.model.drive.AcquisitionGroupResolutionData;
import com.cosog.model.drive.AcquisitionItemInfo;
import com.cosog.model.drive.KafkaConfig;
import com.cosog.model.drive.ModbusProtocolConfig;
import com.cosog.model.gridmodel.AuxiliaryDeviceHandsontableChangedData;
import com.cosog.model.gridmodel.CalculateManagerHandsontableChangedData;
import com.cosog.model.gridmodel.ElecInverCalculateManagerHandsontableChangedData;
import com.cosog.model.gridmodel.InverOptimizeHandsontableChangedData;
import com.cosog.model.gridmodel.ProductionOutGridPanelData;
import com.cosog.model.gridmodel.PumpGridPanelData;
import com.cosog.model.gridmodel.ResProHandsontableChangedData;
import com.cosog.model.gridmodel.ReservoirPropertyGridPanelData;
import com.cosog.model.gridmodel.WellGridPanelData;
import com.cosog.model.gridmodel.WellHandsontableChangedData;
import com.cosog.model.gridmodel.WellProHandsontableChangedData;
import com.cosog.model.gridmodel.WellringGridPanelData;
import com.cosog.model.scada.CallbackDataItems;
import com.cosog.task.EquipmentDriverServerTask;
import com.cosog.utils.DataModelMap;
import com.cosog.utils.EquipmentDriveMap;
import com.cosog.utils.LicenseMap;
import com.cosog.utils.OracleJdbcUtis;
import com.cosog.utils.Page;
import com.cosog.utils.StringManagerUtils;
import com.cosog.utils.LicenseMap.License;
/**
 * <p>
 * 描述：核心服务dao处理接口类
 * </p>
 * 
 * @author gao 2014-06-04
 * @since 2013-08-08
 * @version 1.0
 * 
 */
@SuppressWarnings({ "unused", "unchecked", "rawtypes", "deprecation" })
@Repository("baseDao")
public class BaseDao extends HibernateDaoSupport {
	private static Log log = LogFactory.getLog(BaseDao.class);
	private Session session = null;
	public static String ConvertBLOBtoString(Blob BlobContent) {
		byte[] msgContent = null;
		try {
			msgContent = BlobContent.getBytes(1, (int) BlobContent.length());
		} catch (SQLException e1) {
			e1.printStackTrace();
		} // BLOB转换为字节数组
		String newStr = ""; // 返回字符串
		long BlobLength; // BLOB字段长度
		try {
			BlobLength = BlobContent.length(); // 获取BLOB长度
			if (msgContent == null || BlobLength == 0) // 如果为空，返回空值
			{
				return "";
			} else // 处理BLOB为字符串
			{
				newStr = new String(BlobContent.getBytes(1, 900), "gb2312"); // 简化处理，只取前900字节
				return newStr;
			}
		} catch (Exception e) // oracle异常捕获
		{
			e.printStackTrace();
		}
		return newStr;
	}

	/**
	 * @param dataSheet
	 * @param col
	 * @param row
	 * @param width
	 * @param height
	 * @param imgFile
	 */
	public static void insertImg(WritableSheet dataSheet, int col, int row, int width, int height, File imgFile) {
		WritableImage img = new WritableImage(col, row, width, height, imgFile);
		dataSheet.addImage(img);
	}

	@Transactional
	public <T> void addObject(T clazz) {

		this.save(clazz);
	}


	/**
	 * <p>
	 * 描述：批量删除对象信息
	 * </p>
	 * 
	 * @param hql
	 * @throws Exception
	 * 
	 * @author gao 2014-06-06
	 * 
	 */
	@Transactional
	public void bulkObjectDelete(final String hql) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.executeUpdate();
	}

	/**
	 * <p>
	 * 描述：根据传入的对象类型，删除该对象的一条记录
	 * </p>
	 * 
	 * @param obj
	 *            传入的对象
	 * @return
	 */
	public Serializable delectObject(Object obj) {
		Session session = getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(obj);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 存储过程调用删除 传入数组String[] ->删除(带占位符的)
	 * 
	 * @param callSql
	 * @param values
	 * @return
	 */
	public void deleteCallParameter(final String callSql, final Object... values) {
		Query query = getSessionFactory().getCurrentSession().createQuery(callSql);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		query.executeUpdate();
	}

	public Object deleteCallSql(final String sql, final Object... values) {
		Session session = getSessionFactory().getCurrentSession();
				SQLQuery query = session.createSQLQuery(sql);
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				return query.executeUpdate();
	}

	public int deleteObject(final String hql) {
		Session session = getSessionFactory().getCurrentSession();
				SQLQuery query = session.createSQLQuery(hql);
				return query.executeUpdate();
	}

	public <T> void deleteObject(T clazz) {
		this.getHibernateTemplate().delete(clazz);
	}

	/**
	 * 传入数组String[] ->删除(带占位符的) (批量)
	 * 
	 * @param queryString
	 * @param parametName
	 * @param parametValue
	 * @return
	 */
	public Query deleteQueryParameter(String queryString, String parametName, Object[] parametValue, Object... values) {
		Query query = getSessionFactory().getCurrentSession().createQuery(queryString);
		query.setParameterList(parametName, parametValue);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}

	/**
	 * 修改一个对象
	 * 
	 * @param object
	 */
	@Transactional
	public void edit(Object object) {
		getSessionFactory().getCurrentSession().update(object);
	}

	/**
	 * 执行一个SQL，update或insert
	 * 
	 * @param sql
	 * @return update或insert的记录数
	 */
	public int executeSqlUpdate(String sql) {
		int n = 0;
		Statement stat = null;
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
//			stat=conn.createStatement();
//			n = stat.executeUpdate(sql);
			
			ps=conn.prepareStatement(sql);
			n=ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stat != null) {
					stat.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return n;
	}
	
	/**
	 * 执行一个SQL，update或insert
	 * 
	 * @param sql
	 * @return update或insert的记录数
	 */
	public int executeSqlUpdateClob(String sql,List<String> values) {
		int n = 0;
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
			ps=conn.prepareStatement(sql);
			for(int i=0;i<values.size();i++){
				CLOB clob   = oracle.sql.CLOB.createTemporary(conn, false,oracle.sql.CLOB.DURATION_SESSION);  
				clob.putString(1,  values.get(i)); 
				ps.setClob(i+1, clob);  
			}
			n=ps.executeUpdate();
			
//			n=OracleJdbcUtis.executeSqlUpdateClob(conn, ps, sql, values);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return n;
	}

	/**
	 * HQL查询
	 * 
	 * @param queryString
	 *            HQL语句
	 * @param values
	 *            HQL参数
	 * @return
	 */
	public List find(String queryString, Object... values) {
		Query query = getSessionFactory().getCurrentSession().createQuery(queryString);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query.list();
	}
	
	/**
	 * <p>
	 * 描述：根据传入的hql语句返回一个List数据集合
	 * </p>
	 * 
	 * @author gao 2014-06-04
	 * @param hql
	 *            传入的hql语句
	 * @return List<T>
	 */
//	public <T> List<T> getObjects(String hql) {
//		Session session=getSessionFactory().getCurrentSession();
//		return (List<T>) this.getHibernateTemplate().find(hql);
//	}

	/**
	 * sql调用查询
	 * 
	 * @param queryString
	 *            callSql语句
	 * @param values
	 *            callSql参数
	 * @author qiands
	 * @return List<?>
	 */
	public List<?> findCallSql(final String callSql, final Object... values) {
		Session session=getSessionFactory().getCurrentSession();
				SQLQuery query = session.createSQLQuery(callSql);
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				return query.list();
	}

	public List<Org> findChildOrg(Integer parentId) {
		String queryString = "SELECT u FROM Org u where u.orgParent=" + parentId + " order by u.orgId ";
		return find(queryString);
	}

	/**
	 * 根据ID获取一个对象，如果查不到返回null
	 * 
	 * @param entityClass
	 * @param id
	 *            :查询对象的id
	 * @return <T>
	 */
	public <T> T get(Class<T> entityClass, Serializable id) {
		return (T) getSessionFactory().getCurrentSession().get(entityClass, id);
	}

	public <T> List<T> getAllObjects(Class<T> clazz) {
		return this.getHibernateTemplate().loadAll(clazz);
	}

	/**
	 * HQL Hibernate分页
	 * 
	 * @param hql
	 *            HSQL 查询语句
	 * @param page
	 *            分页条件信息
	 * @return List<T> 查询结果集
	 */
	public <T> List<T> getAllPageByHql(final String hql, final Page page) {
		Session session=getSessionFactory().getCurrentSession();
				Query query = session.createQuery(hql);
				ScrollableResults scrollableResults = query.scroll(ScrollMode.SCROLL_SENSITIVE);
				scrollableResults.last();
				query.setFirstResult(page.getStart());
				query.setMaxResults(page.getLimit());
				page.setTotalCount(scrollableResults.getRowNumber() + 1);
				return query.list();
	}

	/**
	 * 返回当前页的数据信息,执行的是sql查询操作
	 * 
	 * @author gao 2014-05-08
	 * @param sql
	 *            查询的sql语句
	 * @param pager
	 *            分页信息
	 * @param values动态参数
	 * @return list 数据结果集合
	 */
	public <T> List<T> getAllPageBySql(final String sql, final Page pager, final Object... values) {
		Session session=getSessionFactory().getCurrentSession();
				SQLQuery query = session.createSQLQuery(sql);
				/****
				 * 为query对象参数赋值操作
				 * */
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				query.setFirstResult(pager.getStart());// 设置起始位置
				query.setMaxResults(pager.getLimit());// 设置分页条数
			 int totals = getTotalCountRows(sql, values);//设置数据表中的总记录数
				pager.setTotalCount(totals);
				return query.list();
	}
	public <T> List<T> getMyCustomPageBySql(final String sqlAll,final String sql, final Page pager, final Object... values) {
		Session session=getSessionFactory().getCurrentSession();
				SQLQuery query = session.createSQLQuery(sql);
				/****
				 * 为query对象参数赋值操作
				 * */
				for (int i = 0; i < values.length; i++) {
					values[i] = values[i].toString().replace("@", ",");
					query.setParameter(i, values[i]);
				}
				int totals = getTotalCountRows(sqlAll, values);//设置数据表中的总记录数
				pager.setTotalCount(totals);
				return query.list();
	}
	//漏失分析获取平均值
	public <T> List<T> getAverageBySql(final String sqlAll,final String[] col, final Object... values) {
		String sql = "select ";
		for(int i = 0; i < col.length;i++){
			String[] attr = col[i].split(" as ");
			if (null != attr && attr.length > 1) {
				col[i] = attr[attr.length-1];
			}
			if( col[i].equals("id") ){
				sql += "(max(" + col[i] + ")+1) as id,";
			}else if( col[i].equals("jssj") ){
				sql += "'平均值' as jssj,";
			}else if( col[i].contains("pf") || col[i].contains("jljh")){
				sql += "avg(" + col[i] + "),";
			}else if( col[i].contains("js" ) && !col[i].equals("jssj") ){
				sql += "avg(" + col[i] + "),";
			}else if( col[i].contains("lsxs") ){
				sql += "decode(avg(" + col[i-1] + "),0,null,null,null," + "avg(" + col[i-2] + ")/avg(" + col[i-1] + ")) as lsxs,";
			}else{
				sql += "null as " + col[i] +",";
			}
		}
		sql = sql.substring(0, sql.length()-1);
		sql += " from (" + sqlAll + ")";
		Session session=getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		/**为query对象参数赋值操作 **/
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query.list();
	}

	/***
	 * *************************************begin
	 * 
	 * @author qiands
	 */
	/**
	 * 分页方法
	 * 
	 * @param sql
	 * @param pager
	 * @return
	 * @author qiands
	 */
	public <T> List<T> getAllPageBySql(final String sql, final Page pager, final Vector<String> v) {
		Session session=getSessionFactory().getCurrentSession();
				SQLQuery query = session.createSQLQuery(sql);
				int index = -1;
				String data = "";
				for (int i = 0; i < v.size(); i++) {
					if (!"".equals(v.get(i)) && null != v.get(i) && v.get(i).length() > 0) {
						index += 1;
						data = v.get(i);
						query.setParameter(index, data);
					}

				}
				ScrollableResults scrollableResults = query.scroll(ScrollMode.SCROLL_SENSITIVE);
				scrollableResults.last();
				query.setFirstResult(pager.getStart());
				query.setMaxResults(pager.getLimit());
				pager.setTotalCount(scrollableResults.getRowNumber() + 1);
				return query.list();
	}

	public void getChildrenList(List parentitem, Integer orgid) {
		List childlist = null;
		try {
			childlist = findChildOrg(orgid.intValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (childlist != null && childlist.size() > 0) {
			for (int i = 0; i < childlist.size(); i++) {
				Org bean = (Org) childlist.get(i);
				parentitem.add(bean.getOrgId());
				getChildrenList(parentitem, bean.getOrgId());
			}
		}
	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	public Integer getCountRows(String sql) {
		Session session=getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(sql);
		Integer rows = (Integer) query.list().size();
		return rows;
	}

	/**
	 * <p>
	 * 描述：查询数据库中的记录总数
	 * </p>
	 * 
	 * @param sql
	 * @return
	 */
	public Integer getCountSQLRows(String sql) {
		Session session=getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		BigDecimal obj = (BigDecimal) query.uniqueResult();
		return obj.intValue();
	}

	

	public <T> List<T> getListAndTotalCountForPage(final Page pager, final String hql, final String allhql) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					Query query = session.createQuery(hql);
			       ScrollableResults scrollableResults = query.scroll(ScrollMode.SCROLL_SENSITIVE);
					scrollableResults.last();
					 int totals = scrollableResults.getRowNumber()+1;
					pager.setTotalCount(totals);
					query.setFirstResult(pager.getStart());
					query.setMaxResults(pager.getLimit());
					List<T> list = query.list();
					return list;
	}

	public <T> List<T> getListAndTotalForPage(final Page pager, final String hql) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					Query query = session.createQuery(hql);
			         int total = query.list().size();
					pager.setTotalCount(total);
					query.setFirstResult(pager.getStart());
					query.setMaxResults(pager.getLimit());
					List<T> list = query.list();
					return list;
	}

	/**
	 * <p>
	 * 描述：获取记录数据库中的总记录数
	 * </p>
	 * 
	 * @param o
	 * @return
	 */
	public Long getListCountRows(final String o) {
		final String hql = "select count(*) from  " + o;
		Long result = null;
		Session session=getSessionFactory().getCurrentSession();
				Query query = session.createQuery(hql);
				return (Long) query.uniqueResult();
	}

	protected <T> List<T> getListForPage(final Class<T> clazz, final Criterion[] criterions, final int offset, final int length) {
		Session session=getSessionFactory().getCurrentSession();
				Criteria criteria = session.createCriteria(clazz);
				for (int i = 0; i < criterions.length; i++) {
					criteria.add(criterions[i]);
				}
				criteria.setFirstResult(offset);
				criteria.setMaxResults(length);
				return criteria.list();
	}
	
	public <T> List<T> getListForPage(final int offset, final int pageSize,final String hql) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setFirstResult(offset);
		query.setMaxResults(pageSize);
		List<T> list = query.list();
		return list;

	}

	public <T> List<T> getListForPage(final int offset, final int pageSize, final String hql, final List<KeyParameter> params) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					Query query = session.createQuery(hql);
					for (int i = 0; i < params.size(); i++) {
						KeyParameter p = params.get(i);
						if (p.getParamType().equalsIgnoreCase("String")) {
							query.setString(i, "%" + p.getStrParamValue() + "%");
						} else if (p.getParamType().equalsIgnoreCase("Integer")) {
							query.setInteger(i, p.getIntParamValue());
						} else if (p.getParamType().equalsIgnoreCase("Date")) {
							query.setDate(i, p.getDateParamValue());
						} else if (p.getParamType().equalsIgnoreCase("Timestamp")) {
							query.setTimestamp(i, p.getTimeParamValue());
						}
					}
					query.setFirstResult(offset - 1);
					query.setMaxResults(pageSize);
					List<T> list = query.list();
					return list;
	}

	/**
	 * @param offset
	 * @param pageSize
	 * @param pager
	 * @param hql
	 * @param o
	 *            出入当前的 实体类对象
	 * @return 返回分页后的数据集合
	 * @throws Exception
	 */
	public <T> List<T> getListForPage(final Page pager, final String hql, final String o) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					Query query = session.createQuery(hql);
					pager.setTotalCount(Integer.parseInt(getMaxCountValue(o) + ""));
					query.setFirstResult(pager.getStart());
					query.setMaxResults(pager.getLimit());
					List<T> list = query.list();
					return list;
	}

	public <T> List<T> getListForReportPage(final int offset, final int pageSize, final String hql) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					Query query = session.createSQLQuery(hql);
					query.setFirstResult(offset);
					query.setMaxResults(pageSize);
					List<T> list = query.list();
					return list;
	}

	/**
	 * <p>
	 * 描述：hql查询分页方法
	 * </p>
	 * 
	 * @param offset
	 *            数据偏移量
	 * @param pageSize
	 *            分页大小
	 * @param hql
	 *            查询语句
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> getListPage(final int offset, final int pageSize, final String hql) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					Query query = session.createSQLQuery(hql);
					query.setFirstResult(offset);
					query.setMaxResults(pageSize);
					List<T> list = query.list();
					return list;
	}

	public Long getMaxCountValue(final String o) {
		Session session=getSessionFactory().getCurrentSession();
		final String hql = "select count(*) from " + o;
				Query query = session.createQuery(hql);
				return (Long) query.uniqueResult();
	}

	public <T> T getObject(Class<T> clazz, Serializable id) {
		return this.getHibernateTemplate().get(clazz, id);
	}

	
	
	public <T> List<T> getSqlToHqlOrgObjects(String sql) {
		Session session=getSessionFactory().getCurrentSession();
		return (List<T>) session.createSQLQuery(sql).addEntity("Org", Org.class).list();
	}

	
	public <T> List<T> getSQLObjects(final String sql) {
		Session session=getSessionFactory().getCurrentSession();
				SQLQuery query = session.createSQLQuery(sql);
				List list = query.list();
				return (List<T>) list;
	}

	public Integer getRecordCountRows(String hql) {
		Session session=getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		Integer rows = (Integer) query.list().size();
		return rows;
	}

	/**
	 * <p>根据传入的SQL语句来分页查询List集合</p>
	 * 
	 * @param offset
	 * @param pageSize
	 * @param hql
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> getSQLListForPage(final int offset, final int pageSize, final String hql) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					Query query = session.createSQLQuery(hql);
					query.setFirstResult(offset);
					query.setMaxResults(pageSize);
					List<T> list = query.list();
					return list;
	}

	/**
	 * <p>
	 * 描述：根据普通的sql来查询一个结果List集合
	 * </p>
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> getSQLList(final String sql) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					Query query = session.createSQLQuery(sql);
					List<T> list = query.list();
					return list;
	}

	public Integer getTotalCountRows(String sql, final Object... values) {
		String allsql="";
		if(sql.trim().indexOf("count(1)")>0||sql.trim().indexOf("count(*)")>0){
			allsql=sql;
			
		}else{
			if(sql.indexOf("distinct")>0||sql.indexOf("group")>0){
				allsql = "select count(1)  from  (" + sql + ")";
			}else{
				String strarr[]=sql.split("from");
				if(strarr.length>1){
					allsql="select count(1) ";
					for(int i=1;i<strarr.length;i++){
						allsql+="from "+strarr[i];
					}
				}else{
					allsql = "select count(1)  from  (" + sql + ")";
				}
			}
		}
		Integer rows =0;
			SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(allsql);
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].toString().replace("@", ",");
				query.setParameter(i, values[i]);
			}
			rows= Integer.parseInt(query.uniqueResult() + "");
		return rows;
	}

	public Long getTotalCountValue(final String o) {
		Session session=getSessionFactory().getCurrentSession();
				Query query = session.createQuery(o);
				return (Long) query.uniqueResult();
	}

	public Integer getTotalSqlCountRows(String sql, final Object... values) {
		String allsql = "select count(*)  from  (" + sql + ")";
		Query query = getSessionFactory().getCurrentSession().createSQLQuery(allsql);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		List<BigDecimal> list = query.list();
		int count = list.get(0).intValue();
		return count;
	}

	/**
	 * @param orgId
	 * @return 递归取出当前组织Id下的所有Id字符串集合
	 */
	public String getUserOrgIds(int orgId) {
		List childOrgList = new ArrayList();
		String orgIds = orgId + ",";
		getChildrenList(childOrgList, orgId);
		for (int i = 0; i < childOrgList.size(); i++) {
			orgIds = orgIds + childOrgList.get(i) + ",";
		}
		orgIds = orgIds.substring(0, orgIds.length() - 1);
		return orgIds;
	}

	public <T> List<T> getWellListForPage(final int offset, final int pageSize, final String hql, final int orgId) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					Query query = session.createQuery(hql);
					query.setInteger("orgId", orgId);
					query.setFirstResult(offset);
					query.setMaxResults(pageSize);
					List<T> list = query.list();
					return list;
	}

	public Serializable insertObject(Object obj) {
		Session session=getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(obj);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}
		return null;
	}

	public Serializable modifyByObject(String hql) {
		Session session=getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}
		return null;
	}

	public Serializable modifyObject(Object obj) {
		Session session=getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.update(obj);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}
		return null;
	}

	public List MonthJssj(final String sql) {
		Session session=getSessionFactory().getCurrentSession();
				Query query = session.createSQLQuery(sql);
				List list = query.list();
				return list;
	}

	public Integer queryProObjectTotals(String sql) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		int total = 0;
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		try {
			ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			while (rs.next()) {
				total = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(rs!=null){
					rs.close();
				}
				if(ps!=null){
					ps.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return total;
	}

	/**
	 * 新增一个对象
	 * 
	 * @param object
	 */
	@Transactional
	public void save(Object object) {
		getSessionFactory().getCurrentSession().save(object);
	}

	public <T> void saveOrUpdateObject(T clazz) {
		this.getHibernateTemplate().saveOrUpdate(clazz);
	}

	public int updateOrDeleteBySql(String sql) throws SQLException{
		Connection conn=null;
		PreparedStatement ps=null;
		int result=0;
		try {
			conn = SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
			ps=conn.prepareStatement(sql);
			result=ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(sql);
		} finally{
			if(ps!=null){
				ps.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("resource")
	public Boolean savePumpDeviceData(WellHandsontableChangedData wellHandsontableChangedData,String orgId,int deviceType,User user) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		PreparedStatement ps=null;
		List<String> initWellList=new ArrayList<String>();
		List<String> updateWellList=new ArrayList<String>();
		List<String> addWellList=new ArrayList<String>();
		List<String> deleteWellList=new ArrayList<String>();
		Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
		if(equipmentDriveMap.size()==0){
			EquipmentDriverServerTask.loadProtocolConfig();
		}
		License license=LicenseMap.getMapObject().get(LicenseMap.SN);
		try {
			cs = conn.prepareCall("{call prd_save_pumpdevice(?,?,?,?,?,?,?,?,?,?,?,?)}");
			if(wellHandsontableChangedData.getUpdatelist()!=null){
				for(int i=0;i<wellHandsontableChangedData.getUpdatelist().size();i++){
					if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getWellName())){
						
						cs.setString(1, wellHandsontableChangedData.getUpdatelist().get(i).getOrgName());
						cs.setString(2, wellHandsontableChangedData.getUpdatelist().get(i).getWellName());
						cs.setString(3, deviceType+"");
						cs.setString(4, wellHandsontableChangedData.getUpdatelist().get(i).getApplicationScenariosName());
						cs.setString(5, wellHandsontableChangedData.getUpdatelist().get(i).getInstanceName());
						cs.setString(6, wellHandsontableChangedData.getUpdatelist().get(i).getAlarmInstanceName());
						cs.setString(7, wellHandsontableChangedData.getUpdatelist().get(i).getSignInId());
						cs.setString(8, wellHandsontableChangedData.getUpdatelist().get(i).getSlave());
						
						cs.setString(9, wellHandsontableChangedData.getUpdatelist().get(i).getVideoUrl());
						cs.setString(10, wellHandsontableChangedData.getUpdatelist().get(i).getSortNum());
						cs.setString(11, orgId);
						cs.setInt(12, license.getNumber());
						cs.executeUpdate();
						updateWellList.add(wellHandsontableChangedData.getUpdatelist().get(i).getWellName());
						if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getWellName())
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getSignInId()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getSlave()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getInstanceName()) 
								){
							initWellList.add(wellHandsontableChangedData.getUpdatelist().get(i).getWellName());
						}
					}
				}
			}
			if(wellHandsontableChangedData.getInsertlist()!=null){
				for(int i=0;i<wellHandsontableChangedData.getInsertlist().size();i++){
					if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getWellName())){
						
						cs.setString(1, wellHandsontableChangedData.getInsertlist().get(i).getOrgName());
						cs.setString(2, wellHandsontableChangedData.getInsertlist().get(i).getWellName());
						cs.setString(3, deviceType+"");
						cs.setString(4, wellHandsontableChangedData.getInsertlist().get(i).getApplicationScenariosName());
						cs.setString(5, wellHandsontableChangedData.getInsertlist().get(i).getInstanceName());
						cs.setString(6, wellHandsontableChangedData.getInsertlist().get(i).getAlarmInstanceName());
						cs.setString(7, wellHandsontableChangedData.getInsertlist().get(i).getSignInId());
						cs.setString(8, wellHandsontableChangedData.getInsertlist().get(i).getSlave());
						
						cs.setString(9, wellHandsontableChangedData.getInsertlist().get(i).getVideoUrl());
						cs.setString(10, wellHandsontableChangedData.getInsertlist().get(i).getSortNum());
						cs.setString(11, orgId);
						cs.setInt(12, license.getNumber());
						cs.executeUpdate();
						addWellList.add(wellHandsontableChangedData.getInsertlist().get(i).getWellName());
						if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getWellName())
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getSignInId()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getSlave()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getInstanceName()) 
								){
							initWellList.add(wellHandsontableChangedData.getInsertlist().get(i).getWellName());
						}
					}
				}
			}
			if(wellHandsontableChangedData.getDelidslist()!=null&&wellHandsontableChangedData.getDelidslist().size()>0){
				String delIds="";
				String delSql="";
				String queryDeleteWellSql="";
				for(int i=0;i<wellHandsontableChangedData.getDelidslist().size();i++){
					delIds+=wellHandsontableChangedData.getDelidslist().get(i);
					if(i<wellHandsontableChangedData.getDelidslist().size()-1){
						delIds+=",";
					}
				}
				queryDeleteWellSql="select wellname from tbl_pumpdevice t "
						+ " where t.devicetype="+deviceType+" "
						+ " and t.id in ("+StringUtils.join(wellHandsontableChangedData.getDelidslist(), ",")+")"
						+ " and t.orgid in("+orgId+")";
				delSql="delete from tbl_pumpdevice t "
						+ " where t.devicetype="+deviceType+" "
						+ " and t.id in ("+StringUtils.join(wellHandsontableChangedData.getDelidslist(), ",")+") "
						+ " and t.orgid in("+orgId+")";
				List<?> list = this.findCallSql(queryDeleteWellSql);
				for(int i=0;i<list.size();i++){
					deleteWellList.add(list.get(i)+"");
				}
				ps=conn.prepareStatement(delSql);
				int result=ps.executeUpdate();
			}
			saveDeviceOperationLog(updateWellList,addWellList,deleteWellList,deviceType,user);
			
			if(initWellList.size()>0){
				EquipmentDriverServerTask.initPumpDriverAcquisitionInfoConfig(initWellList,"update");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(ps!=null){
				ps.close();
			}
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	@SuppressWarnings("resource")
	public Boolean savePipelineDeviceData(WellHandsontableChangedData wellHandsontableChangedData,String orgId,int deviceType,User user) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		PreparedStatement ps=null;
		List<String> initWellList=new ArrayList<String>();
		List<String> updateWellList=new ArrayList<String>();
		List<String> addWellList=new ArrayList<String>();
		List<String> deleteWellList=new ArrayList<String>();
		Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
		if(equipmentDriveMap.size()==0){
			EquipmentDriverServerTask.loadProtocolConfig();
		}
		License license=LicenseMap.getMapObject().get(LicenseMap.SN);
		try {
			cs = conn.prepareCall("{call prd_save_pipelinedevice(?,?,?,?,?,?,?,?,?,?,?,?)}");
			if(wellHandsontableChangedData.getUpdatelist()!=null){
				for(int i=0;i<wellHandsontableChangedData.getUpdatelist().size();i++){
					if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getWellName())){
						
						cs.setString(1, wellHandsontableChangedData.getUpdatelist().get(i).getOrgName());
						cs.setString(2, wellHandsontableChangedData.getUpdatelist().get(i).getWellName());
						cs.setString(3, deviceType+"");
						cs.setString(4, wellHandsontableChangedData.getUpdatelist().get(i).getApplicationScenariosName());
						cs.setString(5, wellHandsontableChangedData.getUpdatelist().get(i).getInstanceName());
						cs.setString(6, wellHandsontableChangedData.getUpdatelist().get(i).getAlarmInstanceName());
						cs.setString(7, wellHandsontableChangedData.getUpdatelist().get(i).getSignInId());
						cs.setString(8, wellHandsontableChangedData.getUpdatelist().get(i).getSlave());
						
						cs.setString(9, wellHandsontableChangedData.getUpdatelist().get(i).getVideoUrl());
						cs.setString(10, wellHandsontableChangedData.getUpdatelist().get(i).getSortNum());
						cs.setString(11, orgId);
						cs.setInt(12, license.getNumber());
						cs.executeUpdate();
						updateWellList.add(wellHandsontableChangedData.getUpdatelist().get(i).getWellName());
						if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getWellName())
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getSignInId()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getSlave()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getInstanceName()) 
								){
							initWellList.add(wellHandsontableChangedData.getUpdatelist().get(i).getWellName());
						}
					}
				}
			}
			if(wellHandsontableChangedData.getInsertlist()!=null){
				for(int i=0;i<wellHandsontableChangedData.getInsertlist().size();i++){
					if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getWellName())){
						
						cs.setString(1, wellHandsontableChangedData.getInsertlist().get(i).getOrgName());
						cs.setString(2, wellHandsontableChangedData.getInsertlist().get(i).getWellName());
						cs.setString(3, deviceType+"");
						cs.setString(4, wellHandsontableChangedData.getInsertlist().get(i).getApplicationScenariosName());
						cs.setString(5, wellHandsontableChangedData.getInsertlist().get(i).getInstanceName());
						cs.setString(6, wellHandsontableChangedData.getInsertlist().get(i).getAlarmInstanceName());
						cs.setString(7, wellHandsontableChangedData.getInsertlist().get(i).getSignInId());
						cs.setString(8, wellHandsontableChangedData.getInsertlist().get(i).getSlave());
						
						cs.setString(9, wellHandsontableChangedData.getInsertlist().get(i).getVideoUrl());
						cs.setString(10, wellHandsontableChangedData.getInsertlist().get(i).getSortNum());
						cs.setString(11, orgId);
						cs.setInt(12, license.getNumber());
						cs.executeUpdate();
						addWellList.add(wellHandsontableChangedData.getInsertlist().get(i).getWellName());
						if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getWellName())
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getSignInId()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getSlave()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getInstanceName()) 
								){
							initWellList.add(wellHandsontableChangedData.getInsertlist().get(i).getWellName());
						}
					}
				}
			}
			if(wellHandsontableChangedData.getDelidslist()!=null&&wellHandsontableChangedData.getDelidslist().size()>0){
				String delIds="";
				String delSql="";
				String queryDeleteWellSql="";
				for(int i=0;i<wellHandsontableChangedData.getDelidslist().size();i++){
					delIds+=wellHandsontableChangedData.getDelidslist().get(i);
					if(i<wellHandsontableChangedData.getDelidslist().size()-1){
						delIds+=",";
					}
				}
				queryDeleteWellSql="select wellname from tbl_pipelinedevice t "
						+ " where t.devicetype="+deviceType+" "
						+ " and t.id in ("+StringUtils.join(wellHandsontableChangedData.getDelidslist(), ",")+")"
						+ " and t.orgid in("+orgId+")";
				delSql="delete from tbl_pipelinedevice t "
						+ " where t.devicetype="+deviceType+" "
						+ " and t.id in ("+StringUtils.join(wellHandsontableChangedData.getDelidslist(), ",")+") "
						+ " and t.orgid in("+orgId+")";
				List<?> list = this.findCallSql(queryDeleteWellSql);
				for(int i=0;i<list.size();i++){
					deleteWellList.add(list.get(i)+"");
				}
				
				
				ps=conn.prepareStatement(delSql);
				int result=ps.executeUpdate();
			}
			saveDeviceOperationLog(updateWellList,addWellList,deleteWellList,deviceType,user);
			
			if(initWellList.size()>0){
				EquipmentDriverServerTask.initPipelineDriverAcquisitionInfoConfig(initWellList,"update");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(ps!=null){
				ps.close();
			}
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	@SuppressWarnings("resource")
	public Boolean saveSMSDeviceData(WellHandsontableChangedData wellHandsontableChangedData,String orgId,int deviceType,User user) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		PreparedStatement ps=null;
		List<String> initWellList=new ArrayList<String>();
		List<String> updateWellList=new ArrayList<String>();
		List<String> addWellList=new ArrayList<String>();
		List<String> deleteWellList=new ArrayList<String>();
		try {
			cs = conn.prepareCall("{call prd_save_smsdevice(?,?,?,?,?,?)}");
			if(wellHandsontableChangedData.getUpdatelist()!=null){
				for(int i=0;i<wellHandsontableChangedData.getUpdatelist().size();i++){
					if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getWellName())){
						cs.setString(1, wellHandsontableChangedData.getUpdatelist().get(i).getOrgName());
						cs.setString(2, wellHandsontableChangedData.getUpdatelist().get(i).getWellName());
						cs.setString(3, wellHandsontableChangedData.getUpdatelist().get(i).getInstanceName());
						cs.setString(4, wellHandsontableChangedData.getUpdatelist().get(i).getSignInId());
						cs.setString(5, wellHandsontableChangedData.getUpdatelist().get(i).getSortNum());
						cs.setString(6, orgId);
						cs.executeUpdate();
						updateWellList.add(wellHandsontableChangedData.getUpdatelist().get(i).getWellName());
						if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getWellName())
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getSignInId()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getSlave()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getUpdatelist().get(i).getInstanceName()) 
								){
							initWellList.add(wellHandsontableChangedData.getUpdatelist().get(i).getWellName());
						}
					}
				}
			}
			if(wellHandsontableChangedData.getInsertlist()!=null){
				for(int i=0;i<wellHandsontableChangedData.getInsertlist().size();i++){
					if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getWellName())){
						cs.setString(1, wellHandsontableChangedData.getInsertlist().get(i).getOrgName());
						cs.setString(2, wellHandsontableChangedData.getInsertlist().get(i).getWellName());
						cs.setString(3, wellHandsontableChangedData.getInsertlist().get(i).getInstanceName());
						cs.setString(4, wellHandsontableChangedData.getInsertlist().get(i).getSignInId());
						cs.setString(5, wellHandsontableChangedData.getInsertlist().get(i).getSortNum());
						cs.setString(6, orgId);
						cs.executeUpdate();
						addWellList.add(wellHandsontableChangedData.getInsertlist().get(i).getWellName());
						if(StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getWellName())
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getSignInId()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getSlave()) 
								&&StringManagerUtils.isNotNull(wellHandsontableChangedData.getInsertlist().get(i).getInstanceName()) 
								){
							initWellList.add(wellHandsontableChangedData.getInsertlist().get(i).getWellName());
						}
					}
				}
			}
			if(wellHandsontableChangedData.getDelidslist()!=null&&wellHandsontableChangedData.getDelidslist().size()>0){
				String delIds="";
				String delSql="";
				String queryDeleteWellSql="";
				for(int i=0;i<wellHandsontableChangedData.getDelidslist().size();i++){
					delIds+=wellHandsontableChangedData.getDelidslist().get(i);
					if(i<wellHandsontableChangedData.getDelidslist().size()-1){
						delIds+=",";
					}
				}
				queryDeleteWellSql="select wellname from tbl_smsdevice t "
						+ " where  t.id in ("+StringUtils.join(wellHandsontableChangedData.getDelidslist(), ",")+")"
						+ " and t.orgid in("+orgId+")";
				delSql="delete from tbl_smsdevice t "
						+ " where t.id in ("+StringUtils.join(wellHandsontableChangedData.getDelidslist(), ",")+") "
						+ " and t.orgid in("+orgId+")";
				List<?> list = this.findCallSql(queryDeleteWellSql);
				for(int i=0;i<list.size();i++){
					deleteWellList.add(list.get(i)+"");
				}
				
				
				ps=conn.prepareStatement(delSql);
				int result=ps.executeUpdate();
			}
			saveDeviceOperationLog(updateWellList,addWellList,deleteWellList,300,user);
			
			if(initWellList.size()>0){
				EquipmentDriverServerTask.initSMSDevice(initWellList,"update");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(ps!=null){
				ps.close();
			}
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	@SuppressWarnings("resource")
	public Boolean saveAuxiliaryDeviceHandsontableData(AuxiliaryDeviceHandsontableChangedData auxiliaryDeviceHandsontableChangedData) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		PreparedStatement ps=null;
		try {
			cs = conn.prepareCall("{call prd_save_auxiliarydevice(?,?,?,?,?)}");
			if(auxiliaryDeviceHandsontableChangedData.getUpdatelist()!=null){
				for(int i=0;i<auxiliaryDeviceHandsontableChangedData.getUpdatelist().size();i++){
					if(StringManagerUtils.isNotNull(auxiliaryDeviceHandsontableChangedData.getUpdatelist().get(i).getName())){
						cs.setString(1, auxiliaryDeviceHandsontableChangedData.getUpdatelist().get(i).getName());
						cs.setInt(2, "管辅件".equals(auxiliaryDeviceHandsontableChangedData.getUpdatelist().get(i).getType())?1:0);
						cs.setString(3, auxiliaryDeviceHandsontableChangedData.getUpdatelist().get(i).getModel());
						cs.setString(4, auxiliaryDeviceHandsontableChangedData.getUpdatelist().get(i).getRemark());
						cs.setString(5, StringManagerUtils.isInteger(auxiliaryDeviceHandsontableChangedData.getUpdatelist().get(i).getSort())?auxiliaryDeviceHandsontableChangedData.getUpdatelist().get(i).getSort():"9999");
						cs.executeUpdate();
					}
				}
			}
			if(auxiliaryDeviceHandsontableChangedData.getInsertlist()!=null){
				for(int i=0;i<auxiliaryDeviceHandsontableChangedData.getInsertlist().size();i++){
					if(StringManagerUtils.isNotNull(auxiliaryDeviceHandsontableChangedData.getInsertlist().get(i).getName())){
						cs.setString(1, auxiliaryDeviceHandsontableChangedData.getInsertlist().get(i).getName());
						cs.setInt(2, "管辅件".equals(auxiliaryDeviceHandsontableChangedData.getInsertlist().get(i).getType())?1:0);
						cs.setString(3, auxiliaryDeviceHandsontableChangedData.getInsertlist().get(i).getModel());
						cs.setString(4, auxiliaryDeviceHandsontableChangedData.getInsertlist().get(i).getRemark());
						cs.setString(5, StringManagerUtils.isInteger(auxiliaryDeviceHandsontableChangedData.getInsertlist().get(i).getSort())?auxiliaryDeviceHandsontableChangedData.getInsertlist().get(i).getSort():"9999");
						cs.executeUpdate();
					}
				}
			}
			if(auxiliaryDeviceHandsontableChangedData.getDelidslist()!=null){
				String delSql="delete from tbl_auxiliarydevice t where t.id in ("+StringUtils.join(auxiliaryDeviceHandsontableChangedData.getDelidslist(), ",")+")";
				ps=conn.prepareStatement(delSql);
				int result=ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(ps!=null){
				ps.close();
			}
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public boolean saveDeviceOperationLog(List<String> updateWellList,List<String> addWellList,List<String> deleteWellList,int deviceType,User user) throws SQLException{
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call prd_save_deviceOperationLog(?,?,?,?,?,?,?)}");
			String currentTiem=StringManagerUtils.getCurrentTime("yyyy-MM-dd HH:mm:ss");
			for(int i=0;addWellList!=null && i<addWellList.size();i++){
				cs.setString(1, currentTiem);
				cs.setString(2, addWellList.get(i));
				cs.setInt(3, deviceType);
				cs.setInt(4, 0);
				cs.setString(5, user.getUserId());
				cs.setString(6, user.getLoginIp());
				cs.setString(7, "");
				cs.executeUpdate();
			}
			for(int i=0;updateWellList!=null && i<updateWellList.size();i++){
				cs.setString(1, currentTiem);
				cs.setString(2, updateWellList.get(i));
				cs.setInt(3, deviceType);
				cs.setInt(4, 1);
				cs.setString(5, user.getUserId());
				cs.setString(6, user.getLoginIp());
				cs.setString(7, "");
				cs.executeUpdate();
			}
			for(int i=0;deleteWellList!=null && i<deleteWellList.size();i++){
				cs.setString(1, currentTiem);
				cs.setString(2, deleteWellList.get(i));
				cs.setInt(3, deviceType);
				cs.setInt(4, 2);
				cs.setString(5, user.getUserId());
				cs.setString(6, user.getLoginIp());
				cs.setString(7, "");
				cs.executeUpdate();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	
	public boolean saveDeviceControlLog(String wellName,String deviceType,String title,String value,User user) throws SQLException{
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call prd_save_deviceOperationLog(?,?,?,?,?,?,?)}");
			String currentTiem=StringManagerUtils.getCurrentTime("yyyy-MM-dd HH:mm:ss");
			cs.setString(1, currentTiem);
			cs.setString(2, wellName);
			cs.setInt(3, StringManagerUtils.stringToInteger(deviceType));
			cs.setInt(4, 3);
			cs.setString(5, user.getUserId());
			cs.setString(6, user.getLoginIp());
			cs.setString(7, "控制项:"+title+",写入值:"+value);
			cs.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public boolean saveSystemLog(User user) throws SQLException{
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call prd_save_systemLog(?,?,?,?,?)}");
			String currentTiem=StringManagerUtils.getCurrentTime("yyyy-MM-dd HH:mm:ss");
			cs.setString(1, currentTiem);
			cs.setInt(2, 0);
			cs.setString(3, user.getUserId());
			cs.setString(4, user.getLoginIp());
			cs.setString(5, "用户登录");
			cs.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public Boolean saveElecInverPumpingUnitData(ElecInverCalculateManagerHandsontableChangedData elecInverCalculateManagerHandsontableChangedData) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		Statement st=null; 
		CallableStatement cs=null;
		
		try {
			cs = conn.prepareCall("{call prd_save_rpcinformationNoPTF(?,?,?,?,?,?,?,?,?,?,?)}");
			for(int i=0;i<elecInverCalculateManagerHandsontableChangedData.getUpdatelist().size();i++){
				
				cs.setString(1, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getWellName());
				cs.setString(2, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getManufacturer());
				cs.setString(3, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getModel());
				cs.setString(4, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getStroke());
				cs.setString(5, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getCrankRotationDirection());
				cs.setString(6, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getOffsetAngleOfCrank());
				cs.setString(7, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getCrankGravityRadius());
				cs.setString(8, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getSingleCrankWeight());
				cs.setString(9, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getSingleCrankWeight());
				cs.setString(10, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getBalancePosition());
				cs.setString(11, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getBalanceWeight());
				cs.executeUpdate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cs!=null)
				cs.close();
			conn.close();
		}
		return true;
	}
	
	public Boolean saveElecInverOptimizeHandsontableData(ElecInverCalculateManagerHandsontableChangedData elecInverCalculateManagerHandsontableChangedData,String orgId) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		PreparedStatement ps=null;
		try {
			cs = conn.prepareCall("{call prd_save_rpc_inver_opt(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			if(elecInverCalculateManagerHandsontableChangedData.getUpdatelist()!=null){
				for(int i=0;i<elecInverCalculateManagerHandsontableChangedData.getUpdatelist().size();i++){
					if(StringManagerUtils.isNotNull(elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getWellName())){
						
						cs.setString(1, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getWellName());
						cs.setString(2, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getOffsetAngleOfCrankPS());
						cs.setString(3, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getSurfaceSystemEfficiency());
						cs.setString(4, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getFS_LeftPercent());
						cs.setString(5, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getFS_RightPercent());
						cs.setString(6, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getWattAngle());
						cs.setString(7, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getFilterTime_Watt());
						cs.setString(8, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getFilterTime_I());
						cs.setString(9, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getFilterTime_RPM());
						cs.setString(10, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getFilterTime_FSDiagram());
						cs.setString(11, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getFilterTime_FSDiagram_L());
						cs.setString(12, elecInverCalculateManagerHandsontableChangedData.getUpdatelist().get(i).getFilterTime_FSDiagram_R());
						cs.setString(13, orgId);
						cs.executeUpdate();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(ps!=null){
				ps.close();
			}
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public Boolean editWellName(String oldWellName,String newWellName,String orgid) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call prd_change_wellname(?,?,?)}");
			cs.setString(1,oldWellName);
			cs.setString(2, newWellName);
			cs.setString(3, orgid);
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public Boolean editPumpDeviceName(String oldWellName,String newWellName,String orgid) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call prd_change_pumpdevicename(?,?,?)}");
			cs.setString(1,oldWellName);
			cs.setString(2, newWellName);
			cs.setString(3, orgid);
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public Boolean editPipelineDeviceName(String oldWellName,String newWellName,String orgid) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call prd_change_pipelinedevicename(?,?,?)}");
			cs.setString(1,oldWellName);
			cs.setString(2, newWellName);
			cs.setString(3, orgid);
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public Boolean editSMSDeviceName(String oldWellName,String newWellName,String orgid) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call prd_change_smsdevicename(?,?,?)}");
			cs.setString(1,oldWellName);
			cs.setString(2, newWellName);
			cs.setString(3, orgid);
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public Boolean editAuxiliaryDeviceName(String oldName,String newName) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call prd_change_auxiliarydevicename(?,?)}");
			cs.setString(1,oldName);
			cs.setString(2, newName);
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public Boolean savePumpEditerGridData(PumpGridPanelData p, String ids, String comandType) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call PRO_savePumpData(?,?,?,?,?,?,?)}");
			cs.setString(1, p.getSccj());
			cs.setString(2, p.getCybxh());
			cs.setString(3, p.getBlxName());
			cs.setString(4, p.getBjbName());
			cs.setString(5, p.getBtlxName());
			cs.setDouble(6, p.getBj());
			cs.setDouble(7, p.getZsc());
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	
	public Boolean doStatItemsSetSave(String statType,String data) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		Statement st=null; 
		JSONObject jsonObject = JSONObject.fromObject("{\"data\":"+data+"}");//解析数据
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		
		try {
			st=conn.createStatement(); 
			if(!"GLPHD".equalsIgnoreCase(statType)&&!"PHD".equalsIgnoreCase(statType)){
				String sql="delete from tbl_rpc_statistics_conf t where t.s_type='"+statType+"'";
				int updatecount=st.executeUpdate(sql);
			}
			for(int i=0;i<jsonArray.size();i++){
				JSONObject everydata = JSONObject.fromObject(jsonArray.getString(i));
				String statitem=everydata.getString("statitem");
				String downlimit=everydata.getString("downlimit");
				String uplimit=everydata.getString("uplimit");
				String sql="insert into tbl_rpc_statistics_conf(s_level,s_min,s_max,s_type,s_code) values('"+statitem+"',"+downlimit+","+uplimit+",'"+statType+"',"+(i+1)+")";
				if("GLPHD".equalsIgnoreCase(statType)||"PHD".equalsIgnoreCase(statType)){
					sql="update tbl_rpc_statistics_conf set s_min="+downlimit+",s_max="+uplimit+" where s_type='"+statType+"' and s_level='"+statitem+"'";
				}
				int updatecount=st.executeUpdate(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(st!=null)
				st.close();
			conn.close();
		}
		return true;
	}

	/**
	 * 注入sessionFactory
	 */
	@Resource(name = "sessionFactory")
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	public int updatealarmmessage(final String sql) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					SQLQuery query = session.createSQLQuery(sql);
					return query.executeUpdate();

	}

	public int updateBySQL(final String sql, final List pl) throws Exception {
		Session session=getSessionFactory().getCurrentSession();
					SQLQuery query = session.createSQLQuery(sql);
					if (pl != null && !pl.isEmpty()) {
						for (int i = 0; i < pl.size(); i++) {
							query.setParameter(i, pl.get(i));
						}
						return query.executeUpdate();
					}
					return 0;
	}

	/**
	 * 跟新当前传入的数据信息
	 * 
	 * @author ding
	 * @param sql
	 * @return 
	 */
	public Object updateObject(final String sql) {
		Session session=getSessionFactory().getCurrentSession();
				SQLQuery query = session.createSQLQuery(sql);
				return query.executeUpdate();
	}

	/**
	 * <p>
	 * 更新当前对象的数据信息
	 * </p>
	 * 
	 * @author gao 2014-06-04
	 * @param clazz
	 *            传入的对象
	 */
	public <T> void updateObject(T clazz) {
		this.getHibernateTemplate().update(clazz);
	}

	public int updateWellorder(final String hql) {
		Session session=getSessionFactory().getCurrentSession();
				SQLQuery query = session.createSQLQuery(hql);
				return query.executeUpdate();
	}
	
	public boolean updateWellorder(JSONObject everydata,String orgid) throws SQLException {
		Connection conn=null;
		try {
			conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
			CallableStatement cs;
			cs = conn.prepareCall("{call PRO_SAVEWELLORDER(?,?,?)}");
			cs.setString(1, everydata.getString("jh"));
			cs.setInt(2, everydata.getInt("pxbh"));
			cs.setString(3, orgid);
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
		return true;
	}
	

	public boolean updateWellTrajectoryById(WellTrajectory wtvo, int line, int jbh) throws SQLException {
		boolean flag = false;
		int ok = -1;
		ByteArrayInputStream bais = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		StringBuffer sb = new StringBuffer();
		String jsgj = wtvo.getClsd() + "," + wtvo.getCzsd() + "," + wtvo.getJxj() + "," + wtvo.getFwj() + ";";
		String sql = "update t_welltrajectory set jsgj=?0 where jbh=?1";
		if (wtvo != null) {
			try {
				String[] tracks = wtvo.getJsgj().split(";");
				for (int i = 0; i < tracks.length; i++) {
					if ((i + 1) == line) {
						tracks[i] = jsgj;
						sb.append(tracks[i]);
					} else {
						sb.append(tracks[i] + ";");
					}

				}
				ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				byte[] buffer = (sb.toString()).getBytes();
				bais = new ByteArrayInputStream(buffer);
				ps.setBinaryStream(1, bais, bais.available()); // 第二个参数为文件的内容
				ps.setInt(2, jbh);
				ok = ps.executeUpdate();
				if (ok > 0) {
					flag = true;
				}
			} catch (Exception e) {

				e.printStackTrace();
			} finally {
				try {
					if(rs!=null){
						rs.close();
					}
					if(ps!=null){
						ps.close();
					}
					if(conn!=null){
						conn.close();
					}
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return flag;
	}

	public void callProcedureByCallName() {
		String callName = "{Call proc_test(?,?)}";
		ResultSet rs = null;
		CallableStatement call=null;
		Connection conn=null;
		try {
			conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
			call=conn.prepareCall(callName);
			call.setString(1, "");
			call.registerOutParameter(2, Types.VARCHAR);
			rs = call.executeQuery();
		}catch (HibernateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null){
					rs.close();
				}
				if(call!=null){
					call.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 调用存储过程 删除流程相关记录
	public boolean deleteByCallPro(String procinstid) throws SQLException {
		String procdure = "{Call sp_deleteInstByRootID(?)}";
		CallableStatement cs = null;
		Connection conn=null;
		try {
			conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
			cs = conn.prepareCall(procdure);
			cs.setString(1, procinstid);
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(cs!=null){
					cs.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return cs.execute();
	}
	
	public void OnGetDate(final List<CallbackDataItems> list,final String RTUName) throws HibernateException,SQLException{
		
	}

	public Boolean setAlarmLevelColor(AlarmShowStyle alarmShowStyle) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		try {
			cs = conn.prepareCall("{call prd_save_alarmcolor(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			cs.setString(1, alarmShowStyle.getOverview().getNormal().getBackgroundColor());
			cs.setString(2, alarmShowStyle.getOverview().getFirstLevel().getBackgroundColor());
			cs.setString(3, alarmShowStyle.getOverview().getSecondLevel().getBackgroundColor());
			cs.setString(4, alarmShowStyle.getOverview().getThirdLevel().getBackgroundColor());
			cs.setString(5, alarmShowStyle.getOverview().getNormal().getColor());
			cs.setString(6, alarmShowStyle.getOverview().getFirstLevel().getColor());
			cs.setString(7, alarmShowStyle.getOverview().getSecondLevel().getColor());
			cs.setString(8, alarmShowStyle.getOverview().getThirdLevel().getColor());
			cs.setString(9, alarmShowStyle.getOverview().getNormal().getOpacity());
			cs.setString(10, alarmShowStyle.getOverview().getFirstLevel().getOpacity());
			cs.setString(11, alarmShowStyle.getOverview().getSecondLevel().getOpacity());
			cs.setString(12, alarmShowStyle.getOverview().getThirdLevel().getOpacity());
			
			cs.setString(13, alarmShowStyle.getDetails().getNormal().getBackgroundColor());
			cs.setString(14, alarmShowStyle.getDetails().getFirstLevel().getBackgroundColor());
			cs.setString(15, alarmShowStyle.getDetails().getSecondLevel().getBackgroundColor());
			cs.setString(16, alarmShowStyle.getDetails().getThirdLevel().getBackgroundColor());
			cs.setString(17, alarmShowStyle.getDetails().getNormal().getColor());
			cs.setString(18, alarmShowStyle.getDetails().getFirstLevel().getColor());
			cs.setString(19, alarmShowStyle.getDetails().getSecondLevel().getColor());
			cs.setString(20, alarmShowStyle.getDetails().getThirdLevel().getColor());
			cs.setString(21, alarmShowStyle.getDetails().getNormal().getOpacity());
			cs.setString(22, alarmShowStyle.getDetails().getFirstLevel().getOpacity());
			cs.setString(23, alarmShowStyle.getDetails().getSecondLevel().getOpacity());
			cs.setString(24, alarmShowStyle.getDetails().getThirdLevel().getOpacity());
			
			cs.setString(25, alarmShowStyle.getStatistics().getNormal().getBackgroundColor());
			cs.setString(26, alarmShowStyle.getStatistics().getFirstLevel().getBackgroundColor());
			cs.setString(27, alarmShowStyle.getStatistics().getSecondLevel().getBackgroundColor());
			cs.setString(28, alarmShowStyle.getStatistics().getThirdLevel().getBackgroundColor());
			cs.setString(29, alarmShowStyle.getStatistics().getNormal().getColor());
			cs.setString(30, alarmShowStyle.getStatistics().getFirstLevel().getColor());
			cs.setString(31, alarmShowStyle.getStatistics().getSecondLevel().getColor());
			cs.setString(32, alarmShowStyle.getStatistics().getThirdLevel().getColor());
			cs.setString(33, alarmShowStyle.getStatistics().getNormal().getOpacity());
			cs.setString(34, alarmShowStyle.getStatistics().getFirstLevel().getOpacity());
			cs.setString(35, alarmShowStyle.getStatistics().getSecondLevel().getOpacity());
			cs.setString(36, alarmShowStyle.getStatistics().getThirdLevel().getOpacity());
			
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(cs!=null){
				cs.close();
			}
			conn.close();
		}
		return true;
	}
	public int SetWellProductionCycle(String sql) throws SQLException, ParseException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		Statement st=null; 
		int result=0;
		try {
			st=conn.createStatement(); 
			result=st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(cs!=null)
				cs.close();
			if(st!=null)
				st.close();
			conn.close();
		}
		return result;
	}
	
	public Boolean savePSToFSPumpingUnitData(String PumpingUnitData,String PumpingUnitPTRData,String wellName) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		Statement st=null; 
		CallableStatement cs=null;
		JSONObject jsonObject = JSONObject.fromObject("{\"data\":"+PumpingUnitData+"}");//解析数据
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		
		CLOB PTFClob=new CLOB((OracleConnection) conn);
		PTFClob = oracle.sql.CLOB.createTemporary(conn,false,1);
		PTFClob.putString(1, PumpingUnitPTRData);
		try {
			cs = conn.prepareCall("{call prd_save_rpcinformation(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			for(int i=0;i<jsonArray.size();i++){
				JSONObject everydata = JSONObject.fromObject(jsonArray.getString(i));
				cs.setString(1, everydata.getString("WellName"));
				cs.setString(2, everydata.getString("Manufacturer"));
				cs.setString(3, everydata.getString("Model"));
				cs.setString(4, everydata.getString("Stroke"));
				cs.setString(5, everydata.getString("CrankRotationDirection"));
				cs.setString(6, everydata.getString("OffsetAngleOfCrank"));
				cs.setString(7, everydata.getString("CrankGravityRadius"));
				cs.setString(8, everydata.getString("SingleCrankWeight"));
				cs.setString(9, everydata.getString("SingleCrankPinWeight"));
				cs.setString(10, everydata.getString("StructuralUnbalance"));
				cs.setString(11, everydata.getString("BalancePosition"));
				cs.setString(12, everydata.getString("BalanceWeight"));
				cs.setString(13, wellName);
				cs.setClob(14, PTFClob);
				cs.executeUpdate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cs!=null)
				cs.close();
			conn.close();
		}
		return true;
	}
	
	public Boolean savePSToFSMotorData(String MotorData,String MotorPerformanceCurverData,String wellName) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		Statement st=null; 
		CallableStatement cs=null;
		JSONObject jsonObject = JSONObject.fromObject("{\"data\":"+MotorData+"}");//解析数据
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		
		CLOB PerformanceCurverClob=new CLOB((OracleConnection) conn);
		PerformanceCurverClob = oracle.sql.CLOB.createTemporary(conn,false,1);
		PerformanceCurverClob.putString(1, MotorPerformanceCurverData);
		try {
			cs = conn.prepareCall("{call prd_save_rpc_motor(?,?,?,?,?,?,?)}");
			for(int i=0;i<jsonArray.size();i++){
				JSONObject everydata = JSONObject.fromObject(jsonArray.getString(i));
				cs.setString(1, everydata.getString("WellName"));
				cs.setString(2, everydata.getString("Manufacturer"));
				cs.setString(3, everydata.getString("Model"));
				cs.setString(4, everydata.getString("BeltPulleyDiameter"));
				cs.setString(5, everydata.getString("SynchroSpeed"));
				cs.setString(6, wellName);
				cs.setClob(7, PerformanceCurverClob);
				cs.executeUpdate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cs!=null)
				cs.close();
			conn.close();
		}
		return true;
	}
	
	public Boolean savePumpAlarmInfo(String wellName,String deviceType,String acqTime,List<AcquisitionItemInfo> acquisitionItemInfoList) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		
		try {
			cs = conn.prepareCall("{call prd_save_pumpalarminfo(?,?,?,?,?,?,?,?,?,?,?,?)}");
			for(int i=0;i<acquisitionItemInfoList.size();i++){
				if(acquisitionItemInfoList.get(i).getAlarmLevel()>0){
					cs.setString(1, wellName);
					cs.setString(2, deviceType);
					cs.setString(3, acqTime);
					cs.setString(4, acquisitionItemInfoList.get(i).getTitle());
					cs.setInt(5, acquisitionItemInfoList.get(i).getAlarmType());
					cs.setString(6, acquisitionItemInfoList.get(i).getRawValue());
					cs.setString(7, acquisitionItemInfoList.get(i).getAlarmInfo());
					cs.setString(8, acquisitionItemInfoList.get(i).getAlarmLimit()+"");
					cs.setString(9, acquisitionItemInfoList.get(i).getHystersis()+"");
					cs.setInt(10, acquisitionItemInfoList.get(i).getAlarmLevel());
					cs.setInt(11, acquisitionItemInfoList.get(i).getIsSendMessage());
					cs.setInt(12, acquisitionItemInfoList.get(i).getIsSendMail());
					cs.executeUpdate();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cs!=null)
				cs.close();
			conn.close();
		}
		return true;
	}
	
	public Boolean savePipelineAlarmInfo(String wellName,String deviceType,String acqTime,List<AcquisitionItemInfo> acquisitionItemInfoList) throws SQLException {
		Connection conn=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		CallableStatement cs=null;
		
		try {
			cs = conn.prepareCall("{call prd_save_pipelinealarminfo(?,?,?,?,?,?,?,?,?,?,?,?)}");
			for(int i=0;i<acquisitionItemInfoList.size();i++){
				if(acquisitionItemInfoList.get(i).getAlarmLevel()>0){
					cs.setString(1, wellName);
					cs.setString(2, deviceType);
					cs.setString(3, acqTime);
					cs.setString(4, acquisitionItemInfoList.get(i).getTitle());
					cs.setInt(5, acquisitionItemInfoList.get(i).getAlarmType());
					cs.setString(6, acquisitionItemInfoList.get(i).getRawValue());
					cs.setString(7, acquisitionItemInfoList.get(i).getAlarmInfo());
					cs.setString(8, acquisitionItemInfoList.get(i).getAlarmLimit()+"");
					cs.setString(9, acquisitionItemInfoList.get(i).getHystersis()+"");
					cs.setInt(10, acquisitionItemInfoList.get(i).getAlarmLevel());
					cs.setInt(11, acquisitionItemInfoList.get(i).getIsSendMessage());
					cs.setInt(12, acquisitionItemInfoList.get(i).getIsSendMail());
					cs.executeUpdate();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cs!=null)
				cs.close();
			conn.close();
		}
		return true;
	}
}
