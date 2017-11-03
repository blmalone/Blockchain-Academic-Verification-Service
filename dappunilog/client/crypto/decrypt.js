/**
 * Decrypt the ciphertext with the MetaMask privateKey associated with an identity
 * @param  {{privateKey: ?string, publicKey: string}}   identity
 * @param  {string}   cipherText
 * @return {string}   message
 */
decrypt = function (identity, cipherText) {
    var privKey = new bitcore.PrivateKey(identity.privateKey);
    var alice = ECIES().privateKey(privKey);

    var decryptMe = new Buffer(cipherText, 'hex');

    var decrypted = alice.decrypt(decryptMe);
    return decrypted.toString('ascii');
};