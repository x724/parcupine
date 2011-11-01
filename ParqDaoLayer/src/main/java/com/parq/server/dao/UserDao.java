package com.parq.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.parq.server.dao.model.object.User;

/**
 * Dao class responsible for accessing and updating the User Table
 * 
 * @author GZ
 * 
 */
public class UserDao extends AbstractParqDaoParent {

	/**
	 * Name of the local cache use by this dao
	 */
	private static final String cacheName = "UserCache";
	private static Cache myCache;

	private static final String sqlGetUserStatement = "SELECT User_ID, UserName, Password, eMail FROM User ";
	private static final String isNotDeleted = " AND Is_Deleted IS NOT TRUE";
	private static final String sqlGetUserById = sqlGetUserStatement + "WHERE User_ID = ? " + isNotDeleted;
	private static final String sqlGetUserByUserName = sqlGetUserStatement + "WHERE UserName = ? " + isNotDeleted;
	private static final String sqlGetUserByEmail = sqlGetUserStatement + "WHERE eMail = ? " + isNotDeleted;

	private static final String sqlDeleteUserById = "UPDATE User SET Is_Deleted = TRUE, UserName = ? WHERE User_ID = ?";
	private static final String sqlUpdateUser = "UPDATE User SET UserName = ?, Password = ?, eMail = ? "
			+ " WHERE User_ID = ?";
	private static final String sqlCreateUser = "INSERT INTO User (UserName, Password, eMail) "
			+ " VALUES (?, ?, ?)";
	
	private static final String emailCache = "getUserByEmail:";
	private static final String idCache = "getUserById:";
	private static final String userNameCache = "getUserByUserName:";
	

	public UserDao() {
		super();
		if (myCache == null) {
			// create the cache.
			myCache = setupCache(cacheName);
		}
	}

	/**
	 * Create the User model object from the DB query result set.
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private User createUserObject(ResultSet rs) throws SQLException {
		if (rs == null || !rs.isBeforeFirst()) {
			return null;
		}
		User user = new User();
		rs.first();
		user.setUserID(rs.getInt("User_ID"));
		user.setUserName(rs.getString("UserName"));
		user.setPassword(rs.getString("Password"));
		user.setEmail(rs.getString("eMail"));
		return user;
	}

	public User getUserById(int id) {
		// the cache key for this method call;
		String cacheKey = idCache + id;
		
		User user = null;
		if (myCache.get(cacheKey) != null) {
			user = (User) myCache.get(cacheKey).getValue();
			return user;
		}

		// query the DB for the user object
		PreparedStatement pstmt = null;
		Connection con = null;
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlGetUserById);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			user = createUserObject(rs);

		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}

		// put result into cache
		myCache.put(new Element(cacheKey, user));
		
		return user;
	}

	public User getUserByUserName(String userName) {
		// the cache key for this method call;
		String cacheKey = userNameCache + userName;

		User user = null;
		if (myCache.get(cacheKey) != null) {
			user = (User) myCache.get(cacheKey).getValue();
			return user;
		}

		// query the DB for the user object
		PreparedStatement pstmt = null;
		Connection con = null;
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlGetUserByUserName);
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			user = createUserObject(rs);

		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}

		// put result into cache
		myCache.put(new Element(cacheKey, user));
		
		return user;
	}

	public User getUserByEmail(String emailAddress) {
		// the cache key for this method call;
		String cacheKey = emailCache + emailAddress;

		User user = null;
		if (myCache.get(cacheKey) != null) {
			user = (User) myCache.get(cacheKey).getValue();
			return user;
		}

		// query the DB for the user object
		PreparedStatement pstmt = null;
		Connection con = null;
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlGetUserByEmail);
			pstmt.setString(1, emailAddress);
			ResultSet rs = pstmt.executeQuery();

			user = createUserObject(rs);

		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}

		// put result into cache
		myCache.put(new Element(cacheKey, user));
		
		return user;
	}

	public synchronized boolean deleteUserById(int id) {

		if (id <= 0) {
			throw new IllegalStateException("Invalid user delete request");
		}
		
		User delUser = getUserById(id);
		
		// clear out the cache entry for deleted user
		revokeUserCacheById(id);
		
		PreparedStatement pstmt = null;
		Connection con = null;
		boolean deleteSuccessful = false;
		
		if (delUser != null) {
			try {
				con = getConnection();
				pstmt = con.prepareStatement(sqlDeleteUserById);
				pstmt.setString(1, delUser.getUserName() + " deleted_On:" + System.currentTimeMillis());
				pstmt.setInt(2, id);
				deleteSuccessful = pstmt.executeUpdate() > 0;
	
			} catch (SQLException sqle) {
				System.out.println("SQL statement is invalid: " + pstmt);
				sqle.printStackTrace();
				throw new RuntimeException(sqle);
			} finally {
				closeConnection(con);
			}
		}
		
		return deleteSuccessful;
	}

	public synchronized boolean updateUser(User user) {

		if (user == null || user.getUserName() == null
				|| user.getEmail() == null || user.getUserID() <= 0) {
			throw new IllegalStateException("Invalid user update request");
		}
		
		// clear out the cache entry for user that is going to be updated
		revokeUserCacheById(user.getUserID());
		
		PreparedStatement pstmt = null;
		Connection con = null;
		boolean updateSuccessful = false;
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlUpdateUser);
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getEmail());
			pstmt.setInt(4, user.getUserID());
			updateSuccessful = pstmt.executeUpdate() > 0;

		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}

		return updateSuccessful;
	}


	public synchronized boolean createNewUser(User user) {

		if (user == null || user.getUserName() == null
				|| user.getEmail() == null) {
			throw new IllegalStateException("Invalid user create request");
		}
		// test to make sure no duplicate username is created or email used
		else if (getUserByUserName(user.getUserName()) != null) {
			throw new IllegalStateException("Userame: " + user.getUserName() + " already exist");
		}
		else if(getUserByEmail(user.getEmail()) != null) {
			throw new IllegalStateException("Email: " + user.getEmail() + " already exist");
		}
		
		// clear out the cache entry for user that is going to be updated
		revokeCache(myCache, userNameCache, user.getUserName());
		revokeCache(myCache, emailCache, user.getEmail());
		
		PreparedStatement pstmt = null;
		Connection con = null;
		boolean newUserCreated = false;
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlCreateUser);
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getEmail());
			newUserCreated = pstmt.executeUpdate() == 1;

		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}
		
		return newUserCreated;
	}

	
	/**
	 * Revoke all the cache instance of this User by id, username, and email address.
	 * @param userID
	 */
	private synchronized void revokeUserCacheById(int userID) {
		if (userID < 0) {
			return;
		}
		User user = getUserById(userID);
		
		revokeCache(myCache, idCache, "" + userID);
		revokeCache(myCache, userNameCache, user.getUserName());
		revokeCache(myCache, emailCache, user.getEmail());
	}

	/**
	 * manually clear out the cache
	 * @return
	 */
	public boolean clearUserCache() {
		myCache.removeAll();
		return true;
	}
}
