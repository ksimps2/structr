/**
 * Copyright (C) 2010-2017 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.files.ssh;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.sshd.common.Factory;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.config.keys.PublicKeyEntry;
import org.apache.sshd.common.config.keys.PublicKeyEntryResolver;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.DirectoryHandle;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.Handle;
import org.apache.sshd.server.subsystem.sftp.SftpEventListener;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.api.config.Settings;
import org.structr.api.service.Command;
import org.structr.api.service.SingletonService;
import org.structr.api.service.StructrServices;
import org.structr.common.AccessMode;
import org.structr.common.SecurityContext;
import org.structr.console.Console.ConsoleMode;
import org.structr.core.app.StructrApp;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.Principal;
import org.structr.core.graph.Tx;
import org.structr.files.ssh.filesystem.StructrFilesystem;
import org.structr.rest.auth.AuthHelper;

/**
 *
 *
 */
public class SSHService implements SingletonService, PasswordAuthenticator, PublickeyAuthenticator, FileSystemFactory, Factory<org.apache.sshd.server.Command>, SftpEventListener, CommandFactory {

	private static final Logger logger = LoggerFactory.getLogger(SSHService.class.getName());

	private final ScpCommandFactory scp     = new ScpCommandFactory.Builder().build();
	private SshServer server                = null;
	private boolean running                 = false;
	private SecurityContext securityContext = null;

	@Override
	public void injectArguments(final Command command) {
	}

	@Override
	public void initialize(final StructrServices services) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		logger.info("Setting up SSH server..");

		server = SshServer.setUpDefaultServer();

		logger.info("Initializing host key generator..");

		final SimpleGeneratorHostKeyProvider hostKeyProvider = new SimpleGeneratorHostKeyProvider(Paths.get("db/structr_hostkey"));
		hostKeyProvider.setAlgorithm(KeyUtils.RSA_ALGORITHM);

		logger.info("Configuring SSH server..");

		server.setKeyPairProvider(hostKeyProvider);
		server.setPort(Settings.SshPort.getValue());
		server.setPasswordAuthenticator(this);
		server.setPublickeyAuthenticator(this);
		server.setFileSystemFactory(this);
		server.setSubsystemFactories(getSubsystems());
		server.setShellFactory(this);
		server.setCommandFactory(this);

		logger.info("Starting SSH server on port {}", server.getPort());

		try {

			server.start();
			running = true;
			logger.info("Initialization complete.");

		} catch (IOException ex) {
			ex.printStackTrace();
			//logger.error("", ex);
			logger.info("Initialization failed.");
		}

	}

	@Override
	public void shutdown() {

		try {

			server.stop(true);
			running = false;

		} catch (IOException ex) {
			logger.error("", ex);
		}
	}

	@Override
	public void initialized() {
	}

	@Override
	public String getName() {
		return "SSHService";
	}

	@Override
	public boolean isRunning() {
		return server != null && running;
	}

	@Override
	public boolean isVital() {
		return false;
	}

	// ----- interface Feature -----
	@Override
	public String getModuleName() {
		return "file-access";
	}

	// ----- -----
	@Override
	public FileSystem createFileSystem(final Session session) throws IOException {
		return new StructrFilesystem(securityContext);
	}

	@Override
	public boolean authenticate(final String username, final String password, final ServerSession session) {

		boolean isValid     = false;
		Principal principal = null;

		try (final Tx tx = StructrApp.getInstance().tx()) {

			principal = AuthHelper.getPrincipalForPassword(AbstractNode.name, username, password);
			if (principal != null) {

				isValid = true;
				securityContext = SecurityContext.getInstance(principal, AccessMode.Backend);
			}

			tx.success();

		} catch (Throwable t) {
			logger.warn("", t);

			isValid = false;
		}

		try {
			if (isValid) {
				session.setAuthenticated();
			}

		} catch (IOException ex) {
			logger.error("", ex);
		}

		return isValid;
	}

	@Override
	public boolean authenticate(final String username, final PublicKey key, final ServerSession session) {

		boolean isValid = false;

		if (key == null) {
			return isValid;
		}

		try (final Tx tx = StructrApp.getInstance().tx()) {

			final Principal principal = StructrApp.getInstance().nodeQuery(Principal.class).andName(username).getFirst();
			if (principal != null) {

				securityContext = SecurityContext.getInstance(principal, AccessMode.Backend);

				// check single (main) pubkey
				final String pubKeyData = principal.getProperty(Principal.publicKey);
				if (pubKeyData != null) {

					final PublicKey pubKey = PublicKeyEntry.parsePublicKeyEntry(pubKeyData).resolvePublicKey(PublicKeyEntryResolver.FAILING);

					isValid = KeyUtils.compareKeys(pubKey, key);

				}

				// check array of pubkeys for this user
				final String[] pubKeysData = principal.getProperty(Principal.publicKeys);
				if (pubKeysData != null) {

					for (final String k : pubKeysData) {

						if (k != null) {
							final PublicKey pubKey = PublicKeyEntry.parsePublicKeyEntry(k).resolvePublicKey(PublicKeyEntryResolver.FAILING);
							if (KeyUtils.compareKeys(pubKey, key)) {

								isValid = true;
								break;
							}
						}
					}

				}
			}

			tx.success();

		} catch (Throwable t) {
			logger.warn("", t);

			isValid = false;
		}

		try {
			if (isValid) {
				session.setAuthenticated();
			}

		} catch (IOException ex) {
			logger.error("Unable to authenticate session", ex);
		}

		return isValid;
	}

	private Tx currentTransaction = null;

	@Override
	public org.apache.sshd.server.Command create() {
		return new StructrConsoleCommand(securityContext);
	}

	// ----- private methods -----
	private void beginTransaction() {

		if (currentTransaction == null) {

			currentTransaction = StructrApp.getInstance(securityContext).tx(true, false, false);
		}
	}

	private void endTransaction() {

		if (currentTransaction != null) {

			try {
				currentTransaction.success();
				currentTransaction.close();

			} catch (Throwable t) {

				logger.warn("", t);

			} finally {

				currentTransaction = null;
			}
		}
	}

	// ----- interface SftpEventListener -----
	@Override
	public void initialized(ServerSession session, int version) {
	}

	@Override
	public void destroying(ServerSession session) {
	}

	@Override
	public void open(ServerSession session, String remoteHandle, Handle localHandle) {
		beginTransaction();
	}

	@Override
	public void read(ServerSession session, String remoteHandle, DirectoryHandle localHandle, Map<String, Path> entries) {
	}

	@Override
	public void read(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, int readLen) {
	}

	@Override
	public void write(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) {
	}

	@Override
	public void blocking(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length, int mask) {
	}

	@Override
	public void blocked(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length, int mask, Throwable thrown) {
	}

	@Override
	public void unblocking(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length) {
	}

	@Override
	public void unblocked(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length, Boolean result, Throwable thrown) {
	}

	@Override
	public void close(ServerSession session, String remoteHandle, Handle localHandle) {
		endTransaction();
	}

	@Override
	public void creating(ServerSession session, Path path, Map<String, ?> attrs) {
		beginTransaction();
	}

	@Override
	public void created(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) {
		endTransaction();
	}

	@Override
	public void moving(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts) {
		beginTransaction();
	}

	@Override
	public void moved(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts, Throwable thrown) {
		endTransaction();
	}

	@Override
	public void removing(ServerSession session, Path path) {
		beginTransaction();
	}

	@Override
	public void removed(ServerSession session, Path path, Throwable thrown) {
		endTransaction();
	}

	@Override
	public void linking(ServerSession session, Path source, Path target, boolean symLink) {
		beginTransaction();
	}

	@Override
	public void linked(ServerSession session, Path source, Path target, boolean symLink, Throwable thrown) {
		endTransaction();
	}

	@Override
	public void modifyingAttributes(ServerSession session, Path path, Map<String, ?> attrs) {
	}

	@Override
	public void modifiedAttributes(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) {
	}

	// ----- interface CommandFactory -----
	@Override
	public org.apache.sshd.server.Command createCommand(final String command) {

		if (command.startsWith("scp ")) {
			return scp.createCommand(command);
		}

		if (command.startsWith("javascript ")) {
			return new StructrConsoleCommand(securityContext, ConsoleMode.JavaScript, command.substring(11));
		}

		if (command.startsWith("structrscript ")) {
			return new StructrConsoleCommand(securityContext, ConsoleMode.StructrScript, command.substring(14));
		}

		if (command.startsWith("cypher ")) {
			return new StructrConsoleCommand(securityContext, ConsoleMode.Cypher, command.substring(7));
		}

		if (command.startsWith("admin ")) {
			return new StructrConsoleCommand(securityContext, ConsoleMode.AdminShell, command.substring(6));
		}

		if (command.startsWith("rest ")) {
			return new StructrConsoleCommand(securityContext, ConsoleMode.REST, command.substring(5));
		}

		throw new IllegalStateException("Unknown subsystem for command '" + command + "'");
	}

	// ----- private methods -----
	private List<NamedFactory<org.apache.sshd.server.Command>> getSubsystems() {

		final List<NamedFactory<org.apache.sshd.server.Command>> list = new LinkedList<>();

		// sftp
		final SftpSubsystemFactory factory = new SftpSubsystemFactory();
		list.add(factory);
		factory.addSftpEventListener(this);

		return list;
	}
}
