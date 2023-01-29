package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VpkSignatureSection {

    static final int SIZE = 296;

    private final int publicKeySize;
    private final ByteBuffer publicKey;
    private final int signatureSize;
    private final ByteBuffer signature;

    private VpkSignatureSection(int publicKeySize, ByteBuffer publicKey, int signatureSize, ByteBuffer signature) {
        this.publicKeySize = publicKeySize;
        this.publicKey = publicKey;
        this.signatureSize = signatureSize;
        this.signature = signature;
    }

    static VpkSignatureSection load(DataReader r) throws IOException {
        var publicKeySize = r.readUInt();
        if (publicKeySize != 160) {
            throw new VpkException("unexpected publicKeySize");
        }
        var publicKey = r.readByteBuffer(publicKeySize).asReadOnlyBuffer();

        var signatureSize = r.readUInt();
        if (signatureSize != 128) {
            throw new VpkException("unexpected signatureSize");
        }
        var signature = r.readByteBuffer(signatureSize).asReadOnlyBuffer();

        return new VpkSignatureSection(publicKeySize, publicKey, signatureSize, signature);
    }

    public long getPublicKeySize() {
        return Integer.toUnsignedLong(publicKeySize);
    }

    public ByteBuffer getPublicKey() {
        return publicKey;
    }

    public long getSignatureSize() {
        return Integer.toUnsignedLong(signatureSize);
    }

    public ByteBuffer getSignature() {
        return signature;
    }
}
