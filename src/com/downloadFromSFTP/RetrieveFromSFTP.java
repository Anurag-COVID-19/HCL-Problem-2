package com.downloadFromSFTP;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class RetrieveFromSFTP {
	private static final Logger logger = LoggerFactory.getLogger(RetrieveFromSFTP.class);

	private static final int PORT = 22;

	private final String user;
	private final String host;
	private final String privateKey;
	private final byte[] keyPassword;
	
	private JSch jsch;
	private Session session;
	private ChannelSftp sftp;

	private String[] ignoreFiles;
	
	/**
	 * opens a sftp connection with the given privatekey 
	 * @param user
	 * @param host
	 * @param privateKey
	 */
	public RetrieveFromSFTP(String user, String host, String privateKey) {
		this(user, host, privateKey, null);
	}
	
	

	/**
	 * opens a sftp connection with the given private key and the key password
	 * @param user
	 * @param host
	 * @param privateKey
	 * @param keyPassword
	 */
	public RetrieveFromSFTP(String user, String host, String privateKey, byte[] keyPassword) {
		this.user = user;
		this.host = host;
		this.privateKey = privateKey;
		this.keyPassword = keyPassword;

		try {
			init();
		} catch (JSchException e) {
			throw new RuntimeException("Could not connect to host [" + host + "] using KeyFile [" 
					+ privateKey + "] for User [" + user + "]", e);
		}
	}

	public String[] getIgnoreFiles() {
		return ignoreFiles;
	}

	/**
	 * ignore the files which starts with the given strings
	 *
	 * @param ignoreFiles
	 */
	public void setIgnoreFiles(String[] ignoreFiles) {
		this.ignoreFiles = ignoreFiles;
	}

	public ChannelSftp getSftp() {
		return sftp;
	}
	
	private void init() throws JSchException {
		jsch = new JSch();
		jsch.addIdentity(privateKey, keyPassword);
		logger.debug("identity added");

		session = jsch.getSession(user, host, PORT);
		logger.debug("session created.");

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);

		session.connect();
		logger.debug("session connected.....");

		Channel channel = session.openChannel("sftp");
		channel.connect();

		logger.debug("shell channel connected....");

		sftp = (ChannelSftp) channel;
	}

	/**
	 * downloads the files from the remotefolder to the local folder
	 * @param remoteFolder
	 * @param localFolder
	 * @throws SftpException
	 */
	@SuppressWarnings("unchecked")
	public void downloadFiles(String remoteFolder, String localFolder) throws SftpException {
		File localFile = new File(localFolder);
		if (localFile.exists()) {
			Vector<ChannelSftp.LsEntry> fileList = sftp.ls(remoteFolder);
			File destFile;
			for (ChannelSftp.LsEntry file : fileList) {
				if (isRealFile(file.getFilename()) && !ignoreFile(file.getFilename())) {
					destFile = new File(localFolder, file.getFilename());
					if (destFile.exists()) { // if image file already exist
						logger.info("file already exist " + destFile.getAbsolutePath());
					}
					sftp.get(remoteFolder + file.getFilename(), localFolder);
					logger.debug("download " + file.getFilename());
				}
			}
		} else {
			logger.error("local folder \"" + localFile.getAbsolutePath() + "\" does not exist");
		}
	}
	
	private boolean ignoreFile(String fileName) {
		if (ignoreFiles != null) {
			for (String ignore : ignoreFiles) {
				if (fileName.startsWith(ignore)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void destroy() {
		sftp.exit();
		sftp.disconnect();
		session.disconnect();
	}

	/**
	 * check if the filename is not ".." and "." (for unix)
	 * @param filename
	 * @return
	 */
	public static boolean isRealFile(String filename) {
		return (!filename.equals("..") && !filename.equals("."));
	}
	
}
