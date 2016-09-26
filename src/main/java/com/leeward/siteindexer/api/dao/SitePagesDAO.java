package com.leeward.siteindexer.api.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leeward.siteindexer.api.constants.DBConstants;
import com.leeward.siteindexer.api.models.SitePagesModel;
import com.leeward.siteindexer.api.util.AppUtil;
import com.leeward.siteindexer.api.util.DBUtil;
import com.leeward.siteindexer.api.util.PreparedStatementHelper;

public class SitePagesDAO implements DBConstants {

	private transient Connection conn = null;
	private static Logger log = LoggerFactory.getLogger(SitePagesDAO.class);

	public int addSitepages(SitePagesModel bean) {
		int numRows = 0;
		PreparedStatement pstmt = null;
		StringBuffer sInsertStmt = new StringBuffer(200);
		sInsertStmt.append("INSERT INTO " + SITEPAGES + " (").append(" site_url ").append(", title ").append(", text ")
				.append(", page_url ").append(") VALUES ( ").append(" ?").append(", ?")
				.append(", ?").append(", ?").append(")");
		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sInsertStmt.toString());
			PreparedStatementHelper prepHelper = new PreparedStatementHelper(pstmt);
			prepHelper.setString(1, bean.getSiteUrl());
			prepHelper.setString(2, bean.getTitle());
			prepHelper.setString(3, bean.getText());
			prepHelper.setString(4, bean.getPageUrl());
			AppUtil.logPrepStatment(sInsertStmt.toString(), prepHelper, log);
			numRows = prepHelper.executeUpdate();
		} catch (Exception e) {
			log.error("Exception adding record:  " + e.getMessage());
		} finally {
			DBUtil.returnObjects(pstmt, null, conn);
		}
		return numRows;
	}

	public int updateSitepages(SitePagesModel bean) {
		int numRows = 0;
		PreparedStatement pstmt = null;
		StringBuffer sUpdateStmt = new StringBuffer(200);
		sUpdateStmt.append("UPDATE " + SITEPAGES).append(" SET ").append(" id = ? ").append(", site_url = ? ")
				.append(", title = ? ").append(", text = ? ").append(", page_url = ? ").append(", updated = ? ");
		StringBuffer sWhereStmt = new StringBuffer(100);
		sWhereStmt.append(" WHERE id = ?");
		sUpdateStmt.append(sWhereStmt);
		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sUpdateStmt.toString());
			PreparedStatementHelper prepHelper = new PreparedStatementHelper(pstmt);
			prepHelper.setInt(1, bean.getId());
			prepHelper.setString(2, bean.getSiteUrl());
			prepHelper.setString(3, bean.getTitle());
			prepHelper.setString(4, bean.getText());
			prepHelper.setString(5, bean.getPageUrl());
			prepHelper.setTimestamp(6,
					bean.getUpdated() != null ? new java.sql.Timestamp(bean.getUpdated().getTime()) : null);
			prepHelper.setInt(7, bean.getId());
			AppUtil.logPrepStatment(sUpdateStmt.toString(), prepHelper, log);
			numRows = prepHelper.executeUpdate();
		} catch (Exception e) {
			log.error("Exception adding record:  " + e.getMessage());
		} finally {
			DBUtil.returnObjects(pstmt, null, conn);
		}
		return numRows;
	}

	public SitePagesModel getSitepages(SitePagesModel bean) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		SitePagesModel model = null;
		String sqlString = "select " + "id" + ", site_url" + ", title" + ", text" + ", page_url" + ", updated"
				+ " 	from " + SITEPAGES + " where id = ?";
		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sqlString);
			PreparedStatementHelper prepHelper = new PreparedStatementHelper(pstmt);
			prepHelper.setInt(1, bean.getId());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				model = new SitePagesModel();
				model.setId(rs.getInt("id"));
				model.setSiteUrl(rs.getString("site_url"));
				model.setTitle(rs.getString("title"));
				model.setText(rs.getString("text"));
				model.setPageUrl(rs.getString("page_url"));
				model.setUpdated(rs.getTimestamp("updated") != null
						? new java.util.Date(rs.getTimestamp("updated").getTime()) : null);
			}
		} catch (Exception e) {
			log.error("Exception getting record:  " + e.getMessage());
		} finally {
			DBUtil.returnObjects(pstmt, rs, conn);
		}
		return model;
	}

	public List<SitePagesModel> getSitepagesList() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<SitePagesModel> list = new ArrayList<SitePagesModel>();
		SitePagesModel model = null;
		String sqlString = "select " + "id" + ", site_url" + ", title" + ", text" + ", page_url" + ", updated"
				+ " 	from " + SITEPAGES;
		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sqlString);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				model = new SitePagesModel();
				model.setId(rs.getInt("id"));
				model.setSiteUrl(rs.getString("site_url"));
				model.setTitle(rs.getString("title"));
				model.setText(rs.getString("text"));
				model.setPageUrl(rs.getString("page_url"));
				model.setUpdated(rs.getTimestamp("updated") != null
						? new java.util.Date(rs.getTimestamp("updated").getTime()) : null);
				list.add(model);
			}
		} catch (Exception e) {
			log.error("Exception getting record:  " + e.getMessage());
		} finally {
			DBUtil.returnObjects(pstmt, rs, conn);
		}
		return list;
	}

	public int deleteSitepages(String siteName) {
		PreparedStatement pstmt = null;
		int numRows = 0;
		StringBuffer sDeleteStmt = new StringBuffer(200);
		sDeleteStmt.append("DELETE FROM " + SITEPAGES);
		StringBuffer sWhereStmt = new StringBuffer(100);
		sWhereStmt.append(" WHERE site_url = ?");
		sDeleteStmt.append(sWhereStmt);
		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sDeleteStmt.toString());
			PreparedStatementHelper prepHelper = new PreparedStatementHelper(pstmt);
			prepHelper.setString(1, siteName);
			AppUtil.logPrepStatment(sDeleteStmt.toString(), prepHelper, log);
			numRows = prepHelper.executeUpdate();
		} catch (Exception e) {
			log.error("Exception deleting record:  " + e.getMessage());
		} finally {
			DBUtil.returnObjects(pstmt, null, conn);
		}
		return numRows;
	}

	public int deleteSitepages(SitePagesModel bean) {
		PreparedStatement pstmt = null;
		int numRows = 0;
		StringBuffer sDeleteStmt = new StringBuffer(200);
		sDeleteStmt.append("DELETE FROM " + SITEPAGES);
		StringBuffer sWhereStmt = new StringBuffer(100);
		sWhereStmt.append(" WHERE id = ?");
		sDeleteStmt.append(sWhereStmt);
		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sDeleteStmt.toString());
			PreparedStatementHelper prepHelper = new PreparedStatementHelper(pstmt);
			prepHelper.setInt(1, bean.getId());
			AppUtil.logPrepStatment(sDeleteStmt.toString(), prepHelper, log);
			numRows = prepHelper.executeUpdate();
		} catch (Exception e) {
			log.error("Exception deleting record:  " + e.getMessage());
		} finally {
			DBUtil.returnObjects(pstmt, null, conn);
		}
		return numRows;
	}

	public int getAutoIncrementKey() {
		int key = -1;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlString = "select last_insert_id()";
		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sqlString);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				key = rs.getInt(0);
			}
		} catch (Exception e) {
			log.error("Exception getting auto increment key:  " + e.getMessage());
		} finally {
			DBUtil.returnObjects(pstmt, rs, conn);
		}
		return key;
	}

}
