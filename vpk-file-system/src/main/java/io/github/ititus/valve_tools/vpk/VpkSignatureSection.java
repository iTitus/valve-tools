package io.github.ititus.valve_tools.vpk;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VpkSignatureSection {

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
        var publicKey = r.readByteBuffer(publicKeySize).asReadOnlyBuffer();

        var signatureSize = r.readUInt();
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
