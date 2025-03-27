package com.example;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.MdcConnectionListener;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConfig.DtlsRole;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.CredentialsUtil.Mode;

public class CaliforniumDtlsServer extends CoapServer {

	private static final Logger logger = LoggerFactory.getLogger(CaliforniumDtlsServer.class);

	private static final File CONFIG_FILE = new File("Californium3.properties");
	private static final String CONFIG_HEADER = "Californium CoAP Properties file for Secure Server";

	private static DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

		@Override
		public void applyDefinitions(Configuration config) {
			config.set(DtlsConfig.DTLS_ROLE, DtlsRole.SERVER_ONLY);
			config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
			config.set(DtlsConfig.DTLS_PRESELECTED_CIPHER_SUITES, CipherSuite.STRONG_ENCRYPTION_PREFERENCE);
		}
	};

	public static final List<Mode> SUPPORTED_MODES = Arrays.asList(Mode.PSK, Mode.ECDHE_PSK, Mode.RPK, Mode.X509,
			Mode.WANT_AUTH, Mode.NO_AUTH);

	static {
		CoapConfig.register();
		DtlsConfig.register();
	}

	public static void main(String[] args) {
		logger.info("Starting CaliforniumDtlsServer ...");

		Configuration configuration = Configuration.createWithFile(CONFIG_FILE, CONFIG_HEADER, DEFAULTS);
		Configuration.setStandard(configuration);

		int dtlsPort = configuration.get(CoapConfig.COAP_SECURE_PORT);

		CaliforniumDtlsServer server = new CaliforniumDtlsServer();

		server.add(new ResourceHello("hello"));

		DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(configuration)
				.setAddress(new InetSocketAddress(dtlsPort));

		CredentialsUtil.setupCid(new String[] { "PSK" }, builder);
		List<Mode> modes = CredentialsUtil.parse(new String[] { "PSK" }, CredentialsUtil.DEFAULT_SERVER_MODES,
				SUPPORTED_MODES);
		CredentialsUtil.setupCredentials(builder, CredentialsUtil.SERVER_NAME, modes);

		builder.setConnectionListener(new MdcConnectionListener());

		DTLSConnector connector = new DTLSConnector(builder.build());

		CoapEndpoint.Builder coapBuilder = new CoapEndpoint.Builder().setConfiguration(configuration)
				.setConnector(connector);

		server.addEndpoint(coapBuilder.build());

		server.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			server.stop();
		}));

		logger.info("Started CaliforniumDtlsServer");
	}
}
