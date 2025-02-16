package com.parq.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.parq.server.dao.model.object.PaymentAccount;
import com.parq.server.dao.model.object.PaymentAccount.CardType;

/**
 * Dao class responsible for accessing and updating the PaymentAccount Table
 * 
 * 
 * @author GZ
 *
 */
public class PaymentAccountDao extends AbstractParqDaoParent {
	/**
	 * Name of the local cache use by this dao
	 */
	private static final String cacheName = "PaymentAccountCache";
	private static Cache myCache;

	private static final String sqlCreatePaymentAccount = 
		"INSERT INTO paymentaccount (user_id, customer_id, payment_method_id, cc_stub, is_default_payment, card_type) " + 
		"	VALUES (?, ?, ?, ?, ?, ?)";
	private static final String sqlDeletePaymentAccountById =
		"UPDATE paymentaccount SET is_deleted = TRUE WHERE account_id = ?";
	private static final String sqlGetAccountByUserId = 
		"SELECT account_id, user_id, customer_id, payment_method_id, cc_stub, is_default_payment, card_type " +
		"	FROM paymentaccount " +
		"	WHERE user_id = ? " +
		" 	AND is_deleted IS NOT TRUE"; 
	private static final String sqlClearDefaultPaymentMethodByUserId = 
		"UPDATE paymentaccount SET is_default_payment = FALSE " +
		" 	WHERE user_id = ?";
	private static final String sqlGetUserIdByAccountId = 
		"SELECT user_id FROM paymentaccount WHERE account_id = ?";
	

	private static final String paymentMethodsCache = "getAllPaymentMethodForUser:";

	public PaymentAccountDao() {
		super();
		if (myCache == null) {
			// create the cache.
			myCache = setupCache(cacheName);
		}
	}
	
	/**
	 * Create a new payment account.
	 * 
	 * @param paymentRequest
	 * @return <code>true</code> if account creation was successful. <code>false</code> otherwise
	 */
	public boolean createNewPaymentMethod(PaymentAccount request) {
		
		if (request == null || request.getUserId() <= 0) {
			throw new IllegalStateException("Invalid PaymentAccount create request");
		}
		
		// clear out the cache entry for user that is going to be updated
		revokeCache(myCache, paymentMethodsCache, request.getUserId() + "");
		 
		if (request.isDefaultPaymentMethod()) {
			clearDefaultPayment(request.getUserId());
		}

		PreparedStatement pstmt = null;
		Connection con = null;
		boolean newPaymentAccountCreated = false;
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlCreatePaymentAccount);
			pstmt.setLong(1, request.getUserId());
			pstmt.setString(2, request.getCustomerId());
			pstmt.setString(3, request.getPaymentMethodId());
			pstmt.setString(4, request.getCcStub());
			pstmt.setBoolean(5, request.isDefaultPaymentMethod());
			pstmt.setString(6, request.getCardType().name());
			
			newPaymentAccountCreated = pstmt.executeUpdate() == 1;
	
		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}
		
		return newPaymentAccountCreated;
	}
	
	private boolean clearDefaultPayment(long userId) {
	
		if (userId <= 0) {
			throw new IllegalStateException("Invalid ClearDefaultPayment request for userId:" + userId);
		}
		
		PreparedStatement pstmt = null;
		Connection con = null;
		boolean updateSuccessful = false;

		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlClearDefaultPaymentMethodByUserId);
			pstmt.setLong(1, userId);
			updateSuccessful = pstmt.executeUpdate() >= 0;

		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}
		
		return updateSuccessful;
	}

	public boolean deletePaymentMethod(long accountId) {

		if (accountId <= 0) {
			throw new IllegalStateException("Invalid PaymentAccount delete request");
		}
		// clear out the cache entry for user that is going to be updated
		long userId = getUserForPaymentAccountByAccountId(accountId);
		revokeCache(myCache, paymentMethodsCache, userId + "");
		
		PreparedStatement pstmt = null;
		Connection con = null;
		boolean deleteSuccessful = false;

		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlDeletePaymentAccountById);
			pstmt.setLong(1, accountId);
			deleteSuccessful = pstmt.executeUpdate() > 0;

		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}
		
		return deleteSuccessful;
	}

	private long getUserForPaymentAccountByAccountId(long accountId) {
		
		long userId = -1;
		
		// query the DB for the user object
		PreparedStatement pstmt = null;
		Connection con = null;
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlGetUserIdByAccountId);
			pstmt.setLong(1, accountId);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.isBeforeFirst() && rs.next()) {
				userId = rs.getLong("user_id");
			}

		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}
		
		return userId;
	}

	@SuppressWarnings("unchecked")
	public List<PaymentAccount> getAllPaymentMethodForUser(long userId) {
		if (userId <= 0) {
			throw new IllegalStateException("Invalide UserId: " + userId);
		}
		
		// the cache key for this method call;
		String cacheKey = paymentMethodsCache + userId;

		List<PaymentAccount> paymentAccounts = null;
		Element cacheEntry = myCache.get(cacheKey);
		if (cacheEntry  != null) {
			paymentAccounts = (List<PaymentAccount>) cacheEntry.getValue();
			return paymentAccounts;
		}

		// query the DB for the user object
		PreparedStatement pstmt = null;
		Connection con = null;
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sqlGetAccountByUserId);
			pstmt.setLong(1, userId);
			ResultSet rs = pstmt.executeQuery();
			
			paymentAccounts = createPaymentAccountObject(rs);

		} catch (SQLException sqle) {
			System.out.println("SQL statement is invalid: " + pstmt);
			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		} finally {
			closeConnection(con);
		}

		// put result into cache
		myCache.put(new Element(cacheKey, paymentAccounts));
		
		return paymentAccounts;
	}

	private List<PaymentAccount> createPaymentAccountObject(ResultSet rs) throws SQLException {
		if (rs == null || !rs.isBeforeFirst()) {
			return Collections.emptyList();
		}
		
		List<PaymentAccount> paymentAccounts = new ArrayList<PaymentAccount>();
		
		while (rs.next()) {
			PaymentAccount pa = new PaymentAccount();
			long accountId = rs.getLong("account_id");
			long userId = rs.getLong("user_id");
			String customerId = rs.getString("customer_id");
			String paymentMethodId = rs.getString("payment_method_id");
			String ccStub = rs.getString("cc_stub");
			String cardType = rs.getString("card_type");
			boolean isDefaultPaymentMethod = rs.getBoolean("is_default_payment");
			
			pa.setAccountId(accountId);
			pa.setUserId(userId);
			pa.setCustomerId(customerId);
			pa.setPaymentMethodId(paymentMethodId);
			pa.setCcStub(ccStub);
			pa.setDefaultPaymentMethod(isDefaultPaymentMethod);
			if (cardType != null) {
				pa.setCardType(CardType.valueOf(cardType));
			}
			
			paymentAccounts.add(pa);
		}
			
		return paymentAccounts;
	}
}
