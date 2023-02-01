package io.github.ititus.valve_tools.vpk;

import io.github.ititus.valve_tools.vpk.internal.IoUtil;

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

    static VpkSignatureSection load(ByteBuffer bb) {
        var publicKeySize = bb.getInt();
        var publicKey = IoUtil.sliceAdvance(bb, publicKeySize).asReadOnlyBuffer();

        var signatureSize = bb.getInt();
        var signature = IoUtil.sliceAdvance(bb, signatureSize).asReadOnlyBuffer();

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
