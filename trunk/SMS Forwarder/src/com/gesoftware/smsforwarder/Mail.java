/**
 * 
 */
package com.gesoftware.smsforwarder;

import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.content.Context;

public class Mail extends javax.mail.Authenticator {
	
	private Context mContext;
	
	private String mUsername;
	private String mPassword;

	private String[] mRecipients;
	private String mSender;

	private String mPort;
	private String mSocketPort;

	private String mHost;

	private String mSubject;
	private String mBody;

	private boolean mAuth;

	private boolean mDebug;

	private Multipart mMultipart;

	public Mail(Context context) {
		mContext = context;
		
		mHost = mContext.getString(R.string.host); // default smtp server
		mPort = mContext.getString(R.string.smtpPort); // default smtp port
		mSocketPort = mContext.getString(R.string.socketFactoryPort); // default socketfactory port

		mUsername = ""; // username
		mPassword = ""; // password
		mSender = ""; // email sent from
		mSubject = ""; // email subject
		mBody = ""; // email body

		mDebug = false; // debug mode on or off - default off
		mAuth = true; // smtp authentication - default on

		mMultipart = new MimeMultipart();

		// There is something wrong with MailCap, javamail can not find a
		// handler for the multipart/mixed part, so this bit needs to be added.
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap
				.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
	}

	public Mail(Context context, String username, String password) {
		this(context);

		mUsername = username;
		mPassword = password;
	}

	public boolean send() throws Exception {
		Properties props = _setProperties();

		if (!mUsername.equals("") && !mPassword.equals("") && mRecipients.length > 0
				&& !mSender.equals("") && !mSubject.equals("")
				&& !mBody.equals("")) {
			Session session = Session.getInstance(props, this);

			MimeMessage msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(mSender));

			InternetAddress[] addressTo = new InternetAddress[mRecipients.length];
			for (int i = 0; i < mRecipients.length; i++) {
				addressTo[i] = new InternetAddress(mRecipients[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

			msg.setSubject(mSubject);
			msg.setSentDate(new Date());

			// setup message body
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(mBody);
			mMultipart.addBodyPart(messageBodyPart);

			// Put parts in message
			msg.setContent(mMultipart);

			// send email
			Transport.send(msg);

			return true;
		} else {
			return false;
		}
	}

	public void addAttachment(String filename) throws Exception {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);

		mMultipart.addBodyPart(messageBodyPart);
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(mUsername, mPassword);
	}

	private Properties _setProperties() {
		Properties props = new Properties();

		props.put(mContext.getString(R.string.propHost), mHost);

		if (mDebug) {
			props.put(mContext.getString(R.string.propDebug), "true");
		}

		if (mAuth) {
			props.put(mContext.getString(R.string.propAuth), "true");
		}

		props.put(mContext.getString(R.string.propSmtpPort), mPort);
		props.put(mContext.getString(R.string.propSocketFactoryPort), mSocketPort);
		props.put(mContext.getString(R.string.propSocketFactoryClass),
				"javax.net.ssl.SSLSocketFactory");
		props.put(mContext.getString(R.string.propSocketFactoryFallback), "false");

		return props;
	}

	// the getters and setters
	public String getBody() {
		return mBody;
	}

	public void setBody(String body) {
		this.mBody = body;
	}

	public String getSubject() {
		return mSubject;
	}

	public void setSubject(String subject) {
		this.mSubject = subject;
	}

	public String getSender() {
		return mSender;
	}

	public void setSender(String sender) {
		this.mSender = sender;
	}

	public String[] getRecipients() {
		return mRecipients;
	}

	public void setRecipients(String[] recipients) {
		this.mRecipients = new String[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			this.mRecipients[i] = recipients[i];
		}
	}

}
