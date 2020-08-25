package me.nixuehan.tcc.repository;

import me.nixuehan.api.TransactionStage;
import me.nixuehan.tcc.exception.TransactionRuntimeException;
import me.nixuehan.tcc.transaction.ResourceEntity;
import org.apache.dubbo.common.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JdbcTransactionRepositoryImpl implements TransactionRepository {

    private String projectId;

    private DataSource dataSource;

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected DataSource getDataSource() {
        return this.dataSource;
    }

    protected Connection getConnection() {
        try {
            return this.getDataSource().getConnection();
        } catch (SQLException e) {
            throw new TransactionRuntimeException(e);
        }
    }

    public String tableName() {
        return "tcc_" + projectId;
    }

    @Override
    public boolean create(ResourceEntity entity) {

        Connection connection = null;
        PreparedStatement stmt = null;
        int result = 0;

        try{

            connection = getConnection();
            stmt = connection.prepareStatement("INSERT INTO " +  tableName() + " VALUES(null ,?,?,?,?,?,?,?,?,?,?)");

            stmt.setNString(1,entity.getGlobalTransactionId());
            stmt.setNString(2,entity.getBranchResourceId());
            stmt.setNString(3,entity.getModule());
            stmt.setBytes(4,entity.getContent());
            stmt.setInt(5,entity.getStage());
            stmt.setInt(6,entity.getIsDelete());
            stmt.setDate(7, new Date(entity.getCreateTime().getTime()));
            stmt.setInt(8,entity.getRetriedCount());
            stmt.setInt(9,entity.getVersion());
            stmt.setDate(10, new Date(entity.getLastUpdateTime().getTime()));

            result = stmt.executeUpdate();


        }catch (SQLException e) {

            throw new TransactionRuntimeException(e);

        }finally {
            closeStatement(stmt);
            releaseConnection(connection);
        }

        return result == 1 ? true : false;
    }

    @Override
    public boolean delete(ResourceEntity entity) {
        Connection connection = null;
        PreparedStatement stmt = null;
        int result = 0;

        try{

            connection = getConnection();
            stmt = connection.prepareStatement("DELETE FROM " +  tableName() + " WHERE global_transaction_id = ? AND branch_resource_id = ?");

            stmt.setNString(1,entity.getGlobalTransactionId());
            stmt.setNString(2,entity.getBranchResourceId());

            result = stmt.executeUpdate();


        }catch (SQLException e) {

            throw new TransactionRuntimeException(e);

        }finally {
            closeStatement(stmt);
            releaseConnection(connection);
        }

        return result == 1 ? true : false;
    }

    @Override
    public boolean delete() {
        Connection connection = null;
        PreparedStatement stmt = null;
        int result = 0;

        try{

            connection = getConnection();
            stmt = connection.prepareStatement("DELETE FROM " +  tableName() + " WHERE global_transaction_id != ? AND branch_resource_id != ?");

            stmt.setNString(1,"");
            stmt.setNString(2,"");

            result = stmt.executeUpdate();


        }catch (SQLException e) {

            throw new TransactionRuntimeException(e);

        }finally {
            closeStatement(stmt);
            releaseConnection(connection);
        }

        return result == 1 ? true : false;
    }

    @Override
    public boolean update(ResourceEntity entity) {

        Connection connection = null;
        PreparedStatement stmt = null;
        int result = 0;

        try{

            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();

            builder.append("UPDATE " +  tableName() + " SET " +
                    "stage = ?, is_delete = ?,retried_count = ?, last_update_time = ?,version=version+1");

            builder.append(" WHERE global_transaction_id = ?");
            builder.append(" AND version = ?");

            if (StringUtils.isNotEmpty(entity.getBranchResourceId())) {
                builder.append(" AND branch_resource_id = ?");
            }


            stmt = connection.prepareStatement(builder.toString());

            stmt.setInt(1,entity.getStage());
            stmt.setInt(2,entity.getIsDelete());
            stmt.setInt(3,entity.getRetriedCount());
            stmt.setDate(4, new Date(entity.getLastUpdateTime().getTime()));
            stmt.setNString(5,entity.getGlobalTransactionId());
            stmt.setInt(6,entity.getVersion());

            if (StringUtils.isNotEmpty(entity.getBranchResourceId())) {
                stmt.setNString(7,entity.getBranchResourceId());
            }


            result = stmt.executeUpdate();

        }catch (SQLException e) {
            throw new TransactionRuntimeException(e);
        }finally {
            closeStatement(stmt);
            releaseConnection(connection);
        }

        return result == 1 ? true : false;
    }

    @Override
    public List<ResourceEntity> find(String getGlobalTransactionId) {
        Connection connection = null;
        PreparedStatement stmt = null;

        List<ResourceEntity> resourceEntityList = new ArrayList<>();

        try{

            connection = this.getConnection();

            stmt = connection.prepareStatement("SELECT * FROM " + tableName() + " WHERE global_transaction_id = ?");

            stmt.setNString(1,getGlobalTransactionId);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {

                ResourceEntity entity = new ResourceEntity();
                entity.setId(resultSet.getLong(1));
                entity.setGlobalTransactionId(resultSet.getString(2));
                entity.setBranchResourceId(resultSet.getString(3));
                entity.setModule(resultSet.getString(4));
                entity.setContent(resultSet.getBytes(5));
                entity.setStage(resultSet.getInt(6));
                entity.setIsDelete(resultSet.getInt(7));
                entity.setCreateTime(resultSet.getDate(8));
                entity.setRetriedCount(resultSet.getInt(9));
                entity.setVersion(resultSet.getInt(10));
                entity.setLastUpdateTime(resultSet.getDate(11));

                resourceEntityList.add(entity);
            }

        }catch (SQLException e) {
            throw new TransactionRuntimeException(e);
        }finally {
            closeStatement(stmt);
            releaseConnection(connection);
        }

        return resourceEntityList;
    }

    @Override
    public List<ResourceEntity> findUnmodified() {
        Connection connection = null;
        PreparedStatement stmt = null;

        List<ResourceEntity> resourceEntityList = new ArrayList<>();

        try{

            connection = this.getConnection();

            stmt = connection.prepareStatement("SELECT * FROM " + tableName() + " WHERE branch_resource_id = ? and last_update_time > ? and is_delete = ?");

            stmt.setString(1,"");
            stmt.setDate(2,new Date(new java.util.Date().getTime() - 120));
            stmt.setInt(3,0);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {

                ResourceEntity entity = new ResourceEntity();
                entity.setId(resultSet.getLong(1));
                entity.setGlobalTransactionId(resultSet.getString(2));
                entity.setBranchResourceId(resultSet.getString(3));
                entity.setModule(resultSet.getString(4));
                entity.setContent(resultSet.getBytes(5));
                entity.setStage(resultSet.getInt(6));
                entity.setIsDelete(resultSet.getInt(7));
                entity.setCreateTime(resultSet.getDate(8));
                entity.setLastUpdateTime(resultSet.getDate(9));

                resourceEntityList.add(entity);
            }

        }catch (SQLException e) {
            throw new TransactionRuntimeException(e);
        }finally {
            closeStatement(stmt);
            releaseConnection(connection);
        }

        return resourceEntityList;
    }

    protected void closeStatement(Statement stmt) {
        try {
            if (Objects.nonNull(stmt) && stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new TransactionRuntimeException(e);
        }
    }

    protected void releaseConnection(Connection connection) {
        try {
            if (Objects.nonNull(connection) && connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new TransactionRuntimeException(e);
        }
    }
}
