package org.wso2.carbon.issue.tracker.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.issue.tracker.bean.Comment;
import org.wso2.carbon.issue.tracker.dao.CommentDAO;
import org.wso2.carbon.issue.tracker.util.DBConfiguration;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link CommentDAO}
 *
 */
public class CommentDAOImpl implements CommentDAO {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final Log log = LogFactory.getLog(CommentDAOImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Comment> getCommentsForIssue(int issueId) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        String selectSQL = "SELECT ID, COMMENT, CREATED_TIME, UPDATED_TIME, CREATOR, ISSUE_ID FROM COMMENT WHERE ISSUE_ID = ? ORDER BY ID ASC";
        List<Comment> comments = new ArrayList<Comment>();

        try {
            dbConnection = DBConfiguration.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, issueId);

            // execute select SQL stetement
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {

                Comment comment = new Comment();
                comment.setId(rs.getInt("ID"));
                comment.setComment(rs.getString("COMMENT"));

                Timestamp createdTime = rs.getTimestamp("CREATED_TIME");
                String createdTimeStr = dateFormat.format(createdTime);
                comment.setCreatedTime(createdTimeStr);

                Timestamp updatedTime = rs.getTimestamp("UPDATED_TIME");
                String updatedTimeStr = dateFormat.format(updatedTime);
                comment.setCreatedTime(updatedTimeStr);

                comment.setCreator(rs.getString("CREATOR"));
                comment.setIssueId(rs.getInt("ISSUE_ID"));

                comments.add(comment);
            }

        } catch (SQLException e) {
            String msg = "Error while getting comment from DB, issueID: "+ issueId;
            log.error(msg, e);
            throw e;
        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }

        }
        return comments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCommentForIssue(Comment comment) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        String insertTableSQL = "INSERT INTO COMMENT (COMMENT, CREATED_TIME, UPDATED_TIME, CREATOR, ISSUE_ID) VALUES (?, ?, ?, ?, ?)";

        try {
            dbConnection = DBConfiguration.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(insertTableSQL);

            preparedStatement.setString(1, comment.getComment());
            preparedStatement.setTimestamp(2, getCurrentTimeStamp());
            preparedStatement.setTimestamp(3, getCurrentTimeStamp());
            preparedStatement.setString(4, comment.getCreator());
            preparedStatement.setInt(5, comment.getIssueId());

            // execute insert SQL stetement
            preparedStatement.executeUpdate();

            System.out.println("Record is inserted into COMMENT table!");

        } catch (SQLException e) {
            String msg = "Error while adding comment to DB, commentID: "+ comment.getId();
            log.error(msg, e);
            throw e;
        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCommentByCommentId(int commentId) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        String deleteSQL = "DELETE COMMENT WHERE ID = ? AND USER_ID = ?";

        try {
            dbConnection = DBConfiguration.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(deleteSQL);
            preparedStatement.setInt(1, commentId);

            // execute delete SQL statement
            int x = preparedStatement.executeUpdate();


            System.out.println("Record is deleted! " + x);

        } catch (SQLException e) {
            String msg = "Error while deleting comment from DB, commentID: " + commentId;
            log.error(msg, e);
            throw e;
        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void editComment(Comment comment) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        String updateTableSQL = "UPDATE COMMENT SET COMMENT = ?, UPDATED_TIME = ? WHERE ID = ?";


        try {
            dbConnection = DBConfiguration.getDBConnection();
            preparedStatement = dbConnection.prepareStatement(updateTableSQL);

            preparedStatement.setString(1, comment.getComment());
            preparedStatement.setTimestamp(2, getCurrentTimeStamp());
            preparedStatement.setInt(3, comment.getId());
            preparedStatement.setInt(4, comment.getIssueId());
            preparedStatement.setString(5, comment.getCreator());

            // execute update SQL stetement
            preparedStatement.executeUpdate();

            System.out.println("Record is updated to COMMENT  table!");

        } catch (SQLException e) {
            String msg = "Error while editing comment to DB, commentID: "+ comment.getId();
            log.error(msg, e);
            throw e;
        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }

        }
    }

    /**
     * Get current time to log DB
     * @return
     */
    private static Timestamp getCurrentTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new Timestamp(today.getTime());
    }

}
