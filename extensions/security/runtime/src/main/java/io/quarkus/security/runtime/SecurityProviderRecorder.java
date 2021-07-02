package io.quarkus.security.runtime;

import static io.quarkus.security.runtime.SecurityProviderUtils.addProvider;
import static io.quarkus.security.runtime.SecurityProviderUtils.findProviderIndex;
import static io.quarkus.security.runtime.SecurityProviderUtils.insertProvider;
import static io.quarkus.security.runtime.SecurityProviderUtils.loadProvider;
import static io.quarkus.security.runtime.SecurityProviderUtils.loadProviderWithParams;

import java.security.Provider;
import java.security.Security;
import java.util.Set;

import javax.crypto.Cipher;

import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class SecurityProviderRecorder {
    private static final Logger LOG = Logger.getLogger(SecurityProviderRecorder.class);

    public void addBouncyCastleProvider(boolean inFipsMode, Set<String> cipherTransformations,
            ShutdownContext shutdownContext) {
        final String providerName = inFipsMode ? SecurityProviderUtils.BOUNCYCASTLE_FIPS_PROVIDER_CLASS_NAME
                : SecurityProviderUtils.BOUNCYCASTLE_PROVIDER_CLASS_NAME;
        Provider bc = loadProvider(providerName);
        addProvider(bc);
        registerBouncyCastleProvider(bc, cipherTransformations, shutdownContext);
    }

    public void addBouncyCastleJsseProvider(Set<String> cipherTransformations, ShutdownContext shutdownContext) {
        Provider bc = loadProvider(SecurityProviderUtils.BOUNCYCASTLE_PROVIDER_CLASS_NAME);
        Provider bcJsse = loadProvider(SecurityProviderUtils.BOUNCYCASTLE_JSSE_PROVIDER_CLASS_NAME);
        int sunJsseIndex = findProviderIndex(SecurityProviderUtils.SUN_JSSE_PROVIDER_NAME);
        insertProvider(bc, sunJsseIndex);
        insertProvider(bcJsse, sunJsseIndex + 1);

        registerBouncyCastleProvider(bc, cipherTransformations, shutdownContext);
    }

    public void addBouncyCastleFipsJsseProvider(Set<String> cipherTransformations, ShutdownContext shutdownContext) {
        Provider bc = loadProvider(SecurityProviderUtils.BOUNCYCASTLE_FIPS_PROVIDER_CLASS_NAME);
        int sunIndex = findProviderIndex(SecurityProviderUtils.SUN_PROVIDER_NAME);
        insertProvider(bc, sunIndex);
        Provider bcJsse = loadProviderWithParams(SecurityProviderUtils.BOUNCYCASTLE_JSSE_PROVIDER_CLASS_NAME,
                new Class[] { boolean.class, Provider.class }, new Object[] { true, bc });
        insertProvider(bcJsse, sunIndex + 1);
        registerBouncyCastleProvider(bc, cipherTransformations, shutdownContext);
    }

    public void registerBouncyCastleProvider(Provider provider, Set<String> cipherTransformations,
            ShutdownContext shutdownContext) {
        // Make it explicit to the static analysis that below security services should be registered as they are reachable at runtime
        for (String cipherTransformation : cipherTransformations) {
            try {
                LOG.debugf(
                        "Making it explicit to the static analysis that a Cipher with transformation %s could be used at runtime",
                        cipherTransformation);
                Cipher.getInstance(cipherTransformation, provider);
            } catch (Exception e) {
                // The cipher algorithm or padding is not present at runtime, a runtime error will be reported as usual
            }
        }

        shutdownContext.addShutdownTask(new Runnable() {
            @Override
            public void run() {
                Security.removeProvider(provider.getName());
                LOG.debug("Removed Bouncy Castle security provider");
            }
        });
    }
}
